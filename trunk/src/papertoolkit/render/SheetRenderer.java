package papertoolkit.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import javax.media.jai.TiledImage;

import org.jibble.epsgraphics.EpsGraphics2D;

import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.pattern.TiledPattern;
import papertoolkit.pattern.TiledPatternGenerator;
import papertoolkit.pattern.coordinates.PatternLocationToSheetLocationMapping;
import papertoolkit.pattern.coordinates.conversion.TiledPatternCoordinateConverter;
import papertoolkit.pattern.output.PDFPatternGenerator;
import papertoolkit.pattern.output.PostscriptPatternGenerator;
import papertoolkit.units.Pixels;
import papertoolkit.units.Points;
import papertoolkit.units.Units;
import papertoolkit.units.coordinates.Coordinates;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.MathUtils;
import papertoolkit.util.files.FileUtils;
import papertoolkit.util.graphics.GraphicsUtils;
import papertoolkit.util.graphics.ImageUtils;
import papertoolkit.util.graphics.JAIUtils;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

/**
 * <p>
 * This class will render a Sheet into a JPEG, PDF, or Java2D graphics context.
 * </p>
 * <p>
 * For individual regions, it will use specific region renderers (e.g., ImageRenderer, PolygonRenderer, and
 * TextRenderer).
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
	 * Generates pattern for this sheet.
	 */
	private TiledPatternGenerator generator;

	/**
	 * Allows us to save the pattern info to the same directory as the most recently rendered pdf or ps.
	 */
	private File mostRecentlyRenderedFile;

	/**
	 * What color should we render the pattern in?
	 */
	private Color patternColor = Color.BLACK;

	/**
	 * You can make the pattern bigger or smaller depending on your printer... 0 == default. - --> smaller, +
	 * --> bigger. Each unit corresponds to two font points.
	 */
	private int patternDotSizeAdjustment = 0;

	/**
	 * Populate this only when we render the pattern (renderToPDF). After we render to pdf, we can save the
	 * information to a file, for so that we can run the application in the future without rendering more
	 * pattern.
	 */
	private PatternLocationToSheetLocationMapping patternInformation;

	/**
	 * By default, any active regions will be overlaid with pattern (unique to at least this sheet, unless
	 * otherwise specified).
	 */
	protected boolean renderActiveRegionsWithPattern = true;

	/**
	 * The sheet we are to render.
	 */
	protected Sheet sheet;

	/**
	 * Create a new TiledPatternGenerator for this Sheet.
	 * 
	 * @param s
	 */
	public SheetRenderer(Sheet s) {
		this(s, new TiledPatternGenerator());
	}

	/**
	 * Feel free to share TiledPatternGenerator between Sheets. That way, you can get unique pattern across
	 * multiple Sheets.
	 * 
	 * @param s
	 * @param patternGenerator
	 */
	public SheetRenderer(Sheet s, TiledPatternGenerator patternGenerator) {
		sheet = s;
		patternInformation = sheet.getPatternLocationToSheetLocationMapping();
		generator = patternGenerator;
	}

	/**
	 * @return
	 */
	public PatternLocationToSheetLocationMapping getPatternInformation() {
		return patternInformation;
	}

	/**
	 * We will render pattern when outputting PDFs. Rendering pattern to screen is a waste of time, since dots
	 * are not resolvable on screen. Perhaps for screen display (i.e., anything < 600 dpi), we should render
	 * pattern as a faint dotted overlay?
	 * 
	 * WARNING: Does not work for multiple sheets, as we will get the same pattern....
	 * 
	 * @param cb
	 *            a content layer returned by iText
	 * 
	 */
	private void renderPatternToPDF(PdfContentByte cb) {
		// for each region, overlay pattern if it is an active region
		final List<Region> regions = sheet.getRegions();

		// this object will generate the right PDF (itext) calls to create pattern
		final PDFPatternGenerator pgen = new PDFPatternGenerator(cb, sheet.getWidth(), sheet.getHeight());
		pgen.setPatternColor(patternColor);

		// adjust the font size of the pattern...
		pgen.adjustPatternSize(patternDotSizeAdjustment);

		// render each region that is active
		for (Region r : regions) {
			if (!r.isActive()) {
				continue;
			}

			// DebugUtils.println("-------------");

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

			// DebugUtils.println("Rendering Pattern for " + r.getName());
			// render the pattern starting at the region's origin
			pgen.renderPattern(pattern, // the tiled pattern
					Units.add(r.getOriginX(), regionOffset.getX()), // origin + offset
					Units.add(r.getOriginY(), regionOffset.getY()));// same, for y

			// also, at this point, we know what pattern we have assigned to each region
			// we should be able to assign a tile configuration to each region
			// We retrieve it from the HashMap so we can SET the values in the line below
			final TiledPatternCoordinateConverter tiledPatternInRegion = (TiledPatternCoordinateConverter) patternInformation
					.getPatternBoundsOfRegion(r);
			tiledPatternInRegion.setPatternInformationByReadingItFrom(pattern);
			// the name should already be correct, barring the UnitializedMapping business...
			// tiledPatternInRegion.setRegionName(r.getName());
			// now, this object is modified
			// since it is already mapped to the correct region r, we do not need
			// to do anything else!
		}

		// /////////////////////////////////////////////////////
		// /////////////////////////////////////////////////////
		// FOR NOW, SPECIAL CASE THE COMPOUND REGIONS
		// IN THE FUTURE, FIGURE OUT HOW TO INTEGRATE IT NICELY
		// /////////////////////////////////////////////////////
		// /////////////////////////////////////////////////////
		// for (Region r : regions) {
		// if (r instanceof CompoundRegion) {
		// DebugUtils.println("Rendering Pattern for Compound Region!");
		// }
		// }
		// 
		// MUST REARCHITECT Pattern Rendering & Event Handling if we are to allow Compound Regions
		// This is for a future R3 version =\
	}

	/**
	 * @param file
	 */
	private String renderPatternToPostScript() {
		Units width = sheet.getWidth();
		Units height = sheet.getHeight();

		final PostscriptPatternGenerator pgen = new PostscriptPatternGenerator(width, height, generator);

		// for each region, figure out where it is on the sheet, and calculate the pattern coordinates
		final List<Region> regions = sheet.getRegions();
		for (Region r : regions) {
			// We retrieve it from the HashMap so we can SET the values in the line below
			final TiledPatternCoordinateConverter patternCoordinateConverter = (TiledPatternCoordinateConverter) patternInformation
					.getPatternBoundsOfRegion(r);

			Coordinates regionLocation = sheet.getRegionLocationRelativeToSheet(r);

			if (patternCoordinateConverter == null) {
				DebugUtils.println("Null Converter. Is this region not active? " + r);
				continue;
			}

			// the tiledPattern encompasses the whole sheet
			// we set the information of for the converter, by setting the information for the whole pattern,
			// with a clipping bounds, defined by the region's location relative to
			// the sheet's upper left corner
			patternCoordinateConverter.setPatternInformationByReadingItFrom(pgen.getPattern(),
					regionLocation, r.getWidth(), r.getHeight());
		}

		return pgen.getPostscriptPattern();
	}

	/**
	 * We assume the g2d is big enough for us to draw this Sheet to.
	 * 
	 * By default, the transforms works at 72 dots per inch. Scale the transform beforehand if you would like
	 * better or worse rendering.
	 * 
	 * This only renders the regions and region content, but not the pattern.
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
			// a real copy
			final AffineTransform currTransform = new AffineTransform(g2d.getTransform());
			// DebugUtils.println("Rendering " + r.getName());
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
	 *            Converts the graphics2D object into a new coordinate space based on the destination units'
	 *            pixels per inch. This is for the purposes of rendering the document to screen, where
	 *            Graphics2D's default 72ppi isn't always the right way to do it.
	 */
	public void renderToJPEG(File destJPEGFile, Pixels destUnits) {
		final Units width = sheet.getWidth();
		final Units height = sheet.getHeight();

		final double scale = Points.ONE.getScalarMultipleToConvertTo(destUnits);

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
	 * Uses the iText package to render a PDF file from scratch. iText is nice because we can write to a
	 * Graphics2D context. Alternatively, we can use PDF-like commands.
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
		mostRecentlyRenderedFile = destPDFFile;

		final Units width = sheet.getWidth();
		final Units height = sheet.getHeight();
		final float wPoints = (float) width.getValueInPoints();
		final float hPoints = (float) height.getValueInPoints();

		// top layer for regions (changed from bottom layer)
		final Graphics2D g2dOver = topLayer.createGraphicsShapes(wPoints, hPoints);
		// now that we have a G2D, we can just use our other G2D rendering method
		renderToG2D(g2dOver);

		// an efficient dispose, because we are not within a Java paint() method
		g2dOver.dispose();

		// should this be moved to regions???
		if (renderActiveRegionsWithPattern) {
			// DebugUtils.println("Rendering Pattern");
			// after rendering everything, we still need to overlay the pattern on top of active
			// regions; This is only for PDF rendering.

			// top layer for pattern
			renderPatternToPDF(topLayer);
		}
	}

	/**
	 * Uses Jibble and some EPS Hacking to create a PostScript file!
	 * 
	 * @param file
	 */
	public void renderToPostScript(File file) {
		mostRecentlyRenderedFile = file;

		// layer for regions
		final EpsGraphics2D g2d = new EpsGraphics2D("PostScript Render");

		// set the bounding box, and draw it, so that the PS file will be the right size\
		g2d.setStroke(new BasicStroke(0.1f));
		g2d.drawRect(0, 0, (int) sheet.getWidth().getValueInPoints(), (int) sheet.getHeight()
				.getValueInPoints());

		// now that we have a G2D, we can just use our other G2D rendering method
		renderToG2D(g2d);
		String graphicsPostscript = g2d.toString();

		// create an associated pattern file
		if (renderActiveRegionsWithPattern) {
			String patternPostscript = renderPatternToPostScript();

			// then, merge the two!

			// remove the junk from graphicsPostscript
			// remove the opening junk
			graphicsPostscript = graphicsPostscript.replaceAll("(?s)%.*EndComments", "");
			// remove the closing junk, including the showpage
			graphicsPostscript = graphicsPostscript.replaceAll("(?s)showpage.*EOF", "");
			// DebugUtils.println(graphicsPostscript);

			// paste these graphics into our pattern
			String output = patternPostscript.replace("__INSERT_SHEET_POSTSCRIPT_HERE__", graphicsPostscript);
			FileUtils.writeStringToFile(output, file);
		} else {
			// just write the graphics to a file
			FileUtils.writeStringToFile(graphicsPostscript, file);
		}

		// save the pattern info to the same directory automatically
		savePatternInformation(); // do this automatically
	}

	/**
	 * This saves an xml file with the same name/path, but different extension as the most-recently rendered
	 * PDF file.
	 */
	public void savePatternInformation() {
		if (mostRecentlyRenderedFile == null) {
			System.err.println("SheetRenderer: We cannot save the pattern information "
					+ "without a destination file. Please render a PDF or PS first "
					+ "so we know where to put the pattern configuration file!");
		} else {
			File parentDir = mostRecentlyRenderedFile.getParentFile();
			String fileName = mostRecentlyRenderedFile.getName();
			if (fileName.contains(".pdf")) {
				fileName = fileName.replace(".pdf", ".patternInfo.xml");
			} else if (fileName.contains(".ps")) {
				fileName = fileName.replace(".ps", ".patternInfo.xml");
			} else {
				fileName = fileName + ".patternInfo.xml";
			}
			File destFile = new File(parentDir, fileName);
			savePatternInformation(destFile);
		}
	}

	/**
	 * After Rendering Pattern, we now know the particulars of the pattern coordinates for each region. Save
	 * that information to disk.
	 * 
	 * @param patternInfoFile
	 */
	public void savePatternInformation(File patternInfoFile) {
		// save the pattern info to disk as a nice XML File! =)
		patternInformation.saveConfigurationToXML(patternInfoFile);
		// DebugUtils.println("Pattern Information saved to " + patternInfoFile.getAbsolutePath());
	}

	/**
	 * This is really just for debugging, as you want BLACK pattern in general.
	 * 
	 * @param pColor
	 */
	public void setPatternColor(Color pColor) {
		patternColor = pColor;
	}

	/**
	 * Useful for when rendering many sheets at a time. This can guarantee that the pattern is unique across
	 * sheets. If you want to reset the pattern, or pick a particular sheet, you may, by interacting with the
	 * generator object.
	 * 
	 * @param tiledPatternGenerator
	 */
	public void setPatternGenerator(TiledPatternGenerator tiledPatternGenerator) {
		generator = tiledPatternGenerator;
	}

	/**
	 * Used for debugging. If you set this to false, then we will not render any pattern at all.
	 * 
	 * @param activeWithPattern
	 */
	public void setRenderActiveRegionsWithPattern(boolean activeWithPattern) {
		renderActiveRegionsWithPattern = activeWithPattern;
	}

	/**
	 * Call this one or more times before rendering. It's a hint to the renderer.
	 */
	public void useLargerPatternDots() {
		patternDotSizeAdjustment++;
	}

	/**
	 * Call this one or more times before rendering. It's a hint to the renderer.
	 */
	public void useSmallerPatternDots() {
		patternDotSizeAdjustment--;
	}
}
