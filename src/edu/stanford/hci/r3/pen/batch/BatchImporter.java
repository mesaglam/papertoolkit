package edu.stanford.hci.r3.pen.batch;

import java.io.*;

import javax.swing.JFrame;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * If you would like to use your own BatchImporter, create a subclass and tell R3 where to find it.
 */
public class BatchImporter {

	/**
	 * Aug 28, 2006
	 */
	public static void main(String[] args) {
		try {
			System.setOut(new PrintStream(new FileOutputStream("BatchImporter.log")));
			System.out.println("Running Pen Data Importer v1.0");
			for (String arg : args) {
				System.out.println("Argument: " + arg);
			}
			
			JFrame f = new JFrame("Text");
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setSize(640, 480);
			f.setVisible(true);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// figure out the configuration of where to find importers

	}
}
