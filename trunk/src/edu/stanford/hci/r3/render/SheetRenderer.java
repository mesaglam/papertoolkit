package edu.stanford.hci.r3.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import javax.media.jai.TiledImage;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.pattern.PatternPackage;
import edu.stanford.hci.r3.pattern.TiledPattern;
import edu.stanford.hci.r3.pattern.TiledPatternGenerator;
import edu.stanford.hci.r3.pattern.output.PDFPatternGenerator;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.units.Pixels;
import edu.stanford.hci.r3.units.Points;
import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.MathUtils;
import edu.stanford.hci.r3.util.graphics.GraphicsUtils;
import edu.stanford.hci.r3.util.graphics.ImageUtils;
import edu.stanford.hci.r3.util.graphics.JAIUtils;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * This class will render a Sheet into a JPEG, PDF, or Java2D graphics context.
 * 
 * For individual regions, it will use specific region renderers (e.g., ImageRenderer,
 * PolygonRenderer, and TextRenderer).
 */
public class SheetRenderer {

	/**
	 * By Default, any active regions will be overlaid with pattern (unique to at least this sheet,
	 * unless otherwise specified).
	 */
	private boolean renderActiveRegionsWithPattern = true;

	protected Sheet sheet;

	/**
	 * @param s
	 */
	public SheetRenderer(Sheet s) {
		sheet = s;
	}

	/**
	 * We will render pattern when outputting PDFs. Rendering pattern to screen is a waste of time,
	 * since dots are not resolvable on screen. Perhaps for screen display (i.e., anything < 600
	 * dpi), we should render pattern as a faint dotted overlay?
	 * 
	 * @param cb
	 *            a content layer returned by iText
	 */
	private void renderPattern(PdfContentByte cb) {
		// for each region, overlay pattern if it is an active region
		final List<Region> regions = sheet.getRegions();

		// for now, get a tiled pattern generator
		// later on, we might want to pass this in
		final TiledPatternGenerator generator = new TiledPatternGenerator();

		// this object will generate the right PDF (itext) calls to create pattern
		final PDFPatternGenerator pgen = new PDFPatternGenerator(cb, sheet.getWidth(), sheet
				.getHeight());

		// render each region
		for (Region r : regions) {
			if (!r.isActive()) {
				continue;
			}

			System.out.println("SheetRenderer: Rendering Pattern!");
			System.out.println("SheetRenderer: " + r.getShape());

			// TODO: later on, figure out the real width and height....
			final Units unscaledWidth = r.getUnscaledBoundsWidth();
			final Units unscaledHeight = r.getUnscaledBoundsHeight();

			final TiledPattern pattern = generator.getPattern(new Inches(0), new Inches(0),
					unscaledWidth, unscaledHeight);

			DebugUtils.println(r.getOriginX() + " " + r.getOriginY());

			pgen.renderPattern(pattern, r.getOriginX(), r.getOriginY());
		}
	}

	/**
	 * We assume the g2d is big enough for us to draw this Sheet to.
	 * 
	 * By default, the transforms works at 72 dots per inch. Scale the transform beforehand if you
	 * would like better or worse rendering.
	 * 
	 * @param g2d
	 */
	public void renderToG2D(Graphics2D g2d) {
		final List<Region> regions = sheet.getRegions();
		// render each region
		for (Region r : regions) {
			r.getRenderer().renderToG2D(g2d);
		}
	}

	/**
	 * Use the default pixels per inch. Specified in our configuration file.
	 * 
	 * @param file
	 */
	public void renderToJPEG(File file) {
		renderToJPEG(file, Pixels.ONE);
	}

	/**
	 * @param destJPEGFile
	 * @param destUnits
	 *            Converts the graphics2D object into a new coordinate space based on the
	 *            destination units' pixels per inch. This is for the purposes of rendering the
	 *            document to screen, where Graphics2D's default 72ppi isn't always the right way to
	 *            do it.
	 */
	public void renderToJPEG(File destJPEGFile, Pixels destUnits) {
		final Units width = sheet.getWidth();
		final Units height = sheet.getHeight();

		final double scale = Points.ONE.getConversionTo(destUnits);

		final int w = MathUtils.rint(width.getValueIn(destUnits));
		final int h = MathUtils.rint(height.getValueIn(destUnits));
		final TiledImage image = JAIUtils.createWritableBufferWithoutAlpha(w, h);
		final Graphics2D graphics2D = image.createGraphics();
		graphics2D.setRenderingHints(GraphicsUtils.getBestRenderingHints());

		// transform the graphics such that we are in destUnits' pixels per inch, so that when we
		// draw 72 Graphics2D pixels from now on, it will equal the correct number of output pixels
		// in the JPEG.
		graphics2D.setTransform(AffineTransform.getScaleInstance(scale, scale));

		// render a white canvas
		graphics2D.setColor(Color.WHITE);
		graphics2D.fillRect(0, 0, w, h);

		renderToG2D(graphics2D);
		graphics2D.dispose();
		ImageUtils.writeImageToJPEG(image.getAsBufferedImage(), destJPEGFile);
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
			final FileOutputStream fileOutputStream = new FileOutputStream(destPDFFile);

			final Rectangle pageSize = new Rectangle(0, 0, (int) Math.round(width
					.getValueInPoints()), (int) Math.round(height.getValueInPoints()));

			// create a document with these margins (worry about margins later)
			final Document doc = new Document(pageSize, 0, 0, 0, 0);
			final PdfWriter writer = PdfWriter.getInstance(doc, fileOutputStream);
			doc.open();

			// bottom layer for regions
			final PdfContentByte cb = writer.getDirectContentUnder();
			final Graphics2D g2d = cb.createGraphicsShapes(pageSize.width(), pageSize.height());

			// now that we have a G2D, we can just use our other G2D rendering method
			renderToG2D(g2d);

			// an efficient dispose, because we are not within a Java paint() method
			g2d.dispose();

			// should this be moved to regions???
			if (renderActiveRegionsWithPattern) {
				System.out.println("Rendering Pattern");
				// after rendering everything, we still need to overlay the pattern on top of active
				// regions; This is only for PDF rendering.
				// top layer for pattern
				renderPattern(writer.getDirectContent());
			}

			doc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param activeWithPattern
	 */
	public void setRenderActiveRegionsWithPattern(boolean activeWithPattern) {
		renderActiveRegionsWithPattern = activeWithPattern;
	}
}
