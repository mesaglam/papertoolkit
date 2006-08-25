package edu.stanford.hci.r3.events;

import java.net.MalformedURLException;
import java.net.URL;

import edu.stanford.hci.r3.actions.RetrieveURLAction;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.pen.streaming.PenListener;
import edu.stanford.hci.r3.pen.streaming.PenSample;
import edu.stanford.hci.r3.util.DebugUtils;

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
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class EventEngine {

	public EventEngine() {

	}

	public void register(Pen pen) {
		System.out.println("EventEngine: Registering Pen.");
		pen.addLivePenListener(getNewPenListener());
	}

	private PenListener getNewPenListener() {
		return new PenListener() {

			public void penDown(PenSample sample) {
				// TODO Auto-generated method stub

			}

			public void penUp(PenSample sample) {
				try {
					DebugUtils.println("Pen Up");
					URL url = new URL("http://www.flickr.com/");
					RetrieveURLAction action = new RetrieveURLAction(url);
					DebugUtils.println("Action created.");
					action.invoke();
					DebugUtils.println("Action Invoked.");
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}

			public void sample(PenSample sample) {
				System.out.println(sample);
			}
		};
	}
}
