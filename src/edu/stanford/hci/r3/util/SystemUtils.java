package edu.stanford.hci.r3.util;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * <p>
 * Environment Variables, OS specific tasks, etc.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class SystemUtils {

	private static final double BYTES_PER_MB = (1024.0 * 1024.0);

	/**
	 * Where to look for DLLs. Unfortunately, you cannot change this at runtime and expect the classloaders to
	 * find the dlls correctly.
	 */
	public static final String LIBRARY_PATH_KEY = "java.library.path";

	/**
	 * System character(s) for separating lines. Different for Unix/DOS/Mac.
	 */
	public static final String LINE_SEPARATOR = System.getProperties().getProperty("line.separator");

	/**
	 * Different for UNIX/WINDOWS/MAC
	 */
	public static final String PATH_SEPARATOR = System.getProperty("path.separator");

	private static long previousTime;

	/**
	 * This doesn't work. =\
	 * 
	 * TODO: See: http://www.velocityreviews.com/forums/t143553-dll-in-jar.html for a possible solution to
	 * load dlls from the jar file. Alternate idea:
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
	 * @return the number of megabytes of free memory
	 */
	public static double getFreeMemoryInMB() {
		return Runtime.getRuntime().freeMemory() / BYTES_PER_MB;
	}

	/**
	 * @return where new File(".").getAbsolutePath() points...
	 */
	public static File getWorkingDirectory() {
		return new File(System.getProperty("user.dir"));
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

	/**
	 * Sets the user.dir property, so that you can make code dealing with files easier to type/read. WARNING:
	 * Java seems to have a usability bug in this manner.... Do not use this method for now.
	 * 
	 * <code>
	 * SystemUtils.setWorkingDirectory(new File("data/Flickr/"));
	 * // argh! Setting the Working Directory doesn't work for files in this manner. 
	 * // Quite Stupid, in fact. 
	 * System.out.println(new File("Twistr1.xml").exists()); // returns false
	 * System.out.println(new File("Twistr1.xml").getAbsoluteFile().exists()); // returns true
	 * </code>
	 * 
	 * This is a KNOWN Java bug: http://bugs.sun.com/bugdatabase/view_bug.do;:YfiG?bug_id=4117557 Phooey. That
	 * means you have to call getAbsoluteFile() at every point in which you read a file stream. I will have to
	 * instrument SystemUtils & FileUtils to handle this.
	 * 
	 * @param file
	 * @deprecated
	 */
	public static void setWorkingDirectory(File file) {
		System.setProperty("user.dir", file.getAbsolutePath());
	}

	/**
	 * Save the current time, for simple profiling.
	 */
	public static void tic() {
		previousTime = System.currentTimeMillis();
	}

	/**
	 * For saving the time elsewhere.
	 * 
	 * @return
	 */
	public static long ticLocal() {
		return System.currentTimeMillis();
	}

	/**
	 * Prints out the difference between now and the last time we tic'ed.
	 */
	public static void toc() {
		DebugUtils.println("Clock: " + (System.currentTimeMillis() - previousTime) + " ms.");
	}

	/**
	 * Can pass in a time that was saved elsewhere through ticLocal().
	 * 
	 * @param lastTime
	 * @return
	 */
	public static long tocLocal(long lastTime) {
		long t = (System.currentTimeMillis() - lastTime);
		System.out.println("Clock: " + t + " ms.");
		return t;
	}

}
