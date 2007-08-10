package papertoolkit.render.sheets;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import papertoolkit.paper.Region;
import papertoolkit.paper.sheets.PDFSheet;
import papertoolkit.render.SheetRenderer;
import papertoolkit.units.coordinates.Coordinates;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.graphics.GraphicsUtils;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;


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

	/**
	 * @param s
	 */
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
	 * TODO: Finish this! =)
	 * 
	 * @param g2d
	 */
	public void renderToG2D(Graphics2D g2d) {
		/*
		// TODO: FIX THIS AT SOME POINT...
		// render the PDF to the g2d's background (do we need to do this? since we are using a stamper???)
		PdfReader reader = pdfSheet.getReader();

		// deleted some code from here... about PDF Imported Page

		// call the super's renderToG2D to paint all the other regions
		//DebugUtils.println("Commented Out Super.RenderToG2D"); //(uncommented, obviously)
		super.renderToG2D(g2d);
		*/
		
		// anti-aliased, high quality rendering
		g2d.setRenderingHints(GraphicsUtils.getBestRenderingHints());

		final List<Region> regions = sheet.getRegions();

		// render each region
		for (Region r : regions) {
			if (r.isActive()) continue;
			// Weird. g2d.getTransform SHOULD give us a copy....
			// a real copy
			final AffineTransform currTransform = new AffineTransform(g2d.getTransform());
			DebugUtils.println("Rendering " + r.getName());
			final Coordinates regionOffset = sheet.getRegionOffset(r);
			final double xOffsetPts = regionOffset.getX().getValueInPoints();
			final double yOffsetPts = regionOffset.getY().getValueInPoints();
			// System.out.println(xOffsetPts);
			// g2d.transform(AffineTransform.getTranslateInstance(xOffsetPts, yOffsetPts));
			g2d.translate((int) xOffsetPts, (int) yOffsetPts);
			r.getRenderer().renderToG2D(g2d);
			g2d.setTransform(currTransform);
		}
	}

	/**
	 * Uses the iText package to render a PDF file. iText is nice because we can write to a
	 * Graphics2D context. Alternatively, we can use PDF-like commands.
	 * 
	 * @param destPDFFile
	 */
	public void renderToPDF(File destPDFFile) {
		try {
			final FileOutputStream fileOutputStream = new FileOutputStream(destPDFFile);

			final PdfReader reader = pdfSheet.getReader();
			DebugUtils.println("NumPages in Existing PDF: " + reader.getNumberOfPages());

			// allows us to stamp on top of an existing PDF
			final PdfStamper stamp = new PdfStamper(reader, fileOutputStream);

			// change the content on top of page 1
			// bottom layer for regions
			final PdfContentByte topLayer = stamp.getOverContent(1 /* page number */);
			final PdfContentByte bottomLayer = stamp.getUnderContent(1);
			renderToPDFContentLayers(destPDFFile, topLayer, bottomLayer);
			stamp.close();
			
			// save the pattern info to the same directory automatically
			savePatternInformation(); // do this automatically
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
