package papertoolkit.pattern.output;

import papertoolkit.PaperToolkit;
import papertoolkit.pattern.TiledPattern;
import papertoolkit.pattern.TiledPatternGenerator;
import papertoolkit.units.Units;
import papertoolkit.util.files.FileUtils;

/**
 * <p>
 * Given input dimensions, this class will generate a postscript file of tiled Anoto pattern. By default, we
 * will tile the pattern files from left to right, top to bottom, as necessary...
 * 
 * WARNING: This class is unfinished. Use the PDF implementation until this works.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PostscriptPatternGenerator {

	private double dotRadiusLarge = 0.040000;

	/**
	 * In millimeters.
	 */
	private double dotRadiusSmall = 0.030000;

	private Units horizLength;

	/**
	 * Represents the (possibly large) pattern block, that may be tiled from smaller (8.5 x 11) pattern
	 * blocks.
	 */
	private TiledPattern pattern;

	private String template;

	private TiledPatternGenerator tiledPatternGenerator;

	private Units vertLength;

	/**
	 * @param horizontal
	 * @param vertical
	 */
	public PostscriptPatternGenerator(Units horizontal, Units vertical) {
		this(horizontal, vertical, new TiledPatternGenerator());
	}

	/**
	 * @param horizontal
	 * @param vertical
	 * @param tpg
	 */
	public PostscriptPatternGenerator(Units horizontal, Units vertical, TiledPatternGenerator tpg) {
		horizLength = horizontal;
		vertLength = vertical;
		tiledPatternGenerator = tpg;

		// get a large block of pattern the size of this sheet
		pattern = tiledPatternGenerator.getPattern(horizontal, vertical);

		// read in the template file
		template = FileUtils.readFileIntoStringBuffer(
				PaperToolkit.getDataFile("/templates/PostscriptPatternTemplate.txt"), true).toString();

		// add the width, height, and margin
		template = template.replaceAll("__WIDTH_POINTS__", horizLength.getValueInPoints() + "");
		template = template.replaceAll("__HEIGHT_POINTS__", vertLength.getValueInPoints() + "");
		template = template.replaceAll("__WIDTH_POINTS_INT__", (int) Math
				.ceil(horizLength.getValueInPoints())
				+ "");
		template = template.replaceAll("__HEIGHT_POINTS_INT__", (int) Math
				.ceil(vertLength.getValueInPoints())
				+ "");

		template = template.replaceAll("__DOT_RADIUS__", dotRadiusLarge + "");

		insertPattern();
	}

	public TiledPattern getPattern() {
		return pattern;
	}

	public String getPostscriptPattern() {
		return template;
	}

	/**
	 * @param pattern
	 */
	private void insertPattern() {
		// for now, create a string buffer and write to it...
		StringBuilder patternString = new StringBuilder();
		for (int row = 0; row < pattern.getNumTotalRows(); row++) {
			final String patternRow = pattern.getPatternOnRow(row);
			patternString.append("(" + patternRow + ") n\n");
		}

		template = template.replaceAll("__INSERT_PATTERN_HERE__", patternString.toString());
	}
}
