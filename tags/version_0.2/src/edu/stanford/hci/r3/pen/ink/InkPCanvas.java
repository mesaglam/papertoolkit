package edu.stanford.hci.r3.pen.ink;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * <p>
 * A Piccolo Canvas.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class InkPCanvas extends PCanvas {

	/**
	 * Probably shouldn't be off white!
	 */
	public static final Color DEFAULT_INK_COLOR = new Color(0.85f, 0.85f, .95f, 0.8f);

	/**
	 * 
	 */
	private static final BasicStroke DEFAULT_STROKE = new BasicStroke(2.0f, BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_ROUND);

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
		strokesContainer = new PNode();
		getLayer().addChild(strokesContainer);
		strokesContainer.scale(0.273);
	}

	/**
	 * @param ink
	 */
	public void addInk(Ink ink) {
		inkWell.add(ink);

		addInkPaths(ink.getStrokes(), ink.getColor());

		getLayer().repaint();
	}

	/**
	 * @param strokes
	 */
	private void addInkPaths(List<InkStroke> strokes, Color inkColor) {
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
					strokePath.lineTo((float) (lastGoodX + pageOffsetX),
							(float) (lastGoodY + pageOffsetY));
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
}
