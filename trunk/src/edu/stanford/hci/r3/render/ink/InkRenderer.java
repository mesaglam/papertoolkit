package edu.stanford.hci.r3.render.ink;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.List;

import javax.media.jai.TiledImage;

import edu.stanford.hci.r3.pen.ink.Ink;
import edu.stanford.hci.r3.pen.ink.InkStroke;
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
 * Renders Ink to a Graphics2D or PDF.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class InkRenderer {

	private static final Stroke INK_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_ROUND);

	private Ink ink;

	private RenderingTechnique renderingTechnique = new RenderingTechniqueCatmullRom();

	public InkRenderer() {

	}

	/**
	 * @param theInk
	 */
	public InkRenderer(Ink theInk) {
		ink = theInk;
	}

	/**
	 * @param g2d
	 */
	public void renderToG2D(Graphics2D g2d) {
		if (ink == null) {
			DebugUtils.println("Ink Object is NULL");
			return;
		}

		// anti-aliased, high quality rendering
		g2d.setRenderingHints(GraphicsUtils.getBestRenderingHints());
		g2d.setColor(ink.getColor());
		g2d.setStroke(INK_STROKE);

		final List<InkStroke> strokes = ink.getStrokes();
		renderingTechnique.render(g2d, strokes);
	}

	/**
	 * @param destJPEGFile
	 */
	public void renderToJPEG(File destJPEGFile) {
		double maxX = ink.getMaxX();
		double maxY = ink.getMaxY();

		final int w = MathUtils.rint(maxX);
		final int h = MathUtils.rint(maxY);
		final TiledImage image = JAIUtils.createWritableBufferWithoutAlpha(w, h);
		final Graphics2D graphics2D = image.createGraphics();
		graphics2D.setRenderingHints(GraphicsUtils.getBestRenderingHints());

		// render a white canvas
		graphics2D.setColor(Color.WHITE);
		graphics2D.fillRect(0, 0, w, h);

		renderToG2D(graphics2D);
		graphics2D.dispose();
		ImageUtils.writeImageToJPEG(image.getAsBufferedImage(), destJPEGFile);
		DebugUtils.println("Wrote the File");
	}

	/**
	 * Looks very similar to SheetRenderer's. TODO: Can we integrate this?
	 */
	public void renderToJPEG(File destJPEGFile, Pixels resolutionPixelsPerInch, Units width,
			Units height) {
		final double scale = Points.ONE.getConversionTo(resolutionPixelsPerInch);

		final int w = MathUtils.rint(width.getValueIn(resolutionPixelsPerInch));
		final int h = MathUtils.rint(height.getValueIn(resolutionPixelsPerInch));
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
		DebugUtils.println("Wrote the File");
	}

	/**
	 * @param theInk
	 */
	public void setInk(Ink theInk) {
		ink = theInk;
	}

	public void setRenderingTechnique(RenderingTechnique rt) {
		renderingTechnique = rt;
	}

	public void useCatmullRomRendering() {
		setRenderingTechnique(new RenderingTechniqueCatmullRom());
	}

	public void useLineRendering() {
		setRenderingTechnique(new RenderingTechniqueLinear());
	}

	public void useQuadraticRendering() {
		setRenderingTechnique(new RenderingTechniqueQuadratic());
	}
}
