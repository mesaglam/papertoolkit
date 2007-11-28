package papertoolkit.printing;

import java.awt.Graphics2D;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.MultiDoc;
import javax.print.MultiDocPrintJob;
import javax.print.MultiDocPrintService;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.StreamPrintService;
import javax.print.StreamPrintServiceFactory;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintJobAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.DocumentName;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.PrinterName;
import javax.print.event.PrintJobEvent;
import javax.print.event.PrintJobListener;

import papertoolkit.units.Inches;
import papertoolkit.units.Points;
import papertoolkit.util.ArrayUtils;
import papertoolkit.util.SystemUtils;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * For the default printer:
 * 
 * <p>
 * <code>
 * PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
 * </code>
 * </p>
 * 
 * Using the 1.2 Printing SDK, Java2D can be printed through implementing the Printable interface.
 * However, be sure to respect the <code>Graphics2D</code> clip, for performance reasons, as the
 * print method may be called multiple times.
 * 
 * 
 * <p>
 * Available Printers: <br>
 * <code>
 * Win32PrintService Array: [ <br>
 * 	Win32 Printer : \\171.64.77.184\duotone,<br> 
 * 	Win32 Printer : \\171.64.77.184\hue, <br>
 * 	Win32 Printer : \\171.64.77.184\offset, <br>
 * 	Win32 Printer : Microsoft Office Document Image Writer,<br> 
 * 	Win32 Printer : jr-plotter, <br>
 * 	Win32 Printer : Adobe PDF]<br></code>
 * </p>
 * 
 * <p>
 * Offset Supports: <br>
 * <code>
 *    Object Array: [ <br>
 *        (DocFlavor$BYTE_ARRAY: image/gif; class="[B") <br>
 *        (DocFlavor$INPUT_STREAM: image/gif; class="java.io.InputStream") <br>
 *        (DocFlavor$URL: image/gif; class="java.net.URL") <br>
 *        (DocFlavor$BYTE_ARRAY: image/jpeg; class="[B") <br>
 *        (DocFlavor$INPUT_STREAM: image/jpeg; class="java.io.InputStream") <br>
 *        (DocFlavor$URL: image/jpeg; class="java.net.URL") <br>
 *        (DocFlavor$BYTE_ARRAY: image/png; class="[B") <br>
 *        (DocFlavor$INPUT_STREAM: image/png; class="java.io.InputStream") <br> 
 *        (DocFlavor$URL: image/png; class="java.net.URL") <br>
 *        (DocFlavor$SERVICE_FORMATTED: application/x-java-jvm-local-objectref; class="java.awt.print.Pageable") <br>
 *        (DocFlavor$SERVICE_FORMATTED: application/x-java-jvm-local-objectref; class="java.awt.print.Printable") <br>
 *        (DocFlavor$BYTE_ARRAY: application/octet-stream; class="[B") <br>
 *        (DocFlavor$URL: application/octet-stream; class="java.net.URL") <br>
 *        (DocFlavor$INPUT_STREAM: application/octet-stream; class="java.io.InputStream") <br>
 *        (DocFlavor$BYTE_ARRAY: application/postscript; class="[B") <br>
 *        (DocFlavor$INPUT_STREAM: application/postscript; class="java.io.InputStream") <br>
 *        (DocFlavor$URL: application/postscript; class="java.net.URL")] <br>
 * </code>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> ( ronyeh(AT)cs.stanford.edu )
 * @author Jonas Boli
 */
public class PrinterBridge {

	/**
	 * For the GUI to list the available printers.
	 */
	private static ArrayList<PrintServiceWrapper> availablePrinters;

	/**
	 * Make sure your Windows Printers are named as follows...
	 */
	public static final String PRINTER_EPSON_9800 = "EPSON Stylus Pro 9800";

	public static final String PRINTER_EPSON_9800_VIA_COLORBURST_RIP = "ColorBurst RIP";

	public static final String PRINTER_GFX_PLOTTER = "\\\\171.64.77.184\\duotone";

	public static final String PRINTER_HUE = "\\\\171.64.77.184\\hue";

	public static final String PRINTER_JRBP_LASER = "jr-laserjet";

	// private static final String PRINTER_MS_PRINT_TO_FILE = "Microsoft Office Document Image
	// Writer";

	public static final String PRINTER_JRBP_PLOTTER = "jr-plotter";

	public static final String PRINTER_OFFSET = "\\\\171.64.77.184\\offset";

	public static final String PRINTER_SAVE_TO_PDF = "Adobe PDF";

	/**
	 * @param printJob
	 * @created Feb 7, 2006
	 * @author Ron Yeh
	 */
	public static void displayAllPrintJobAttributes(DocPrintJob printJob) {
		final PrintJobAttributeSet attributes = printJob.getAttributes();
		final Attribute[] attributesArr = attributes.toArray();
		System.out.println(attributesArr);
		ArrayUtils.printArrayOfUnknownObjects(attributesArr);
	}

	/**
	 * @param service
	 * @created Feb 7, 2006
	 * @author Ron Yeh
	 */
	public static void displayAllPrintServiceAttributes(PrintService service) {
		final PrintServiceAttributeSet attributeSet = service.getAttributes();
		final Attribute[] attrArray = attributeSet.toArray();
		ArrayUtils.printArrayOfUnknownObjects(attrArray);
	}

	/**
	 * Lists all the Printers attached to this system.
	 * 
	 * @created Feb 6, 2006
	 * @author Ron Yeh
	 */
	public static void displayAvailablePrinters() {
		final PrintService[] services = PrinterJob.lookupPrintServices();
		ArrayUtils.printArray(services);
	}

	/**
	 * @param ps
	 * @created Feb 28, 2006
	 * @author Ron Yeh
	 */
	public static void displayDefaultPrinterTransform(PrintService ps) {
		final PrinterJob printerJob = PrinterJob.getPrinterJob();
		try {
			printerJob.setPrintService(ps);
		} catch (PrinterException e) {
			e.printStackTrace();
		}
		final PageFormat format = printerJob.defaultPage();
		printerJob.validatePage(format);
		ArrayUtils.printArray(format.getMatrix());
	}

	/**
	 * @param orientation
	 * @created Feb 28, 2006
	 * @author Ron Yeh
	 */
	public static void displayOrientation(int orientation) {
		System.out.print("Orientation is ");
		switch (orientation) {
		case PageFormat.PORTRAIT:
			System.out.println("Portrait");
			break;
		case PageFormat.LANDSCAPE:
			System.out.println("Landscape");
			break;
		case PageFormat.REVERSE_LANDSCAPE:
			System.out.println("Reverse Landscape");
			break;
		default:
			System.out.println("Unknown");
			break;
		}
	}

	/**
	 * @param pageFormat
	 */
	public static void displayPageFormat(PageFormat pageFormat) {
		System.out.println("::::: Page Format :::::");
		System.out.println("Page Width: " + new Points(pageFormat.getWidth()).toInches()
				+ " Height: " + new Points(pageFormat.getHeight()).toInches());

		System.out.println("Imageable X: " + new Points(pageFormat.getImageableX()).toInches()
				+ " Y: " + new Points(pageFormat.getImageableY()).toInches());

		System.out.println("Imageable Width: "
				+ new Points(pageFormat.getImageableWidth()).toInches() + " Height: "
				+ new Points(pageFormat.getImageableHeight()).toInches());
		System.out.print("Matrix: ");
		ArrayUtils.printArray(pageFormat.getMatrix());
		System.out.println("::::: End Page Format :::::");
		System.out.flush();
	}

	/**
	 * Debug the paper size.
	 * 
	 * @param p
	 * @created Feb 28, 2006
	 * @author Ron Yeh
	 */
	public static void displayPaperSpecifications(Paper p) {
		System.out.println("Paper: [");
		System.out.println("\tWidth: " + new Points(p.getWidth()).toInches().getValue());
		System.out.println("\tHeight: " + new Points(p.getHeight()).toInches().getValue());
		System.out.println("\tLeft Margin: " + new Points(p.getImageableX()).toInches().getValue());
		System.out.println("\tTop Margin: " + new Points(p.getImageableY()).toInches().getValue());
		System.out.println("\tRight Margin: "
				+ new Points(p.getWidth() - p.getImageableX() - p.getImageableWidth()).toInches()
						.getValue());
		System.out.println("\tBottom Margin: "
				+ new Points(p.getHeight() - p.getImageableY() - p.getImageableHeight()).toInches()
						.getValue());
		System.out.println("]");
	}

	/**
	 * @param ps
	 * @created Feb 28, 2006
	 * @author Ron Yeh
	 */
	public static void displayPrintServiceAttributes(PrintService ps) {
		final PrintServiceAttributeSet attributes = ps.getAttributes();
		final Attribute[] attrArr = attributes.toArray();
		for (final Attribute a : attrArr) {
			System.out.println(a.getCategory().getName());
			System.out.println(a.getName());
		}
	}

	/**
	 * @param service
	 */
	public static void displaySupportedAttributeCategories(PrintService service) {
		final Class<?>[] supportedAttributeCategories = service.getSupportedAttributeCategories();
		for (Class c : supportedAttributeCategories) {
			System.out.println(c.getName());
		}
	}

	/**
	 * @param printService
	 * @created Feb 7, 2006
	 * @author Ron Yeh
	 */
	public static void displaySupportedDocFlavors(PrintService printService) {
		final DocFlavor[] supportedDocFlavors = printService.getSupportedDocFlavors();
		System.out.println(printService.getName() + " supports:");
		ArrayUtils.printArrayOfUnknownObjectsOnePerLine(supportedDocFlavors);
	}

	/**
	 * @return a list of available printing services...
	 */
	public static ArrayList<PrintServiceWrapper> getAvailablePrinters() {
		if (availablePrinters == null) {
			availablePrinters = new ArrayList<PrintServiceWrapper>();
			PrintService[] services = PrinterJob.lookupPrintServices();
			for (PrintService s : services) {
				availablePrinters.add(PrintServiceWrapper.getPrintServiceWrapper(s));
			}
		}
		return availablePrinters;
	}

	/**
	 * Query things like the default media size, and margins, etc. This does NOT specify things like
	 * Landscape or Portrait Orientation.
	 * 
	 * @param ps
	 * @created Feb 28, 2006
	 * @author Ron Yeh
	 */
	public static Paper getDefaultMedia(PrintService ps) {
		final PrinterJob printerJob = PrinterJob.getPrinterJob();
		try {
			printerJob.setPrintService(ps);
		} catch (PrinterException e) {
			e.printStackTrace();
		}
		final PageFormat format = printerJob.defaultPage();
		return format.getPaper();
	}

	/**
	 * @param ps
	 * @return
	 * @created Feb 28, 2006
	 * @author Ron Yeh
	 */
	public static int getDefaultOrientation(PrintService ps) {
		final PrinterJob printerJob = PrinterJob.getPrinterJob();
		try {
			printerJob.setPrintService(ps);
		} catch (PrinterException e) {
			e.printStackTrace();
		}
		final PageFormat format = printerJob.defaultPage();
		return format.getOrientation();
	}

	/**
	 * @return
	 */
	public static PrintService getDefaultPrintService() {
		return PrintServiceLookup.lookupDefaultPrintService();
	}

	/**
	 * @param printerName
	 * @return an array of printers that match this name
	 */
	public static MultiDocPrintService getMultiDocPrintServiceByName(String printerName) {
		final AttributeSet aset = new HashAttributeSet();
		aset.add(new PrinterName(printerName, null));
		final MultiDocPrintService[] services = PrintServiceLookup.lookupMultiDocPrintServices(null,
				aset);
		if (services.length > 0) {
			return services[0];
		} else {
			return null;
		}
	}

	/**
	 * @param printerName
	 * @return an array of printers that match this name
	 */
	public static PrintService getPrintServiceByName(String printerName) {
		final AttributeSet aset = new HashAttributeSet();
		aset.add(new PrinterName(printerName, null));
		final PrintService[] services = PrintServiceLookup.lookupPrintServices(null, aset);
		if (services.length > 0) {
			return services[0];
		} else {
			return null;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TestDriver.testPrintPhoto();
		// TestDriver.testPrintMultiplePSFiles();
		// listAvailablePrinters();
		// TestDriver.testPrintPSFile();

		// MultiDocPrintService multiDocPrintServiceByName =
		// getMultiDocPrintServiceByName(PRINTER_OFFSET);
		// System.out.println(multiDocPrintServiceByName);
		// listAvailablePrinters();

		final PrintService printService = getDefaultPrintService();
		System.out.println("Name: " + printService.getName());
		final Paper defaultMedia = getDefaultMedia(printService);
		displayPaperSpecifications(defaultMedia);
		displayOrientation(getDefaultOrientation(printService));
		displayDefaultPrinterTransform(printService);
		System.out.println("DPI: "
				+ PrintServiceWrapper.getPrintServiceWrapper(printService).getDPI());
	}

	/**
	 * @created Feb 27, 2006
	 * @author Ron Yeh
	 * 
	 * @param printable
	 * @param service
	 * @param media
	 * @param showPrintDialog
	 * @param showPageDialog
	 * @created Mar 9, 2006
	 * @author Ron Yeh
	 */
	public static void print(Printable printable, PrintService service, Paper media,
			boolean showPrintDialog, boolean showPageDialog) {

		final PrinterJob printerJob = PrinterJob.getPrinterJob();

		try {
			printerJob.setPrintService(service);
		} catch (PrinterException e) {
			e.printStackTrace();
		}

		// default printing to ON
		boolean printConfirmed = true;
		if (showPrintDialog) {
			printConfirmed = printerJob.printDialog();
		}

		final PageFormat defaultFormat = printerJob.defaultPage();
		final Paper paper = defaultFormat.getPaper();
		paper.setImageableArea(media.getImageableX(), media.getImageableY(), media
				.getImageableWidth(), media.getImageableHeight());

		defaultFormat.setPaper(paper);
		final PageFormat validatedFormat = printerJob.validatePage(defaultFormat);
		PageFormat finalFormat = null;
		if (showPageDialog) {
			finalFormat = printerJob.pageDialog(validatedFormat);
		} else {
			finalFormat = validatedFormat;
		}

		// System.out.println("PrinterBridge :: Print Dialog Returned " + printOK);
		PrinterBridge.displayPageFormat(finalFormat);
		printerJob.setPrintable(printable, finalFormat);

		try {
			if (printConfirmed) {
				printerJob.print();
				System.out.println("PrinterBridge::Printing...");
			} else {
				System.out.println("PrinterBridge::Not Printing");
			}
		} catch (PrinterException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param printable
	 * @created Mar 8, 2006
	 * @author Ron Yeh
	 * 
	 * TODO: CLONE SELF BEFORE PRINTING. The Printable passed in shouldb e a clone of what is being
	 * displayed to screen. Otherwise, responsiveness will not be so good, as the screen cannot paint
	 * while printing happens.
	 * @param listener
	 * @param service
	 */
	public static void print(Printable printable, PrintService service, Paper media,
			PrintJobListener listener) {

		// set the size of the media
		// set the size of the imageable part of the media
		final MediaPrintableArea mediaPrintableArea = new MediaPrintableArea(

		(float) new Points(media.getImageableX()).toInches().getValue() /* left margin */,

		(float) new Points(media.getImageableY()).toInches().getValue() /* top margin */,

		(float) new Points(media.getImageableWidth()).toInches().getValue() /* width inches */,

		(float) new Points(media.getImageableHeight()).toInches().getValue() /* height inches */,

		MediaPrintableArea.INCH);

		// either one of these will work.
		// let's use the first one.
		final DocAttributeSet das = new HashDocAttributeSet();
		das.add(mediaPrintableArea);

		// Compression doesn't seem to work
		// das.add(Compression.COMPRESS);
		// doesn't work
		// das.add(Compression.DEFLATE);
		// doesn't work
		// das.add(Compression.GZIP);

		final PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		// this also works, but we don't need to set it.
		// pras.add(mediaPrintableArea);

		PrinterBridge.printToService(service, printable, das, pras, listener);
	}

	/**
	 * @param pages
	 * @created Mar 14, 2006
	 * @author Ron Yeh
	 */
	public static void printAllPagesToDefaultPrinter(Printable... pages) {
		final PrinterJob job = PrinterJob.getPrinterJob();
		final Book book = new Book();
		final PageFormat pageFormat = job.defaultPage();
		final Paper paper = pageFormat.getPaper();

		final double marginLeft = 0;
		final double marginTop = 0;
		final double paperWidth = 100;
		final double paperHeight = 100;

		paper.setImageableArea(

		new Inches(marginLeft).toPoints().getValue(),

		new Inches(marginTop).toPoints().getValue(),

		new Inches(paperWidth).toPoints().getValue(),

		new Inches(paperHeight).toPoints().getValue()

		);

		pageFormat.setPaper(paper);
		final PageFormat validatedFormat = job.validatePage(pageFormat);

		// see what it validates to
		PrinterBridge.displayPageFormat(validatedFormat);

		for (Printable page : pages) {
			book.append(page, validatedFormat);
		}

		job.setPageable(book);
		if (job.printDialog()) {
			try {
				job.print();
			} catch (PrinterException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param printable
	 * @param flavor
	 * @param listener
	 */
	public static void printToDefaultService(Printable printable, PrintJobListener listener) {
		printToService(PrintServiceLookup.lookupDefaultPrintService(), printable, listener);
	}

	/**
	 * @param service
	 * @param printable
	 * @param set
	 * @param listener
	 * @created Feb 27, 2006
	 * @author Ron Yeh
	 */
	public static void printToService(PrintService service, Printable printable,
			DocAttributeSet set, PrintJobListener listener) {
		printToService(service, printable, set, new HashPrintRequestAttributeSet(), listener);
	}

	/**
	 * Print a Printable.
	 * 
	 * @param service
	 * @param printable
	 * @param das
	 * @param listener
	 * @created Feb 7, 2006
	 * @author Ron Yeh
	 */
	public static void printToService(PrintService service, Printable printable,
			DocAttributeSet das, PrintRequestAttributeSet pras, PrintJobListener listener) {
		final DocPrintJob job = service.createPrintJob();
		if (listener != null) {
			job.addPrintJobListener(listener);
		}
		final DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
		final Doc doc = new SimpleDoc(printable, flavor, das);
		try {
			job.print(doc, pras);
		} catch (PrintException pe) {
			pe.printStackTrace();
		}
	}

	/**
	 * A helper method that creates an empty DocAttributeSet...
	 * 
	 * @param service
	 * @param printable
	 * @param listener
	 *           if listener is null, it is not used
	 * 
	 * @created Feb 7, 2006
	 * @author Ron Yeh
	 */
	public static void printToService(PrintService service, Printable printable,
			PrintJobListener listener) {
		printToService(service, printable, new HashDocAttributeSet(), listener);
	}

	/**
	 * Convert from Java's 72 DPI to the Printer's DPI.
	 * 
	 * @param g2d
	 * @created Mar 3, 2006
	 * @author Ron Yeh
	 */
	public static void scaleGraphicsToPrinterDPI(Graphics2D g2d, PrintServiceWrapper printService) {
		// graphics object will be in java screen points
		// we need to multiply the g2d by 72 / DPI
		final double scaleFactor = 72.0 / printService.getDPI();
		g2d.scale(scaleFactor, scaleFactor);
	}
}
