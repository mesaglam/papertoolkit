package edu.stanford.hci.r3.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.hci.r3.events.PenEvent.PenEventModifier;
import edu.stanford.hci.r3.events.replay.EventReplayManager;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.pattern.coordinates.PatternLocationToSheetLocationMapping;
import edu.stanford.hci.r3.pattern.coordinates.conversion.PatternCoordinateConverter;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.pen.PenSample;
import edu.stanford.hci.r3.pen.streaming.listeners.PenListener;
import edu.stanford.hci.r3.units.coordinates.PercentageCoordinates;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * When you ask the PaperToolkit to run a paper Application, there will be exactly one EventEngine
 * handling all pen events for that Application. This EventEngine will process batched pen data, and
 * also handle streaming data. We will tackle streaming first.
 * </p>
 * <p>
 * 
 * </p>
 * <p>
 * This class is responsible for creating clicks, drags, etc.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class EventEngine {

	/**
	 * Send all unmapped events here...
	 */
	private List<EventHandler> catchAllHandlers = new ArrayList<EventHandler>();

	/**
	 * Set when handling regular samples, so that we can set the location of the pen up.
	 */
	private PercentageCoordinates lastKnownLocation;

	/**
	 * Used by penUp to notify event handlers. This is because a pen up event has no coordinates, so
	 * we cannot figure out what region it belongs to.
	 */
	private List<EventHandler> mostRecentEventHandlers = new ArrayList<EventHandler>();

	/**
	 * We keep a count of how many events we have "trashed." These events could not be mapped to any
	 * active region.
	 */
	private int numTrashedPenEvents = 0;

	/**
	 * Lets us figure out which sheets and regions should handle which events. Interacting with this
	 * list should be as efficient as possible, because many "events" may be thrown per second!
	 */
	private List<PatternLocationToSheetLocationMapping> patternToSheetMaps = Collections
			.synchronizedList(new ArrayList<PatternLocationToSheetLocationMapping>());

	/**
	 * Keeps track of how many times a pen has been registered. If during an unregister, this count
	 * drops to zero, we remove the pen altogether.
	 */
	private Map<Pen, Integer> penRegistrationCount = new HashMap<Pen, Integer>();

	/**
	 * Allows us to identify a pen by ID (the position of the pen in this list).
	 */
	private List<Pen> pensCurrentlyMonitoring = new ArrayList<Pen>();

	/**
	 * Each pen gets one and only one event engine listener...
	 */
	private Map<Pen, PenListener> penToListener = new HashMap<Pen, PenListener>();

	/**
	 * For saving and replaying sets of PenEvents.
	 */
	private EventReplayManager replayManager;

	/**
	 * This object handles event dispatch by hooking up pen listeners to local and remote pen
	 * servers. It will figure out where to dispatch incoming pen samples... and will activate the
	 * correct event handlers.
	 */
	public EventEngine() {
		replayManager = new EventReplayManager(this);
	}

	/**
	 * This can detect and process events when it's outside of any other region...
	 * 
	 * @param handler
	 */
	public void addEventHandlerForUnmappedEvents(EventHandler handler) {
		catchAllHandlers.add(handler);
	}

	/**
	 * @param pen
	 * @param listener
	 */
	private void addPenToInternalLists(Pen pen, PenListener listener) {
		penToListener.put(pen, listener);
		pen.addLivePenListener(listener);
	}

	/**
	 * Creates a new PenEvent from the Pen Name and Identifier.
	 * 
	 * @param sample
	 * @return
	 */
	private PenEvent createPenEvent(String penName, int penID, PenSample sample) {
		// make an event object so that someone can send it to the right event handler
		final PenEvent event = new PenEvent(penID, penName, System.currentTimeMillis(), sample);
		return event;
	}

	/**
	 * @param pen
	 * @return the registration count AFTER the decrement.
	 */
	private int decrementPenRegistrationCount(Pen pen) {
		Integer count = penRegistrationCount.get(pen);
		if (count == null) {
			// huh? We don't have a record for this pen...
			DebugUtils.println("We do not have a record for this pen, and "
					+ "cannot decrement the registration count.");
			return 0;
		} else if (count == 1) {
			penRegistrationCount.remove(pen); // decrement from one to zero
			return 0;
		} else {
			penRegistrationCount.put(pen, count - 1);
			return count - 1;
		}
	}

	/**
	 * @return the replay manager, allowing access to saved event streams.
	 */
	public EventReplayManager getEventReplayManager() {
		return replayManager;
	}

	/**
	 * @param pen
	 * @return a pen listener that will report data to this event engine. The engine will then
	 *         package the data and report it to all event handlers (read: interactors) that are
	 *         interested in this data.
	 */
	private PenListener getNewPenListener(final Pen pen) {
		pensCurrentlyMonitoring.add(pen);

		// properties of the pen
		final int penID = pensCurrentlyMonitoring.indexOf(pen);
		final String penName = pen.getName();

		return new PenListener() {

			/**
			 * @see edu.stanford.hci.r3.pen.streaming.listeners.PenListener#penDown(edu.stanford.hci.r3.pen.PenSample)
			 */
			public void penDown(PenSample sample) {
				final PenEvent event = createPenEvent(penName, penID, sample);
				event.setModifier(PenEventModifier.DOWN);

				// a pendown generated through a real pen listener should be saved
				// so that future sessions can replay the stream of events
				replayManager.saveEvent(event);
				handlePenEvent(event);
			}

			/**
			 * A penup sample has 0,0 coordinates, so we need to tell the LAST region handlers to
			 * handle the penUp.
			 * 
			 * @see edu.stanford.hci.r3.pen.streaming.listeners.PenListener#penUp(edu.stanford.hci.r3.pen.PenSample)
			 */
			public void penUp(PenSample sample) {
				final PenEvent event = createPenEvent(penName, penID, sample);
				event.setModifier(PenEventModifier.UP);

				// save the pen up also!
				// do this before setting the location
				// the location will be determined later, when the event is resent
				replayManager.saveEvent(event);
				handlePenUpEvent(event);
			}

			/**
			 * @see edu.stanford.hci.r3.pen.streaming.listeners.PenListener#sample(edu.stanford.hci.r3.pen.PenSample)
			 */
			public void sample(PenSample sample) {
				final PenEvent event = createPenEvent(penName, penID, sample);
				replayManager.saveEvent(event);
				handlePenEvent(event);
			}
		};
	}

	/**
	 * All pen events go through here. We dispatch it to the right handlers in this method. Will
	 * this have a ConcurrentModification problem, because we are iterating through the actual
	 * patternToSheetMaps list that can be updated at runtime?
	 * 
	 * <p>
	 * TODO: Should this be multithreaded, for performance reasons?
	 * <p>
	 * TODO: Should this have synchronized access to patternToSheetMaps, for race conditions?
	 * 
	 * @param penEvent
	 */
	public void handlePenEvent(PenEvent penEvent) {
		// System.out.println("Dispatching Event for pen #" + penID + " " + sample);
		mostRecentEventHandlers.clear();

		synchronized (patternToSheetMaps) {

			boolean eventHandledAtLeastOnce = false;

			// for each sample, we first have to convert it to a location on the sheet.
			// THEN, we will be able to make more interesting events...
			for (final PatternLocationToSheetLocationMapping pmap : patternToSheetMaps) {

				// this is a key step!
				// the event engine figures out which pattern region contains
				// this sample. This determines the set of event handlers the event
				// should be sent to
				final PatternCoordinateConverter coordinateConverter = pmap
						.getCoordinateConverterForSample(penEvent.getOriginalSample());

				// this map doesn't know about this sample; next!
				if (coordinateConverter == null) {
					continue;
				}

				// which sheet are we on?
				final Sheet sheet = pmap.getSheet();

				// which region are we on?
				final String regionName = coordinateConverter.getRegionName();
				final Region region = sheet.getRegion(regionName);

				// where are we on this region?
				final PercentageCoordinates relativeLocation = coordinateConverter
						.getRelativeLocation(penEvent.getStreamedPatternCoordinate());
				penEvent.setPercentageLocation(relativeLocation);

				lastKnownLocation = relativeLocation;

				// does this region have any event handler?
				// if not, just go onto the next region
				final List<EventHandler> eventHandlers = region.getEventHandlers();
				// send the event to every event handler!
				// so long as the event is not consumed
				for (EventHandler eh : eventHandlers) {
					eh.handleEvent(penEvent);
					eventHandledAtLeastOnce = true;
					mostRecentEventHandlers.add(eh);
					if (penEvent.isConsumed()) {
						// we are done handling this event
						// look at no more event handlers
						// look at no more pattern maps
						// DebugUtils.println("Event Consumed");
						return;
					}
				} // check the next event handler

			} // check the next pattern map

			if (!eventHandledAtLeastOnce) {

				// send the event to our "catch-all" event handlers...
				for (EventHandler eh : catchAllHandlers) {
					eventHandledAtLeastOnce = true;
					eh.handleEvent(penEvent);
					mostRecentEventHandlers.add(eh);
				} // check the next event handler

				if (!eventHandledAtLeastOnce) {
					// if still, no one has had a chance to deal with this event
					// it becomes TRASH!

					// there is no pattern map that matches the incoming pen event!
					// we dump it in a trash bin, and notify the user of this situation
					// perhaps the developer would like to register a mapping at run time?
					if (numTrashedPenEvents % 29 == 0) {
						// print out this warning once in a while...
						System.err
								.println("The event engine cannot map these pen events to any active region. "
										+ "We'll trash these events for now, but perhaps you should investigate "
										+ "attaching a pattern mapping (even at runtime) to your regions.");
					}
					numTrashedPenEvents++;
					DebugUtils.println("Cannot Map " + penEvent);
				}
			}
		}
	}

	/**
	 * Send the penUp to the event handlers...
	 * 
	 * @param event
	 */
	public void handlePenUpEvent(PenEvent event) {
		event.setPercentageLocation(lastKnownLocation);
		for (EventHandler h : mostRecentEventHandlers) {
			h.handleEvent(event);
		}
	}

	/**
	 * @param pen
	 */
	private void incrementPenRegistrationCount(Pen pen) {
		Integer count = penRegistrationCount.get(pen);
		if (count == null) {
			penRegistrationCount.put(pen, 1); // incremented from zero to one
		} else {
			penRegistrationCount.put(pen, count + 1);
		}
		DebugUtils.println("We have registered " + penRegistrationCount.get(pen)
				+ " pens in total.");
	}

	/**
	 * If you register a pen multiple times, a different pen listener will be attached to the pen.
	 * Only ONE EventEngine listener will be attached to a pen at one time. Otherwise, multiple
	 * events would get fired by the same pen.
	 * 
	 * @param pen
	 */
	public void register(Pen pen) {
		// get the old listener, if it exists
		PenListener listener = penToListener.get(pen);
		if (listener != null) {
			removePenFromInternalLists(pen, listener);
		}
		// this pen has never been registered, or
		// we just removed the old listener...

		// add a new listener
		listener = getNewPenListener(pen);
		addPenToInternalLists(pen, listener);
		incrementPenRegistrationCount(pen);
	}

	/**
	 * Really, this is the only method we need to do runtime binding. Who cares if the sheet doesn't
	 * have the right mapping, anyways?
	 * 
	 * @param mapping
	 */
	public void registerPatternMapForEventHandling(PatternLocationToSheetLocationMapping mapping) {
		DebugUtils.println("Registering A Pattern Location to Sheet Location Map");
		if (patternToSheetMaps.contains(mapping)) {
			DebugUtils.println("EventEngine is already aware of this pattern map.");
		}
		patternToSheetMaps.add(mapping);
	}

	/**
	 * Keep track of the pattern on sheets, so we can dispatch events appropriately.
	 * 
	 * @param patternMaps
	 */
	public void registerPatternMapsForEventHandling(
			Collection<PatternLocationToSheetLocationMapping> patternMaps) {
		DebugUtils.println("Registering the (Pattern Location --> Sheet Location) Maps ["
				+ patternMaps + "]");
		patternToSheetMaps.addAll(patternMaps);
		DebugUtils.println("Registered " + patternMaps.size() + " New Maps");
	}

	/**
	 * @param strokeHandler
	 */
	public void removeEventHandlerForUnmappedEvents(EventHandler handler) {
		catchAllHandlers.remove(handler);
	}

	/**
	 * @param pen
	 *            removes this pen from our internal lists without updating the registration count.
	 * @param listener
	 */
	private void removePenFromInternalLists(Pen pen, PenListener listener) {
		penToListener.remove(pen);
		pen.removeLivePenListener(listener);
		pensCurrentlyMonitoring.remove(pen);
	}

	/**
	 * To reset the event engine at runtime.
	 */
	public void unregisterAllPatternMaps() {
		patternToSheetMaps.clear();
	}

	/**
	 * @param patternMap
	 */
	public void unregisterPatternMapForEventHandling(
			PatternLocationToSheetLocationMapping patternMap) {
		patternToSheetMaps.remove(patternMap);
	}

	/**
	 * Remove these maps from our runtime list.
	 * 
	 * @param patternMaps
	 */
	public void unregisterPatternMapsForEventHandling(
			Collection<PatternLocationToSheetLocationMapping> patternMaps) {
		patternToSheetMaps.removeAll(patternMaps);
	}

	/**
	 * @param pen
	 */
	public void unregisterPen(Pen pen) {
		int newCount = decrementPenRegistrationCount(pen);
		if (newCount == 0) {
			DebugUtils.println("Count is at Zero. Let's remove the pen and its listener...");
			PenListener listener = penToListener.get(pen);
			removePenFromInternalLists(pen, listener);
		}
	}
}
