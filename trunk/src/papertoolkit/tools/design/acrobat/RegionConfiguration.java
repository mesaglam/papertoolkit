package papertoolkit.tools.design.acrobat;

import java.util.ArrayList;
import java.util.List;

import papertoolkit.paper.Region;
import papertoolkit.units.Points;


/**
 * <p>
 * This class is used for communicating the region definition between the Acrobat plugin and the R3 toolkit.
 * It can also be used to serialize and unserialize regions, if you need to build your own sheet/region design
 * tool.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class RegionConfiguration {

	/**
	 * Height of the Sheet.
	 */
	private Points heightInPoints;

	private List<Region> regions = new ArrayList<Region>();

	/**
	 * Width of the sheet.
	 */
	private Points widthInPoints;

	/**
	 * 
	 */
	public RegionConfiguration() {
	}

	/**
	 * @param r
	 */
	public void addRegion(Region r) {
		regions.add(r);
	}

	/**
	 * @return
	 */
	public Points getHeight() {
		return heightInPoints;
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
	public Points getWidth() {
		return widthInPoints;
	}

	/**
	 * Specified in points, since Acrobat specifies everything in points (1/72nd of an inch).
	 * 
	 * @param h
	 */
	public void setDocumentHeight(double h) {
		heightInPoints = new Points(h);
	}

	/**
	 * Specified in points, since Acrobat specifies everything in points (1/72nd of an inch).
	 * 
	 * @param w
	 */
	public void setDocumentWidth(double w) {
		widthInPoints = new Points(w);
	}

}
