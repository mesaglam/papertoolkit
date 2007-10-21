package papertoolkit.tools.design.sketch;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import javax.swing.Timer;

import papertoolkit.PaperToolkit;
import papertoolkit.pen.Pen;
import papertoolkit.pen.PenSample;
import papertoolkit.pen.handwriting.HandwritingRecognitionService;
import papertoolkit.pen.ink.Ink;
import papertoolkit.pen.ink.InkStroke;
import papertoolkit.pen.ink.InkUtils;
import papertoolkit.pen.streaming.listeners.PenListener;
import papertoolkit.pen.streaming.listeners.PenStrokeListener;
import papertoolkit.pen.synch.PenSynch;
import papertoolkit.util.ArrayUtils;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.files.FileUtils;
import papertoolkit.util.graphics.GraphicsUtils;

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
		new SketchToPaperUI().translate(new File(fileName), "SketchedPaperUI", new File("."));
	}

	public static void main(String[] args) {
		new SketchToPaperUI();
	}

	private Pen pen;

	private Ink sketchInk;

	private Timer notificationTimer;

	private static HandwritingRecognitionService hwRecService;

	public SketchToPaperUI() {
		this(new Pen());
	}

	/**
	 * Handwriting Recognizer dies if you send in huge numbers.... Make sure your ink is recentered!
	 */
	public SketchToPaperUI(Pen p) {
		if (hwRecService == null) {
			hwRecService = HandwritingRecognitionService.getInstance();
		}

		sketchInk = new Ink();

		notificationTimer = new Timer(1500, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				notificationTimer.stop();
				// recognize here!
				// recognize on every stroke!
				// if it gets slow... then recognize after nothing happens for about 2 seconds
				// Timer?
				new SketchRecognizer(sketchInk);
			}
		});

		
		DebugUtils.println("New Sketch To Paper UI");
		pen = p;

		// set up the pen, and a pen listener...
		pen.startLiveMode();
		pen.addLivePenListener(new PenStrokeListener() {
			public void strokeArrived(InkStroke stroke) {
				sketchInk.addStroke(stroke);
				// DebugUtils.println("InkWell has " + sketchInk.getNumStrokes() + " strokes");
				notificationTimer.restart();
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
		// DebugUtils.println("Exiting Sketch To Paper UI");
		pen.stopLiveMode();
	}

	private static class SketchRecognizer {

		public InkStroke biggestStroke;
		public List<InkStroke> regionStrokes;
		public List<InkStroke> connectors;
		public List<InkStroke> outsideStrokes;
		public List<Ink> events;
		private Rectangle2D sheetRect;

		public SketchRecognizer(Ink ink) {
			ArrayList<Ink> listOfInk = new ArrayList<Ink>();
			listOfInk.add(ink);
			recognize(listOfInk);
		}

		public SketchRecognizer(List<Ink> inkToRecognize) {
			recognize(inkToRecognize);
		}

		private void recognize(List<Ink> inkToRecognize) {
			// Biggest stroke becomes the sheet
			biggestStroke = InkUtils.getStrokeWithLargestArea(inkToRecognize);

			// Strokes inside sheet are regions
			regionStrokes = InkUtils.getAllStrokesContainedWithin(inkToRecognize, biggestStroke);

			// Strokes that overlap the sheet but go outside are connectors
			connectors = InkUtils.getStrokesPartlyOutside(inkToRecognize, biggestStroke);

			// Strokes outside the sheet are events
			outsideStrokes = InkUtils.getAllStrokesOutside(inkToRecognize, biggestStroke);

			// Cluster events (since they're words)
			events = InkUtils.clusterStrokes(outsideStrokes, 2);

			// do some processing!
			sheetRect = biggestStroke.getBounds();
			// the longer side == 11... the shorter == 8.5

			int regionID = 0;

			// find regions inside this sheet, if any...
			for (InkStroke region : regionStrokes) {
				DebugUtils.println("Region " + regionID);
				regionID++;

				// Find the connectors that match this region
				for (InkStroke connection : connectors) {
					if (connection.getBounds().intersects(region.getBounds())) {
						// DebugUtils.println("\t Connector");

						// Find the endpoint outside of the region
						PenSample outsidePt = connection.getStart();
						if (region.getBounds().contains(outsidePt.getX(), outsidePt.getY())) {
							outsidePt = connection.getEnd();
						}

						Ink eventHandlerNameInk = InkUtils.getInkNearestToPoint(events, new Point2D.Double(
								outsidePt.getX(), outsidePt.getY()));

						// If an event is found...
						if (eventHandlerNameInk != null) {
							// recentering is KEY
							final String eventToRecognize = eventHandlerNameInk.getRecentered().toXMLString(false);
							// Recognize the text...
							// DebugUtils.println(eventToRecognize);
							String result = hwRecService.recognizeHandwriting(eventToRecognize);
							// DebugUtils.println("\t\t Event: " + result);
						}
					}
				}
			}
		}
	}

	private static class RegionTemplate {
		Rectangle2D bounds = null;
		String eventType = null;
		String name = null;
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

		// TODO: some kind of intelligent grouping of strokes that end near each other

		SketchRecognizer recognizerResults = new SketchRecognizer(importedInk);

		// Calculate the size of the sheet in inches (make it fit in 8.5x11)
		// or rather, assume it's either 8.5 x 11 or vice versa.... no need to make a square sheet :-)
		final double sheetWidthRaw = recognizerResults.sheetRect.getWidth();
		final double sheetHeightRaw = recognizerResults.sheetRect.getHeight();
		double scale = GraphicsUtils.getScaleToFitFirstBoxInSecond(sheetWidthRaw, sheetHeightRaw, 8.5, 11);

		// ////////////////////////////////
		// XML / JAVA STUFF
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

		outXML.println("<sheet width=\"" + df.format(sheetWidthRaw * scale) + "\" height=\""
				+ df.format(sheetHeightRaw * scale) + "\">");
		// END XML / JAVA STUFF
		// ////////////////////////////////

		List<RegionTemplate> regions = new ArrayList<RegionTemplate>();

		// Print out regions
		int regionID = 0;
		for (InkStroke region : recognizerResults.regionStrokes) {

			RegionTemplate r = new RegionTemplate();
			regions.add(r);
			r.bounds = region.getBounds();
			InkStroke c = null;
			// Find the connection that matches this region
			for (InkStroke connection : recognizerResults.connectors) {
				if (connection.getBounds().intersects(r.bounds)) {
					c = connection;
					break;
				}
			}
			Ink event = null;

			if (c != null) {

				// Find the endpoint outside of the region
				PenSample p = c.getStart();
				if (r.bounds.contains(p.getX(), p.getY())) {
					p = c.getEnd();
				}

				// Find the event nearest that endpoint
				event = InkUtils.getInkNearPoint(recognizerResults.events, new Point2D.Double(p.getX(), p
						.getY()), 40.0);

				// If an event is found...
				if (event != null) {
					// Recognize the text...
					String result = hwRecService.recognizeHandwriting(event
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
			if (r.name == null) {
				r.name = "region" + (regionID++);
			}

			r.name = Character.toUpperCase(r.name.charAt(0)) + r.name.substring(1);

			// Print it out
			outXML.print("  <region name=\"" + r.name + "\" x=\""
					+ df.format((r.bounds.getX() - recognizerResults.sheetRect.getX()) * scale) + "\" y=\""
					+ df.format((r.bounds.getY() - recognizerResults.sheetRect.getY()) * scale)
					+ "\" width=\"" + df.format(r.bounds.getWidth() * scale) + "\" height=\""
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
				PaperToolkit.getDataFile("/designer/template.txt")).toString();

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
			for (RegionTemplate region : regions) {

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
		hwRecService.exitServer();

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
