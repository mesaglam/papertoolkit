package edu.stanford.hci.r3.pattern.coordinates.conversion;

import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.units.Percentage;
import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.units.coordinates.PercentageCoordinates;
import edu.stanford.hci.r3.units.coordinates.StreamedPatternCoordinates;

/**
 * <p>
 * This coordinate converter will automatically move to wherever the anchor point is set, and will take on the
 * width and height of its associated region. The anchor will be set to the first sample that comes in, if it
 * is not otherwise set. Thus, this pattern coordinate converter can be easily changed at runtime.
 * 
 * Be careful if you have more than one of these in your paper application, as the first one encountered may
 * consume the event before the other one sees if (if the two regions somehow become "overlapped"). Also, if
 * you never set your anchor point, this region may opportunistically steal pen input from another region on
 * your sheet, if it is encountered first by the EventEngine.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class FlexiblePatternCoordinateConverter implements PatternCoordinateConverter {

	private StreamedPatternCoordinates anchor;

	private double anchorXVal;

	private double anchorYVal;

	private double bottomMostBorder;

	private final Units height;

	private double heightInDots;

	private Region region;

	private double rightMostBorder;

	private final Units width;

	private double widthInDots;

	/**
	 * Only create one per application. This is most useful for debugging... =)
	 * 
	 * @param floatingRegion
	 */
	public FlexiblePatternCoordinateConverter(Region floatingRegion) {
		region = floatingRegion;
		width = region.getWidth();
		height = region.getHeight();
		widthInDots = width.getValueInPatternDots();
		heightInDots = height.getValueInPatternDots();
	}

	/**
	 * 
	 * @see edu.stanford.hci.r3.pattern.coordinates.conversion.PatternCoordinateConverter#contains(edu.stanford.hci.r3.units.coordinates.StreamedPatternCoordinates)
	 */
	public boolean contains(StreamedPatternCoordinates coord) {
		if (anchor == null) {
			setAnchor(coord);
		}
		final double xval = coord.getXVal();
		final double yval = coord.getYVal();
		return xval >= anchorXVal && //
				xval < rightMostBorder && //
				yval >= anchorYVal && //
				yval < bottomMostBorder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.stanford.hci.r3.pattern.coordinates.conversion.PatternCoordinateConverter#getArea()
	 */
	@Override
	public double getArea() {
		return widthInDots * heightInDots;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.stanford.hci.r3.pattern.coordinates.conversion.PatternCoordinateConverter#getRegionName()
	 */
	public String getRegionName() {
		return region.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.stanford.hci.r3.pattern.coordinates.conversion.PatternCoordinateConverter#getRelativeLocation(edu.stanford.hci.r3.units.coordinates.StreamedPatternCoordinates)
	 */
	public PercentageCoordinates getRelativeLocation(StreamedPatternCoordinates coord) {
		if (anchor == null) {
			setAnchor(coord);
		}
		final double xval = coord.getXVal();
		final double yval = coord.getYVal();

		final double pctX = (xval - anchorXVal) / widthInDots * 100;
		final double pctY = (yval - anchorYVal) / heightInDots * 100;

		return new PercentageCoordinates( // 
				new Percentage(pctX, width), // fraction of width
				new Percentage(pctY, height)); // fraction of height
	}

	/**
	 * We need an upper left corner... The anchor will be the minX,minY of the region.
	 * 
	 * @param coord
	 */
	private void setAnchor(StreamedPatternCoordinates coord) {
		anchor = coord;
		anchorXVal = anchor.getXVal();
		anchorYVal = anchor.getYVal();
		rightMostBorder = anchorXVal + widthInDots;
		bottomMostBorder = anchorYVal + heightInDots;
	}
}
