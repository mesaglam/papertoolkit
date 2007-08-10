package papertoolkit.printing.types;

import javax.print.PrintService;

import papertoolkit.printing.Printer;


/**
 * <p>Printing through Java2D. To be implemented.</p>
 * <p><span class="BSDLicense">
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.
 * </span></p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Java2DPrinter extends Printer {

	public Java2DPrinter(PrintService serv) {
		super(serv);
	}
}
