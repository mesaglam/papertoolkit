package edu.stanford.hci.r3.demos.batched;

import java.io.File;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.application.Application;
import edu.stanford.hci.r3.pen.batch.BatchedEventHandler;
import edu.stanford.hci.r3.pen.ink.Ink;
import edu.stanford.hci.r3.render.ink.InkRenderer;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.units.Pixels;
import edu.stanford.hci.r3.units.conversion.PixelsPerInch;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * A Hello World Application to tell when the user synchronizes his/her pen.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class HelloBatched extends Application {

	public static void main(String[] args) {
		HelloBatched batched = new HelloBatched();
		PaperToolkit paperToolkit = new PaperToolkit(true /* use the app manager */);
		paperToolkit.startApplication(batched);
	}

	private BatchedEventHandler inkHandler;

	/**
	 * Run this application first, then write on your notepad and plug it in...
	 */
	public HelloBatched() {
		super("Hello World for Batch Processing of Ink");
		addBatchEventHandler(getInkHandler());
	}

	/**
	 * If you set PixelsPerInch to be higher, you may need to increase the Java Heap space.
	 * 
	 * @return
	 */
	private BatchedEventHandler getInkHandler() {
		if (inkHandler == null) {
			inkHandler = new BatchedEventHandler("Ink Handler") {
				@Override
				public void inkArrived(Ink inkOnThisPage) {
					DebugUtils.println("Ink Arrived" + inkOnThisPage.getName());
					new InkRenderer(inkOnThisPage).renderToJPEG(new File("data/Batched/"
							+ inkOnThisPage.getName() + ".jpeg"), //
							new PixelsPerInch(80), new Inches(5.375), new Inches(8));
				}
			};
		}
		return inkHandler;
	}
}