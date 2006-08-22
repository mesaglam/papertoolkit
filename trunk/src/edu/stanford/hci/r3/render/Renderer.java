package edu.stanford.hci.r3.render;

import java.awt.Graphics2D;
import java.io.File;

import edu.stanford.hci.r3.core.Sheet;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * This class will render a Sheet into a JPEG, PDF, or Java2D graphics context.
 */
public class Renderer {

	private Sheet sheet;

	public Renderer(Sheet s) {
		sheet = s;
	}

	/**
	 * @param destFile
	 */
	public void renderToJPEG(File destJPEGFile) {

	}

	/**
	 * @param destPDFFile
	 */
	public void renderToPDF(File destPDFFile) {
		
	}

	/**
	 * @param g2d
	 */
	public void renderToG2D(Graphics2D g2d) {

	}
}
