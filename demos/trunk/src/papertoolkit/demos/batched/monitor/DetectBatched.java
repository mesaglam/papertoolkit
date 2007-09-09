package papertoolkit.demos.batched.monitor;

import java.io.File;

import papertoolkit.PaperToolkit;
import papertoolkit.application.Application;
import papertoolkit.application.config.StartupOptions;
import papertoolkit.events.PenEvent;
import papertoolkit.events.handlers.InkHandler;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.pen.ink.Ink;
import papertoolkit.pen.ink.InkStroke;
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
		StartupOptions options = new StartupOptions();
		PaperToolkit paperToolkit = new PaperToolkit(options);
		paperToolkit.startApplication(batched);
	}

	private InkHandler inkHandler;

	/**
	 * Run this application first, then write on your notepad and plug it in...
	 */
	public DetectBatched() {
		super("Hello World for Batch Processing of Ink");
		Sheet sheet = createSheet();
		Region region = sheet.createRegion();
		region.addEventHandler(getInkHandler());
	}

	/**
	 * If you set PixelsPerInch to be higher, you may need to increase the Java Heap space.
	 * 
	 * @return
	 */
	private InkHandler getInkHandler() {
		if (inkHandler == null) {
			inkHandler = new InkHandler() {
				// Render Multiple Times... on each stroke
				// This is probably inefficient. We need a better way to detect when the batched input is
				// "done."
				public void handleInkStroke(PenEvent event, InkStroke mostRecentStroke) {
					Ink inkOnThisPage = getInk();
					DebugUtils.println("Ink Arrived: " + inkOnThisPage.getName());
					new InkRenderer(inkOnThisPage).renderToJPEG(new File("data/Batched/"
							+ inkOnThisPage.getName() + ".jpeg"), //
							new PixelsPerInch(80), new Inches(5.375), new Inches(8));
				}
			};
		}
		return inkHandler;
	}
}