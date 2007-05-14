package edu.stanford.hci.r3.demos.externallibs.java;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * Random tests of Java features.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class RandomTests {

	/**
	 * Test user.dir, which turns out to be a READ-ONLY property (even though you can change it).
	 */
	@SuppressWarnings("unused")
	private static void changingTheWorkingDirectoryAtRuntimeDoesNotWork() {
		String workingDir = System.getProperty("user.dir");
		System.out.println(workingDir);
		System.setProperty("user.dir", //
				new File(workingDir, "data/Flickr/Twistr").getAbsolutePath());
		workingDir = System.getProperty("user.dir");
		System.out.println(workingDir);
		System.out.println(new File(".").getAbsolutePath());
	}

	/**
	 * Try out the DecimalFormat object.
	 */
	@SuppressWarnings("unused")
	private static void formatNumbers() {
		Double d = new Double(18008083247892.62344);

		DecimalFormat format = new DecimalFormat("#.000");
		String string = format.format(d);
		System.out.println(string);
		System.out.println(d);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<String> testOfUniqueObjects = new ArrayList<String>();
		final String s = "Hello";
		testOfUniqueObjects.add(s);
		testOfUniqueObjects.add(s);
		DebugUtils.println(testOfUniqueObjects.size());
		DebugUtils.println(testOfUniqueObjects.contains("Hello"));
		testOfUniqueObjects.remove("Hello");
		DebugUtils.println(testOfUniqueObjects.size());
	}
}
