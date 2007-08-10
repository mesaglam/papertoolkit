package papertoolkit.render.regions;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import papertoolkit.paper.regions.PolygonalRegion;
import papertoolkit.render.RegionRenderer;
import papertoolkit.units.Points;
import papertoolkit.units.Units;


/**
 * <p>
 * Renders a Polygon Region. You can customize the renderer by changing the type of stroke it uses,
 * etc.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PolygonRenderer extends RegionRenderer {

	private PolygonalRegion polyRegion;

	private Stroke stroke;

	private Color strokeColor;

	/**
	 * @param r
	 */
	public PolygonRenderer(PolygonalRegion r) {
		super(r);
		polyRegion = r;
		stroke = new BasicStroke(r.getStrokeThickness());
		strokeColor = r.getStrokeColor();
	}

	/**
	 * @see papertoolkit.render.RegionRenderer#renderToG2D(java.awt.Graphics2D)
	 */
	public void renderToG2D(Graphics2D g2d) {
		if (RegionRenderer.DEBUG_REGIONS) {
			super.renderToG2D(g2d);
		}

		// draw the polygon with these attributes
		g2d.setStroke(stroke);
		g2d.setColor(strokeColor);

		// get the polygon's parameters
		final double sX = polyRegion.getScaleX();
		final double sY = polyRegion.getScaleY();
		final double x = polyRegion.getOffsetX().getValue();
		final double y = polyRegion.getOffsetY().getValue();
		final Units u = polyRegion.getUnits();

		// convert the graphis transform to the Java2D standard (72 pts per inch)
		final double conv = u.getScalarMultipleToConvertTo(Points.ONE);
		final AffineTransform transform = new AffineTransform();
		// this has to be first, so we are operating in Java2D points
		transform.scale(conv, conv);

		// System.out.println("Conversion: " + conv);

		// scale the width and height of the polygon, and also the offsets =(
		transform.scale(sX, sY);

		// System.out.println("ScaleX: " + sX + " ScaleY: " + sY);

		final double tX = x / sX - x;
		final double tY = y / sY - y;

		// System.out.println("Original Offsets: " + x + " " + y);
		// System.out.println("Translations: " + tX + " " + tY);

		// this line's tricky
		// since we scaled everything (including the polygon's offsets)
		// we now need to compensate for the shrunken (or larger) offsets
		transform.translate(tX, tY);

		// get a scaled and translated shape and draw it here.
		final GeneralPath gp = new GeneralPath();
		gp.append(polyRegion.getShape().getPathIterator(transform), false);
		g2d.draw(gp);

		// System.out.println(g2d.getTransform());
	}

	/**
	 * @param stroke
	 */
	public void setStroke(Stroke s) {
		stroke = s;
	}
}
