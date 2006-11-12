package edu.stanford.hci.r3.pattern.calibrate;

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.pattern.coordinates.PageAddress;
import edu.stanford.hci.r3.pen.ink.Ink;
import edu.stanford.hci.r3.pen.ink.InkStroke;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.files.FileUtils;

/**
 * <p>
 * Aligns two Ink objects to determine the clock offset and parameters for our pattern package.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class CalibrationEngine {

	/**
	 * If you run it from the command line, it asks you to provide two xml files (which are then read in to
	 * create Ink objects). Alternatively, you can use this class directly. For example, CalibrationCaptureApp
	 * uses it to align two strokes as soon as possible.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		PaperToolkit.initializeLookAndFeel();

		// display file chooser
		JFileChooser chooser = FileUtils.createNewFileChooser(new String[] { "xml" });
		chooser.setCurrentDirectory(new File("data/calibration/"));
		chooser.setMultiSelectionEnabled(true);
		int result = chooser.showDialog(null, "Align Two XML Files");

		// load two ink objects
		if (result == JFileChooser.APPROVE_OPTION) {
			File[] selectedFiles = chooser.getSelectedFiles();
			if (selectedFiles.length < 2) {
				DebugUtils.println("Please select at least two XML files...");
			} else {

				File streamed = null;
				File batched = null;

				for (File f : selectedFiles) {
					final String fileName = f.getName();
					if (streamed == null && fileName.contains("streamed")) {
						// look for the streamed file
						streamed = f;
					}
					if (batched == null && fileName.contains("batched")) {
						// look for the batched file
						batched = f;
					}

					if ((streamed != null) && (batched != null)) {
						// once we have both... align them using this class
						new CalibrationEngine().alignInkStrokes(new Ink(streamed), new Ink(batched));
					}
				}
			}
		}
	}

	/**
	 * @param streamedInk
	 * @param batchedInk
	 */
	public void alignInkStrokes(Ink streamedInk, Ink batchedInk) {
		final int numStreamedStrokes = streamedInk.getNumStrokes();
		final int numBatchedStrokes = batchedInk.getNumStrokes();

		if (numStreamedStrokes != numBatchedStrokes) {
			DebugUtils
					.println("The number of strokes do not match. We will try our best to align the two...");
		}

		DebugUtils.println(numStreamedStrokes + " streamed strokes.");
		DebugUtils.println(numBatchedStrokes + " batched strokes.");

		final List<InkStroke> streamedStrokes = streamedInk.getStrokes();
		final List<InkStroke> batchedStrokes = batchedInk.getStrokes();

		PageAddress bPageAddress = batchedInk.getSourcePageAddress();

		double avgMillisBehind = 0;
		double numSamples = 0;

		for (int i = 0; i < streamedStrokes.size(); i++) {
			DebugUtils.println("Aligning Stroke");
			InkStroke sStroke = streamedStrokes.get(i);
			InkStroke bStroke = batchedStrokes.get(i);

			int[] sForceSamples = sStroke.getForceSamples();
			int[] bForceSamples = bStroke.getForceSamples();

			long[] sTimeSamples = sStroke.getTimeSamples();
			long[] bTimeSamples = bStroke.getTimeSamples();

			double[] sSamplesX = sStroke.getXSamples();
			double[] bSamplesX = bStroke.getXSamples();

			double[] sSamplesY = sStroke.getYSamples();
			double[] bSamplesY = bStroke.getYSamples();

			for (int j = 0; j < sForceSamples.length; j++) {
				if (sForceSamples[j] == bForceSamples[j]) {
					final double millisBehind = (sTimeSamples[j] - bTimeSamples[j]);
					final String behindOrAhead = getBehindOrAheadString(millisBehind);

					DebugUtils.println("Streamed: " + sSamplesX[j] + ", " + sSamplesY[j] + " <--> "
							+ bSamplesX[j] + ", " + bSamplesY[j] + " on page " + bPageAddress
							+ " with the Pen's Clock" + behindOrAhead + "by " + Math.abs(millisBehind)
							+ " milliseconds.");

					// an incremental averaging...
					numSamples++;
					avgMillisBehind = (avgMillisBehind * ((numSamples - 1) / numSamples))
							+ (millisBehind / numSamples);
					DebugUtils.println("Average: " + avgMillisBehind);
				}
			}
		}

		DebugUtils.println("After " + numSamples + " samples, we find that the Pen's Clock is"
				+ getBehindOrAheadString(avgMillisBehind) + "by " + Math.abs(avgMillisBehind)
				+ " milliseconds, on average.");
	}

	/**
	 * @param millisBehind
	 * @return
	 */
	private String getBehindOrAheadString(double millisBehind) {
		String behindOrAhead = (millisBehind < 0) ? " ahead " : " behind ";
		return behindOrAhead;
	}
}
