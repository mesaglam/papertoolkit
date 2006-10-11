package edu.stanford.hci.r3.util.components.ribbons;

import java.awt.Color;
import java.awt.Font;

/**
 * <p>
 * Constants for the Ribbon-styled toolbar
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * @created Feb 18, 2006
 */
public interface RibbonConstants {

	public static final Font CATEGORY_LABEL_FONT = new Font("Tahoma", Font.PLAIN, 13);

	/**
	 * Heading of the tool panels.
	 */
	public static final Color HEADER_COLOR = new Color(167, 177, 202);

	public static final Font HEADER_FONT = new Font("Tahoma", Font.PLAIN, 10);

	public static final int HEADER_HEIGHT = 18;

	public static final Color NOT_SELECTED_COLOR = new Color(183, 188, 189);

	public static final Color PANEL_BEGIN_COLOR = new Color(207, 213, 225);

	public static final Color PANEL_END_COLOR = new Color(193, 200, 216);

	public static final Color SELECTED_COLOR = new Color(203, 215, 231);

	public static final Color STRIP_DARK = new Color(180, 183, 202);

	public static final Color STRIP_LIGHT = new Color(219, 223, 234);
}
