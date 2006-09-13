package edu.stanford.hci.r3.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.pattern.coordinates.PatternLocationToSheetLocationMapping;
import edu.stanford.hci.r3.pattern.coordinates.TiledPatternCoordinateConverter;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.pen.streaming.PenListener;
import edu.stanford.hci.r3.pen.streaming.PenSample;
import edu.stanford.hci.r3.units.coordinates.PercentageCoordinates;
import edu.stanford.hci.r3.units.coordinates.StreamedPatternCoordinates;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * When you ask the PaperToolkit to run a paper Application, there will be exactly one EventEngine
 * handling all pen events for that Application. This EventEngine will process batched pen data, and
 * also handle streaming data. We will tackle streaming first.
 * </p>
 * <p>
 * This class is responsible for creating clicks, drags, etc.
 * </p>
 * <p>
 * TODO: Test if multiple pens work!
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class EventEngine {

	private List<ContentFilter> lastEventFilters = new ArrayList<ContentFilter>();

	private List<EventHandler> lastEventHandlers = new ArrayList<EventHandler>();

	/**
	 * Lets us figure out which sheets and regions should handle which events. Interacting with this
	 * list should be as efficient as possible, because many "events" may be thrown per second!
	 */
	private List<PatternLocationToSheetLocationMapping> patternToSheetMaps = new ArrayList<PatternLocationToSheetLocationMapping>();

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
	 * Set when handling regular samples, so that we can set the location of the pen up.
	 */
	private PercentageCoordinates lastKnownLocation;

	/**
	 * 
	 */
	public EventEngine() {

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
	 * @param pen
	 * @return a pen listener that will report data to this event engine. The engine will then
	 *         package the data and report it to all event handlers (read: interactors) that are
	 *         interested in this data.
	 */
	private PenListener getNewPenListener(final Pen pen) {
		pensCurrentlyMonitoring.add(pen);
		final int penID = pensCurrentlyMonitoring.indexOf(pen);

		return new PenListener() {
			/**
			 * @param sample
			 * @return
			 */
			private PenEvent createPenEvent(PenSample sample) {
				// make an event object and send it to the event handler
				PenEvent event = new PenEvent(penID, System.currentTimeMillis());
				event.setOriginalSample(sample);
				return event;
			}

			/**
			 * @see edu.stanford.hci.r3.pen.streaming.PenListener#penDown(edu.stanford.hci.r3.pen.streaming.PenSample)
			 */
			public void penDown(PenSample sample) {
				PenEvent event = createPenEvent(sample);
				event.setModifier(PenEvent.PEN_DOWN_MODIFIER);
				handlePenSample(event);
			}

			/**
			 * A penup sample has 0,0 coordinates, so we need to tell the LAST region handlers to
			 * handle the penUp.
			 * 
			 * @see edu.stanford.hci.r3.pen.streaming.PenListener#penUp(edu.stanford.hci.r3.pen.streaming.PenSample)
			 */
			public void penUp(PenSample sample) {
				final PenEvent event = createPenEvent(sample);
				event.setModifier(PenEvent.PEN_UP_MODIFIER);
				event.setPercentageLocation(lastKnownLocation);
				
				for (EventHandler h : lastEventHandlers) {
					h.handleEvent(event);
				}
				for (ContentFilter f : lastEventFilters) {
					f.filterEvent(event);
				}
			}

			/**
			 * @see edu.stanford.hci.r3.pen.streaming.PenListener#sample(edu.stanford.hci.r3.pen.streaming.PenSample)
			 */
			public void sample(PenSample sample) {
				PenEvent event = createPenEvent(sample);
				handlePenSample(event);
			}
		};
	}

	/**
	 * All pen events go here. We dispatch it to the right handlers in this method.
	 * 
	 * @param penEvent
	 */
	private void handlePenSample(PenEvent penEvent) {
		// System.out.println("Dispatching Event for pen #" + penID + " " + sample);
		lastEventHandlers.clear();
		lastEventFilters.clear();

		// for each sample, we first have to convert it to a location on the sheet.
		// THEN, we will be able to make more interesting events...
		for (PatternLocationToSheetLocationMapping pmap : patternToSheetMaps) {
			final TiledPatternCoordinateConverter coordinateConverter = pmap
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
				lastEventHandlers.add(eh);
				if (penEvent.isConsumed()) {
					// we are done handling this event
					// look at no more event handlers
					// look at no more pattern maps
					// DebugUtils.println("Event Consumed");
					return;
				}
			} // check the next event handler

			// also, send this event to all the filters, if the event is not yet consumed by one of
			// the above handlers
			List<ContentFilter> eventFilters = region.getEventFilters();
			for (ContentFilter ef : eventFilters) {
				ef.filterEvent(penEvent);
				lastEventFilters.add(ef);
				// filters do not consume events
				// but they are lower priority than event handlers
				// should they be HIGHER priority???
			}

		} // check the next pattern map
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
		DebugUtils.println("Count is at " + penRegistrationCount.get(pen));
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
	 * Keep track of the pattern on sheets, so we can dispatch events appropriately.
	 * 
	 * @param patternMaps
	 */
	public void registerPatternMapsForEventHandling(
			Collection<PatternLocationToSheetLocationMapping> patternMaps) {
		for (PatternLocationToSheetLocationMapping map : patternMaps) {
			patternToSheetMaps.add(map);
		}
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
	 * @param patternMaps
	 */
	public void unregisterPatternMapsForEventHandling(
			Collection<PatternLocationToSheetLocationMapping> patternMaps) {
		for (PatternLocationToSheetLocationMapping map : patternMaps) {
			patternToSheetMaps.remove(map);
		}
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
