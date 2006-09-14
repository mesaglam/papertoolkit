package edu.stanford.hci.r3.printing;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javax.print.PrintServiceLookup;

/**
 * <p>Allows you to query and work with the system's Printers.</p>
 * <p><span class="BSDLicense">
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.
 * </span></p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Printers {

	/**
	 * @return
	 */
	public static Printer getDefaultPrinter() {
		return new Printer(PrintServiceLookup.lookupDefaultPrintService());
	}

	/**
	 * @param f
	 *            the file to print, using the system's default settings.
	 */
	public static void print(File f) {
		if (!Desktop.isDesktopSupported()
				|| !Desktop.getDesktop().isSupported(Desktop.Action.PRINT)) {
			System.err.println("Printers: Cannot print the file, because "
					+ "Java 1.6 Desktop printing is not supported.");
			return;
		}

		// use the system's default printing mechanism
		try {
			Desktop.getDesktop().print(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
