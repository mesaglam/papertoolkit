package edu.stanford.hci.r3.paper.regions;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.render.RegionRenderer;
import edu.stanford.hci.r3.render.regions.CompoundRenderer;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.units.coordinates.Coordinates;

/**
 * <p>
 * Put more than one region together.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class CompoundRegion extends Region {

	/**
	 * Regions and their offsets from this compound region's top left corner.
	 */
	private Map<Region, Coordinates> regionsAndRelativeLocations = new HashMap<Region, Coordinates>();

	/**
	 * The origin specified by the call to the constructor.
	 */
	private Units x;

	private Units y;

	/**
	 * 
	 */
	public CompoundRegion(String name, Units xOrigin, Units yOrigin) {
		// it is dimensionless until we calculate the actual size
		super(name, xOrigin, yOrigin, new Inches(0), new Inches(0));

		// since it is compound, we need to KEEP these origins aroudn for later calculations
		x = xOrigin;
		y = yOrigin.getUnitsObjectOfSameLengthIn(x);
	}

	/**
	 * A compound region is made up of a bunch of regions and their coordinates relative to the
	 * origin of the compound region.
	 * 
	 * TODO: Should renderers of this class respect the master scaling factor???
	 * 
	 * @param childRegion
	 *            a child region
	 * @param relativeCoord
	 */
	public void addChild(Region childRegion, Coordinates relativeCoord) {
		regionsAndRelativeLocations.put(childRegion, relativeCoord);

		// get the shape
		final Rectangle2D myBounds = (Rectangle2D) getShape();
		final Units myUnits = getUnits();

		// update the coordinates by taking the union of the current bounds with the
		// bounds of the child (translated by relativeCoords)
		final Rectangle2D childBounds = childRegion.getShape().getBounds2D();
		final Units childUnits = childRegion.getUnits();

		// change the child's bounds into bounds that can be interpreted in OUR units
		final double c = childUnits.getConversionTo(myUnits);
		// DebugUtils.println("Conversion is: " + c);
		final Rectangle2D childBoundsInOurUnits = new Rectangle2D.Double( //
				childBounds.getX() * c, childBounds.getY() * c, //
				childBounds.getWidth() * childRegion.getScaleX() * c, //
				childBounds.getHeight() * childRegion.getScaleY() * c);

		// the origin of the child is myOrigin + childOrigin + childOffset
		final double childXTranslated = x.getValue() + childBoundsInOurUnits.getX()
				+ relativeCoord.getX().getValueIn(myUnits);
		final double childYTranslated = y.getValue() + childBoundsInOurUnits.getY()
				+ relativeCoord.getY().getValueIn(myUnits);

		final Rectangle2D childBoundsTranslated = new Rectangle2D.Double( //
				childXTranslated, childYTranslated, //
				childBoundsInOurUnits.getWidth(), //
				childBoundsInOurUnits.getHeight());

		// set the shape to be the union of the two rectangles
		// the catch here is that if you set the origin at 0,0 and you add a small rectangle
		// way out there, the final bounds will be huge, because it is RELATIVE to the original
		// origin!!!! You have been warned.
		final Rectangle2D unionOfMyBoundsAndChildBounds = myBounds
				.createUnion(childBoundsTranslated);
		setShape(unionOfMyBoundsAndChildBounds);
	}

	/**
	 * @param child
	 * @return
	 */
	public Coordinates getChildOffset(Region child) {
		return regionsAndRelativeLocations.get(child);
	}

	/**
	 * @return
	 */
	public Set<Region> getChildren() {
		return regionsAndRelativeLocations.keySet();
	}

	/**
	 * @see edu.stanford.hci.r3.paper.Region#getRenderer()
	 */
	public RegionRenderer getRenderer() {
		return new CompoundRenderer(this);
	}

	/**
	 * @see edu.stanford.hci.r3.paper.Region#toString()
	 */
	public String toString() {
		final Set<Region> children = regionsAndRelativeLocations.keySet();
		final StringBuilder childNames = new StringBuilder();
		int i = 0;
		for (Region r : children) {
			childNames.append(r.getName());
			if (i < children.size() - 1) {
				childNames.append(", ");
			}
			i++;
		}
		return "Compound Region {" + childNames.toString() + "} at Bounds: [x="
				+ getOriginX().getValue() + " y=" + getOriginY().getValue() + " w="
				+ getShape().getBounds2D().getWidth() + " h="
				+ getShape().getBounds2D().getHeight() + "] in " + referenceUnits.getUnitName();
	}
}
