package edu.stanford.hci.r3.util.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * <p>
 * Displays an Image in a JPanel. Different than DisplayJAI, because it does not use JAI, and it
 * allows for positioning of the image.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class BufferedImagePanel extends JPanel {

	/**
	 * 
	 */
	private BufferedImage image;

	/**
	 * 
	 */
	private Dimension size;

	/**
	 * 
	 */
	private int x = 0;

	/**
	 * 
	 */
	private int y = 0;

	/**
	 * 
	 */
	public BufferedImagePanel() {

	}

	/**
	 * @param img
	 */
	public BufferedImagePanel(BufferedImage img) {
		setImage(img);
	}

	/**
	 * @return
	 */
	public int getImageHeight() {
		return image.getHeight();
	}

	/**
	 * @return
	 */
	public int getImageWidth() {
		return image.getWidth();
	}

	/**
	 * @see javax.swing.JComponent#getMinimumSize()
	 */
	public Dimension getMinimumSize() {
		return size;
	}

	/**
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		final Graphics2D g2d = (Graphics2D) g;
		if (image != null) {
			g2d.drawImage(image, null, x, y);
		}
	}

	/**
	 * @param img
	 */
	public void setImage(BufferedImage img) {
		setImage(img, this.x, this.y);
	}

	/**
	 * @param img
	 *            setting this to null will not repaint the panel. This allows use to release the
	 *            memory right before any new setImage calls...
	 */
	public void setImage(BufferedImage img, int imgX, int imgY) {
		setOrigin(imgX, imgY);
		image = img;
		if (image == null) { // release memory
			repaint();
			return;
		}
		size = new Dimension(image.getWidth(), image.getHeight());
		// System.out.println(size);
		repaint();
	}

	/**
	 * @param img
	 */
	public void setImageCentered(BufferedImage img) {
		setImage(img, (int) ((getWidth() - img.getWidth()) / 2.0), //
				(int) ((getHeight() - img.getHeight()) / 2.0));
	}

	/**
	 * @param x
	 * @param y
	 */
	public void setOrigin(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
