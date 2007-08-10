package papertoolkit.paper.regions;

import papertoolkit.paper.Region;
import papertoolkit.units.Units;

/**
 * <p>
 * A region with custom graphics. Provide it your own Java2D commands to render.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class GraphicsRegion extends Region {

	public GraphicsRegion(String name, Units x, Units y, Units w, Units h) {
		super(name, x, y, w, h);
	}
}
