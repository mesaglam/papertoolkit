package edu.stanford.hci.r3.examples.pdf;

import java.awt.image.BufferedImage;
import java.io.File;

import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;

import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.graphics.ImageUtils;

/**
 * <p>
 * Takes in PDF file and turns it into JPEG/PNG thumbnails.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PDFToThumbnails {

	public static void main(String[] args) {

		final PdfDecoder decoder = new PdfDecoder(true);
		final double dpi = 72;
		decoder.setExtractionMode(0, 72 /* Image DPI */, (float) (dpi / 72) /* Scaling */);
		try {
			decoder.openPdfFile(new File("data/TestFiles/ButterflyNetCHI2006.pdf").getAbsolutePath());
		} catch (PdfException e) {
			e.printStackTrace();
		}

		// page range
		int start = 1;
		int end = decoder.getPageCount();

		// counts from 1
		for (int page = start; page < end + 1; page++) { // read pages
			try {
				BufferedImage pageThumbnail = decoder.getPageAsImage(page);
				ImageUtils.writeImageToJPEG(pageThumbnail, new File("data/TestFiles/Page" + page + ".jpeg"));
				decoder.flushObjectValues(true);
			} catch (PdfException e) {
				DebugUtils.println("Skipping page " + page + " due to " + e.getLocalizedMessage());
			} catch (Exception e) {
				DebugUtils.println("Skipping page " + page + " due to " + e.getLocalizedMessage());
			}
		}
		decoder.closePdfFile();

	}
}
