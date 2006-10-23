package edu.stanford.hci.r3.paper.sheets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfReader;

import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.render.SheetRenderer;
import edu.stanford.hci.r3.render.sheets.PDFSheetRenderer;
import edu.stanford.hci.r3.units.Points;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * Create a Sheet that is based on a single page of a PDF file. Since a sheet is one page, if we
 * have a multipage PDF, we can only access one page of it. Later on, we will add support for
 * Bundles to read multipage PDFs.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PDFSheet extends Sheet {

	private File file;

	private int pageNum;

	private PdfReader pdfReader;

	/**
	 * This will create a Sheet out of page 1 of the PDF File.
	 * 
	 * @param pdfFile
	 */
	public PDFSheet(File pdfFile) {
		this(pdfFile, 1);
	}

	/**
	 * @param pdfFile
	 * @param pageNumber
	 *            Which page should we create this sheet out of?
	 */
	public PDFSheet(File pdfFile, int pageNumber) {
		file = pdfFile;
		addConfigurationPath(pdfFile.getParentFile());

		pageNum = pageNumber;
		try {
			pdfReader = new PdfReader(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// PDF pages count from 1, not 0
		final int numberOfPages = pdfReader.getNumberOfPages();
		if (pageNum < 1) {
			System.out.println("WARNING: pageNum: " + pageNum
					+ " is an invalid page. Setting it to 1. (PDFSheet.java)");
			pageNum = 1;
		}
		if (pageNum > numberOfPages) {
			System.out.println("WARNING: pageNum: " + pageNum
					+ " is an invalid page. Setting it to " + numberOfPages + ". (PDFSheet.java)");
			pageNum = numberOfPages;
		}

		// get the size of page
		final Rectangle pageSize = pdfReader.getPageSize(pageNum);
		setSize(new Points(pageSize.width()), new Points(pageSize.height()));

		// hmm... how should we handle rotations?
		DebugUtils.println("The Rotation of the PDFSheet is: " + pdfReader.getPageRotation(pageNumber));
	}

	/**
	 * @see edu.stanford.hci.r3.paper.Sheet#getRenderer()
	 */
	public SheetRenderer getRenderer() {
		return new PDFSheetRenderer(this);
	}

	/**
	 * @return
	 */
	public PdfReader getReader() {
		return pdfReader;
	}
}
