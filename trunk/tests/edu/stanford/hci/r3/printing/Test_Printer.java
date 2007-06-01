package edu.stanford.hci.r3.printing;

import java.io.File;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * 
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class Test_Printer {

	public static void seeDefaultPrinter() {
		DebugUtils.println(new Printer());
	}

	public static void printStreamingOnOffPattern() {
		Printer printer = new Printer();
		File streamingOnOff = PaperToolkit
				.getResourceFile("/pattern/StreamingSU-1B/NokiaSU1BStreamingOnOff.ps");
		DebugUtils.println(streamingOnOff.exists());
		Printer.print(streamingOnOff);
	}

	public static void main(String[] args) {
		printStreamingOnOffPattern();
	}
}
