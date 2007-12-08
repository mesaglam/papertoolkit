package papertoolkit.tools.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import papertoolkit.pen.ink.Ink;
import papertoolkit.pen.ink.InkStroke;
import papertoolkit.render.ink.InkRenderer;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.graphics.GraphicsUtils;

/**
 * <p>
 * Renders Ink in a JPanel using catmull-rom splines. You may swap in simpler renderers if this becomes slow.
 * TODO: Also, this provides some customization that allows you to zoom in and out, pan, and align the ink
 * with imagery. =)
 * </p>
 * <p>
 * If you find that nothing appears, you may want to check whether the ink color and your panel's background
 * color are in fact the same color!
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class InkPanel extends JPanel {

	private static int MAX_ZOOM_MAGNITUDE = 5;

	private int dX = 0;

	private int dY = 0;

	/**
	 * Zoom in (> 1.0) or out (< 1.0) to the ink canvas.
	 */
	private double inkScale = 1.0;

	/**
	 * A collection of ink objects that have been added to this JPanel. Feel free to access it, if you are a
	 * subclass. Make sure to call repaint though!
	 */
	protected List<Ink> inkWell = Collections.synchronizedList(new LinkedList<Ink>());

	/**
	 * 
	 */
	protected boolean invertInkColors = false;

	
	/**
	 * Provides access to the most recently added ink object.
	 */
	protected Ink mostRecentInk;

	private Point mouseDownLocation = new Point();

	/**
	 * adjust these offsets for your application ideally, the ink can center itself
	 */
	private int offX = -0;
	private int offY = -0;

	private String overlayText = "";

	private double paddingLeft = 15;

	private double paddingTop = 25;

	/**
	 * 
	 */
	protected InkRenderer renderer;

	/**
	 * By default, recenters the ink so that it is displayed in the upper left corner. Set it to false if your
	 * coordinates are correct already...
	 */
	private boolean shouldRecenter = true;

	private int tempDX = 0;

	private int tempDY = 0;

	private int zoomLevel = 0;

	/**
	 * Default Catmull-Rom method.
	 */
	public InkPanel() {
		this(new InkRenderer(), new Color(250, 250, 250));
	}

	/**
	 * Choose your own renderer.
	 * 
	 * @param inkRenderer
	 * @param bgColor
	 */
	public InkPanel(InkRenderer inkRenderer, Color bgColor) {
		setBackground(bgColor);
		renderer = inkRenderer;
		renderer.useCatmullRomRendering(); // catmull-rom by default...

		addMouseListeners();
	}

	/**
	 * Add an Ink object to the internal rendering list.
	 * 
	 * @param ink
	 */
	public void addInk(Ink ink) {
		mostRecentInk = ink;
		inkWell.add(ink);
		repaint();
	}

	private void addMouseListeners() {
		addMouseWheelListener(new MouseWheelListener() {

			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getWheelRotation() > 0) { // zoom out
					zoomLevel--;
					if (zoomLevel < -MAX_ZOOM_MAGNITUDE) {
						zoomLevel = -MAX_ZOOM_MAGNITUDE;
					}
				} else {

					zoomLevel++;
					if (zoomLevel > MAX_ZOOM_MAGNITUDE) {
						zoomLevel = MAX_ZOOM_MAGNITUDE;
					}
				}
				repaint();
			}
		});

		addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				DebugUtils.println("Clicked " + e.getPoint());
				if (SwingUtilities.isRightMouseButton(e)) {
					// re-animate the ink...
					DebugUtils.println("Animating...");
					animateInk();
				}
			}

			public void mouseEntered(MouseEvent e) {
				DebugUtils.println("Entered " + e.getPoint());
			}

			public void mouseExited(MouseEvent e) {
				DebugUtils.println("Exited " + e.getPoint());
			}

			public void mousePressed(MouseEvent e) {
				DebugUtils.println("Pressed " + e.getPoint());
				mouseDownLocation = e.getPoint();
			}

			public void mouseReleased(MouseEvent e) {
				DebugUtils.println("Released " + e.getPoint());
				dX = dX + e.getPoint().x - mouseDownLocation.x;
				dY = dY + e.getPoint().y - mouseDownLocation.y;
				tempDX = 0;
				tempDY = 0;
				repaint();
			}
		});
		addMouseMotionListener(new MouseMotionListener() {

			public void mouseDragged(MouseEvent e) {
				// DebugUtils.println("Dragged " + e.getPoint());

				Point localPoint = e.getPoint();
				tempDX = localPoint.x - mouseDownLocation.x;
				tempDY = localPoint.y - mouseDownLocation.y;

				repaint();
			}

			public void mouseMoved(MouseEvent e) {
				// DebugUtils.println("Moved " + e);
			}
		});

	}

	/**
	 * @return the newly added Ink object. Feel free to add strokes to the returned Ink object... as long as
	 *         you refresh this InkPanel occasionally, it should show up!
	 */
	public Ink addNewInk() {
		final Ink newInk = new Ink();
		addInk(newInk);
		return newInk;
	}

	/**
	 * TODO: There's a known bug... if you right-click twice, you may lose ink strokes because we are actually
	 * modifying the list. Also, if a second thread starts up,we should probably kill the first thread...
	 */
	private void animateInk() {
		new Thread(new Runnable() {
			public void run() {
				List<Ink> inkWellToAnimate = inkWell;
				inkWell = Collections.synchronizedList(new LinkedList<Ink>());
				repaint();

				for (Ink i : inkWellToAnimate) {
					Ink inkToAdd = new Ink();
					inkWell.add(inkToAdd);

					List<InkStroke> strokes = i.getStrokes();
					for (InkStroke s : strokes) {
						inkToAdd.addStroke(s);
						repaint();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
	}

	/**
	 * Remove all the ink from the ink panel.
	 */
	public void clear() {
		inkWell.clear();
		repaint();
	}

	/**
	 * 
	 */
	public void displayInvertedInkColor() {
		invertInkColors = true;
	}

	/**
	 * @return the reference to the ink. This is NOT a copy!
	 */
	public List<Ink> getAllInk() {
		return inkWell;
	}

	/**
	 * @return
	 */
	public double getScale() {
		return inkScale;
	}

	/**
	 * Redraw the ink strokes.
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		final Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHints(GraphicsUtils.getBestRenderingHints());

		g2d.translate(dX + tempDX, dY + tempDY);
		double scale = Math.pow(1.15, zoomLevel);
		g2d.scale(scale, scale);

		if (!overlayText.equals("")) {
			g2d.setColor(Color.WHITE);
			g2d.setFont(new Font("Tahoma", Font.BOLD, 72));
			g2d.drawString(overlayText, 40, 350); // a hack for demo
		}

		final AffineTransform transform = g2d.getTransform();
		g2d.translate(offX, offY);
		g2d.scale(inkScale, inkScale);

		drawInk(g2d);

		g2d.setTransform(transform);
	}

	protected void drawInk(final Graphics2D g2d) {
		// the drawing of ink is "atomic" with respect to
		// adding and removing from the inkWell
		synchronized (inkWell) {
			recenter(g2d, inkWell);

			for (Ink ink : inkWell) {
				renderer.setInk(ink);
				if (invertInkColors) {
					renderer.useInvertedInkColors();
				}
				renderer.renderToG2D(g2d);
			}
		}
	}

	/**
	 * @param g2d
	 */
	protected void recenter(Graphics2D g2d, List<Ink> inks) {
		if (!shouldRecenter) {
			return;
		}

		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		for (Ink ink : inks) {
			minX = Math.min(ink.getMinX(), minX);
			minY = Math.min(ink.getMinY(), minY);
		}
		g2d.translate(-(minX - paddingLeft), -(minY - paddingTop));
	}

	/**
	 * Used to remove the most-recently added ink object.
	 */
	public void removeLastBatchOfInk() {
		synchronized (inkWell) {
			if (inkWell.size() > 0) {
				inkWell.remove(inkWell.size() - 1);
				repaint();
			}
		}
	}

	/**
	 * @param oX
	 * @param oY
	 */
	public void set2DOffset(int oX, int oY) {
		offX = oX;
		offY = oY;
	}

	/**
	 * @param theInk
	 */
	public void setAllInk(List<Ink> theInk) {
		inkWell = theInk;
		repaint();
	}

	public void setBackground(Color bgColor) {
		super.setBackground(bgColor);
		if ((bgColor.getRed() + bgColor.getBlue() + bgColor.getGreen()) / 3.0 < 128) {
			displayInvertedInkColor();
		}
	}

	public void setOverlayText(String text) {
		overlayText = text;
		repaint();
	}

	/**
	 * @param recFlag
	 */
	public void setRecenterFlag(boolean recFlag) {
		shouldRecenter = recFlag;
	}

	/**
	 * @param r
	 */
	public void setRenderer(InkRenderer r) {
		renderer = r;
	}

	/**
	 * @param theScale
	 */
	public void setScale(double theScale) {
		inkScale = theScale;
	}
	
	public void useLinearRendering() {
		renderer.useLineRendering();
	}
	
	public void useCatmullRomRendering() {
		renderer.useCatmullRomRendering();
	}

	public void useHybridRendering() {
		renderer.useHybridRendering();
	}

	public void useQuadraticRendering() {
		renderer.useQuadraticRendering();
	}

	public void setDebugRendering(boolean flag) {
		renderer.setDebugRendering(flag);
	}

}
