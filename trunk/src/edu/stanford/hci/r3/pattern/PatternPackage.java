package edu.stanford.hci.r3.pattern;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.util.files.FileUtils;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * Represents a set of pattern files. One can tile these files, and create Postscript and PDF files
 * out of them. This package can load specific pattern files into byte[][] so that you can index
 * them for drawing on screen or into little graphics that represent pattern buttons.
 * 
 * All interaction with the specific pattern files should go in this class.
 */
public class PatternPackage {

	/**
	 * All pattern files' names look like N.pattern, where N is the page number.
	 */
	private static final String PATTERN_FILE_EXTENSION = ".pattern";

	private String name;

	/**
	 * The width of a pattern file, in num dots.
	 */
	private int numPatternColsPerFile;

	/**
	 * <p>
	 * How many pattern files do we have? <br>
	 * We assume the names of the files are 0.pattern up to (numPatternFiles-1).pattern
	 * </p>
	 */
	private int numPatternFiles;

	/**
	 * The height of a pattern file, in num dots.
	 */
	private int numPatternRowsPerFile;

	/**
	 * Enables access to a pattern file by the pattern file number N. It will retrieve "N.pattern"
	 */
	private Map<Integer, File> numToPatternFile;

	/**
	 * Where we will find the pattern definition files.
	 */
	private File patternDefinitionPath;

	/**
	 * A list of all the files that store pattern definition.
	 */
	private List<File> patternFiles;

	/**
	 * @param location
	 */
	public PatternPackage(File location) {
		patternDefinitionPath = location;

		name = location.getName();

		// look at the directory to see how many pattern files are available
		// System.out.println(patternDefinitionPath.getAbsolutePath());
		List<File> visibleFiles = FileUtils.listVisibleFiles(patternDefinitionPath,
				new String[] { "pattern" });

		numPatternFiles = visibleFiles.size();

		if (numPatternFiles == 0) {
			System.err.println("There are 0 pattern files in " + location);
			System.err.println("This pattern package is not usable.");
			return;
		}

		patternFiles = visibleFiles;

		// populate the map from page number --> pattern file
		numToPatternFile = new HashMap<Integer, File>();
		for (File f : patternFiles) {
			String fileName = f.getName();
			// get the number in the name
			Integer num = Integer.parseInt(fileName.substring(0, fileName
					.indexOf(PATTERN_FILE_EXTENSION)));
			numToPatternFile.put(num, f);
		}

		// open a .pattern file to see how many dots tall/across each .pattern file is
		File patternFile = patternFiles.get(0);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(patternFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// start a linenumberreader and check how many rows we have
		// potentially spin this off into the FileUtil class
		LineNumberReader lnr = new LineNumberReader(br);
		String firstLine = null;
		try {
			firstLine = lnr.readLine();
			lnr.skip(patternFile.length() - firstLine.length());
		} catch (IOException e) {
			e.printStackTrace();
		}

		// see how many dots tall each file is by checking how many lines there are in the file
		// see how many dots across each .pattern file is by checking length of the first line
		numPatternColsPerFile = firstLine.length();
		numPatternRowsPerFile = lnr.getLineNumber();

		// close this file
		try {
			lnr.close();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the name of the package (same as the directory's name)
	 */
	public String getName() {
		return name;
	}

	public int getNumPatternColsPerFile() {
		return numPatternColsPerFile;
	}

	public int getNumPatternRowsPerFile() {
		return numPatternRowsPerFile;
	}

	/**
	 * @param numPatternFile
	 *            The number of the pattern file (numPatternFile.pattern).
	 * @param originX
	 *            Which column of pattern to start from (0 dots is the leftmost column)
	 * @param originY
	 *            Which row of pattern to start reading from (0 dots is the topmost column)
	 * @param width
	 *            How many dots across do we need? (in whatever Units is most convenient for you)
	 * @param height
	 *            How many dots down do we need?
	 * 
	 * @return a String[] representing the requested pattern (encoded as uldr directions) each entry
	 *         of the array represents one row of pattern. The columns are represented in the
	 *         String. We expect this will be much easier to manipulate, especially since we do not
	 *         need to index dots randomly.
	 * 
	 * We verify that numDotsX and numDotsY do not exceed the amount of dots in one file. If so, the
	 * requested number of dots are modified to fit. Thus, the dimension of the String[] may be
	 * smaller than you requested. You may want to make sure numDotsX/Y are correct if you do not
	 * want to be surprised.
	 */
	public String[] readPatternFromFile(int numPatternFile, Units originX, Units originY,
			Units width, Units height) {

		// regardless of the units, convert them to pattern dots
		int startDotsX = (int) Math.round(originX.getValueInPatternDots());
		int startDotsY = (int) Math.round(originX.getValueInPatternDots());
		int numDotsAcross = (int) Math.round(width.getValueInPatternDots());
		int numDotsDown = (int) Math.round(height.getValueInPatternDots());

		// ///////////////////////////////////////
		// begin: making sure the units make sense
		if (startDotsX < 0) {
			startDotsX = 0;
		}
		if (startDotsX > numPatternColsPerFile - 1) {
			startDotsX = numPatternColsPerFile - 1;
		}
		if (startDotsY < 0) {
			startDotsY = 0;
		}
		if (startDotsY > numPatternRowsPerFile - 1) {
			startDotsY = numPatternRowsPerFile - 1;
		}
		if (numDotsAcross < 0) {
			numDotsAcross = 0;
		}
		// if the position of the rightmost dot is greater than the number of dots available, we
		// adjust the requested number.
		// 
		// for example, if the startDotsX is index 12 (counting from 0), and we are requesting 8
		// dots across, we will need dots 12, 13, 14, 15, 16, 17, 18, 19.
		// 
		// if this file only has 15 dots across, we calculate that 12 + 8 = 20
		// 20 > 15 is bad... and 20-15 is 5.
		// we can request at max 8-5=3 dots.
		// So we can get dots numbered 12, 13, and 14... which makes sense in a 15-dot wide file
		// indexed from 0
		final int rightMostDot = startDotsX + numDotsAcross;
		if (rightMostDot > numPatternColsPerFile) {
			numDotsAcross -= (rightMostDot - numPatternColsPerFile);
		}
		if (numDotsDown < 0) {
			numDotsDown = 0;
		}
		// same reasoning as above
		final int bottomMostDot = startDotsY + numDotsDown;
		if (bottomMostDot > numPatternRowsPerFile) {
			numDotsDown -= (bottomMostDot - numPatternRowsPerFile);
		}
		// end: making sure the units make sense
		// /////////////////////////////////////

		// create a data structure large enough to store however many dots we need
		final String[] pattern = new String[numDotsDown];

		// read the file and populate the array with jitter directions
		// open up file numPatternFile
		final File patternFile = numToPatternFile.get(numPatternFile);

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(patternFile));

			// skip the requested number of vertical lines
			while (startDotsY > 0) {
				br.readLine();
				startDotsY--;
			}

			// for each of the remaining lines, crop out the requested part of the string
			for (int i = 0; i < numDotsDown; i++) {
				String line = br.readLine();
				pattern[i] = line.substring(startDotsX, rightMostDot);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pattern;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "PatternPackage: {" + patternDefinitionPath + "}";
	}
}
