package edu.stanford.hci.r3.util;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class SystemUtils {

	public static final String LIBRARY_PATH_KEY = "java.library.path";

	/**
	 * System character(s) for separating lines. Different for Unix/DOS/Mac.
	 */
	public static final String LINE_SEPARATOR = System.getProperties()
			.getProperty("line.separator");

	public static final String PATH_SEPARATOR = System.getProperty("path.separator");

	/**
	 * This doesn't work. =\
	 * 
	 * TODO: See: http://www.velocityreviews.com/forums/t143553-dll-in-jar.html for a possible
	 * solution to load dlls from the jar file. Alternate idea:
	 * http://forum.java.sun.com/thread.jspa?threadID=563861&messageID=3123713
	 * 
	 * @param url
	 */
	public static void addToLibraryPath(URL url) {
		try {
			final String newPath = new File(url.toURI()).getAbsolutePath() + PATH_SEPARATOR
					+ System.getProperty(LIBRARY_PATH_KEY);
			System.setProperty(LIBRARY_PATH_KEY, newPath);
			System.out.println(System.getProperty(LIBRARY_PATH_KEY));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

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
