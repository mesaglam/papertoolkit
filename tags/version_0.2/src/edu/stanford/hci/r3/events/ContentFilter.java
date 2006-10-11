package edu.stanford.hci.r3.events;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.paper.Region;

/**
 * <p>
 * Filters are both similar to and different from EventHandlers. They are similar in the sense that
 * they can be added to regions. When ink is written on a region, the data will be sent to a filter
 * that is attached to that region.
 * </p>
 * <p>
 * Filters are different that event handlers because they transform incoming pen data into something
 * qualitatively different, instead of just telling a piece of code that an event has arrived. For
 * example, one could implement a filter that inverts pen samples' y value on a page (dunno why, but
 * it's a fun example). Then, you can add an event handler to this filter. The event handler would
 * then get PenEvents with upside down pen samples.
 * </p>
 * <p>
 * A more intricate filter would be an ink container. It can collect all ink that passes through the
 * filter, and tell a handler that new ink has arrived. Whenever the handler needs the ink, it can
 * request it from the ink container. Because of the custom nature of filters, it might be that each
 * filter has a custom event handler. For example, an InkContainer might have an InkContainer
 * listener. We can implement these as inner classes.
 * </p>
 * <p>
 * Filters can change the incoming event stream. For example, it might only care about events that
 * affect certain parts of the region. Events that are not close to these parts will be ignored.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public abstract class ContentFilter {

	/**
	 * Events are sent to this filter only if it is active.
	 */
	private boolean active = false;

	/**
	 * The event listeners attached to this filter.
	 */
	protected List<ContentFilterListener> filterListeners = new ArrayList<ContentFilterListener>();

	/**
	 * 
	 */
	private Region region;

	/**
	 * 
	 */
	public ContentFilter() {

	}

	/**
	 * @param handler
	 */
	public void addListener(ContentFilterListener listener) {
		filterListeners.add(listener);
		active = true;
	}

	/**
	 * Consuming this event has no effect. Additionally, filters are handled AFTER regular event
	 * handling. Thus, consuming this event will not do anything, except impact the event handlers
	 * that are attached to this filter.
	 */
	public abstract void filterEvent(PenEvent event);

	/**
	 * @return
	 * 
	 */
	public Region getRegion() {
		return region;
	}

	/**
	 * @return
	 */
	public boolean isActive() {
		return active;
	}


	/**
	 * 
	 */
	protected void notifyAllListenersOfNewContent() {
		for (ContentFilterListener listener : filterListeners) {
			listener.contentArrived();
		}
	}

	/**
	 * @param r
	 *           the region to monitor
	 */
	public void setRegion(Region r) {
		region = r;
	}

	/**
	 * @return the Filter's Name
	 */
	public abstract String toString();
}
