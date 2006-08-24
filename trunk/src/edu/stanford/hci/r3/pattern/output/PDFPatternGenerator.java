package edu.stanford.hci.r3.pattern.output;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

import edu.stanford.hci.r3.pattern.PatternJitter;
import edu.stanford.hci.r3.pattern.PatternPackage;
import edu.stanford.hci.r3.pattern.TiledPatternGenerator;
import edu.stanford.hci.r3.units.Inches;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * <p>
 * This sits on top of PatternPackage and/or TiledPattern to create PDFs that can be printed. It
 * uses the iText library to create and manipulate PDFs.
 * </p>
 * 
 * So far, printing dots with the bullet point • in Tahoma works decently. We will also try drawing
 * circles, or using some sort of stamping pattern if possible.
 */
public class PDFPatternGenerator {

	// font creation
	private static final BaseFont BFONT = createBaseFont();

	private static final int DEFAULT_JITTER = 5;

	private static final int DEFAULT_PADDING = 30;

	private static final String DOT_SYMBOL = "•";

	private static final float FONT_SIZE = 29;

	private static final int X_FONT_OFFSET = -2;

	private static final int Y_FONT_OFFSET = 9;

	/**
	 * @return
	 */
	private static BaseFont createBaseFont() {
		try {
			return BaseFont.createFont("data/tahoma.ttf", BaseFont.CP1252, BaseFont.EMBEDDED);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		TiledPatternGenerator generator = new TiledPatternGenerator();
		PatternPackage pkg = generator.getCurrentPatternPackage();
		String[] pattern = pkg.readPatternFromFile(0, new Inches(.5), new Inches(1),
				new Inches(.5), new Inches(5));

		try {
			// System.out.println(PageSize.LETTER);
			final Document document = new Document(PageSize.LETTER, 50, 50, 50, 50);
			final PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(
					"testData/Test.pdf"));
			// access the document
			document.open();
			// write direct content
			final PdfContentByte cb = writer.getDirectContent();

			PDFPatternGenerator pgen = new PDFPatternGenerator(cb, pattern);
			pgen.renderPattern();

			document.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private PdfContentByte content;

	private String[] pattern;

	/**
	 * 
	 */
	public PDFPatternGenerator(PdfContentByte cb, String[] dotPattern) {
		content = cb;
		pattern = dotPattern;
	}

	/**
	 * 
	 */
	public void renderPattern() {
		content.beginText();
		content.setFontAndSize(BFONT, 12);

		// GRAY, etc. do not work! The printer will do halftoning, which messes things up.
		content.setColorFill(Color.BLACK);

		content.showTextAligned(PdfContentByte.ALIGN_LEFT, "Tahoma " + FONT_SIZE + " Padding: "
				+ DEFAULT_PADDING, 288, 288, 0);
		content.endText();

		// convert from points (72 in an inch, to 1/100 of a millimeter)
		content.concatCTM(1f, 0f, 0f, -1f, 0f, PageSize.LETTER.height());
		content.transform(AffineTransform.getScaleInstance(72 / 2540.0, 72 / 2540.0));

		content.beginText();
		content.setFontAndSize(BFONT, FONT_SIZE);

		final int initX = 72;

		int gridXPosition = initX;
		int gridYPosition = 72;

		int xJitter = 0;
		int yJitter = 0;
		char currentJitterDirection;

		for (String patternRow : pattern) {
			int rowLength = patternRow.length();
			for (int i = 0; i < rowLength; i++) {

				// read the direction
				currentJitterDirection = patternRow.charAt(i);

				// reset the jitters (this is key!)
				xJitter = 0;
				yJitter = 0;

				switch (currentJitterDirection) {
				case PatternJitter.DOWN:
					// System.out.print("d");
					yJitter = DEFAULT_JITTER;
					break;
				case PatternJitter.UP:
					// System.out.print("u");
					yJitter = -DEFAULT_JITTER;
					break;
				case PatternJitter.LEFT:
					// System.out.print("l");
					xJitter = -DEFAULT_JITTER;
					break;
				case PatternJitter.RIGHT:
					// System.out.print("r");
					xJitter = DEFAULT_JITTER;
					break;
				}

				content.showTextAligned(PdfContentByte.ALIGN_CENTER, DOT_SYMBOL,

				gridXPosition + xJitter + X_FONT_OFFSET,

				gridYPosition + yJitter + Y_FONT_OFFSET, 0);

				gridXPosition += DEFAULT_PADDING;
			}
			gridXPosition = initX;
			gridYPosition += DEFAULT_PADDING;
			// System.out.println();
		}

		content.endText();
	}
}
