package papertoolkit.pen.streaming.listeners;

import java.util.ArrayList;
import java.util.List;

import papertoolkit.pen.PenSample;
import papertoolkit.pen.ink.InkStroke;
import papertoolkit.util.DebugUtils;


/**
 * <p>
 * This lives in the streaming.listeners package because you cannot use this listener directly if
 * your pattern spans multiple sheets. This listener stores information in raw Streaming PenSamples,
 * so you will have to do the calculations yourself... This class is definitely for experts only!
 * </p>
 * <p>
 * The class also doesn't do any filtering, so if the pen is bad, you will get lots of
 * PenDown/PenUps in rapid succession.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public abstract class PenStrokeListener implements PenListener {

	/**
	 * 
	 */
	private List<PenSample> currentStroke;

	/**
	 * @see papertoolkit.pen.streaming.listeners.PenListener#penDown(papertoolkit.pen.PenSample)
	 */
	public void penDown(PenSample sample) {
		// start a new stroke, which is just a list of PenSamples
		currentStroke = new ArrayList<PenSample>();
		currentStroke.add(sample);
		// DebugUtils.println(sample);
	}

	/**
	 * Notify that a new penstroke has occured.
	 * 
	 * @param stroke
	 */
	public abstract void penStroke(InkStroke stroke);

	/**	
	 * @see papertoolkit.pen.streaming.listeners.PenListener#penUp(papertoolkit.pen.PenSample)
	 */
	public void penUp(PenSample sample) {
		if (currentStroke == null) {
			return;
		}
		
		// end the stroke... Since the up sample is the same as the last sample, do not add it
		final List<PenSample> stroke = currentStroke;

		// this line will be useful if we decide to spawn a new thread to notify the
		// PenStrokeListener
		currentStroke = null;

		// notify the PenStrokeListener
		penStroke(new InkStroke(stroke));
	}

	/**
	 * @see papertoolkit.pen.streaming.listeners.PenListener#sample(papertoolkit.pen.PenSample)
	 */
	public void sample(PenSample sample) {
		currentStroke.add(sample);
		// DebugUtils.println(sample);
	}
}
