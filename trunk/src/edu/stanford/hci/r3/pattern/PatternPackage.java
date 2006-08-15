package edu.stanford.hci.r3.pattern;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.List;

import edu.stanford.hci.r3.util.files.FileUtils;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * Represents a set of pattern files. One can tile these files, and create Postscript files out of
 * them.
 */
public class PatternPackage {

	/**
	 * Where we will find the pattern definition files.
	 */
	private File patternDefinitionPath;

	/**
	 * <p>
	 * How many pattern files do we have? <br>
	 * We assume the names of the files are 0.pattern up to (numPatternFiles-1).pattern
	 * </p>
	 */
	private int numPatternFiles;

	private List<File> patternFiles;

	/**
	 * The height of a pattern file, in num dots.
	 */
	private int numPatternRowsPerFile;

	/**
	 * The width of a pattern file, in num dots.
	 */
	private int numPatternColsPerFile;

	private String name;

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

		// open a .pattern file to see how many dots tall/across each .pattern file is
		File patternFile = patternFiles.get(0);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(patternFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// start a linenumberreader from 1, and check how many rows we have
		LineNumberReader lnr = new LineNumberReader(br);
		String firstLine = null;
		lnr.setLineNumber(1);
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
	}

	/**
	 * @return the name of the package (same as the directory's name)
	 */
	public String getName() {
		return name;
	}
}
