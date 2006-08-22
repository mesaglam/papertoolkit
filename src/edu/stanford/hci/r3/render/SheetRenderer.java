package edu.stanford.hci.r3.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.pdfbox.exceptions.COSVisitorException;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDPage;
import org.pdfbox.pdmodel.common.PDRectangle;
import org.pdfbox.pdmodel.edit.PDPageContentStream;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

import edu.stanford.hci.r3.core.Region;
import edu.stanford.hci.r3.core.Sheet;
import edu.stanford.hci.r3.core.regions.TextRegion;
import edu.stanford.hci.r3.units.Points;
import edu.stanford.hci.r3.units.Units;

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

	private boolean debugRegions = true;

	/**
	 * By Default, any active regions will be overlaid with pattern (unique to at least this sheet,
	 * unless otherwise specified).
	 */
	private boolean renderActiveRegionsWithPattern = true;

	private Sheet sheet;

	public SheetRenderer(Sheet s) {
		sheet = s;
	}

	/**
	 * We assume the g2d is big enough for us to draw this Sheet to.
	 * 
	 * @param g2d
	 */
	public void renderToG2D(Graphics2D g2d) {
		final List<Region> regions = sheet.getRegions();

		// by default, the transforms works at 72 dots per inch
		AffineTransform transform = g2d.getTransform();

		Color regionColor = new Color(123, 123, 123, 30);
		Color fontColor = Color.BLACK;

		Font f = new Font("Trebuchet MS", Font.PLAIN, 8);
		LineMetrics lineMetrics = f.getLineMetrics("Height of Line", new FontRenderContext(null,
				true, true));
		float lineHeight = lineMetrics.getHeight();
		g2d.setFont(f);

		for (Region r : regions) {

			Rectangle2D b = r.getUnscaledBounds2D();

			float scaleX = (float) r.getScaleX();
			float scaleY = (float) r.getScaleY();

			Units units = r.getUnits();
			double scale = units.getConversionTo(Points.ONE);

			float xPts = (float) Math.round(scale * b.getX());
			float yPts = (float) Math.round(scale * b.getY());
			float wPts = (float) Math.round(scale * b.getWidth());
			float hPts = (float) Math.round(scale * b.getHeight());

			int finalX = (int) Math.round(xPts * scaleX);
			int finalY = (int) Math.round(yPts * scaleY);
			int finalW = (int) Math.round(wPts * scaleX);
			int finalH = (int) Math.round(hPts * scaleY);

			// handle different regions differently
			if (debugRegions) {
				g2d.setColor(regionColor);
				g2d.fillRect(finalX, finalY, finalW, finalH);
				g2d.setColor(fontColor);
				g2d.drawString(r.toString(), finalX, finalY + lineHeight);
			}

			if (r instanceof TextRegion) {
				TextRegion tr = (TextRegion) r;
				tr.getHeightInPoints();

				Font oldFont = g2d.getFont();
				g2d.setFont(tr.getFont());

				double offset = tr.getAscentInPoints().getValue();
				double textLineHeight = tr.getLineHeightInPoints().getValue();

				// handle multiple lines
				String[] linesOfText = tr.getLinesOfText();
				int xOffset = (int) Math.round(tr.getX().getValueInPoints());
				double yOffset = tr.getY().getValueInPoints() + offset;
				for (String line : linesOfText) {
					g2d.drawString(line, xOffset, (int) Math.round(yOffset));
					yOffset += textLineHeight;
				}

				g2d.setFont(oldFont);

				tr.printLineMetrics();
			} else {
				// call r's custom G2D renderer?
				// how will we handle custom renderers?
				// todo =)
			}

		}
	}

	/**
	 * @param destFile
	 */
	public void renderToJPEG(File destJPEGFile) {

	}

	/**
	 * Uses the iText package to render a PDF file. iText is nice because we can write to a
	 * Graphics2D context.
	 * 
	 * @param destPDFFile
	 */
	public void renderToPDFWithIText(File destPDFFile) {
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

			// top layer for pattern
			final PdfContentByte cb = writer.getDirectContent();
			final Graphics2D g2d = cb.createGraphicsShapes(pageSize.width(), pageSize.height());

			// now that we have a G2D, we can just use our other G2D rendering method
			renderToG2D(g2d);

			// an efficient dispose, because we are not within a Java paint() method
			g2d.dispose();
			doc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param destPDFFile
	 */
	public void renderToPDFWithPDFBox(File destPDFFile) {
		final Units width = sheet.getWidth();
		final Units height = sheet.getHeight();
		List<Region> regions = sheet.getRegions();

		PDDocument document = null;
		try {
			document = new PDDocument();
			PDPage mainPage = new PDPage();
			document.addPage(mainPage);

			// set the size of the document in points
			mainPage.setMediaBox(new PDRectangle((float) width.getValueInPoints(), (float) height
					.getValueInPoints()));

			// get the content stream
			PDPageContentStream contentStream = new PDPageContentStream(document, mainPage);

			// render all the regions to this page
			Color fillColor = Color.ORANGE;

			for (Region r : regions) {

				// first fill the entire background with cyan
				contentStream.setNonStrokingColor(fillColor);

				Rectangle2D b = r.getUnscaledBounds2D();

				float scaleX = (float) r.getScaleX();
				float scaleY = (float) r.getScaleY();

				Units units = r.getUnits();
				double scale = units.getConversionTo(Points.ONE);

				float xPts = (float) Math.round(scale * b.getX());
				float yPts = (float) Math.round(scale * b.getY());
				float wPts = (float) Math.round(scale * b.getWidth());
				float hPts = (float) Math.round(scale * b.getHeight());

				contentStream.fillRect(xPts * scaleX, yPts * scaleY, wPts * scaleX, hPts * scaleY);
			}
			contentStream.close();

			document.save(new FileOutputStream(destPDFFile));
		} catch (COSVisitorException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (document != null) {
				try {
					document.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * @param activeWithPattern
	 */
	public void setRenderActiveRegionsWithPattern(boolean activeWithPattern) {
		renderActiveRegionsWithPattern = activeWithPattern;
	}
}
