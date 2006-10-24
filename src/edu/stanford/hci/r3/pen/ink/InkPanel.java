package edu.stanford.hci.r3.pen.ink;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import edu.stanford.hci.r3.util.graphics.GraphicsUtils;

/**
 * <p>
 * Renders Ink in a JPanel using a very simple lineTo(...) method. You can swap in better renderers depending
 * on how quickly or slowly you want the ink to be rendered.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class InkPanel extends JPanel {

	/**
	 * 
	 */
	private List<Ink> inkWell = Collections.synchronizedList(new LinkedList<Ink>());

	/**
	 * 
	 */
	public InkPanel() {
		setBackground(Color.WHITE);
	}

	/**
	 * @param ink
	 */
	public void addInk(Ink ink) {
		inkWell.add(ink);
		repaint();
	}

	/**
	 * 
	 */
	public void clear() {
		inkWell.clear();
		repaint();
	}

	/**
	 * Redraw the
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		final Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHints(GraphicsUtils.getBestRenderingHints());

		// the drawing of ink is "atomic" with respect to
		// adding and removing from the inkWell
		synchronized (inkWell) {
			for (Ink ink : inkWell) {
				Color color = ink.getColor();
				g2d.setColor(color);
				List<InkStroke> strokes = ink.getStrokes();
				for (InkStroke stroke : strokes) {
					final Path2D.Double path = new Path2D.Double();
					final double[] x = stroke.getXSamples();
					final double[] y = stroke.getYSamples();
					path.moveTo(x[0], y[0]);
					for (int i = 1; i < stroke.getNumSamples(); i++) {
						path.lineTo(x[i], y[i]);
					}
					g2d.draw(path);
				}
			}
		}
	}

	/**
	 * Used to remove the most-recently added ink object.
	 */
	public void removeLastBatchOfInk() {
		synchronized (inkWell) {
			if (inkWell.size() > 0) {
				inkWell.remove(inkWell.size() - 1);
				repaint();
			}
		}
	}
}
