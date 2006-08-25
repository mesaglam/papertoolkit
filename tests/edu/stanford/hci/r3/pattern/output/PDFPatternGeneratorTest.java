package edu.stanford.hci.r3.pattern.output;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

import edu.stanford.hci.r3.pattern.PatternPackage;
import edu.stanford.hci.r3.pattern.TiledPatternGenerator;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.units.Points;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PDFPatternGeneratorTest {

	public static void main(String[] args) {
		TiledPatternGenerator generator = new TiledPatternGenerator();
		PatternPackage pkg = generator.getCurrentPatternPackage();
		String[] pattern = pkg.readPatternFromFile(0, new Inches(.5), new Inches(1),
				new Inches(1.5), new Inches(3.5));

		try {
			final Rectangle pageRect = PageSize.LETTER;
			// System.out.println(PageSize.LETTER);
			final Document document = new Document(pageRect, 50, 50, 50, 50);
			final PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(
					"testData/Test.pdf"));
			// access the document
			document.open();
			// write direct content
			final PdfContentByte cb = writer.getDirectContent();

			final PDFPatternGenerator pgen = new PDFPatternGenerator(cb, new Points(pageRect
					.width()), new Points(pageRect.height()));
			pgen.renderPattern(pattern, new Inches(3), new Inches(4));

			document.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

}
