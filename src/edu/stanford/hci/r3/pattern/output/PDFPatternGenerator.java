package edu.stanford.hci.r3.pattern.output;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;

import edu.stanford.hci.r3.pattern.PatternJitter;
import edu.stanford.hci.r3.pattern.TiledPattern;
import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.util.MathUtils;

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
 * 
 * setFontSize(...) is an important method, as you will need to adjust it based on your printer. We
 * picked a decent default (21 pt Tahoma) since it works for both of our printers.
 */
public class PDFPatternGenerator {

	// font creation
	private static final BaseFont BFONT = createBaseFont();

	/**
	 * Given a value in Hundredths of a millimeter (1/2540 inches) convert it to the same length in
	 * Points (1/72 of an inch). So, 2540 hundredths of mm == 72 points.
	 */
	private static final double convertHundredthsOfMMToPoints = 72 / 2540.0;

	/**
	 * Convert from Points to 1/100 mm.
	 */
	private static final double convertPointsToHundredthsOfMM = 2540.0 / 72;

	/**
	 * Render some information about the pattern.
	 */
	private static final boolean DEBUG_PATTERN = false;

	private static final int DEFAULT_FONT_SIZE = 21;

	private static final int DEFAULT_JITTER = 5;

	private static final int DEFAULT_PADDING = 30;

	private static final String DOT_SYMBOL = "•";

	private static final int X_FONT_OFFSET = -2;

	private static final int Y_FONT_OFFSET = 9;

	/**
	 * @return loads a font from disk.
	 */
	private static BaseFont createBaseFont() {
		try {
			return BaseFont.createFont("/fonts/tahoma.ttf", BaseFont.CP1252, BaseFont.EMBEDDED);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Represents the content layer of the PDF Document.
	 */
	private PdfContentByte content;

	private int fontSize;

	/**
	 * The height of the PDF document.
	 */
	private Units height;

	/**
	 * The width of the PDFdocument.
	 */
	private Units width;

	/**
	 * @param cb
	 *            The content byte that you pass into this object will be transformed! Beware if you
	 *            want to use it later on for another purpose. Probably, it would be good to
	 *            dedicate a content layer for this pattern generator.
	 * @param w
	 *            Width of the PDF Document.
	 * @param h
	 *            Height of the PDF Document.
	 */
	public PDFPatternGenerator(PdfContentByte cb, Units w, Units h) {
		content = cb;
		width = w;
		height = h;

		// transforms the content layer ONCE
		// instead of specifying stuff in points (72 in an inch), we can now
		// specify in 1/100 of a millimeter
		// we need to scale down the numbers so when we specify something at 2540,
		// we get only 72 points...
		content.transform(AffineTransform.getScaleInstance(convertHundredthsOfMMToPoints,
				convertHundredthsOfMMToPoints));
		setFontSize((int) DEFAULT_FONT_SIZE);
	}

	/**
	 * Rend the given pattern starting at the designated origin.
	 * 
	 * @param pattern
	 * @param xOrigin
	 * @param yOrigin
	 */
	public void renderPattern(TiledPattern pattern, Units xOrigin, Units yOrigin) {
		// flip the transform so that the top left of the page is 0,0
		float heightOfPDF = (float) height.getValueInPoints();

		// convert the origins to Points
		final double xOrigInPoints = xOrigin.getValueInPoints();
		final double yOrigInPoints = yOrigin.getValueInPoints();

		if (DEBUG_PATTERN) {
			// write debug output
			content.beginText();
			content.setFontAndSize(BFONT, 10);
			// ArrayUtils.printMatrix(BFONT.getFamilyFontName());
			content.setColorFill(new Color(128, 128, 255, 128));
			content.showTextAligned(PdfContentByte.ALIGN_LEFT, "Tahoma " + (int) fontSize
					+ " Padding: " + DEFAULT_PADDING, (float) xOrigInPoints, heightOfPDF
					- (float) yOrigInPoints + 2, 0);
			content.endText();
		}

		// this actually mirrors everything
		// text will display upside down!
		// this doesn't matter for symmetrical dots, though
		// content.concatCTM(1f, 0f, 0f, -1f, 0f, heightOfPDF);

		// work in hundredths of a millimeter
		final float heightInHundredths = (float) (heightOfPDF * convertPointsToHundredthsOfMM);

		content.beginText();
		// GRAY, etc. do not work! The printer will do halftoning, which messes things up.
		content.setColorFill(Color.BLACK);
		content.setFontAndSize(BFONT, fontSize);

		final int initX = MathUtils.rint(xOrigInPoints * convertPointsToHundredthsOfMM);

		int gridXPosition = initX;
		int gridYPosition = MathUtils.rint(yOrigInPoints * convertPointsToHundredthsOfMM);

		System.out.println("PDFPatternGenerator: " + gridXPosition + " " + gridYPosition);

		int xJitter = 0;
		int yJitter = 0;
		char currentJitterDirection;

		for (int row = 0; row < pattern.getNumRows(); row++) {
			final String patternRow = pattern.getPatternOnRow(row);
			final int rowLength = patternRow.length();

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

				heightInHundredths - (gridYPosition + yJitter + Y_FONT_OFFSET), 0);

				gridXPosition += DEFAULT_PADDING;

			}
			gridXPosition = initX;
			gridYPosition += DEFAULT_PADDING;
			// System.out.println();
		}

		content.endText();
	}

	/**
	 * You will need to customize the font size. Different sizes work for different printers at
	 * different DPIs.
	 * 
	 * @param size
	 */
	public void setFontSize(int size) {
		fontSize = size;
	}
}
