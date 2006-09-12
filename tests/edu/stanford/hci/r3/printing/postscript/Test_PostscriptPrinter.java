package edu.stanford.hci.r3.printing.postscript;

import java.io.File;

import edu.stanford.hci.r3.PaperToolkit;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Test_PostscriptPrinter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		printPatternedPage();
	}

	/**
	 * 
	 */
	private static void printPatternedPage() {
		new PaperToolkit();
		PostscriptPrinter.printPostscriptFileToDefaultPrinter(new File("testData/page0and1.ps"));
	}
}
