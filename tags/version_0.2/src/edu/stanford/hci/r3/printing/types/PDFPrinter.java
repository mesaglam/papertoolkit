package edu.stanford.hci.r3.printing.types;

import javax.print.PrintService;

import edu.stanford.hci.r3.printing.Printer;

/**
 * <p>Print PDF Files</p>
 * <p><span class="BSDLicense">
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.
 * </span></p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PDFPrinter extends Printer {

	public PDFPrinter(PrintService serv) {
		super(serv);
	}

}
