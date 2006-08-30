package edu.stanford.hci.r3;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

import edu.stanford.hci.r3.events.EventEngine;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.units.Centimeters;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.units.Pixels;
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
	 * @param o
	 * @return a string representing o translated into XML
	 */
	public static String toXML(Object o) {
		return getXMLEngine().toXML(o);
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
	 * Start up a paper toolkit.
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
		paperApp.setApplicationEventListener(eventEngine);
	}
}
