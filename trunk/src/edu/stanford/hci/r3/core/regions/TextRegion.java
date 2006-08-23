package edu.stanford.hci.r3.core.regions;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

import edu.stanford.hci.r3.core.Region;
import edu.stanford.hci.r3.render.RegionRenderer;
import edu.stanford.hci.r3.render.types.PolygonRenderer;
import edu.stanford.hci.r3.render.types.TextRenderer;
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

	/**
	 * In case the 'text' variable is multiline, we store each individual line in this array.
	 */
	private String[] lines;

	private Units originX;

	private Units originY;

	private String text;

	private Points widthInPoints;

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
		widthInPoints = new Points(stringSize.getWidth());
		final Rectangle2D rect = new Rectangle2D.Double(origX.getValue(), origY.getValueIn(units),
				widthInPoints.getValueIn(units), heightInPoints.getValueIn(units));
		bounds = rect;
		setShape(rect);
	}

	/**
	 * @see edu.stanford.hci.r3.core.Region#getRenderer()
	 */
	public RegionRenderer getRenderer() {
		return new TextRenderer(this);
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
	public String[] getLinesOfText() {
		return lines;
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
}
