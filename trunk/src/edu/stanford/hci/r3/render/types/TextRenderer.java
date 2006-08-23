package edu.stanford.hci.r3.render.types;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;

import edu.stanford.hci.r3.core.regions.TextRegion;
import edu.stanford.hci.r3.render.RegionRenderer;
import edu.stanford.hci.r3.units.Points;
import edu.stanford.hci.r3.units.Units;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class TextRenderer extends RegionRenderer {

	private Font font;

	private LineMetrics lineMetrics;

	private String text;

	/**
	 * @param tr
	 */
	public TextRenderer(TextRegion tr) {
		super(tr);
		region = tr;
		text = tr.getText();
		font = tr.getFont();
		lineMetrics = font.getLineMetrics(text, new FontRenderContext(null, true, true));
	}

	/**
	 * @return
	 */
	public Units getAscentInPoints() {
		return new Points(lineMetrics.getAscent());
	}

	public Points getLineHeightInPoints() {
		return new Points(lineMetrics.getHeight());
	}

	/**
	 * @param g2d
	 */
	public void renderToG2D(Graphics2D g2d) {
		if (RegionRenderer.DEBUG_REGIONS) {
			super.renderToG2D(g2d);
		}
		
		final TextRegion tr = (TextRegion) region;

		// so that we can reset it later
		final Font oldFont = g2d.getFont();
		g2d.setFont(tr.getFont());

		final double offset = getAscentInPoints().getValue();
		final double textLineHeight = getLineHeightInPoints().getValue();

		// handle multiple lines
		final String[] linesOfText = tr.getLinesOfText();
		final int xOffset = (int) Math.round(tr.getX().getValueInPoints());
		double yOffset = tr.getY().getValueInPoints() + offset;
		for (String line : linesOfText) {
			g2d.drawString(line, xOffset, (int) Math.round(yOffset));
			yOffset += textLineHeight;
		}

		// rest the font
		g2d.setFont(oldFont);
	}

}
