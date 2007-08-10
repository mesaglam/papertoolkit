package papertoolkit.paper.regions;

import papertoolkit.paper.Region;
/**
 * XML regions are Region objects augmented with extra meta data from an XMLSheet.
 * 
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author Marcello
 */

public class XMLRegion extends Region {
	
	/**
	 * 
	 */
	private String type;

	/**
	 * Constructs a new XMLRegion
	 * 
	 * @param name
	 * @param xInches
	 * @param yInches
	 * @param wInches
	 * @param hInches
	 * @param type
	 */
	public XMLRegion(String name, double xInches, double yInches, double wInches, double hInches, String type) {
		super(name, xInches, yInches, wInches, hInches);
		this.type = type;
	}

	/**
	 * Returns the type associated with this XMLRegion
	 * @return
	 */
	public String getType() {
		return type;
	}
	
}
