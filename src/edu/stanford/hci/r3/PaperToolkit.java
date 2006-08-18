package edu.stanford.hci.r3;

import com.thoughtworks.xstream.XStream;

import edu.stanford.hci.r3.core.Sheet;
import edu.stanford.hci.r3.units.Centimeters;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.units.Pixels;
import edu.stanford.hci.r3.util.WindowUtils;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
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
	 * 
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
