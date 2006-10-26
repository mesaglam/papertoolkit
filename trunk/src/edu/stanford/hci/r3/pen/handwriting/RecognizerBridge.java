package edu.stanford.hci.r3.pen.handwriting;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * Allows us to use the HWRecognition server (written in C#/.NET) from Java.
 * This acts as a client that relays messagse to our Handwriting Recognition Server.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class RecognizerBridge {
	
	/**
	 * Runs the .exe file.
	 */
	public static void startLocalHandwritingRecognitionServer() {
		// off of the PaperToolkit directory...
		// handwritingRec\bin\HandwritingRecognition.exe
		File hwrec = new File(PaperToolkit.getToolkitRootPath(), "handwritingRec/bin/HandwritingRecognition.exe");
		ProcessBuilder builder = new ProcessBuilder(hwrec.getPath());
		try {
			Process process = builder.start();
			InputStream inputStream = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while ((line = br.readLine()) != null) {
				DebugUtils.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		startLocalHandwritingRecognitionServer();
	}
}
