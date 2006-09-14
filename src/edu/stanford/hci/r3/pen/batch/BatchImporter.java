package edu.stanford.hci.r3.pen.batch;

import java.io.*;

/**
 * <p>
 * The batch importer will be called by the pen importer (.NET code) every time you synchronize the
 * pen. If you would like to use your own BatchImporter, create a subclass and tell R3 where to find
 * it.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class BatchImporter {

	/**
	 * Aug 28, 2006
	 */
	public static void main(String[] args) {
		try {
			System.setOut(new PrintStream(new FileOutputStream("BatchImporter.log")));
			System.out.println("Running the Batched Pen Data Importer");
			for (String arg : args) {
				System.out.println("Argument: " + arg);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// figure out the configuration of where to find importers

	}
}