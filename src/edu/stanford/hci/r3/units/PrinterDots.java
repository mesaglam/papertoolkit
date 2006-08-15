/**
 * 
 */
package edu.stanford.hci.r3.units;

import edu.stanford.hci.r3.printing.Printer;
import edu.stanford.hci.r3.printing.Printers;


/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PrinterDots extends Units {

	private Printer printer;

	public PrinterDots(Printer p) {
		printer = p;
	}

	/**
	 * Create it for the current printer.
	 */
	public PrinterDots() {
		printer = Printers.getDefaultPrinter();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.stanford.hci.diamondsedge.printing.api.units.Units#getNumberOfUnitsInOneInch()
	 */
	@Override
	public double getNumberOfUnitsInOneInch() {
		return printer.getDPI();
	}
}
