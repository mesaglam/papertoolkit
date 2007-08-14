package papertoolkit.events;

/**
 * <p>
 * Describes pen events...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public enum PenEventModifier {
	// a pen down event
	DOWN,

	// a regular tracking sample event
	SAMPLE,

	// a pen up event
	UP
}
