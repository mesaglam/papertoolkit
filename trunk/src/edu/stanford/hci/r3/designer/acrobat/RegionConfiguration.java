package edu.stanford.hci.r3.designer.acrobat;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.units.Points;

/**
 * <p>
 * This class is used for communicating the region definition between the Acrobat plugin and the R3
 * toolkit.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class RegionConfiguration {

	private Points heightInPoints;

	private List<Region> regions = new ArrayList<Region>();

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
