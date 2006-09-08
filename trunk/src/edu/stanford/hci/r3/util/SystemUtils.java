package edu.stanford.hci.r3.util;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class SystemUtils {

	/**
	 * System character(s) for separating lines. Different for Unix/DOS/Mac.
	 */
	public static final String LINE_SEPARATOR = System.getProperties()
			.getProperty("line.separator");

	/**
	 * http://developer.apple.com/technotes/tn2002/tn2110.html
	 * 
	 * @return true if the jvm is running on mac os x
	 */
	public static boolean operatingSystemIsMacOSX() {
		String lcOSName = System.getProperty("os.name").toLowerCase();
		return lcOSName.startsWith("mac os x");
	}

	/**
	 * @return if the jvm is running on windows 9x/NT/2000/XP/Vista
	 */
	public static boolean operatingSystemIsWindowsVariant() {
		String lcOSName = System.getProperty("os.name").toLowerCase();
		return lcOSName.startsWith("windows");
	}

}
