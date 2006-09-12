package edu.stanford.hci.r3.events;

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
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Filter {

}
