package edu.stanford.hci.r3.paper.regions;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.render.RegionRenderer;
import edu.stanford.hci.r3.render.regions.TextRenderer;
import edu.stanford.hci.r3.units.Points;
import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.util.StringUtils;

/**
 * <p>
 * Represents some text that can be drawn on a page.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class TextRegion extends Region {

	/**
	 * 
	 */
	private Rectangle2D bounds;

	/**
	 * 
	 */
	private Color color = new Color(100, 100, 100, 128);

	/**
	 * 
	 */
	private Font font;

	/**
	 * 
	 */
	private Points heightInPoints;

	/**
	 * In case the 'text' variable is multiline, we store each individual line in this array.
	 */
	private String[] lines;

	/**
	 * 
	 */
	private Units originX;

	/**
	 * 
	 */
	private Units originY;

	/**
	 * 
	 */
	private String text;

	private Points widthInPoints;

	/**
	 * True if the text should be automatically line wrapped to the width
	 * of the region.  If this is true, the lines will not go beyond the region
	 * in either dimension.
	 */
	private boolean isLineWrapped = false;
	
	/**
	 * Maximum number of lines to typeset.  If <= 0, all lines will be
	 * typeset (up to the size of the region)
	 */
	private int maxLines = -1;
	
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
	public TextRegion(String name, String theText, Font theFont, Units origX, Units origY) {
		super(name, origX);
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
		final Rectangle2D rect = new Rectangle2D.Double(origX.getValue(), origY
				.getValueIn(referenceUnits), widthInPoints.getValueIn(referenceUnits),
				heightInPoints.getValueIn(referenceUnits));
		bounds = rect;
		setShape(rect);
	}

	/**
	 * @param name
	 * @param theText
	 * @param theFont
	 * @param origX
	 * @param origY
	 * @param width
	 *            override the text's actual width with this value
	 * @param height
	 *            override the text's actual height with this value
	 */
	public TextRegion(String name, String theText, Font theFont, Units origX, Units origY,
			Units width, Units height) {
		super(name, origX);
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
		final Rectangle2D rect = new Rectangle2D.Double(origX.getValue(), origY
				.getValueIn(referenceUnits), width.getValueIn(referenceUnits), height
				.getValueIn(referenceUnits));
		bounds = rect;
		setShape(rect);
	}

	/**
	 * @return
	 */
	public Color getColor() {
		return color;
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
	 * @see edu.stanford.hci.r3.paper.Region#getRenderer()
	 */
	public RegionRenderer getRenderer() {
		return new TextRenderer(this);
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
	 * @param c
	 */
	public void setColor(Color c) {
		color = c;
	}

	/**
	 * @param b whether automatic line wrapping should occur.  If ture, the text will be
	 * constrained to the region boundaries.
	 */
	public void setLineWrapped(boolean b) {
		isLineWrapped = b;
	}
	
	/**
	 * @return whether automatic line wrapping should occur.  If true, the text will be
	 * constrained to the region boundaries.
	 */
	public boolean isLineWrapped() {
		return isLineWrapped;
	}
	
	/**
	 * @param i the maximum number of lies to set.  If <= 0, all lines will be set.
	 */
	public void setMaxLines(int i) {
		maxLines = i;
	}
	
	/**
	 * @return the maximum number of lines to be set.  If <= 0, all lines will be set.
	 */
	public int getMaxLines() {
		return maxLines;
	}
	
	/**
	 * @see edu.stanford.hci.r3.paper.Region#toString()
	 */
	public String toString() {
		return "Text: {" + text + "} " + font.getSize() + "pt " + font.getName()
				+ " at Bounds: [x=" + originX.getValue() + " y=" + originY.getValue() + " w="
				+ bounds.getWidth() + " h=" + bounds.getHeight() + "] in "
				+ referenceUnits.getUnitName();
	}
}
