package edu.stanford.hci.r3.printing.pdf;

import java.io.File;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PDFPreviewTest {

	public static void main(String[] args) {
		final PDFPreview pdf = new PDFPreview(new File("testData/ButterflyNetCHI2006.pdf"));
		pdf.setExitApplicationOnClose();
	}
}
