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
import papertoolkit.pen.InputDevice;
import papertoolkit.pen.PenSample;
import papertoolkit.pen.streaming.listeners.PenListener;
import papertoolkit.tools.services.ToolkitMonitor;
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
	 * Allows us to identify a pen by ID (the position of the pen in this list).
	 */
	private List<InputDevice> pensCurrentlyMonitoring = new ArrayList<InputDevice>();

	/**
	 * Each pen gets one and only one listener for the EventDispatcher...
	 */
	private Map<InputDevice, PenListener> penToListener = new HashMap<InputDevice, PenListener>();

	/**
	 * Broadcasts toolkit internals to external services.
	 */
	private ToolkitMonitor toolkitMonitor;

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
	private void addPenToInternalLists(InputDevice pen, PenListener listener) {
		penToListener.put(pen, listener);
		pen.addLivePenListener(listener);
	}

	/**
	 * @param penInputDevice
	 * @return a pen listener that will report data to this event dispatcher. The engine will then package the
	 *         data and report it to all event handlers that are interested in this data.
	 */
	private PenListener getNewPenListener(final InputDevice penInputDevice) {
		pensCurrentlyMonitoring.add(penInputDevice);

		// properties of the pen
		final int penID = penInputDevice.getID();
		final String penName = penInputDevice.getName();

		// TODO: Should the time be gotten from the sample instead? This may have impact if we are processing
		// Batched data...
		// See the three calls to System.currentTimeMillis() below...
		return new PenListener() {
			public void penDown(PenSample sample) {
				// DebugUtils.println("D " + sample);
				handlePenEvent(new PenEvent(penID, penName, System.currentTimeMillis(), sample,
						PenEventType.DOWN, true));
			}

			/**
			 * A penup sample has 0,0 coordinates, so we need to tell the LAST region handlers to handle the
			 * penUp.
			 * 
			 * @see papertoolkit.pen.streaming.listeners.PenListener#penUp(papertoolkit.pen.PenSample)
			 */
			public void penUp(PenSample sample) {
				// DebugUtils.println("U " + sample);
				handlePenEvent(new PenEvent(penID, penName, System.currentTimeMillis(), sample,
						PenEventType.UP, true));
			}

			public void sample(PenSample sample) {
				// DebugUtils.println("S " + sample);
				handlePenEvent(new PenEvent(penID, penName, System.currentTimeMillis(), sample,
						PenEventType.SAMPLE, true));
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

		// handle Pen UP events differently...
		// as pen up objects don't actually have a location
		if (penEvent.isTypePenUp()) {
			penEvent.setPercentageLocation(lastKnownLocation);
			for (EventHandler h : mostRecentEventHandlers) {
				monitoredHandleEvent(h, penEvent);
			}
			return; // done!
		}

		// handle Pen DOWN and Pen SAMPLE events here...

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
						monitoredHandleEvent(eh, penEvent);
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
					monitoredHandleEvent(eh, penEvent);
					mostRecentEventHandlers.add(eh);
				} // check the next event handler
			}

			// if in the end, no one has had a chance to deal with this event yet
			// we sent this event to ALL the regions we know about!
			// TODO: This doesn't really make sense... we should reevaluate it
			// remove it for now
			// eventHandledAtLeastOnce = sendEventToAllKnownRegions(penEvent, eventHandledAtLeastOnce);

			// if this application has no sheets or regions... it'll fall all the way to here
			if (!eventHandledAtLeastOnce) {
				DebugUtils.println("Event Not Mapped to any Regions: " + penEvent);
				monitoredHandleEvent(null, penEvent);
			}
		}
	}

	@SuppressWarnings("unused")
	private boolean sendEventToAllKnownRegions(PenEvent penEvent, boolean eventHandledAtLeastOnce) {
		if (!eventHandledAtLeastOnce) {
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
						monitoredHandleEvent(eh, penEvent);
						mostRecentEventHandlers.add(eh);
						if (penEvent.isConsumed()) {
							// we are done handling this event
							// look at no more event handlers
							// look at no more pattern maps
							// DebugUtils.println("Event Consumed");
							return eventHandledAtLeastOnce;
						}
					} // check the next event handler
				}
			}
		}
		return eventHandledAtLeastOnce;
	}

	/**
	 * @param handler
	 * @param event
	 */
	private void monitoredHandleEvent(EventHandler handler, PenEvent event) {
		if (handler != null) {
			handler.handleEvent(event);
		}
		if (toolkitMonitor != null) {
			toolkitMonitor.eventHandled(handler, event);
		}
	}

	/**
	 * You cannot register a pen multiple times with the same dispatcher. Otherwise, multiple events would get
	 * fired by the same pen.
	 * 
	 * @param pen
	 */
	public void register(InputDevice pen) {
		if (pensCurrentlyMonitoring.contains(pen)) {
			DebugUtils.println("Cannot register a pen with the EventDispatcher multiple times.");
			return;
		}

		// add a new listener
		addPenToInternalLists(pen, getNewPenListener(pen));
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
	private void removePenFromInternalLists(InputDevice pen, PenListener listener) {
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
	public void unregisterPen(InputDevice pen) {
		removePenFromInternalLists(pen, penToListener.get(pen));
	}

	/**
	 * @param monitor
	 */
	public void setMonitor(ToolkitMonitor monitor) {
		toolkitMonitor = monitor;
	}
}
