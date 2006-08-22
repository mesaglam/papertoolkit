package edu.stanford.hci.r3.examples.pdf;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.etymon.pjx.*;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * Uses the PJX Library. It seems like PJX isn't quite as useful as PDFBox, after all.
 */
public class PJXTest {

	/**
	 * Aug 21, 2006
	 */
	public static void main(String[] args) {
		try {
			PdfReader pdfr = new PdfReader(new PdfInputBuffer(new File(
					"testData/ButterflyNetCHI2006.pdf")));
			PdfManager mgr = new PdfManager(pdfr);

			PdfDictionary dict = mgr.getTrailerDictionary();
			Map map = dict.getMap();
			PdfReference ref = (PdfReference) map.get(new PdfName("Info"));
			PdfDictionary info = (PdfDictionary) mgr.getObjectIndirect(ref);

			System.out.println(info.getMap());

			// modify it

			// write a doc
			PdfWriter w = new PdfWriter(new File("testData/Temp.pdf"));
			mgr.writeDocument(w);
			w.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (PdfFormatException e) {
			e.printStackTrace();
		}

	}
}
