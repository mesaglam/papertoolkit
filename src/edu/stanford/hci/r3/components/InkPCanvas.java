package edu.stanford.hci.r3.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.util.LinkedList;
import java.util.List;

import edu.stanford.hci.r3.pen.ink.Ink;
import edu.stanford.hci.r3.pen.ink.InkStroke;
import edu.stanford.hci.r3.util.geometry.CatmullRomSpline;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * <p>
 * A Piccolo Canvas. Uses simple quadTo rendering for now, but allows some interactivity.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class InkPCanvas extends PCanvas {

	public static final Color DARK_THEMED_INK_COLOR = new Color(0.85f, 0.85f, .95f, 0.8f);

	public static final Color DEFAULT_INK_COLOR = new Color(0.1f, 0.1f, .1f, 0.9f);

	/**
	 * 
	 */
	private static final BasicStroke DEFAULT_STROKE = new BasicStroke(2.0f, BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_ROUND);

	private Color defaultInkColor;

	/**
	 * 
	 */
	private LinkedList<Ink> inkWell = new LinkedList<Ink>();

	private double pageOffsetX = 0;

	private double pageOffsetY = 0;

	private PNode strokesContainer;

	/**
	 * 
	 */
	public InkPCanvas() {
		setPreferredSize(new Dimension(320, 240));
		setMinimumSize(new Dimension(320, 240));
		strokesContainer = new PNode();
		getLayer().addChild(strokesContainer);
		useDefaultTheme();
	}

	/**
	 * Use the Ink object's color...
	 * 
	 * @param ink
	 */
	public void addInk(Ink ink) {
		inkWell.add(ink);
		addInkPaths(ink.getStrokes(), ink.getColor());
		getLayer().repaint();
	}

	/**
	 * @param strokes
	 * @param inkColor
	 * @deprecated
	 */
	@SuppressWarnings("unused")
	private void addInkBezierPaths(List<InkStroke> strokes, Color inkColor) {

		// Each Stroke will be One PPath (it's just more efficient this way)
		for (final InkStroke s : strokes) {

			final double[] xArr = s.getXSamples();
			final double[] yArr = s.getYSamples();

			final PPath strokePath = new PPath();
			// ink stroke style
			strokePath.setStroke(DEFAULT_STROKE);
			// the ink color
			strokePath.setStrokePaint(inkColor);
			strokePath.addAttribute("timestamp", new Long(s.getFirstTimestamp()));
			strokesContainer.addChild(strokePath);

			final int len = xArr.length;
			if (len > 0) {
				strokePath.moveTo((float) (xArr[0] + pageOffsetX), (float) (yArr[0] + pageOffsetY));
			}

			// keeps last known "good point"
			double lastGoodX = xArr[0];
			double lastGoodY = yArr[0];

			// connect the samples w/ quadratic curve segments
			int numPointsCollected = 0;
			for (int i = 0; i < len; i++) {
				final double currX = xArr[i];
				final double currY = yArr[i];

				numPointsCollected++;

				final double diffFromLastX = currX - lastGoodX;
				final double diffFromLastY = currY - lastGoodY;

				if (Math.abs(diffFromLastX) > 500 || Math.abs(diffFromLastY) > 500) {
					// too much error; eliminate totally random data...
					strokePath.lineTo((float) (lastGoodX + pageOffsetX), (float) (lastGoodY + pageOffsetY));
				} else {

					if (numPointsCollected == 2) {
						numPointsCollected = 0;

						// OK, not that much error
						strokePath.quadTo((float) (lastGoodX + pageOffsetX),
								(float) (lastGoodY + pageOffsetY), (float) (currX + pageOffsetX),
								(float) (currY + pageOffsetY));
					}

					// set the last known good point
					lastGoodX = currX;
					lastGoodY = currY;
				}
			}

			// if there's any points left, just render them
			if (numPointsCollected == 1) {
				strokePath.lineTo((float) (lastGoodX + pageOffsetX), (float) (lastGoodY + pageOffsetY));
			}

		}

		strokesContainer.repaint();
	}

	/**
	 * Internal method for adding ink paths to the strokes layer.
	 * 
	 * @param strokes
	 */
	private void addInkPaths(List<InkStroke> strokes, Color inkColor) {
		// Each Stroke will be One PPath (it's just more efficient this way)
		for (final InkStroke s : strokes) {
			final CatmullRomSpline crspline = new CatmullRomSpline();
			final double[] x = s.getXSamples();
			final double[] y = s.getYSamples();
			crspline.setPoints(x, y);

			final PPath strokePath = new PPath(crspline.getShape());

			// ink stroke style
			strokePath.setStroke(DEFAULT_STROKE);

			// the ink color
			strokePath.setStrokePaint(inkColor);
			strokePath.addAttribute("timestamp", new Long(s.getFirstTimestamp()));
			strokesContainer.addChild(strokePath);
		}
		strokesContainer.repaint();
	}

	/**
	 * Use the default color to override the ink object's color.
	 * 
	 * @param strokes
	 */
	public void addInkWithDefaultColor(Ink ink) {
		inkWell.add(ink);
		addInkPaths(ink.getStrokes(), defaultInkColor);
		getLayer().repaint();
	}

	/**
	 * Resets the Camera View.
	 */
	public void resetViewOffsetAndScale() {
		getCamera().setViewTransform(new AffineTransform());
	}

	/**
	 * @param ink
	 */
	public void setInk(Ink ink) {
		inkWell.clear();
		inkWell.add(ink);
		strokesContainer.removeAllChildren();
		addInkPaths(ink.getStrokes(), ink.getColor());
		getLayer().repaint();
	}

	/**
	 * @param scaleFactor
	 */
	public void setStrokesScale(double scaleFactor) {
		strokesContainer.setScale(scaleFactor);
	}

	/**
	 * 
	 */
	public void useDarkTheme() {
		setBackground(new Color(40, 40, 40));
		defaultInkColor = DARK_THEMED_INK_COLOR;
	}

	public void useDefaultTheme() {
		setBackground(new Color(240, 240, 240));
		defaultInkColor = DEFAULT_INK_COLOR;
	}
}
