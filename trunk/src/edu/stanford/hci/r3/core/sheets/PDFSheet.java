package edu.stanford.hci.r3.core.sheets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfReader;

import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.units.Points;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * Create a Sheet that is based on a single page of a PDF file. Since a sheet is one page, if we
 * haev a multipage PDF, we can only access one page of it.
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
	}
}
