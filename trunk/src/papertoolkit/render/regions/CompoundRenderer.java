package papertoolkit.render.regions;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Set;

import papertoolkit.paper.Region;
import papertoolkit.paper.regions.CompoundRegion;
import papertoolkit.render.RegionRenderer;
import papertoolkit.units.coordinates.Coordinates;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.MathUtils;
import papertoolkit.util.graphics.GraphicsUtils;

/**
 * <p>
 * Allows us to build up more complicated regions. Think of it as a Swing JComponent with children.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class CompoundRenderer extends RegionRenderer {

	private CompoundRegion compoundRegion;

	/**
	 * @param cr
	 */
	public CompoundRenderer(CompoundRegion cr) {
		super(cr);
		compoundRegion = cr;
	}

	/**
	 * @see papertoolkit.render.RegionRenderer#renderToG2D(java.awt.Graphics2D)
	 */
	public void renderToG2D(Graphics2D g2d) {
		g2d.setRenderingHints(GraphicsUtils.getBestRenderingHints());
		final double originXPts = compoundRegion.getOriginX().getValueInPoints();
		final double originYPts = compoundRegion.getOriginY().getValueInPoints();

		if (RegionRenderer.DEBUG_REGIONS) {
			super.renderToG2D(g2d);
			g2d.drawOval(MathUtils.rint(originXPts - 3), MathUtils.rint(originYPts - 3), 7, 7);
		}

		final AffineTransform oldTransform = new AffineTransform(g2d.getTransform());
		// offset by the origin
		final AffineTransform originTransform = new AffineTransform(g2d.getTransform());
		originTransform.translate(originXPts, originYPts);

		System.out.println("Rendering Compound Region");
		Set<Region> children = compoundRegion.getChildren();
		for (Region child : children) {
			g2d.setTransform(originTransform); // start from the origin

			// move to the correct offset in Points
			Coordinates childOffset = compoundRegion.getChildOffset(child);
			final double xOffsetPts = childOffset.getX().getValueInPoints();
			final double yOffsetPts = childOffset.getY().getValueInPoints();
			// DebugUtils.println("Rendering Child Region: [" + child.getName()
			// + "] with Child Offset: " + childOffset);
			final AffineTransform translated = g2d.getTransform();
			translated.translate(xOffsetPts, yOffsetPts);
			g2d.setTransform(translated); // move to the correct offset
			System.out.println(g2d.getTransform());
			final RegionRenderer renderer = child.getRenderer();
			renderer.renderToG2D(g2d);
		}

		g2d.setTransform(oldTransform);
	}
}
