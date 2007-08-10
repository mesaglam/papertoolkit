package papertoolkit.pen.synch;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import papertoolkit.pattern.coordinates.PageAddress;
import papertoolkit.pen.PenSample;
import papertoolkit.pen.ink.Ink;
import papertoolkit.pen.ink.InkStroke;
import papertoolkit.units.PatternDots;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.files.FileUtils;


/**
 * <p>
 * Reads in XML request files as Ink objects.
 * 
 * TODO: We need to change this to allow for real event handling, after the fact. Basically, we could allow a
 * user to "play back" the batched events, if necessary. They could accomplish this through a slider or
 * something (as a debugging environment). Would this be useful for the end user too? Imagine if the end user
 * saw the stream of events, and could see which event handlers were actuated when. Maybe we can cancel some
 * strokes (after the fact)?
 * 
 * For example, if the system recognizes a TODO somewhere, we might want to cancel that event.
 * 
 * TODO: We should also change this to parse XML "for real", so we can get at attributes such as the segment,
 * shelf, book, page, etc...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public abstract class BatchedEventHandler {

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
	 * Create an Event Handler for reading in Batched Ink Files, synched over USB.
	 */
	public BatchedEventHandler(String theName) {
		name = theName;
	}

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

			// for every page, we create ONE ink object
			final Ink inkOnThisPage = new Ink();
			inkOnThisPage.setName("Ink for " + pageAddress);

			// save where we got this ink, so we will know later on...
			final PageAddress address = new PageAddress(pageAddress);
			inkOnThisPage.setSourcePageAddress(address);

			// extract front and end matter
			// final String beginText = requestBuffer.substring(beginTagStartIndex, beginTagEndIndex);
			// final String endText = requestBuffer.substring(endTagStartIndex, endTagEndIndex);

			// extract text in between the begin and end tags
			final String insideText = requestBuffer.substring(beginTagEndIndex, endTagStartIndex);
			// System.out.println("Internal Text Length: " + insideText.length());
			// System.out.println(insideText);

			final Matcher matcherStrokeBegin = PATTERN_BEGIN_STROKE.matcher(insideText);
			final Matcher matcherStrokeEnd = PATTERN_END_STROKE.matcher(insideText);
			// look through the strokes for this page
			while (matcherStrokeBegin.find() && matcherStrokeEnd.find()) {
				final String strokeTimeStamp = matcherStrokeBegin.group(1);
				final long ts = Long.parseLong(strokeTimeStamp);
				// date/time of the beginning of the stroke!
				DebugUtils.println("Stroke Time: " + new Date(ts));

				// samples between the <stroke...></stroke>
				final String strokeSampleText = insideText.substring(matcherStrokeBegin.end(),
						matcherStrokeEnd.start());

				final List<PenSample> samples = new ArrayList<PenSample>();

				final Matcher matcherSample = PATTERN_SAMPLE.matcher(strokeSampleText);
				while (matcherSample.find()) {
					final String x = matcherSample.group(1);
					final String y = matcherSample.group(2);
					final String f = matcherSample.group(3);
					final String t = matcherSample.group(4);

					// make samples and stuff.... add it to the ink
					// DebugUtils.println(x + " " + y + " f=" + f + " ts=" + t);

					final PenSample sample = new PenSample(Double.parseDouble(x), Double.parseDouble(y),
							Integer.parseInt(f), Long.parseLong(t));
					samples.add(sample);
				}

				// we create one stroke object and add it to the current ink object
				// we also add all the recent samples into this stroke
				final InkStroke stroke = new InkStroke(samples, referenceUnit);
				inkOnThisPage.addStroke(stroke);
			}

			inkArrived(inkOnThisPage);
		}
		// System.out.println();
	}

	/**
	 * Handlers will get this notification for every <page>...</page> that is read in from disk.
	 * 
	 * @param inkOnThisPage
	 */
	public abstract void inkArrived(Ink inkOnThisPage);

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name;
	}
}
