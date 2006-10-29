package edu.stanford.hci.r3.pen.batch;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.hci.r3.pen.ink.Ink;
import edu.stanford.hci.r3.pen.ink.InkSample;
import edu.stanford.hci.r3.pen.ink.InkStroke;
import edu.stanford.hci.r3.units.PatternDots;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.files.FileUtils;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public abstract class BatchEventHandler {

	/**
	 * .*? is a reluctant matcher (i.e., not greedy)
	 */
	private static final String BEGIN_PAGE_TAG = "<page address=\"(.*?)\".*?>";

	private static final String BEGIN_SAMPLE_TAG_END_SAMPLE_TAG = "<p x=\"(.*?)\" y=\"(.*?)\" f=\"(.*?)\" t=\"(.*?)\".*?/>";

	private static final String BEGIN_STROKE_TAG = "<stroke begin=\"(.*?)\".*?>";

	/**
	 * the end tag
	 */
	private static final String END_PAGE_TAG = "</page>";

	private static final String END_STROKE_TAG = "</stroke>";

	private static final Pattern PATTERN_BEGIN_PAGE = Pattern.compile(BEGIN_PAGE_TAG);

	private static final Pattern PATTERN_BEGIN_STROKE = Pattern.compile(BEGIN_STROKE_TAG);

	private static final Pattern PATTERN_END_PAGE = Pattern.compile(END_PAGE_TAG);

	private static final Pattern PATTERN_END_STROKE = Pattern.compile(END_STROKE_TAG);

	private static final Pattern PATTERN_SAMPLE = Pattern.compile(BEGIN_SAMPLE_TAG_END_SAMPLE_TAG);

	private String name;

	private PatternDots referenceUnit = new PatternDots();

	/**
	 * 
	 */
	public BatchEventHandler(String theName) {
		name = theName;
	}

	/**
	 * 
	 * @param inkOnThisPage
	 */
	public abstract void inkArrived(Ink inkOnThisPage);

	/**
	 * @param xmlDataFile
	 */
	public void batchedDataArrived(File xmlDataFile) {
		// parse it like we used to do... in BNet
		DebugUtils.println("BatchEventHandler got the file: " + xmlDataFile);

		// read in the whole request file into a String
		// is this an issue if the xml file is large, say 20MB?
		final StringBuilder requestBuffer = FileUtils.readFileIntoStringBuffer(xmlDataFile);

		final Matcher matcherPageBegin = PATTERN_BEGIN_PAGE.matcher(requestBuffer);
		final Matcher matcherPageEnd = PATTERN_END_PAGE.matcher(requestBuffer);

		while (matcherPageBegin.find() && matcherPageEnd.find()) {
			// DebugUtils.println("Processing Page: ");

			// location of the opening tag <page ...>
			final int beginTagEndIndex = matcherPageBegin.end();
			final int beginTagStartIndex = matcherPageBegin.start();

			// location of the closing tag </page>
			final int endTagStartIndex = matcherPageEnd.start();
			final int endTagEndIndex = matcherPageEnd.end();

			// DebugUtils.println(BEGIN_PAGE_TAG + " found at " + beginTagStartIndex + " to "
			// + beginTagEndIndex);
			// DebugUtils.println(END_PAGE_TAG + " found at " + endTagStartIndex + " to "
			// + endTagEndIndex);

			// extract page address
			final String pageAddress = matcherPageBegin.group(1);
			DebugUtils.println("Page Address: " + pageAddress);

			// extract front and end matter
			// final String beginText = requestBuffer.substring(beginTagStartIndex,
			// beginTagEndIndex);
			// final String endText = requestBuffer.substring(endTagStartIndex, endTagEndIndex);

			// extract text in between the begin and end tags
			final String insideText = requestBuffer.substring(beginTagEndIndex, endTagStartIndex);
			// System.out.println("Internal Text Length: " + insideText.length());
			// System.out.println(insideText);

			// for every page, we create ONE ink object
			final Ink inkOnThisPage = new Ink();
			inkOnThisPage.setName("Ink for " + pageAddress);

			final Matcher matcherStrokeBegin = PATTERN_BEGIN_STROKE.matcher(insideText);
			final Matcher matcherStrokeEnd = PATTERN_END_STROKE.matcher(insideText);
			// look through the strokes for this page
			while (matcherStrokeBegin.find() && matcherStrokeEnd.find()) {
				String strokeTimeStamp = matcherStrokeBegin.group(1);
				long ts = Long.parseLong(strokeTimeStamp);
				// date/time of the beginning of the stroke!
				DebugUtils.println("Stroke Time: " + new Date(ts));

				// samples between the <stroke...></stroke>
				final String strokeSampleText = insideText.substring(matcherStrokeBegin.end(),
						matcherStrokeEnd.start());

				final List<InkSample> samples = new ArrayList<InkSample>();

				final Matcher matcherSample = PATTERN_SAMPLE.matcher(strokeSampleText);
				while (matcherSample.find()) {
					final String x = matcherSample.group(1);
					final String y = matcherSample.group(2);
					final String f = matcherSample.group(3);
					final String t = matcherSample.group(4);

					// make samples and stuff.... add it to the ink
					// DebugUtils.println(x + " " + y + " f=" + f + " ts=" + t);

					final InkSample sample = new InkSample(Double.parseDouble(x), Double
							.parseDouble(y), Integer.parseInt(f), Long.parseLong(t));
					samples.add(sample);
				}

				// we create one stroke object and add it to the current ink object
				// we also add all the recent samples into this stroke
				final InkStroke stroke = new InkStroke(samples, referenceUnit);
				inkOnThisPage.addStroke(stroke);
			}

			inkArrived(inkOnThisPage);
		}
		System.out.println();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name;
	}
}
