package edu.stanford.hci.r3;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

import edu.stanford.hci.r3.designer.acrobat.RegionConfiguration;
import edu.stanford.hci.r3.events.EventEngine;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.pattern.coordinates.TiledPatternCoordinateConverter;
import edu.stanford.hci.r3.pattern.coordinates.PatternLocationToSheetLocationMapping.RegionID;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.units.Centimeters;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.units.Pixels;
import edu.stanford.hci.r3.units.Points;
import edu.stanford.hci.r3.util.WindowUtils;

/**
 * <p>
 * Every PaperToolit has one EventEngine that handles input from users, and schedules output for the
 * system. A PaperToolkit can run one or more Applications at the same time. You can also deactivate
 * applications (to pause them). Or, you can remove them altogether.
 * </p>
 * 
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class PaperToolkit {

	/**
	 * Serializes/Unserializes toolkit objects to/from XML strings.
	 */
	private static XStream xmlEngine;

	/**
	 * @param xmlFile
	 * @return
	 */
	public static Object fromXML(File xmlFile) {
		Object o = null;
		try {
			o = getXMLEngine().fromXML(new FileInputStream(xmlFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return o;
	}

	/**
	 * @return the XStream processor that parses and creates XML.
	 */
	private static synchronized XStream getXMLEngine() {
		if (xmlEngine == null) {
			xmlEngine = new XStream();

			// Add Aliases Here (for more concise XML)
			xmlEngine.alias("Sheet", Sheet.class);
			xmlEngine.alias("Inches", Inches.class);
			xmlEngine.alias("Centimeters", Centimeters.class);
			xmlEngine.alias("Pixels", Pixels.class);
			xmlEngine.alias("Points", Points.class);
			xmlEngine.alias("RegionConfiguration", RegionConfiguration.class);
			xmlEngine.alias("Region", Region.class);
			xmlEngine.alias("Rectangle2DDouble", Rectangle2D.Double.class);
			xmlEngine.alias("TiledPatternCoordinateConverter", TiledPatternCoordinateConverter.class);
			xmlEngine.alias("RegionID", RegionID.class);
		}
		return xmlEngine;
	}

	/**
	 * Sets up parameters for any Java Swing UI we need.
	 */
	public static void initializeLookAndFeel() {
		WindowUtils.setNativeLookAndFeel();
	}

	/**
	 * @param obj
	 * @return a string representing the object translated into XML
	 */
	public static String toXML(Object obj) {
		return getXMLEngine().toXML(obj);
	}

	/**
	 * @param object
	 * @param stream
	 *            write the xml to disk or another output stream.
	 */
	public static void toXML(Object object, OutputStream stream) {
		getXMLEngine().toXML(object, stream);
	}

	/**
	 * A list of all applications running in this system.
	 */
	private List<Application> applications = new ArrayList<Application>();

	/**
	 * The engine that processes all pen events, producing the correct outputs and calling the right
	 * event handlers.
	 */
	private EventEngine eventEngine;

	/**
	 * Start up a paper toolkit. A toolkit can load multiple applications, and dispatch events
	 * accordingly (and between applications, ideally). There will be one event engine in the paper
	 * toolkit, and all events that applications generate will be fed through this single event
	 * engine.
	 */
	public PaperToolkit() {
		printInitializationMessages();
		initializeLookAndFeel();
		eventEngine = new EventEngine();
	}

	/**
	 * @param sheet
	 */
	public void print(Sheet sheet) {

	}

	/**
	 * 
	 */
	private void printInitializationMessages() {
		System.out.println("Reduce, Recycle, Reuse: A Paper Applications Toolkit");
	}

	/**
	 * @param paperApp
	 */
	public void runApplication(Application paperApp) {
		applications.add(paperApp);
		List<Pen> pens = paperApp.getPens();

		// add all the live pens to the eventEngine
		for (Pen pen : pens) {
			if (pen.isLive()) {
				eventEngine.register(pen);
			}
		}
	}

	/**
	 * Remove the application and stop receiving events from its pens....
	 * 
	 * @param paperApp
	 */
	public void stopApplication(Application paperApp) {
		applications.remove(paperApp);
		List<Pen> pens = paperApp.getPens();

		for (Pen pen : pens) {
			if (pen.isLive()) {
				eventEngine.unregisterPen(pen);
			}
		}
	}
}
