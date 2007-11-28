/**
 * 
 */
package papertoolkit.printing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.HashMap;

import javax.print.PrintService;

import papertoolkit.units.Inches;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * Does a number of things, including determining the DPI of the wrapped print service.
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PrintServiceWrapper {

	private static final int DEFAULT_DPI = 600;

	/**
	 * Make sure there's a one to one mapping.
	 */
	private static HashMap<PrintService, PrintServiceWrapper> serviceToWrapper = new HashMap<PrintService, PrintServiceWrapper>();

	/**
	 * @param ps
	 * @return
	 */
	public static PrintServiceWrapper getPrintServiceWrapper(PrintService ps) {
		PrintServiceWrapper wrapper = serviceToWrapper.get(ps);
		if (wrapper == null) {
			wrapper = new PrintServiceWrapper(ps);
			serviceToWrapper.put(ps, wrapper);
		}
		return wrapper;
	}

	public static PrintServiceWrapper getDefaultPrintServiceWrapper() {
		return getPrintServiceWrapper(PrinterBridge.getDefaultPrintService());
	}

	/**
	 * What resolution is the printer set at?
	 */
	private int dpi = 0;

	/**
	 * The wrapped service.
	 */
	private PrintService service;

	/**
	 * @param ps
	 */
	private PrintServiceWrapper(PrintService ps) {
		service = ps;
	}

	/**
	 * @return
	 */
	private boolean detectEpson9800() {
		if (service.getName().equals(PrinterBridge.PRINTER_EPSON_9800)) {
			// dpi = 720;
			dpi = 1440;

			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return
	 * @created Feb 28, 2006
	 * @author Ron Yeh
	 */
	private boolean detectVirtualPrinter() {
		if (service.getName().equals(PrinterBridge.PRINTER_SAVE_TO_PDF)) {
			dpi = 1200;
			return true;
			// } else if (service.getName().equals(PrinterBridge.PRINTER_MS_PRINT_TO_FILE)) {
			// dpi = 300;
			// return true;
		}
		return false;
	}

	/**
	 * Retrieves the DPI of the printer by printing a fake job (empty job) and getting the scale part
	 * of the transform.
	 * 
	 * @created Feb 28, 2006
	 * @author Ron Yeh
	 */
	private void determineDPI() {
		if (detectVirtualPrinter()) {
			return;
		}

		// DPI Override
		if (detectEpson9800()) {
			return;
		}

		final PrinterJob printerJob = PrinterJob.getPrinterJob();
		try {
			printerJob.setPrintService(service);
			printerJob.setJobName("DPI Test");
		} catch (PrinterException e) {
			e.printStackTrace();
		}
		printerJob.setPrintable(new Printable() {
			public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
					throws PrinterException {
				final Graphics2D g2d = (Graphics2D) graphics;
				final double scaleAsInches = g2d.getTransform().getScaleX();
				dpi = (int) new Inches(scaleAsInches).toPoints().getValue();
				// System.out.println("DPI: " + dpi);
				return Printable.NO_SUCH_PAGE;
			}
		});
		try {
			printerJob.print();
		} catch (PrinterException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return DPI of the printer.
	 * @created Feb 28, 2006
	 * @author Ron Yeh
	 */
	public int getDPI() {
		if (dpi == 0) {
			determineDPI();
		}
		// if the dpi is still 0 (i.e. determineDPI didnt work on this printer for whatever reason,
		// return a default value
		if (dpi == 0) {
			return DEFAULT_DPI;
		}
		return dpi;
	}

	public PrintService getService() {
		return service;
	}

	/**
	 * Returns the shortname.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return service.getName();
	}

}