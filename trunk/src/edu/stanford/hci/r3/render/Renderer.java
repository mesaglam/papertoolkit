package edu.stanford.hci.r3.render;

import java.awt.Color;
import java.awt.Graphics2D;
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

import edu.stanford.hci.r3.core.Region;
import edu.stanford.hci.r3.core.Sheet;
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
 */
public class Renderer {

	/**
	 * By Default, any active regions will be overlaid with pattern (unique to at least this sheet,
	 * unless otherwise specified).
	 */
	private boolean renderActiveRegionsWithPattern = true;

	private Sheet sheet;

	public Renderer(Sheet s) {
		sheet = s;
	}

	/**
	 * @param g2d
	 */
	public void renderToG2D(Graphics2D g2d) {

	}

	/**
	 * @param destFile
	 */
	public void renderToJPEG(File destJPEGFile) {

	}

	/**
	 * @param destPDFFile
	 */
	public void renderToPDF(File destPDFFile) {
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
