package edu.stanford.hci.r3.pen.ink;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import edu.stanford.hci.r3.util.graphics.GraphicsUtils;

/**
 * <p>
 * Renders Ink in a JPanel using a very simple lineTo(...) method.
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
	private LinkedList<Ink> inkWell = new LinkedList<Ink>();

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

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		final Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHints(GraphicsUtils.getBestRenderingHints());

		for (Ink ink : inkWell) {
			Color color = ink.getColor();
			g2d.setColor(color);
			List<InkStroke> strokes = ink.getStrokes();
			for (InkStroke stroke : strokes) {
				Path2D.Double path = new Path2D.Double();
				double[] x = stroke.getXSamples();
				double[] y = stroke.getYSamples();
				path.moveTo(x[0], y[0]);
				for (int i = 1; i < stroke.getNumSamples(); i++) {
					path.lineTo(x[i], y[i]);
				}
				g2d.draw(path);
			}
		}
	}

	/**
	 * 
	 */
	public void clear() {
		inkWell.clear();
		repaint();
	}

	/**
	 * 
	 */
	public void removeLastBatchOfInk() {
		if (inkWell.size() > 0) {
			inkWell.removeLast();
			repaint();
		}
	}
}
