package papertoolkit.events.handlers;

/**
 * <p>
 * Events are dispatched normally during batched event handling. However, at the end of the event dispatch, we
 * dispatch one last event, a PenSynch event... to the PenSynchHandler, which notifies any region of all the
 * Ink it has received. This is useful if you are only interested in the ink that is written on that page, and
 * do not care about receiving stroke-level events.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PenSynchHandler {

	
	// TODO
}
