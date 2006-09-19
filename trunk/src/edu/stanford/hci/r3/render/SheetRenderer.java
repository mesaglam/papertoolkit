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
import edu.stanford.hci.r3.pattern.TiledPattern;
import edu.stanford.hci.r3.pattern.TiledPatternGenerator;
import edu.stanford.hci.r3.pattern.coordinates.PatternLocationToSheetLocationMapping;
import edu.stanford.hci.r3.pattern.coordinates.TiledPatternCoordinateConverter;
import edu.stanford.hci.r3.pattern.output.PDFPatternGenerator;
import edu.stanford.hci.r3.units.Pixels;
import edu.stanford.hci.r3.units.Points;
import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.units.coordinates.Coordinates;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.MathUtils;
import edu.stanford.hci.r3.util.graphics.GraphicsUtils;
import edu.stanford.hci.r3.util.graphics.ImageUtils;
import edu.stanford.hci.r3.util.graphics.JAIUtils;

/**
 * <p>
 * This class will render a Sheet into a JPEG, PDF, or Java2D graphics context.
 * </p>
 * <p>
 * For individual regions, it will use specific region renderers (e.g., ImageRenderer,
 * PolygonRenderer, and TextRenderer).
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class SheetRenderer {

	/**
	 * For now, get a tiled pattern generator.
	 * 
	 * TODO: later on, we might want to pass this in.
	 */
	private final TiledPatternGenerator generator = new TiledPatternGenerator();

	/**
	 * Allows us to save the pattern info to the same directory as the most recently rendered pdf.
	 */
	private File mostRecentlyRenderedPDFFile;

	/**
	 * You can make the pattern bigger or smaller depending on your printer... 0 == default. - -->
	 * smaller, + --> bigger. Each unit corresponds to two font points.
	 */
	private int patternDotSizeAdjustment = 0;

	/**
	 * Populate this only when we render the pattern (renderToPDF). After we render to pdf, we can
	 * save the information to a file, for so that we can run the application in the future without
	 * rendering more pattern.
	 */
	private PatternLocationToSheetLocationMapping patternInformation;

	/**
	 * By Default, any active regions will be overlaid with pattern (unique to at least this sheet,
	 * unless otherwise specified).
	 */
	protected boolean renderActiveRegionsWithPattern = true;

	/**
	 * The sheet we are to render.
	 */
	protected Sheet sheet;

	/**
	 * @param s
	 */
	public SheetRenderer(Sheet s) {
		sheet = s;
		patternInformation = new PatternLocationToSheetLocationMapping(sheet);
	}

	/**
	 * @return
	 */
	public PatternLocationToSheetLocationMapping getPatternInformation() {
		return patternInformation;
	}

	/**
	 * We will render pattern when outputting PDFs. Rendering pattern to screen is a waste of time,
	 * since dots are not resolvable on screen. Perhaps for screen display (i.e., anything < 600
	 * dpi), we should render pattern as a faint dotted overlay?
	 * 
	 * WARNING: Does not work for multiple sheets, as we will get the same pattern....
	 * 
	 * @param cb
	 *            a content layer returned by iText
	 * 
	 */
	protected void renderPattern(PdfContentByte cb) {
		// for each region, overlay pattern if it is an active region
		final List<Region> regions = sheet.getRegions();

		// for now, reset the pattern location once per sheet
		// WARNING: this does not make sense for multiple sheets
		// Need to change once we start rendering bundles
		generator.resetUniquePatternTracker();

		// this object will generate the right PDF (itext) calls to create pattern
		final PDFPatternGenerator pgen = new PDFPatternGenerator(cb, sheet.getWidth(), sheet
				.getHeight());

		// adjust the font size of the pattern...
		pgen.adjustPatternSize(patternDotSizeAdjustment);

		// render each region that is active
		for (Region r : regions) {
			if (!r.isActive()) {
				continue;
			}

			// add the region's offset from the top left corner of the sheet
			Coordinates regionOffset = sheet.getRegionOffset(r);

			// System.out.println("SheetRenderer: Rendering Pattern:" + r.getShape());
			// DebugUtils.println(r.getOriginX() + " " + r.getOriginY());

			// Figure out the real width and height....
			final Units scaledWidth = r.getWidth();
			final Units scaledHeight = r.getHeight();

			// get pattern of the given width and height
			// by default, the pattern returned will be unique if possible (and a warning thrown
			// otherwise). If you want to use the same pattern in different places, you will
			// need to keep the returned pattern object around
			final TiledPattern pattern = generator.getPattern(scaledWidth, scaledHeight);

			// render the pattern starting at the region's origin
			pgen.renderPattern(pattern, // the tiled pattern
					Units.add(r.getOriginX(), regionOffset.getX()), // origin + offset
					Units.add(r.getOriginY(), regionOffset.getY()));// same, for y

			// also, at this point, we know what pattern we have assigned to each region
			// we should be able to assign a tile configuration to each region
			final TiledPatternCoordinateConverter tiledPatternInRegion = patternInformation
					.getPatternBoundsOfRegion(r);
			// set all the information here
			tiledPatternInRegion.setPatternInformationByReadingItFrom(pattern);
			// now, this object is modified
			// since it is already mapped to the correct region r, we do not need
			// to do anything else!
		}

		// /////////////////////////////////////////////////////
		// /////////////////////////////////////////////////////
		// FOR NOW, SPECIAL CASE THE COMPOUND REGIONS
		// IN THE FUTURE, FIGURE OUTHOW TO INTEGRATE IT NICELY
		// /////////////////////////////////////////////////////
		// /////////////////////////////////////////////////////
		// for (Region r : regions) {
		// if (r instanceof CompoundRegion) {
		// DebugUtils.println("Rendering Pattern for Compound Region!");
		// }
		// }
		// 
		// MUST REARCHITECT Pattern Rendering & Event Handling if we are to allow Compound Regions
		// This is for R3 version 0.2 =\ AFTER September 29th...
	}

	/**
	 * We assume the g2d is big enough for us to draw this Sheet to.
	 * 
	 * By default, the transforms works at 72 dots per inch. Scale the transform beforehand if you
	 * would like better or worse rendering.
	 * 
	 * 
	 * @param g2d
	 */
	public void renderToG2D(Graphics2D g2d) {
		// anti-aliased, high quality rendering
		g2d.setRenderingHints(GraphicsUtils.getBestRenderingHints());

		final List<Region> regions = sheet.getRegions();

		// render each region
		for (Region r : regions) {
			// Weird. g2d.getTransform SHOULD give us a copy....
			final AffineTransform currTransform = new AffineTransform(g2d.getTransform()); // a
			// real
			// copy
			DebugUtils.println("Rendering " + r.getName());
			final Coordinates regionOffset = sheet.getRegionOffset(r);
			final double xOffsetPts = regionOffset.getX().getValueInPoints();
			final double yOffsetPts = regionOffset.getY().getValueInPoints();
			System.out.println(xOffsetPts);
			// g2d.transform(AffineTransform.getTranslateInstance(xOffsetPts, yOffsetPts));
			g2d.translate((int) xOffsetPts, (int) yOffsetPts);
			r.getRenderer().renderToG2D(g2d);
			g2d.setTransform(currTransform);
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
	 * Uses the iText package to render a PDF file from scratch. iText is nice because we can write
	 * to a Graphics2D context. Alternatively, we can use PDF-like commands.
	 * 
	 * @param destPDFFile
	 */
	public void renderToPDF(File destPDFFile) {
		try {
			final FileOutputStream fileOutputStream = new FileOutputStream(destPDFFile);

			final Rectangle pageSize = new Rectangle(0, 0, (int) Math.round(sheet.getWidth()
					.getValueInPoints()), (int) Math.round(sheet.getHeight().getValueInPoints()));

			// create a document with these margins (worry about margins later)
			final Document doc = new Document(pageSize, 0, 0, 0, 0);
			final PdfWriter writer = PdfWriter.getInstance(doc, fileOutputStream);
			doc.open();

			final PdfContentByte topLayer = writer.getDirectContent();
			final PdfContentByte bottomLayer = writer.getDirectContentUnder();
			renderToPDFContentLayers(destPDFFile, topLayer, bottomLayer);

			doc.close();

			// save the pattern info to the same directory automatically
			savePatternInformation(); // do this automatically
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param destPDFFile
	 * @param topLayer
	 * @param bottomLayer
	 */
	protected void renderToPDFContentLayers(File destPDFFile, PdfContentByte topLayer,
			PdfContentByte bottomLayer) {
		mostRecentlyRenderedPDFFile = destPDFFile;

		final Units width = sheet.getWidth();
		final Units height = sheet.getHeight();
		final float wPoints = (float) width.getValueInPoints();
		final float hPoints = (float) height.getValueInPoints();

		// bottom layer for regions
		final Graphics2D g2dUnder = bottomLayer.createGraphicsShapes(wPoints, hPoints);
		// now that we have a G2D, we can just use our other G2D rendering method
		renderToG2D(g2dUnder);

		// an efficient dispose, because we are not within a Java paint() method
		g2dUnder.dispose();

		// should this be moved to regions???
		if (renderActiveRegionsWithPattern) {
			DebugUtils.println("Rendering Pattern");
			// after rendering everything, we still need to overlay the pattern on top of active
			// regions; This is only for PDF rendering.

			// top layer for pattern
			renderPattern(topLayer);
		}
	}

	/**
	 * This saves an xml file with the same name/path, but different extension as the most-recently
	 * rendered PDF file.
	 */
	public void savePatternInformation() {
		if (mostRecentlyRenderedPDFFile == null) {
			System.err.println("SheetRenderer: We cannot save the pattern information "
					+ "without a destination file. Please render a PDF first "
					+ "so we know where to put the pattern configuration file!");
		} else {
			File parentDir = mostRecentlyRenderedPDFFile.getParentFile();
			String fileName = mostRecentlyRenderedPDFFile.getName();
			if (fileName.contains(".pdf")) {
				fileName = fileName.replace(".pdf", ".patternInfo.xml");
			} else {
				fileName = fileName + ".patternInfo.xml";
			}
			File destFile = new File(parentDir, fileName);
			savePatternInformation(destFile);
		}
	}

	/**
	 * After Rendering Pattern, we now know the particulars of the pattern coordinates for each
	 * region. Save that information to disk.
	 * 
	 * @param patternInfoFile
	 */
	public void savePatternInformation(File patternInfoFile) {
		// save the pattern info to disk as a nice XML File! =)
		patternInformation.saveConfigurationToXML(patternInfoFile);
		DebugUtils.println("Pattern Information saved to " + patternInfoFile.getAbsolutePath());
	}

	/**
	 * @param activeWithPattern
	 */
	public void setRenderActiveRegionsWithPattern(boolean activeWithPattern) {
		renderActiveRegionsWithPattern = activeWithPattern;
	}

	/**
	 * 
	 */
	public void useLargerPatternDots() {
		patternDotSizeAdjustment++;
	}

	/**
	 * 
	 */
	public void useSmallerPatternDots() {
		patternDotSizeAdjustment--;
	}
}
