package edu.stanford.hci.r3.core.regions;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;

import edu.stanford.hci.r3.core.Region;
import edu.stanford.hci.r3.units.Points;
import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.util.StringUtils;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * Represents some text that can be drawn on a page.
 */
public class TextRegion extends Region {

	private Rectangle2D bounds;

	private Font font;

	private Points heightInPoints;

	private Units originX;

	private Units originY;

	private String text;

	private LineMetrics lineMetrics;

	/**
	 * In case the 'text' variable is multiline, we store each individual line in this array.
	 */
	private String[] lines;

	/**
	 * 
	 * @param theText
	 *            What text is displayed.
	 * @param theFont
	 *            The Font Family. Specify the size in points through the font object. We will
	 *            consider the point size of the font as an exact 1/72nd of an inch translation,
	 *            regardless of the device.
	 * @param origX
	 * @param origY
	 */
	public TextRegion(String theText, Font theFont, Units origX, Units origY) {
		super(origX);
		text = theText;
		font = theFont;

		originX = origX;
		originY = origY;

		lines = theText.split("\n");

		// determine the font's boundaries
		// represent it as a Rectangle (x, y, w, h)
		final Dimension stringSize = StringUtils.getStringSize(text, font);
		heightInPoints = new Points(stringSize.getHeight());
		lineMetrics = font.getLineMetrics(text, new FontRenderContext(null, true, true));
		final Rectangle2D rect = new Rectangle2D.Double(origX.getValue(), origY.getValueIn(units),
				new Points(stringSize.getWidth()).getValueIn(units), heightInPoints
						.getValueIn(units));
		bounds = rect;
		setShape(rect);
	}

	/**
	 * @return
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * @return
	 */
	public Points getHeightInPoints() {
		return heightInPoints;
	}

	public Points getDescentInPoints() {
		return new Points(lineMetrics.getDescent());
	}

	public Points getLeadingInPoints() {
		return new Points(lineMetrics.getLeading());
	}

	/**
	 * @return
	 */
	public String[] getLinesOfText() {
		return lines;
	}

	public void printLineMetrics() {
		System.out.println(lineMetrics.getHeight() + " == " + lineMetrics.getLeading() + "+"
				+ lineMetrics.getAscent() + "+" + lineMetrics.getDescent());
	}

	/**
	 * @return the internal text to be rendered.
	 */
	public String getText() {
		return text;
	}

	/**
	 * @return
	 */
	public Units getX() {
		return originX;
	}

	/**
	 * @return
	 */
	public Units getY() {
		return originY;
	}

	/**
	 * @see edu.stanford.hci.r3.core.Region#toString()
	 */
	public String toString() {
		return "Text: {" + text + "} " + font.getSize() + "pt " + font.getName()
				+ " at Bounds: [x=" + originX.getValue() + " y=" + originY.getValue() + " w="
				+ bounds.getWidth() + " h=" + bounds.getHeight() + "] in " + units.getUnitName();
	}

	public Points getLineHeightInPoints() {
		return new Points(lineMetrics.getHeight());
	}

	public Units getAscentInPoints() {
		return new Points(lineMetrics.getAscent());
	}
}
