package edu.stanford.hci.r3.demos.batched.monitor;

import java.io.File;

import papertoolkit.PaperToolkit;
import papertoolkit.application.Application;
import papertoolkit.pen.ink.Ink;
import papertoolkit.pen.synch.BatchedEventHandler;
import papertoolkit.render.ink.InkRenderer;
import papertoolkit.units.Inches;
import papertoolkit.units.conversion.PixelsPerInch;
import papertoolkit.util.DebugUtils;


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
public class DetectBatched extends Application {

	public static void main(String[] args) {
		DetectBatched batched = new DetectBatched();
		PaperToolkit paperToolkit = new PaperToolkit(true /* use the app manager */);
		paperToolkit.startApplication(batched);
	}

	private BatchedEventHandler inkHandler;

	/**
	 * Run this application first, then write on your notepad and plug it in...
	 */
	public DetectBatched() {
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