package edu.stanford.hci.r3.examples.pdf;

import java.io.*;

import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentCatalog;
import org.pdfbox.pdmodel.common.PDMetadata;

/**
 * <p>
 * This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt"> BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PDFBoxTest {

	/**
	 * Aug 21, 2006
	 */
	public static void main(String[] args) {
		try {
			PDDocument doc = PDDocument.load(new File("testData/ButterflyNetCHI2006.pdf"));
			PDDocumentCatalog cat = doc.getDocumentCatalog();
			PDMetadata metadata = cat.getMetadata();
			
			InputStream stream = metadata.createInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String line;
			while ((line = br.readLine())!=null) {
				System.out.println(line);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
}
