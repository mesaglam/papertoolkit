package edu.stanford.hci.r3.pattern.output;

import java.io.File;

import edu.stanford.hci.r3.pattern.TiledPatternGenerator;
import edu.stanford.hci.r3.units.Units;

/**
 * <p>
 * Given input dimensions, this class will generate a postscript file of tiled Anoto pattern. By
 * default, we will tile the pattern files from left to right, top to bottom, as necessary...
 * 
 * WARNING: This class is unfinished. Use the PDF implementation until this works.
 * </p>
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class PostscriptPatternGenerator {

	private Units horizLength;

	private Units vertLength;

	private TiledPatternGenerator tiledPatternGenerator;

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
	}

	/**
	 * @param destinationFile
	 */
	public void generatePostscriptFile(File destinationFile) {
		System.err.println("Unimplemented");
	}

}
