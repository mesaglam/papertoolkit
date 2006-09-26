package edu.stanford.hci.r3.render.regions;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.LineMetrics;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import edu.stanford.hci.r3.paper.regions.TextRegion;
import edu.stanford.hci.r3.render.RegionRenderer;
import edu.stanford.hci.r3.units.Points;

/**
 * <p>
 * Renders a Text Region.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class TextRenderer extends RegionRenderer {

	private Font font;

	/**
	 * Calculate how big the text is.
	 */
	private LineMetrics lineMetrics;

	private String text;

	/**
	 * Render the Text with this Color.
	 */
	private Color textColor;

	/**
	 * @param tr
	 */
	public TextRenderer(TextRegion tr) {
		super(tr);
		region = tr;
		text = tr.getText();
		font = tr.getFont();
		textColor = tr.getColor();
		lineMetrics = font.getLineMetrics(text, new FontRenderContext(null, true, true));
	}

	/**
	 * @return
	 */
	public Points getAscentInPoints() {
		return new Points(lineMetrics.getAscent());
	}

	/**
	 * @return
	 */
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

		g2d.setColor(textColor);
		
		if (!tr.isLineWrapped()) {
			g2d.setFont(tr.getFont());
			// System.out.println(tr.getFont());

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

		} else {  // isLineWrapped is true
			float xOffset, yOffset, maxYOffset, wrappingWidth, linebreakOffset;
			xOffset = (float) tr.getX().getValueInPoints();
			yOffset = (float) tr.getY().getValueInPoints();
			maxYOffset = yOffset + (float) tr.getHeight().getValueInPoints();
			wrappingWidth = (float) tr.getWidth().getValueInPoints();
			
			FontRenderContext frc = g2d.getFontRenderContext();
			
			final String[] linesOfText = tr.getLinesOfText();
			
			// We'll be done when any of the following occur:
			//   - We run out of text to set
			//   - Our "pen" moves below the lower bound of the region
			//   - We set the number of lines specified by maxLines (when maxLines > 0)
			boolean done = false;
			int lineCount = 0;
			linebreakOffset = 0.0f;
			for (String line : linesOfText) {
				// Can't layout an empty string (this will happen with consecutive newlines)
				// so if we have one, make it a space instead.
				if (line.length() == 0) line = " ";
				
				// Turn the string into an AttributedString, with an Attribute that is the font
				AttributedString styledText = new AttributedString(line);
				AttributedCharacterIterator styledTextIterator = styledText.getIterator();
				styledText.addAttribute(TextAttribute.FONT, tr.getFont());

				LineBreakMeasurer measurer = new LineBreakMeasurer(styledTextIterator, frc);

				while (measurer.getPosition() < styledTextIterator.getEndIndex()) {
					// Layout the next line of text
					TextLayout layout = measurer.nextLayout(wrappingWidth);
					linebreakOffset = (layout.getAscent() + layout.getDescent() + layout.getLeading())/2;
					yOffset += (layout.getAscent());
					float dx = layout.isLeftToRight() ? 0 : (wrappingWidth - layout.getAdvance());

					// Determine if the baseline of this line of text would be placed outside the region 
					if (yOffset > maxYOffset) {
						done = true;
						break;
					}

					// Draw the text
					layout.draw(g2d, xOffset + dx, yOffset);
					
					// Move the pen down to the next line
					yOffset += layout.getDescent() + layout.getLeading();
					lineCount++;
					if (tr.getMaxLines() > 0 && lineCount >= tr.getMaxLines()) {
						done = true;
						break;
					}
				}
				
				yOffset += linebreakOffset;
				
				if (done) break;
			}

		}
		
		// reset the font
		g2d.setFont(oldFont);
	}

}
