package edu.stanford.hci.r3.pattern.output;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;

import edu.stanford.hci.r3.pattern.PatternJitter;
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
 */
public class PDFPatternGenerator {

	// font creation
	private static final BaseFont BFONT = createBaseFont();

	/**
	 * Render some information about the pattern.
	 */
	private static final boolean DEBUG_PATTERN = false;

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

	private PdfContentByte content;

	private Units width;

	private Units height;

	/**
	 * @param cb
	 * @param w
	 * @param h
	 */
	public PDFPatternGenerator(PdfContentByte cb, Units w, Units h) {
		content = cb;
		width = w;
		height = h;
	}

	/**
	 * Rend the given pattern starting at the designated origin.
	 * 
	 * @param pattern
	 * @param xOrigin
	 * @param yOrigin
	 */
	public void renderPattern(String[] pattern, Units xOrigin, Units yOrigin) {
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
			content.showTextAligned(PdfContentByte.ALIGN_LEFT, "Tahoma " + (int) FONT_SIZE
					+ " Padding: " + DEFAULT_PADDING, (float) xOrigInPoints, heightOfPDF
					- (float) yOrigInPoints + 2, 0);
			content.endText();
		}

		// convert from points (72 in an inch, to 1/100 of a millimeter)
		// this actually mirrors everything
		// text will display upside down!
		// this doesn't matter for symmetrical dots, though
		// content.concatCTM(1f, 0f, 0f, -1f, 0f, heightOfPDF);

		// work in hundredths of a millimeter
		final double convPointsToHundredthsOfMM = 72 / 2540.0;
		content.transform(AffineTransform.getScaleInstance(convPointsToHundredthsOfMM,
				convPointsToHundredthsOfMM));
		final float heightInHundredths = (float) (heightOfPDF / convPointsToHundredthsOfMM);

		content.beginText();
		// GRAY, etc. do not work! The printer will do halftoning, which messes things up.
		content.setColorFill(Color.BLACK);
		content.setFontAndSize(BFONT, FONT_SIZE);

		final int initX = MathUtils.rint(xOrigInPoints / convPointsToHundredthsOfMM);

		int gridXPosition = initX;
		int gridYPosition = MathUtils.rint(yOrigInPoints / convPointsToHundredthsOfMM);

		System.out.println("PDFPatternGenerator: " + gridXPosition + " " + gridYPosition);

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

				heightInHundredths - (gridYPosition + yJitter + Y_FONT_OFFSET), 0);

				gridXPosition += DEFAULT_PADDING;
			}
			gridXPosition = initX;
			gridYPosition += DEFAULT_PADDING;
			// System.out.println();
		}

		content.endText();
	}
}
