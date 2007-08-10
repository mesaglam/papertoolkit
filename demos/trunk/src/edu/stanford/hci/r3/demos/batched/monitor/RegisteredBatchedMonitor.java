package edu.stanford.hci.r3.demos.batched.monitor;

import java.util.Arrays;

import papertoolkit.util.DebugUtils;

import santiagosoft.TestClass;

/**
 * <p>
 * We put a config file PaperToolkit/penSynch/RegisteredBatchedMonitors/ that points to this class.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class RegisteredBatchedMonitor {

	public static void main(String[] args) {
		DebugUtils.println("The Registered Batched Monitor is now processing: "
				+ Arrays.toString(args));

		// This tests JAR loading, so you can access the classes you have imported through Eclipse
		TestClass testClass = new TestClass();
		DebugUtils.println(testClass.getValue());
	}
}
