package papertoolkit.printing;

import java.awt.Graphics2D;
import java.awt.print.PageFormat;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> [ronyeh(AT)cs.stanford.edu]
 * @created Mar 8, 2006
 * 
 * Utilities for manipulating and working with the PrinterGraphics object passed into print methods.
 */
public class PrinterGraphicsUtils {

	/**
	 * This function is not idempotent. Call it twice, and it will translate more and more...
	 * 
	 * @param g2d
	 * @param pageFormat
	 * @created Mar 8, 2006
	 * @author Ron Yeh
	 */
	public static void translateToTopLeftMargin(Graphics2D g2d, PageFormat pageFormat) {
		g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
	}
	
	
}
