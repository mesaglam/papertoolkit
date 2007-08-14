package papertoolkit.printing.types;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

import papertoolkit.printing.Printer;
import papertoolkit.util.DebugUtils;

/**
 * <p>
 * Enables you to print Postscript files.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PostscriptPrinter extends Printer {

	/**
	 * @param serv
	 */
	public PostscriptPrinter(PrintService serv) {
		super(serv);
	}

	/**
	 * Sends the designated PSFile to the Default Printer.
	 * 
	 * @param psFile
	 * 
	 * @todo Does this work with PDF files?
	 * @note This seems slow for some reason. ServiceUI takes a while to display the UI.
	 */
	public static void printPostscriptFileToDefaultPrinter(File psFile) {
		final PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		final DocFlavor flavor = DocFlavor.INPUT_STREAM.POSTSCRIPT;
		// DebugUtils.println("Looking up Services");
		final PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
		// DebugUtils.println("Looking up Default Service");
		final PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
		// DebugUtils.println("Starting Printing Service UI");
		final PrintService service = ServiceUI.printDialog(null, 200, 200, printService, defaultService,
				flavor, pras);
		if (service != null) {
			// DebugUtils.println("Creating Print Job");
			final DocPrintJob job = service.createPrintJob();
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(psFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			final DocAttributeSet das = new HashDocAttributeSet();
			final Doc doc = new SimpleDoc(fis, flavor, das);

			try {
				// DebugUtils.println("Printing");
				job.print(doc, pras);
			} catch (PrintException e) {
				e.printStackTrace();
			}
		}
	}
}
