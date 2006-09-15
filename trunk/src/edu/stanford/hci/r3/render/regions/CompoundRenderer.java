package edu.stanford.hci.r3.render.regions;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Set;

import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.regions.CompoundRegion;
import edu.stanford.hci.r3.render.RegionRenderer;
import edu.stanford.hci.r3.units.coordinates.Coordinates;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.MathUtils;
import edu.stanford.hci.r3.util.graphics.GraphicsUtils;

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
	 * @see edu.stanford.hci.r3.render.RegionRenderer#renderToG2D(java.awt.Graphics2D)
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
		g2d.getTransform().translate(originXPts, originYPts);
		final AffineTransform originTransform = new AffineTransform(g2d.getTransform());
		System.out.println(originTransform);

		System.out.println("Rendering Compound Region");
		Set<Region> children = compoundRegion.getChildren();
		for (Region child : children) {
			// move to the correct offset in Points
			Coordinates childOffset = compoundRegion.getChildOffset(child);
			final double xOffsetPts = childOffset.getX().getValueInPoints();
			final double yOffsetPts = childOffset.getY().getValueInPoints();
			DebugUtils.println("Rendering Child Region: [" + child.getName()
					+ "] with Child Offset: " + childOffset);
			g2d.getTransform().translate(xOffsetPts, yOffsetPts); // push a transform matrix
			System.out.println(g2d.getTransform());
			final RegionRenderer renderer = child.getRenderer();
			renderer.renderToG2D(g2d);
			g2d.setTransform(originTransform); // pop the transform matrix
		}

		g2d.setTransform(oldTransform);
	}
}
