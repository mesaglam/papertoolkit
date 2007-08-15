package papertoolkit.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.pattern.coordinates.PatternToSheetMapping;
import papertoolkit.pattern.coordinates.conversion.PatternCoordinateConverter;
import papertoolkit.pen.PenInput;
import papertoolkit.pen.PenSample;
import papertoolkit.pen.streaming.listeners.PenListener;
import papertoolkit.units.Size;
import papertoolkit.units.coordinates.PercentageCoordinates;
import papertoolkit.util.DebugUtils;

/**
 * <p>
 * When you ask the PaperToolkit to run a paper Application, there will be exactly one EventDispatcher
 * handling all pen events for that Application. This EventDispatcher will process batched pen data, and also
 * handle streaming data. We will tackle streaming first.
 * </p>
 * <p>
 * This class is responsible for sending data to the event handlers, which will create clicks, drags, etc.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class EventDispatcher {

	/**
	 * Send all unmapped events here...
	 */
	private List<EventHandler> catchAllHandlers = new ArrayList<EventHandler>();

	/**
	 * Set when handling regular samples, so that we can set the location of the pen up.
	 */
	private PercentageCoordinates lastKnownLocation = new PercentageCoordinates(0, 0, new Size());

	/**
	 * Used by penUp to notify event handlers. This is because a pen up event has no coordinates, so we cannot
	 * figure out what region it belongs to.
	 */
	private List<EventHandler> mostRecentEventHandlers = new ArrayList<EventHandler>();

	/**
	 * Lets us figure out which sheets and regions should handle which events. Interacting with this list
	 * should be as efficient as possible, because many "events" may be thrown per second!
	 */
	private List<PatternToSheetMapping> patternToSheetMaps = Collections
			.synchronizedList(new ArrayList<PatternToSheetMapping>());

	/**
	 * Keeps track of how many times a pen has been registered. If during an unregister, this count drops to
	 * zero, we remove the pen altogether.
	 */
	private Map<PenInput, Integer> penRegistrationCount = new HashMap<PenInput, Integer>();

	/**
	 * Allows us to identify a pen by ID (the position of the pen in this list).
	 */
	private List<PenInput> pensCurrentlyMonitoring = new ArrayList<PenInput>();

	/**
	 * Each pen gets one and only one listener for the EventDispatcher...
	 */
	private Map<PenInput, PenListener> penToListener = new HashMap<PenInput, PenListener>();

	/**
	 * This object handles event dispatch by hooking up pen listeners to local and remote pen servers. It will
	 * figure out where to dispatch incoming pen samples... and will activate the correct event handlers.
	 */
	public EventDispatcher() {
		// nothing
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
	private void addPenToInternalLists(PenInput pen, PenListener listener) {
		penToListener.put(pen, listener);
		pen.addLivePenListener(listener);
	}

	/**
	 * Creates a new PenEvent from the Pen Name and Identifier.
	 * 
	 * TODO: Should the time be gotten from the sample instead?
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
	private int decrementPenRegistrationCount(PenInput pen) {
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
	 * @param penInputDevice
	 * @return a pen listener that will report data to this event engine. The engine will then package the
	 *         data and report it to all event handlers (read: interactors) that are interested in this data.
	 */
	private PenListener getNewPenListener(final PenInput penInputDevice) {
		pensCurrentlyMonitoring.add(penInputDevice);

		// properties of the pen
		final int penID = pensCurrentlyMonitoring.indexOf(penInputDevice);
		final String penName = penInputDevice.getName();

		return new PenListener() {

			/**
			 * @see papertoolkit.pen.streaming.listeners.PenListener#penDown(papertoolkit.pen.PenSample)
			 */
			public void penDown(PenSample sample) {
				final PenEvent event = createPenEvent(penName, penID, sample);
				event.setModifier(PenEventModifier.DOWN);

				// a pendown generated through a real pen listener should be saved
				// so that future sessions can replay the stream of events
				handlePenEvent(event);
			}

			/**
			 * A penup sample has 0,0 coordinates, so we need to tell the LAST region handlers to handle the
			 * penUp.
			 * 
			 * @see papertoolkit.pen.streaming.listeners.PenListener#penUp(papertoolkit.pen.PenSample)
			 */
			public void penUp(PenSample sample) {
				final PenEvent event = createPenEvent(penName, penID, sample);
				event.setModifier(PenEventModifier.UP);

				// save the pen up also!
				// do this before setting the location
				// the location will be determined later, when the event is resent
				handlePenUpEvent(event);
			}

			/**
			 * @see papertoolkit.pen.streaming.listeners.PenListener#sample(papertoolkit.pen.PenSample)
			 */
			public void sample(PenSample sample) {
				final PenEvent event = createPenEvent(penName, penID, sample);
				handlePenEvent(event);
			}
		};
	}

	/**
	 * All pen events go through here. We dispatch it to the right handlers in this method. Will this have a
	 * ConcurrentModification problem, because we are iterating through the actual patternToSheetMaps list
	 * that can be updated at runtime?
	 * 
	 * <p>
	 * TODO: Should this be multithreaded, for performance reasons?
	 * </p>
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
			for (final PatternToSheetMapping pmap : patternToSheetMaps) {

				// this is a key step!
				// the event engine figures out which patterned regions contains
				// this sample. This determines the set of event handlers the event
				// should be sent to...
				List<PatternCoordinateConverter> coordinateConvertersForSample = pmap
						.getCoordinateConvertersForSample(penEvent.getOriginalSample());

				for (final PatternCoordinateConverter coordinateConverter : coordinateConvertersForSample) {

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

					// does this region have any event handlers?
					// if not, just go onto the next region
					final List<EventHandler> eventHandlers = region.getEventHandlers();
					// send the event to every event handler!
					// so long as the event is not consumed
					for (EventHandler eh : eventHandlers) {
						eventHandledAtLeastOnce = true;
						eh.handleEvent(penEvent);
						mostRecentEventHandlers.add(eh);
						if (penEvent.isConsumed()) {
							// we are done handling this event
							// look at no more event handlers
							// look at no more pattern maps
							// DebugUtils.println("Event Consumed");
							return;
						}
					} // check the next event handler
				} // check the next coordinate converter / matching region
			} // check the next pattern map

			// if none of the handlers own this event, we send the event to our "catch-all" event handlers...
			if (!eventHandledAtLeastOnce) {
				for (EventHandler eh : catchAllHandlers) {
					eventHandledAtLeastOnce = true;
					eh.handleEvent(penEvent);
					mostRecentEventHandlers.add(eh);
				} // check the next event handler
			}

			if (!eventHandledAtLeastOnce) {
				// if in the end, no one has had a chance to deal with this event yet
				// we sent this event to ALL the regions we know about!
				// 
				// before, we would just trash it...
				// however, this new approach allows us to avoid common errors
				// and allow us to create paper applications as fast as possible
				for (final PatternToSheetMapping pmap : patternToSheetMaps) {
					final List<Region> regs = pmap.getSheet().getRegions();
					for (Region r : regs) {
						// does this region have any event handlers?
						// if not, just go onto the next region
						final List<EventHandler> eventHandlers = r.getEventHandlers();
						// send the event to every event handler!
						// so long as the event is not consumed
						for (EventHandler eh : eventHandlers) {
							eventHandledAtLeastOnce = true;
							eh.handleEvent(penEvent);
							mostRecentEventHandlers.add(eh);
							if (penEvent.isConsumed()) {
								// we are done handling this event
								// look at no more event handlers
								// look at no more pattern maps
								// DebugUtils.println("Event Consumed");
								return;
							}
						} // check the next event handler

					}
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
	 * 
	 * @param pen
	 *            the input device that provides pen-like data.
	 */
	private void incrementPenRegistrationCount(PenInput pen) {
		final Integer count = penRegistrationCount.get(pen);
		if (count == null) {
			penRegistrationCount.put(pen, 1); // incremented from zero to one
		} else {
			penRegistrationCount.put(pen, count + 1);
		}
		// DebugUtils.println("We have registered " + penRegistrationCount.get(pen) + " pens in total.");
	}

	/**
	 * If you register a pen multiple times, a different pen listener will be attached to the pen. Only ONE
	 * EventEngine listener will be attached to a pen at one time. Otherwise, multiple events would get fired
	 * by the same pen.
	 * 
	 * Why would you want to register a single pen multiple times? I dunnno. For some reason I added support
	 * for it... but perhaps I'll remove it in the future. :)
	 * 
	 * @param pen
	 */
	public void register(PenInput pen) {
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
	 * Really, this is the only method we need to do runtime binding. Who cares if the sheet doesn't have the
	 * right mapping, anyways?
	 * 
	 * @param mapping
	 */
	public void registerPatternMapForEventHandling(PatternToSheetMapping mapping) {
		// DebugUtils.println("Registering A Pattern Location to Sheet Location Map");
		if (patternToSheetMaps.contains(mapping)) {
			// DebugUtils.println("EventEngine is already aware of this pattern map.");
			return;
		}
		patternToSheetMaps.add(mapping);
	}

	/**
	 * Keep track of the pattern on sheets, so we can dispatch events appropriately.
	 * 
	 * @param patternMaps
	 */
	public void registerPatternMapsForEventHandling(Collection<PatternToSheetMapping> patternMaps) {
		// DebugUtils.println("Registering the (Pattern Location --> Sheet Location) Maps " + "[" +
		// patternMaps + "]");
		patternToSheetMaps.addAll(patternMaps);
		// DebugUtils.println("Registered " + patternMaps.size() + " New Maps");
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
	private void removePenFromInternalLists(PenInput pen, PenListener listener) {
		penToListener.remove(pen);
		pen.removeLivePenListener(listener);
		pensCurrentlyMonitoring.remove(pen);
	}

	/**
	 * To reset the event engine/dispatcher at runtime.
	 */
	public void unregisterAllPatternMaps() {
		patternToSheetMaps.clear();
	}

	/**
	 * @param patternMap
	 *            forget about this pattern map for this session...
	 */
	public void unregisterPatternMapForEventHandling(PatternToSheetMapping patternMap) {
		patternToSheetMaps.remove(patternMap);
	}

	/**
	 * Remove these maps from our runtime list.
	 * 
	 * @param patternMaps
	 */
	public void unregisterPatternMapsForEventHandling(Collection<PatternToSheetMapping> patternMaps) {
		patternToSheetMaps.removeAll(patternMaps);
	}

	/**
	 * Stop watching this pen input device.
	 * 
	 * @param pen
	 */
	public void unregisterPen(PenInput pen) {
		int newCount = decrementPenRegistrationCount(pen);
		if (newCount == 0) {
			// DebugUtils.println("Count is at Zero. Let's remove the pen and its listener...");
			PenListener listener = penToListener.get(pen);
			removePenFromInternalLists(pen, listener);
		}
	}
}
