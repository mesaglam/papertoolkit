package papertoolkit.pattern.output;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

import papertoolkit.PaperToolkit;
import papertoolkit.pattern.PatternJitter;
import papertoolkit.pattern.TiledPattern;
import papertoolkit.units.Units;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.MathUtils;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;

/**
 * <p>
 * This sits on top of PatternPackage and/or TiledPattern to create PDFs that can be printed. It uses the
 * iText library to create and manipulate PDFs.
 * </p>
 * <p>
 * The circle approach works best. We draw and fill a circle the size of an Anoto pattern dot.
 * </p>
 * <p>
 * Other approaches that this class supports:
 * 
 * Printing dots with the bullet point � in Tahoma works decently. We will also try drawing circles, or using
 * some sort of stamping pattern if possible.
 * 
 * setFontSize(...) is an important method, as you will need to adjust it based on your printer. We picked a
 * decent default (21 pt Tahoma) since it works for both of our printers.
 * 
 * ZapfDingbats seems to work even better, as it is a built-in Adobe font. The 'l' lowercase L character looks
 * like a dot. =)
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PDFPatternGenerator {

	/**
	 * font creation
	 */
	private static final BaseFont BFONT_TAHOMA = createBaseFontTahoma();

	/**
	 * 
	 */
	private static final BaseFont BFONT_ZAPF = createBaseFontZapfDingbats();

	/**
	 * Given a value in Hundredths of a millimeter (1/2540 inches) convert it to the same length in Points
	 * (1/72 of an inch). So, 2540 hundredths of mm == 72 points.
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

	/**
	 * 
	 */
	private static final int DEFAULT_JITTER = 5;

	/**
	 * 
	 */
	private static final int DEFAULT_PADDING = 30;

	/**
	 * 
	 */
	private static final int X_FONT_OFFSET = -2;

	/**
	 * 
	 */
	private static final int Y_FONT_OFFSET = 9;

	/**
	 * @return the tahoma font from disk.
	 * TODO: What happened? It used to work... Where does BaseFont Look?
	 */
	private static BaseFont createBaseFontTahoma() {
		// TODO RON YEH xxxx
		DebugUtils.println(new File(".").getAbsolutePath());
		
		try {
			return BaseFont.createFont(PaperToolkit.getDataFile("fonts/tahoma.ttf").getAbsolutePath(), BaseFont.CP1252, BaseFont.EMBEDDED);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @return
	 */
	private static BaseFont createBaseFontZapfDingbats() {
		try {
			return BaseFont.createFont(BaseFont.ZAPFDINGBATS, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
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

	/**
	 * 
	 */
	private BaseFont debugFont;

	/**
	 * 
	 */
	private String dotSymbol;

	/**
	 * A circle that is drawn over and over again, for the dot pattern.
	 */
	private PdfTemplate dotTemplate;

	private int fontSize;

	/**
	 * The height of the PDF document.
	 */
	private Units height;

	/**
	 * The default color of pattern dots is Black. You can customize this if you like (useful for testing).
	 */
	private Color patternColor = Color.BLACK;

	/**
	 * 
	 */
	private BaseFont patternFont;

	/**
	 * Template-based drawing of dots seems better. Use this by default.
	 */
	private boolean useTemplateInsteadOfFont = true;

	/**
	 * The width of the PDFdocument.
	 */
	private Units width;

	/**
	 * @param cb
	 *            The content byte that you pass into this object will be transformed! Beware if you want to
	 *            use it later on for another purpose. Probably, it would be good to dedicate a content layer
	 *            for this pattern generator.
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

		if (useTemplateInsteadOfFont) {
			createDotTemplate(0 /* default */);
		}

		// even if we are using templates, initialize fonts... for debugging
		// initializePatternFont_Tahoma();
		initializePatternFont_Zapf(); // *slightly* smaller file due to built-in font
	}

	/**
	 * 0 means no adjustment. - implies smaller pattern, + implies calls to larger pattern
	 * 
	 * @param patternDotSizeAdjustment
	 */
	public void adjustPatternSize(int patternDotSizeAdjustment) {
		if (useTemplateInsteadOfFont) {
			// if template aproach
			createDotTemplate(patternDotSizeAdjustment);
		} else {
			// if font approach
			fontSize += patternDotSizeAdjustment;
		}
	}

	/**
	 * defaultRadius = 3 works great
	 * 
	 * 
	 * @param adjustment
	 */
	private void createDotTemplate(float adjustment) {
		float radiusAdjustment = 0.5f * adjustment;
		final float defaultRadius = 2.8f; // this is key (the right size)
		float xCenter = defaultRadius;
		float yCenter = defaultRadius;

		// the dot as a pdf template (a rubber stamp)
		dotTemplate = content.createTemplate(2 * xCenter + 1, 2 * yCenter + 1);
		dotTemplate.circle(xCenter, yCenter, defaultRadius + radiusAdjustment);
		dotTemplate.fill();
	}

	/**
	 * 21 works for both laser and wide-format inkjet.
	 */
	@SuppressWarnings("unused")
	private void initializePatternFont_Tahoma() {
		setPatternFontSize(21);
		debugFont = BFONT_TAHOMA;
		patternFont = BFONT_TAHOMA;
		dotSymbol = "�";
	}

	/**
	 * Font size 11 works for laser printers. Font size 7 works for Epson 9800 at 1440. Let's see how small we
	 * can get and still have it work on a laser printer.
	 */
	private void initializePatternFont_Zapf() {
		setPatternFontSize(7);
		debugFont = BFONT_TAHOMA;
		patternFont = BFONT_ZAPF;
		dotSymbol = "l";
	}

	/**
	 * Render the given pattern starting at the designated origin. Also render a white box underneath the
	 * pattern, if we were asked to do so... This helps the pattern stand out better.
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

		final int numRows = pattern.getNumTotalRows();
		final int numCols = pattern.getNumTotalColumns();

		// this actually mirrors everything
		// text will display upside down!
		// this doesn't matter for symmetrical dots, though
		// content.concatCTM(1f, 0f, 0f, -1f, 0f, heightOfPDF);

		// work in hundredths of a millimeter
		final float heightInHundredths = (float) (heightOfPDF * convertPointsToHundredthsOfMM);

		// if we use the font approach
		if (!useTemplateInsteadOfFont) {
			content.beginText();
			// GRAY, etc. do not work! The printer will do halftoning, which messes things up.
			content.setFontAndSize(patternFont, fontSize);
		}

		content.setColorFill(patternColor);
		// content.setColorFill(Color.CYAN);
		// content.setColorFill(Color.BLACK);

		final int initX = MathUtils.rint(xOrigInPoints * convertPointsToHundredthsOfMM);

		int gridXPosition = initX;
		int gridYPosition = MathUtils.rint(yOrigInPoints * convertPointsToHundredthsOfMM);

		// DebugUtils.println("PDFPatternGenerator: Dot Position is " + gridXPosition + " " + gridYPosition);

		int xJitter = 0;
		int yJitter = 0;
		char currentJitterDirection;

		for (int row = 0; row < numRows; row++) {

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

				if (useTemplateInsteadOfFont) {
					content.addTemplate(dotTemplate, gridXPosition + xJitter, //
							heightInHundredths - (gridYPosition + yJitter));
				} else {
					content.showTextAligned(PdfContentByte.ALIGN_CENTER, dotSymbol, //
							gridXPosition + xJitter + X_FONT_OFFSET, //
							heightInHundredths - (gridYPosition + yJitter + Y_FONT_OFFSET), 0);
				}

				gridXPosition += DEFAULT_PADDING;

			}
			gridXPosition = initX;
			gridYPosition += DEFAULT_PADDING;
			// System.out.println();
		}

		if (!useTemplateInsteadOfFont) {
			content.endText();
		}
	}

	/**
	 * @param c
	 */
	public void setPatternColor(Color c) {
		patternColor = c;
	}

	/**
	 * You will need to customize the font size. Different sizes work for different printers at different
	 * DPIs.
	 * 
	 * @param size
	 */
	private void setPatternFontSize(int s) {
		fontSize = s;
	}
}
