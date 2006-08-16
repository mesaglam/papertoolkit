package edu.stanford.hci.r3.pattern.output;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

import edu.stanford.hci.r3.pattern.PatternJitter;
import edu.stanford.hci.r3.pattern.PatternPackage;
import edu.stanford.hci.r3.pattern.TiledPatternGenerator;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.units.PatternDots;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * This sits on top of PatternPackage and/or TiledPattern to create PDFs that can be printed. It
 * uses the iText library to create and manipulate PDFs.
 */
public class PDFPatternGenerator {

	private static final int DEFAULT_JITTER = 5;

	private static final int DEFAULT_PADDING = 30;

	private static final String DOT_SYMBOL = "•";

	private static final int X_FONT_OFFSET = -2;

	private static final int Y_FONT_OFFSET = 9;

	private static final float FONT_SIZE = 30;
	

	public static void main(String[] args) {
		TiledPatternGenerator generator = new TiledPatternGenerator();
		PatternPackage pkg = generator.getCurrentPatternPackage();
		String[] pattern = pkg.readPatternFromFile(0, new PatternDots(0), new PatternDots(0),
				new Inches(2), new Inches(2));

		try {
			Document document = new Document(PageSize.LETTER, 50, 50, 50, 50);
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(
					"testData/test.pdf"));

			// font creation
			BaseFont bfont = BaseFont.createFont("data/tahoma.ttf", BaseFont.CP1252,
					BaseFont.EMBEDDED);

			// access the document
			document.open();

			// write direct content
			PdfContentByte cb = writer.getDirectContent();

			cb.beginText();
			cb.setFontAndSize(bfont, 12);
			cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Tahoma " + FONT_SIZE, 288, 288, 0);
			cb.endText();
			
			// convert from points (72 in an inch, to 1/100 of a millimeter)
			cb.concatCTM(1f, 0f, 0f, -1f, 0f, PageSize.LETTER.height());
			cb.transform(AffineTransform.getScaleInstance(72 / 2540.0, 72 / 2540.0));

			cb.beginText();
			cb.setFontAndSize(bfont, FONT_SIZE);
			cb.setColorFill(Color.BLACK);

			int gridXPosition = 0;
			int gridYPosition = 0;

			int xJitter = 0;
			int yJitter = 0;
			char currentJitterDirection;

			for (String patternRow : pattern) {
				int rowLength = patternRow.length();
				for (int i = 0; i < rowLength; i++) {
					currentJitterDirection = patternRow.charAt(i);
					switch (currentJitterDirection) {
					case PatternJitter.DOWN:
						yJitter = DEFAULT_JITTER;
						break;
					case PatternJitter.UP:
						yJitter = -DEFAULT_JITTER;
						break;
					case PatternJitter.LEFT:
						xJitter = -DEFAULT_JITTER;
						break;
					case PatternJitter.RIGHT:
						xJitter = DEFAULT_JITTER;
						break;
					}

					cb.showTextAligned(PdfContentByte.ALIGN_CENTER, DOT_SYMBOL,

					gridXPosition + xJitter + X_FONT_OFFSET,

					gridYPosition + yJitter + Y_FONT_OFFSET, 0);

					gridXPosition += DEFAULT_PADDING;
				}
				gridXPosition = 0;
				gridYPosition += DEFAULT_PADDING;
			}

			cb.endText();

			document.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
