package edu.stanford.hci.r3.render.types;

import edu.stanford.hci.r3.core.regions.TextRegion;
import edu.stanford.hci.r3.render.RegionRenderer;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class TextRenderer extends RegionRenderer {

	public TextRenderer(TextRegion tr) {
		region = tr;
	}
}
