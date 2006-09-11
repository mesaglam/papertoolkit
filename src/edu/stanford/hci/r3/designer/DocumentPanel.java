package edu.stanford.hci.r3.designer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JPanel;

import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.util.graphics.GraphicsUtils;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * @created Feb 26, 2006
 */
public class DocumentPanel extends JPanel {

	/**
	 * Gradient Top.
	 */
	private static final Color BG_BEGIN_COLOR = new Color(114, 121, 139);

	/**
	 * Gradient Bottom.
	 */
	private static final Color BG_END_COLOR = new Color(167, 177, 202);

	/**
	 * Drop shadow for the (on screen) rendered paper
	 */
	public static final int DROP_SHADOW_DISTANCE = 4;

	/**
	 * Color of the paper's drop shadow.
	 */
	public static final Color SHADOW_COLOR = new Color(0, 0, 0, 99);

	/**
	 * extra space to the left AND right of the document (pixels)
	 */
	private int paddingHorizontal = 20;

	/**
	 * above AND below the document (screen pixels)
	 */
	private int paddingVertical = 25;

	private Sheet sheet;

	public DocumentPanel(Sheet s) {
		sheet = s;
	}

	/**
	 * @param document
	 * @created Feb, 2006
	 * @author Ron Yeh
	 */
	public void computeAndSetDocumentBounds() {

		// compute the document panel's visible bounds
		final int documentPanelWidth = getWidth();
		final int documentPanelHeight = getHeight();

		final int documentWidthDisplayed = getDisplayedDocumentWidth();
		final int documentHeightDisplayed = getDisplayedDocumentHeight();

		// pad to the left, right, top, and bottom w/ screen pixels
		final int minPanelWidth = (getDefaultHorizontalScreenPadding() + documentWidthDisplayed);
		final int minPanelHeight = (getDefaultVerticalScreenPadding() + documentHeightDisplayed);

		// take the current size of this panel, subtract what we think the min size is..., and
		// divide by two. If the document panel is bigger than we need it to be, then we have
		// computed the extra slack space on the left, right, top, and bottom. If the document panel
		// is smaller than our minimum size, then these values can go negative...
		final int slackWidth = (documentPanelWidth - minPanelWidth) / 2;
		final int slackHeight = (documentPanelHeight - minPanelHeight) / 2;

		// if the second term goes negative, we will choose zero (flush top, left)
		final int topLeftOfDisplayedPaperX = Math.max(0, slackWidth + paddingHorizontal);
		final int topLeftOfDisplayedPaperY = Math.max(0, slackHeight + paddingVertical);

	}

	/**
	 * @param g2d
	 * 
	 * @created Feb 16, 2006
	 * @author Ron Yeh
	 */
	private void drawGradientBackground(Graphics2D g2d) {
		final GradientPaint gradient = new GradientPaint(0, 0, BG_BEGIN_COLOR, 0, getHeight(),
				BG_END_COLOR, false);
		g2d.setPaint(gradient);
		g2d.fillRect(0, 0, getWidth(), getHeight());
	}

	/**
	 * @return
	 * @created Feb 26, 2006
	 * @author Ron Yeh
	 */
	public int getDefaultHorizontalScreenPadding() {
		return 2 * paddingHorizontal;
	}

	/**
	 * @return number of pixels to pad the document vertically.
	 * @created Feb 26, 2006
	 * @author Ron Yeh
	 */
	public int getDefaultVerticalScreenPadding() {
		return 2 * paddingVertical;
	}

	/**
	 * @return how tall it should be on my physical screen, given zoom.
	 * @created Feb 28, 2006
	 * @author Ron Yeh
	 */
	public int getDisplayedDocumentHeight() {
		return 768;
	}

	/**
	 * @return how wide it should be on my physical screen, accounting for zoom!
	 * @created Feb 28, 2006
	 * @author Ron Yeh
	 */
	public int getDisplayedDocumentWidth() {
		return 1024;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
		return new Dimension(getDisplayedDocumentWidth() + getDefaultHorizontalScreenPadding(),
				getDisplayedDocumentHeight() + getDefaultVerticalScreenPadding());
	}

	/**
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 * 
	 * The approach we take is to draw all the background materials, and then let the Document paint
	 * itself. This enables the Document to be placed in a full screen window, or some other cool
	 * layout.
	 */
	protected void paintComponent(Graphics g) {

		// do whatever JPanel/JComponent likes to do...
		super.paintComponent(g);

		final Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHints(GraphicsUtils.getBestRenderingHints());

		// smooth gradient
		drawGradientBackground(g2d);

		// the dark drop shadow
		g2d.setColor(SHADOW_COLOR);
		final Rectangle docBounds = new Rectangle(0, 0, 1024, 768);
		g2d.fillRect((int) docBounds.getX() + DROP_SHADOW_DISTANCE, (int) docBounds.getY()
				+ DROP_SHADOW_DISTANCE, docBounds.width, docBounds.height);

		// System.out.println("DesignerDocumentPanel:: " + document.getClass());
		// System.out.println("DesignerDocumentPanel:: Bounds: "+ getBounds());//documentBounds);
		// System.out.println("DesignerDocumentPanel:: Clip: "+ g.getClip());

	}

}
