package edu.stanford.hci.r3.printing;

import java.awt.print.PageFormat;

import javax.print.PrintService;

import edu.stanford.hci.r3.units.Points;
import edu.stanford.hci.r3.util.ArrayUtils;

/**
 * <p>Represents a printer, and enables you to control it.</p>
 * <p><span class="BSDLicense">
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.
 * </span></p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Printer {

	/**
	 * @param pageFormat
	 */
	public static void displayPageFormat(PageFormat pageFormat) {
		System.out.println("Page Format {");
		System.out.println("\tPage Width: " + new Points(pageFormat.getWidth()).toInches()
				+ " Height: " + new Points(pageFormat.getHeight()).toInches());
		System.out.println("\tImageable X: " + new Points(pageFormat.getImageableX()).toInches()
				+ " Y: " + new Points(pageFormat.getImageableY()).toInches());
		System.out.println("\tImageable W: "
				+ new Points(pageFormat.getImageableWidth()).toInches() + " H: "
				+ new Points(pageFormat.getImageableHeight()).toInches());
		System.out.print("\tMatrix: ");
		ArrayUtils.printArray(pageFormat.getMatrix());
		System.out.println("} End Page Format");
		System.out.flush();
	}

	private PrintService service;

	private boolean showPageSetupDialog = false;

	private boolean showPrintPreferencesDialog = false;

	/**
	 * Creates a Printer that points to the default print service.
	 * 
	 * @param serv
	 */
	public Printer(PrintService serv) {
		service = serv;
	}

	/**
	 * @return the dots per inch (resolution) of this printer.
	 */
	public double getDPI() {
		// TODO Get real DPI
		System.err.println("Printer :: getDPI currently hardcoded to 600 DPI");
		return 600.0;
	}

}
