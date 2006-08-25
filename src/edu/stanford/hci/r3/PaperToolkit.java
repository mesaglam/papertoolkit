package edu.stanford.hci.r3;

import com.thoughtworks.xstream.XStream;

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
	 * @param o
	 * @return a string representing o translated into XML
	 */
	public static String toXML(Object o) {
		return getXMLEngine().toXML(o);
	}

	/**
	 * Start up a paper toolkit.
	 */
	public PaperToolkit() {
		initializeJavaSwing();
	}

	/**
	 * Sets up parameters for any Java Swing UI we need.
	 */
	private void initializeJavaSwing() {
		WindowUtils.setNativeLookAndFeel();
	}
}
