package edu.stanford.hci.r3.tools.design.swing;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import edu.stanford.hci.r3.paper.Region;

/**
 * 
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author Marcello
 *
 */
public class RegionComponent extends JComponent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8845814285246446932L;
	private Region region;

	public RegionComponent(Region r) {
		this.region = r;
		setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(120,120,120)));
	}
	
	/**
	 * Returns the region.
	 * @return
	 */
	public Region getRegion() {
		return region;
	}

}