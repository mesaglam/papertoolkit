package edu.stanford.hci.r3.events;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.pen.streaming.PenListener;
import edu.stanford.hci.r3.pen.streaming.PenSample;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * When you ask the PaperToolkit to run a paper Application, there will be exactly one EventEngine
 * handling all pen events for that Application. This EventEngine will process batched pen data, and
 * also handle streaming data. We will tackle streaming first.
 * 
 * This class is responsible for creating clicks, drags, etc.
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class EventEngine {

	/**
	 * 
	 */
	private List<PenListener> listeners = new ArrayList<PenListener>();

	public EventEngine() {

	}

	/**
	 * @param pen
	 */
	public void register(Pen pen) {
		PenListener penListener = getNewPenListener();
		pen.addLivePenListener(penListener);
	}

	/**
	 * @return
	 */
	private PenListener getNewPenListener() {
		return new PenListener() {
			public void penDown(PenSample sample) {
				System.out.println(sample);
			}

			public void penUp(PenSample sample) {
				System.out.println(sample);
			}

			public void sample(PenSample sample) {
				System.out.println(sample);
			}
		};
	}
}
