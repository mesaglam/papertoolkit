package edu.stanford.hci.r3.util.components.ribbons;

import static edu.stanford.hci.r3.util.components.ribbons.RibbonConstants.HEADER_COLOR;
import static edu.stanford.hci.r3.util.components.ribbons.RibbonConstants.HEADER_FONT;
import static edu.stanford.hci.r3.util.components.ribbons.RibbonConstants.HEADER_HEIGHT;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import edu.stanford.hci.r3.util.graphics.GraphicsUtils;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * @created Feb 16, 2006
 */
public class RibbonPanelHeader extends JPanel {

	/**
	 * 
	 */
	private String name;

	/**
	 * @param displayName
	 */
	RibbonPanelHeader(String displayName) {
		name = displayName;
		setMinimumSize(new Dimension(getWidth(), HEADER_HEIGHT));
		setMaximumSize(new Dimension(getWidth(), HEADER_HEIGHT));
		setPreferredSize(new Dimension(getWidth(), HEADER_HEIGHT));
		setOpaque(false);
	}

	/**
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		final Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHints(GraphicsUtils.getBestRenderingHints());

		g2d.setColor(HEADER_COLOR);
		g2d.fillRoundRect(0, -14, getWidth(), getHeight()+14, 14, 14);

		g2d.setFont(HEADER_FONT);
		Rectangle2D stringBounds = g2d.getFontMetrics().getStringBounds(name, g2d);
		g2d.setColor(Color.DARK_GRAY);
		g2d.drawString(name, 6, (int) (1 + stringBounds.getHeight()));
		g2d.setColor(Color.WHITE);
		g2d.drawString(name, 5, (int) (stringBounds.getHeight()));
	}

}
