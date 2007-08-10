package papertoolkit.paper.layout;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import papertoolkit.paper.Region;
import papertoolkit.units.Inches;
import papertoolkit.units.Units;
import papertoolkit.units.coordinates.Coordinates;


/**
 * <p>
 * Essentially, a stupid version of CompoundRegion. It is a collection of Regions that will share a
 * single offset relative to their parent sheet. They also have offsets within this RegionGroup.
 * However, you cannot put RegionGroups within RegionGroups. It's just a flat list of Regions, to
 * assist with layout.
 * 
 * Internally, everything is computed in Inches.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class RegionGroup {

	private static final Inches MY_UNITS = new Inches();

	private double maxXInches = Double.MIN_VALUE;

	private double maxYInches = Double.MIN_VALUE;

	private double minXInches = Double.MAX_VALUE;

	private double minYInches = Double.MAX_VALUE;

	private String name;

	private List<Region> regions = new ArrayList<Region>();

	/**
	 * Regions and their offsets from this region group's top left corner.
	 */
	private Map<Region, Coordinates> regionsAndRelativeLocations = new HashMap<Region, Coordinates>();

	private Units xOffset;

	private Units yOffset;

	/**
	 * 
	 */
	public RegionGroup(String rgName, Units xOrigin, Units yOrigin) {
		name = rgName;
		xOffset = xOrigin;
		yOffset = yOrigin.getUnitsObjectOfSameLengthIn(xOffset);
	}

	/**
	 * @param childRegion
	 *            a child region
	 * @param relativeCoord
	 */
	public void addRegion(Region childRegion, Coordinates relativeCoord) {
		regionsAndRelativeLocations.put(childRegion, relativeCoord);
		regions.add(childRegion);

		// update the coordinates by taking the union of the current bounds with the
		// bounds of the child (translated by relativeCoords)
		final Rectangle2D childBounds = childRegion.getShape().getBounds2D();
		final Units childUnits = childRegion.getUnits();

		// change the child's bounds into bounds that can be interpreted in OUR units
		final double c = childUnits.getScalarMultipleToConvertTo(MY_UNITS);
		// DebugUtils.println("Conversion is: " + c);
		final Rectangle2D childBoundsInOurUnits = new Rectangle2D.Double( //
				childBounds.getX() * c, childBounds.getY() * c, //
				childBounds.getWidth() * childRegion.getScaleX() * c, //
				childBounds.getHeight() * childRegion.getScaleY() * c);

		// the origin of the child RELATIVE TO THIS GROUP childOrigin + childOffset
		final double childXTranslated = childBoundsInOurUnits.getX()
				+ relativeCoord.getX().getValueIn(MY_UNITS);
		final double childYTranslated = childBoundsInOurUnits.getY()
				+ relativeCoord.getY().getValueIn(MY_UNITS);

		final Rectangle2D childBoundsTranslated = new Rectangle2D.Double( //
				childXTranslated, childYTranslated, //
				childBoundsInOurUnits.getWidth(), //
				childBoundsInOurUnits.getHeight());

		// take the union our bounds...
		maxXInches = Math.max(maxXInches, childXTranslated + childBoundsTranslated.getWidth());
		maxYInches = Math.max(maxYInches, childYTranslated + childBoundsTranslated.getHeight());
		minXInches = Math.min(minXInches, childXTranslated);
		minYInches = Math.min(minYInches, childYTranslated);
	}

	public Units getHeight() {
		return new Inches(maxYInches);
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param child
	 * @return
	 */
	public Coordinates getRegionOffset(Region child) {
		return regionsAndRelativeLocations.get(child);
	}

	/**
	 * @return
	 */
	public List<Region> getRegions() {
		return regions;
	}

	/**
	 * @return
	 */
	public Units getWidth() {
		return new Inches(maxXInches);
	}

	/**
	 * @return
	 */
	public double getXOffsetInInches() {
		return xOffset.getValueInInches();
	}

	/**
	 * @return
	 */
	public double getYOffsetInInches() {
		return yOffset.getValueInInches();
	}
}
