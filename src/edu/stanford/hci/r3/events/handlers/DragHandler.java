package edu.stanford.hci.r3.events.handlers;

import edu.stanford.hci.r3.events.EventHandler;

/**
 * <p>
 * This event handler can detect the starting and ending locations of a drag operation (pen down,
 * pen move, pen up)... Drags are single-stroke operations. You can specify what happens due to this
 * drag, and can access the source and destination regions... Ideally, you can drag across
 * non-patterned paper, as long as the source and dest are both patterened regions.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public abstract class DragHandler extends EventHandler {

	
	// TODO
	
}
