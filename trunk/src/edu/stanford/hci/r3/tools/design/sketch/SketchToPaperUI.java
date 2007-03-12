package edu.stanford.hci.r3.tools.design.sketch;

import java.io.File;
import java.util.List;

import edu.stanford.hci.r3.pen.batch.PenSynch;
import edu.stanford.hci.r3.pen.ink.Ink;
import edu.stanford.hci.r3.pen.ink.InkStroke;
import edu.stanford.hci.r3.pen.ink.InkUtils;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.files.FileUtils;

/**
 * <p>
 * Interprets a set of pen strokes, and outputs a paper UI xml file.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class SketchToPaperUI {

	/**
	 * Loads the most recent pen XML and generates a paper UI XML file.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		String fileName = "penSynch/data/XML/2007_03_10__01_09_38_SketchedPaperUI.xml";

		PenSynch penSynch = new PenSynch(new File(fileName));
		List<Ink> importedInk = penSynch.getImportedInk();

		// This becomes the sheet
		InkStroke biggestStroke = InkUtils.findStrokeWithLargestArea(importedInk);


		// Find other strokes within the sheet stroke...
		// These become the regions
		List<InkStroke> insideStrokes = InkUtils.findAllStrokesContainedWithin(importedInk,
				biggestStroke);
		highlight(insideStrokes);

		// Debug by printing it to the console..., but this console debugging is hard!
		for (InkStroke insideStroke : insideStrokes) {
			DebugUtils.println(insideStroke);
		}
		// OR, use PaperToolkit.showMe(insideStroke); // this colors the stroke in an external
		// view...
		// it renders an XML file of the INK with some cool colors. =)
		// then it opens the browser to the right HTML page with the right query string. =)

		// Find strokes that go from within the sheet to outside the sheet
		// These are event handlers

		// Find the next near the endpoints of these event handlers.
		// Do handwriting recognition on them.

		// Create Event Handlers!

		// Write about this!!! =)

		// Part II...
		// A second approach is more of a connected components approach?
		// Take a look at Andy Wilson's?
		// Close off strokes, and find buttons and such...
	}

	private static void highlight(List<InkStroke> strokes) {

		StringBuilder sb = new StringBuilder();
		sb.append("<highlight>");
		for (InkStroke s : strokes) {
			sb.append("<stroke begin=\"" + s.getFirstTimestamp() + "\"/>\n");
		}
		sb.append("</highlight>");
		FileUtils
				.writeStringToFile(sb.toString(), new File("flash/data/highlightTheseStrokes.xml"));
	}
}
