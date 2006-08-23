package edu.stanford.hci.r3.render.types;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import edu.stanford.hci.r3.core.regions.PolygonalRegion;
import edu.stanford.hci.r3.render.RegionRenderer;
import edu.stanford.hci.r3.units.Points;
import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.util.graphics.GraphicsUtils;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PolygonRenderer extends RegionRenderer {

	private PolygonalRegion polyRegion;

	private BasicStroke stroke = new BasicStroke(1);

	private Color strokeColor = Color.RED;

	public PolygonRenderer(PolygonalRegion r) {
		super(r);
		polyRegion = r;
	}

	/**
	 * @see edu.stanford.hci.r3.render.RegionRenderer#renderToG2D(java.awt.Graphics2D)
	 */
	public void renderToG2D(Graphics2D g2d) {
		if (RegionRenderer.DEBUG_REGIONS) {
			super.renderToG2D(g2d);
		}

		final Units units = polyRegion.getUnits();
		final double conv = units.getConversionTo(Points.ONE);

		g2d.setStroke(stroke);
		g2d.setColor(strokeColor);
		AffineTransform transform = new AffineTransform();
		transform.scale(conv, conv);
		double s = polyRegion.getScaleX();
		double y = polyRegion.getOffsetY().getValue();
		double x = polyRegion.getOffsetX().getValue();
		transform.scale(s, polyRegion.getScaleY());
		transform.translate(x / s - x, y / polyRegion.getScaleY() - y);
		final GeneralPath gp = new GeneralPath();
		gp.append(polyRegion.getShape().getPathIterator(transform), false);
		g2d.draw(gp);
	}
}
