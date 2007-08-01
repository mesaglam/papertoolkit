package edu.stanford.hci.r3.tools.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import edu.stanford.hci.r3.pen.ink.Ink;
import edu.stanford.hci.r3.render.ink.InkRenderer;
import edu.stanford.hci.r3.util.graphics.GraphicsUtils;

/**
 * <p>
 * Renders Ink in a JPanel using catmull-rom splines. You may swap in simpler renderers if this becomes slow.
 * TODO: Also, this provides some customization that allows you to zoom in and out, pan, and align the ink
 * with imagery. =)
 * </p>
 * <p>
 * If you find that nothing appears, you may want to check whether the ink color and your panel's background
 * color are in fact the same color!
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class InkPanel extends JPanel {

	private Ink currentInk;

	/**
	 * Zoom in (> 1.0) or out (< 1.0) to the ink canvas.
	 */
	private double inkScale = 1.0;

	/**
	 * A collection of ink objects that have been added to this JPanel. Feel free to access it, if you are a
	 * subclass. Make sure to call repaint though!
	 */
	protected List<Ink> inkWell = Collections.synchronizedList(new LinkedList<Ink>());

	/**
	 * 
	 */
	private boolean invertInkColors = false;

	private double minX = Double.MAX_VALUE;

	private double minY = Double.MAX_VALUE;

	private double paddingLeft = 15;

	private double paddingTop = 25;

	/**
	 * 
	 */
	private InkRenderer renderer;

	/**
	 * Default Catmull-Rom method.
	 */
	public InkPanel() {
		this(new InkRenderer(), new Color(250, 250, 250));
	}

	/**
	 * Choose your own renderer.
	 * 
	 * @param inkRenderer
	 * @param bgColor
	 */
	public InkPanel(InkRenderer inkRenderer, Color bgColor) {
		setBackground(bgColor);
		renderer = inkRenderer;
	}

	/**
	 * Add an Ink object to the internal rendering list.
	 * 
	 * @param ink
	 */
	public void addInk(Ink ink) {
		currentInk = ink;
		inkWell.add(ink);
		minX = Math.min(minX, ink.getMinX());
		minY = Math.min(minY, ink.getMinY());
		repaint();
	}

	/**
	 * @return the newly added Ink object. Feel free to add strokes to the returned Ink object... as long as
	 *         you refresh this InkPanel occasionally, it should show up!
	 */
	public Ink addNewInk() {
		final Ink newInk = new Ink();
		addInk(newInk);
		return newInk;
	}

	/**
	 * Remove all the ink from the ink panel.
	 */
	public void clear() {
		inkWell.clear();
		minX = Double.MAX_VALUE;
		minY = Double.MAX_VALUE;
		repaint();
	}

	/**
	 * 
	 */
	public void displayInvertedInkColor() {
		invertInkColors = true;
	}

	/**
	 * @return the reference to the ink. This is NOT a copy!
	 */
	public List<Ink> getAllInk() {
		return inkWell;
	}

	/**
	 * @return
	 */
	public double getScale() {
		return inkScale;
	}

	/**
	 * Redraw the ink strokes.
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		final Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHints(GraphicsUtils.getBestRenderingHints());

		final AffineTransform transform = g2d.getTransform();
		g2d.scale(inkScale, inkScale);

		recenter(g2d);

		// the drawing of ink is "atomic" with respect to
		// adding and removing from the inkWell
		synchronized (inkWell) {
			for (Ink ink : inkWell) {
				renderer.setInk(ink);
				if (invertInkColors) {
					renderer.useInvertedInkColors();
				}
				renderer.renderToG2D(g2d);
			}
		}
		g2d.setTransform(transform);
	}

	/**
	 * @param g2d
	 */
	private void recenter(Graphics2D g2d) {
		g2d.translate(-(minX - paddingLeft), -(minY - paddingTop));
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

	/**
	 * @param theInk
	 */
	public void setAllInk(List<Ink> theInk) {
		inkWell = theInk;
		repaint();
	}

	/**
	 * @param r
	 */
	public void setRenderer(InkRenderer r) {
		renderer = r;
	}

	/**
	 * @param theScale
	 */
	public void setScale(double theScale) {
		inkScale = theScale;
	}
}
