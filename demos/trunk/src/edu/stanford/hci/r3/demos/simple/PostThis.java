package edu.stanford.hci.r3.demos.simple;

import java.awt.Font;

import papertoolkit.PaperToolkit;
import papertoolkit.application.Application;
import papertoolkit.events.PenEvent;
import papertoolkit.events.handlers.ClickAdapter;
import papertoolkit.events.handlers.HandwritingHandler;
import papertoolkit.events.handlers.InkHandler;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.paper.regions.TextRegion;
import papertoolkit.pen.ink.Ink;
import papertoolkit.pen.ink.InkStroke;
import papertoolkit.units.Inches;
import papertoolkit.units.Units;
import papertoolkit.util.DebugUtils;


/**
 * <p>
 * A smart sticky-note app for the UIST figures and video.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class PostThis extends Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PaperToolkit.runApplication(new PostThis());
	}

	private HandwritingHandler handwritingRecognizer;
	private InkHandler inkWell;
	private Region inkingRegion;

	public PostThis() {
		super("PostThis");
		createPaperUI();
		addInputDevices();
		addOutputDevices();
	}

	/**
	 * @param inkingRegion
	 */
	private void addInkingHandler(Region inkingRegion) {
		inkWell = new InkHandler() {
			public void handleInkStroke(PenEvent event, InkStroke mostRecentStroke) {
			}
		};
		inkingRegion.addEventHandler(inkWell);
	}

	/**
	 * 
	 */
	private void addInputDevices() {

	}

	/**
	 * 
	 */
	private void addOutputDevices() {

	}

	/**
	 * @param tagRegion
	 */
	private void addTagInkHandler(Region tagRegion) {
		handwritingRecognizer = new HandwritingHandler() {
			@Override
			public void contentArrived() {
				
			}
		};
		tagRegion.addEventHandler(handwritingRecognizer);
	}

	/**
	 * @param uploadRegion
	 */
	private void addUploadHandler(final Region uploadRegion) {
		uploadRegion.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				String handwriting = handwritingRecognizer.recognizeHandwriting();
				DebugUtils.println("You tagged the ink with: [" + handwriting + "]");

				// get new ink from ink collector
				Ink ink = inkWell.getInk();
				if (ink.getNumStrokes() == 0) {
					DebugUtils.println("No Ink!");
				} else {
					// save the ink to a file
					DebugUtils.println("Rendering the Ink to file.");
					Units widthInches = inkingRegion.getWidth();
					Units heightInches = inkingRegion.getHeight();
					double widthDots = widthInches.getValueInPatternDots();
					double heightDots = heightInches.getValueInPatternDots();

					// ink.renderToJPEGFile(widthDots, heightDots);
					ink.renderToJPEGFile();
				}

				DebugUtils.println("Uploading ink to your web calendar!");
			}
		});
	}

	/**
	 * 
	 */
	private void createPaperUI() {
		Sheet s = new Sheet(8.5, 11);

		Region titleRegion = new TextRegion("Title", "PostThis!", new Font("Trebuchet MS",
				Font.PLAIN, 22), new Inches(1), new Inches(0.5));

		// Region inkingRegion = new Region("InkArea", 1, 1.25, 1, 1);
		// Region uploadRegion = new Region("Submit", 5.5, 8.25, 1, 1);
		// Region tagRegion = new Region("Tags", 1, 8.25, 1, 1);

		inkingRegion = new Region("InkArea", 1, 1.25, 6.5, 6.5);
		Region uploadRegion = new Region("Submit", 5.5, 8.25, 2, 2);
		Region tagRegion = new Region("Tags", 1, 8.25, 4, 2);

		addInkingHandler(inkingRegion);
		addUploadHandler(uploadRegion);
		addTagInkHandler(tagRegion);

		s.addRegion(titleRegion);
		s.addRegion(inkingRegion);
		s.addRegion(uploadRegion);
		s.addRegion(tagRegion);
		addSheet(s);
	}
}
