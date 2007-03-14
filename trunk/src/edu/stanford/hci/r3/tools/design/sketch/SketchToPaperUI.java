package edu.stanford.hci.r3.tools.design.sketch;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.List;

import edu.stanford.hci.r3.pen.PenSample;
import edu.stanford.hci.r3.pen.batch.PenSynch;
import edu.stanford.hci.r3.pen.handwriting.HandwritingRecognitionService;
import edu.stanford.hci.r3.pen.ink.Ink;
import edu.stanford.hci.r3.pen.ink.InkStroke;
import edu.stanford.hci.r3.pen.ink.InkUtils;

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
	 * Returns the scale necessary to make width/height fit inside 
	 * maxWidth/maxHeight.
	 * @param width
	 * @param height
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static double makeItFit(double width, double height, 
			double maxWidth, double maxHeight) {
		double xprop, yprop;
		if ((xprop = width / maxWidth) < (yprop = height / maxHeight))
			return 1/yprop;
		return 1/xprop;
	}

	
	/**
	 * Loads the most recent pen XML and generates a paper UI XML file.
	 * 
	 * @param args
	 */
	


	public static void main(String[] args) {

		String fileName = "penSynch/data/XML/2007_03_10__01_09_38_SketchedPaperUI.xml";

		PenSynch penSynch = new PenSynch(new File(fileName));
		List<Ink> importedInk = penSynch.getImportedInk();
		
		// TODO: some kind of intelligent grouping of strokes that end near
		// each other

		// Biggest stroke becomes the sheet
		InkStroke biggestStroke = 
			InkUtils.findStrokeWithLargestArea(importedInk);

		// Strokes inside sheet are regions
		List<InkStroke> regions = 
			InkUtils.findAllStrokesContainedWithin(importedInk, biggestStroke);
		
		// Strokes that overlap the sheet but go outside are connectors
		List<InkStroke> connectors = 
			InkUtils.findAllStrokesPartlyOutside(importedInk, biggestStroke);

		// Strokes outside the sheet are events
		List<InkStroke> outsideStrokes = 
			InkUtils.findAllStrokesOutside(importedInk, biggestStroke);
		// Cluster events (since they're words)
		List<Ink> events = InkUtils.clusterStrokes(outsideStrokes, 2);
		
		// Start the recognizer
		HandwritingRecognitionService service = 
			HandwritingRecognitionService.getInstance();

		

		// Calculate the size of the sheet in inches (make it fit in 8.5x11)
		Rectangle2D sheet = biggestStroke.getBounds();
		double scale = makeItFit(sheet.getWidth(), sheet.getHeight(), 8.5, 11);
		
		// Print out to...
		PrintStream out = System.out;
		
		
		// Number format...
		DecimalFormat df = new DecimalFormat("0.###");
		
		
		out.println("<sheet width=\""+df.format(sheet.getWidth()*scale)+
					"\" height=\""+df.format(sheet.getHeight()*scale)+"\">");
		
		// Print out regions
		int strokeId = 1;
		for (InkStroke region : regions) {
			Rectangle2D regionBounds = region.getBounds();
			InkStroke c = null;
			// Find the connection that matches this region
			for (InkStroke connection : connectors) {
				if (connection.getBounds().intersects(regionBounds)) {
					c = connection;
					break;
				}	
			}
			
			// Find the endpoint outside of the region
			PenSample p = c.getStart();
			if (regionBounds.contains(p.getX(),p.getY()))
				p = c.getEnd();
			
			// Find the event nearest that endpoint
			Ink event = InkUtils.findInkNearPoint(events, 
					new Point2D.Double(p.getX(),p.getY()), 40.0);
			
			// If an event is found...
			String name = null;
			String eventType = null;
			if (event != null) {
				// Recognize the text...
				String result = service.recognizeHandwriting(
						event.getAsXML(false /* no separator lines */));
				
				// Split it on non-alpha numeric characters
				String pieces[] = result.split("[^a-zA-Z0-9]");
				// Second piece is name
				if (pieces.length>1)
					name = pieces[1];
				// First is event type
				eventType = pieces[0];
				// TODO: there is probably a better way to split this
			}
			
			// If we got no name, auto-generate one (this is problematic if you
			// revise your sketch, since they could potentially be renumbered)
			if (name == null)
				name = "region"+(strokeId++);
			
			// Print it out
			out.print("  <region name=\""+name+
									"\" x=\""+df.format((regionBounds.getX()-sheet.getX())*scale)+
									"\" y=\""+df.format((regionBounds.getY()-sheet.getY())*scale)+
									"\" width=\""+df.format(regionBounds.getWidth()*scale)+
									"\" height=\""+df.format(regionBounds.getHeight()*scale)+"\"");
			if (event != null) {
				out.println(">");
				out.println("   <eventHandler type=\""+eventType +"\"/>");
				out.println("  </region>");
			} else {
				out.println("/>");
			}
		}
		out.println("</sheet>");

		// Close down recognizer
		service.exitServer();
		
		/*
		 * Ron's notes...
		 * 
		 * <sheet width="8.5" height="11">
		 *   <region name="submit" type="button" x="6.5" y="9" w="1.5" h="1">
		 *     <eventHandler type="click" name="onSubmit"/>
		 *   </region>
		 *   <region name="logo" type="image" src="files/logo.png"/>
		 *   <region name="inkCapture" type="capture">
		 *     <eventHandler type="inkWell" name="onInkArrived"/>
		 *   </region>
		 * </sheet>
		 */
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
/*
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
	*/
}
