package edu.stanford.hci.r3.render.sheets;

import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.sheets.PDFSheet;
import edu.stanford.hci.r3.render.SheetRenderer;
import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * Note: Really, an existing PDF is more like a bundle.
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PDFSheetRenderer extends SheetRenderer {

	/**
	 * Wraps an existing PDF File.
	 */
	private PDFSheet pdfSheet;

	public PDFSheetRenderer(PDFSheet s) {
		super(s);
		pdfSheet = s;
	}

	/**
	 * We assume the g2d is big enough for us to draw this Sheet to.
	 * 
	 * By default, the transforms works at 72 dots per inch. Scale the transform beforehand if you
	 * would like better (more dots per inch) or worse rendering (fewer dots per inch).
	 * 
	 * @param g2d
	 */
	public void renderToG2D(Graphics2D g2d) {
		// render the PDF to the g2d's background
		PdfReader reader = pdfSheet.getReader();
//		reader.get;
//		PdfImportedPage p = new PdfImportedPage();
		
		// call the super's renderToG2D to paint all the other regions
		DebugUtils.println("Commented Out Super.RenderToG2D");
		//super.renderToG2D(g2d);
	}

	/**
	 * Uses the iText package to render a PDF file. iText is nice because we can write to a
	 * Graphics2D context. Alternatively, we can use PDF-like commands.
	 * 
	 * @param destPDFFile
	 */
	public void renderToPDF(File destPDFFile) {
		try {
			final Units width = sheet.getWidth();
			final Units height = sheet.getHeight();
			final PdfReader reader = pdfSheet.getReader();
			DebugUtils.println("NumPages in Existing PDF: " + reader.getNumberOfPages());

			// allows us to stamp on top of an existing PDF
			final PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(destPDFFile));

			// change the content on top of page 1
			float wPoints = (float) width.getValueInPoints();
			float hPoints = (float) height.getValueInPoints();

			// bottom layer for regions
			final PdfContentByte cb = stamp.getUnderContent(1);
			final Graphics2D g2Under = cb.createGraphicsShapes(wPoints, hPoints);

			// now that we have a G2D, we can just use our other G2D rendering method
			renderToG2D(g2Under);

			// an efficient dispose, because we are not within a Java paint() method
			g2Under.dispose();

			// should this be moved to regions???
			if (renderActiveRegionsWithPattern) {
				DebugUtils.println("Rendering Pattern");
				// after rendering everything, we still need to overlay the pattern on top of active
				// regions; This is only for PDF rendering.

				// top layer for pattern
				PdfContentByte over = stamp.getOverContent(1);
				renderPattern(over);
			}

			stamp.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
