package papertoolkit.util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

import papertoolkit.util.classpath.JarClassLoader;


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

	/**
	 * 
	 */
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

	/**
	 * 
	 */
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
	 * @param f
	 */
	public static void open(File f) {
		try {
			Desktop.getDesktop().open(f);
		} catch (IOException e) {
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
		final String lowercaseOSName = System.getProperty("os.name").toLowerCase();
		return lowercaseOSName.startsWith("windows");
	}

	/**
	 * Run an executable file.
	 */
	public static void run(File executableFile, String[] args) {
		try {
			String command = null;
			if (!executableFile.exists()) {
				// this probably means it is in the path, instead of in the local directory
				command = executableFile.getName();
			} else {
				// run this file!
				command = executableFile.getAbsolutePath();
			}
			DebugUtils.println("Command: " + command);
			DebugUtils.println("Arguments: " + Arrays.asList(args));

			String[] cmdWithArguments = new String[1 + args.length];
			cmdWithArguments[0] = command;
			for (int i = 0; i < args.length; i++) {
				cmdWithArguments[i + 1] = args[i];
			}
			ProcessBuilder builder = new ProcessBuilder(cmdWithArguments);
			Map<String, String> env = builder.environment();
			final String envPath = env.get("PATH");
			String append = null;
			if (envPath == null) {
				append = "";
			} else {
				append = System.getProperty("path.separator") + envPath;
			}
			env.put("PATH", System.getProperty("java.library.path") + append);
			builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Runs a JAR file... using a custom classloader.
	 * @param f
	 * @param strings
	 */
	public static void runJar(File f, String[] args) {
		try {
			final JarClassLoader jar = new JarClassLoader(f);
			jar.invokeClass(jar.getMainClassName(), args);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
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
