package papertoolkit.paper.bundles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import papertoolkit.paper.Bundle;
import papertoolkit.paper.sheets.PDFSheet;

import com.lowagie.text.pdf.PdfReader;


/**
 * <p>
 * Create a Bundle that contains PDFSheets. The PDF manipulation code in this class leverages iText.
 * This class exists to enable us to open an existing PDF and modify its pages.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PDFBundle extends Bundle {

	/**
	 * The PDF file we will turn into a bundle.
	 */
	private File file;

	/**
	 * 
	 */
	private int numSheets;

	/**
	 * @param pdfFile
	 */
	public PDFBundle(File pdfFile) {
		super("PDFSheet: " + pdfFile.getName());
		file = pdfFile;
		addPDFSheetsFromFile();
	}

	/**
	 * Reads the PDF file. For each page, create a new PDFSheet referencing that page. Add that
	 * sheet to this bundle.
	 */
	private void addPDFSheetsFromFile() {
		try {
			final PdfReader reader = new PdfReader(new FileInputStream(file));
			numSheets = reader.getNumberOfPages();
			reader.close();
			for (int i = 0; i < numSheets; i++) {
				// (i+1) because Page Numbers start from 1
				addSheets(new PDFSheet(file, i + 1));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
