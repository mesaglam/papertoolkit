package edu.stanford.hci.r3.tools.design.sketch;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.pen.PenSample;
import edu.stanford.hci.r3.pen.batch.PenSynch;
import edu.stanford.hci.r3.pen.handwriting.HandwritingRecognitionService;
import edu.stanford.hci.r3.pen.ink.Ink;
import edu.stanford.hci.r3.pen.ink.InkStroke;
import edu.stanford.hci.r3.pen.ink.InkUtils;
import edu.stanford.hci.r3.pen.streaming.listeners.PenListener;
import edu.stanford.hci.r3.tools.design.util.Regions;
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
 */
public class SketchToPaperUI {

	/**
	 * Loads the most recent pen XML and generates a paper UI XML file.
	 */
	private static void testTranslateXMLFile() {
		String fileName = // "penSynch/data/XML/2007_03_24__19_51_50_AJ3-AAA-ZU3-7X.xml";
		"penSynch/data/XML/2007_03_10__01_09_38_SketchedPaperUI.xml";
		new SketchToPaperUI(new Pen()).translate(new File(fileName), "SketchedPaperUI", new File("."));
	}

	private Pen pen;

	/**
	 * 
	 */
	public SketchToPaperUI(Pen p) {
		DebugUtils.println("New Sketch To Paper UI");
		pen = p;

		// set up the pen, and a pen listener...
		pen.startLiveMode();
		pen.addLivePenListener(new PenListener() {
			public void penDown(PenSample sample) {
			}

			public void penUp(PenSample sample) {
			}

			public void sample(PenSample sample) {
			}
		});
	}

	// private static void highlight(List<InkStroke> strokes) {
	//
	// StringBuilder sb = new StringBuilder();
	// sb.append("<highlight>");
	// for (InkStroke s : strokes) {
	// sb.append("<stroke begin=\"" + s.getFirstTimestamp() + "\"/>\n");
	// }
	// sb.append("</highlight>");
	// FileUtils
	// .writeStringToFile(sb.toString(), new File("flash/data/highlightTheseStrokes.xml"));
	// }

	/**
	 * Other modules, like a Flash GUI, can listen in on the pen samples.
	 * 
	 * @param penListener
	 */
	public void addPenListener(PenListener penListener) {
		pen.addLivePenListener(penListener);
	}

	/**
	 * 
	 */
	public void exit() {
		DebugUtils.println("Exiting Sketch To Paper UI");
		pen.stopLiveMode();
	}

	/**
	 * @param strokeFile
	 * @param className
	 * @param outputFolder
	 * @throws IOException
	 */
	public void translate(File strokeFile, String className, File outputFolder) {
		PenSynch penSynch = new PenSynch(strokeFile);
		List<Ink> importedInk = penSynch.getImportedInk();

		// TODO: some kind of intelligent grouping of strokes that end near
		// each other

		// Biggest stroke becomes the sheet
		InkStroke biggestStroke = InkUtils.getStrokeWithLargestArea(importedInk);

		// Strokes inside sheet are regions
		List<InkStroke> regionStrokes = InkUtils.getAllStrokesContainedWithin(importedInk, biggestStroke);

		// Strokes that overlap the sheet but go outside are connectors
		List<InkStroke> connectors = InkUtils.getStrokesPartlyOutside(importedInk, biggestStroke);

		// Strokes outside the sheet are events
		List<InkStroke> outsideStrokes = InkUtils.getAllStrokesOutside(importedInk, biggestStroke);
		// Cluster events (since they're words)
		List<Ink> events = InkUtils.clusterStrokes(outsideStrokes, 2);

		// Start the recognizer
		HandwritingRecognitionService service = HandwritingRecognitionService.getInstance();

		// Calculate the size of the sheet in inches (make it fit in 8.5x11)
		Rectangle2D sheet = biggestStroke.getBounds();
		double scale = Regions.makeItFit(sheet.getWidth(), sheet.getHeight(), 8.5, 11);

		// Print out to...
		PrintStream outXML = null;
		PrintStream outJava = null;
		try {
			outXML = new PrintStream(new FileOutputStream(new File(outputFolder, className + ".xml")));
			outJava = new PrintStream(new FileOutputStream(new File(outputFolder, className + ".java")));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		// Number format...
		DecimalFormat df = new DecimalFormat("0.###");

		outXML.println("<sheet width=\"" + df.format(sheet.getWidth() * scale) + "\" height=\""
				+ df.format(sheet.getHeight() * scale) + "\">");

		class Region {
			// InkStroke stroke = null;
			Rectangle2D bounds = null;
			String eventType = null;
			String name = null;
		}

		List<Region> regions = new ArrayList<Region>();

		// Print out regions
		int strokeId = 1;
		for (InkStroke region : regionStrokes) {
			Region r = new Region();
			regions.add(r);
			// r.stroke = region;
			r.bounds = region.getBounds();
			InkStroke c = null;
			// Find the connection that matches this region
			for (InkStroke connection : connectors) {
				if (connection.getBounds().intersects(r.bounds)) {
					c = connection;
					break;
				}
			}
			Ink event = null;

			if (c != null) {

				// Find the endpoint outside of the region
				PenSample p = c.getStart();
				if (r.bounds.contains(p.getX(), p.getY()))
					p = c.getEnd();

				// Find the event nearest that endpoint
				event = InkUtils.getInkNearPoint(events, new Point2D.Double(p.getX(), p.getY()), 40.0);

				// If an event is found...
				if (event != null) {
					// Recognize the text...
					String result = service.recognizeHandwriting(event
							.toXMLString(false /* no separator lines */));

					// Split it on non-alpha numeric characters
					String pieces[] = result.split("[^a-zA-Z0-9]");
					// Second piece is name
					if (pieces.length > 1)
						r.name = pieces[1];
					// First is event type
					r.eventType = pieces[0].toLowerCase();
					// TODO: there is probably a better way to split this
				}
			}

			// If we got no name, auto-generate one (this is problematic if you
			// revise your sketch, since they could potentially be renumbered)
			if (r.name == null)
				r.name = "region" + (strokeId++);

			r.name = Character.toUpperCase(r.name.charAt(0)) + r.name.substring(1);

			// Print it out
			outXML.print("  <region name=\"" + r.name + "\" x=\""
					+ df.format((r.bounds.getX() - sheet.getX()) * scale) + "\" y=\""
					+ df.format((r.bounds.getY() - sheet.getY()) * scale) + "\" width=\""
					+ df.format(r.bounds.getWidth() * scale) + "\" height=\""
					+ df.format(r.bounds.getHeight() * scale) + "\"");
			if (event != null) {
				outXML.println(">");
				outXML.println("   <eventHandler type=\"" + r.eventType + "\"/>");
				outXML.println("  </region>");
			} else {
				outXML.println("/>");
			}
		}
		outXML.println("</sheet>");
		outXML.close();

		// The Template for creating a Paper UI Class...
		String template = FileUtils.readFileIntoStringBuffer(
				PaperToolkit.getResourceFile("/designer/template.txt")).toString();

		template = template.replace("{CLASSNAME}", className);

		// Find patterns of type {REPEAT:REGIONS} ... {/REPEAT:REGIONS}
		// in the template
		Matcher repeatMatcher = Pattern.compile("\\{REPEAT:REGIONS\\}([\\s\\S]*?)\\{/REPEAT:REGIONS\\}",
				Pattern.MULTILINE | Pattern.CASE_INSENSITIVE).matcher(template);
		System.out.println("Matches = " + repeatMatcher.matches());

		int lastPosition = 0;

		while (repeatMatcher.find()) {
			// Print text before the repeat region
			outJava.print(template.substring(lastPosition, repeatMatcher.start()));

			// Loop through regions...
			for (Region region : regions) {

				String repeatString = repeatMatcher.group(1).replace("{REGION.NAME}", region.name);

				// Find {IF:XX} ... {/IF:XX} blocks in the repeat region

				Matcher ifMatcher = Pattern.compile("\\{IF:([A-Z]+)\\}([\\s\\S]*?)\\{/IF:([A-Z]+)\\}",
						Pattern.MULTILINE | Pattern.CASE_INSENSITIVE).matcher(repeatString);

				int lastPosition2 = 0;

				while (ifMatcher.find()) {
					// print text before {IF}
					outJava.print(repeatString.substring(lastPosition2, ifMatcher.start()));

					String type = ifMatcher.group(1);
					String ifString = ifMatcher.group(2);

					// If this event is valid for this region, print out the
					// if block
					if (type.toLowerCase().equals(region.eventType))
						outJava.print(ifString);
					lastPosition2 = ifMatcher.end();
				}
				// print text after {/IF}
				outJava.print(repeatString.substring(lastPosition2));
			}
			lastPosition = repeatMatcher.end();
		}
		// print text after the region
		outJava.print(template.substring(lastPosition));

		outJava.close();

		// Close down recognizer
		service.exitServer();

		// Ron's notes...
		// <sheet width="8.5" height="11">
		// <region name="submit" type="button" x="6.5" y="9" w="1.5" h="1">
		// <eventHandler type="click" name="onSubmit"/>
		// </region>
		// <region name="logo" type="image" src="files/logo.png"/>
		// <region name="inkCapture" type="capture">
		// <eventHandler type="inkWell" name="onInkArrived"/>
		// </region>
		// </sheet>
		//
		// OR, use PaperToolkit.showMe(insideStroke); // this colors the stroke in an external
		// view...
		// it renders an XML file of the INK with some cool colors. =)
		// then it opens the browser to the right HTML page with the right query string. =)
		// Find strokes that go from within the sheet to outside the sheet
		// These are event handlers
		// xxx, at each step, remove the strokes from consideration! =)
		// write about this algorithm in the paper!
		// Find the next near the endpoints of these event handlers.
		// Do handwriting recognition on them.
		// Create Event Handlers!
		// Write about this!!! =)
		// Part II...
		// A second approach is more of a connected components approach?
		// Take a look at Andy Wilson's?
		// Close off strokes, and find buttons and such...
	}
}
