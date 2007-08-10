package papertoolkit.tools.design.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import papertoolkit.paper.Sheet;
import papertoolkit.units.Pixels;
import papertoolkit.util.MathUtils;
import papertoolkit.util.graphics.GraphicsUtils;


/**
 * <p>
 * Renders the document in our interactive designer. It actually renders the Sheet to a JPEG, so we
 * don't have to do interactive rendering (which sucks for complicated sheets).
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class DocumentPanel extends JPanel {

	/**
	 * Gradient Top.
	 */
	private static final Color BG_BEGIN_COLOR = new Color(114, 121, 139);

	/**
	 * Gradient Bottom.
	 */
	private static final Color BG_END_COLOR = new Color(167, 177, 202);

	/**
	 * Drop shadow for the (on screen) rendered paper
	 */
	public static final int DROP_SHADOW_DISTANCE = 4;

	private static final int MIN_PADDING_HORIZ = 20;

	private static final int MIN_PADDING_VERT = 25;

	/**
	 * Color of the paper's drop shadow.
	 */
	public static final Color SHADOW_COLOR = new Color(0, 0, 0, 99);

	/**
	 * extra space to the left AND right of the document (pixels)
	 */
	private int paddingHorizontal = MIN_PADDING_HORIZ;

	/**
	 * above AND below the document (screen pixels)
	 */
	private int paddingVertical = MIN_PADDING_VERT;

	private JFrame parentFrame;

	private JScrollPane parentScrollPane;

	final Pixels pixelsReferenceUnit = new Pixels();

	/**
	 * 
	 */
	private Sheet sheet = new Sheet(8.5, 11);

	/**
	 * @param s
	 */
	public DocumentPanel() {
		setBackground(Color.WHITE);
		setLayout(new BorderLayout());
	}

	/**
	 * @param g2d
	 * 
	 * @created Feb 16, 2006
	 * @author Ron Yeh
	 */
	private void drawGradientBackground(Graphics2D g2d) {
		final GradientPaint gradient = new GradientPaint(0, 0, BG_BEGIN_COLOR, 0, getHeight(),
				BG_END_COLOR, false);
		g2d.setPaint(gradient);
		g2d.fillRect(0, 0, getWidth(), getHeight());
	}

	/**
	 * @return
	 * @created Feb 26, 2006
	 * @author Ron Yeh
	 */
	public int getDefaultHorizontalScreenPadding() {
		return 2 * paddingHorizontal;
	}

	/**
	 * @return number of pixels to pad the document vertically.
	 * @created Feb 26, 2006
	 * @author Ron Yeh
	 */
	public int getDefaultVerticalScreenPadding() {
		return 2 * paddingVertical;
	}

	private int getDisplayedDocumentHeight() {
		return MathUtils.rint(sheet.getHeight().getValueIn(pixelsReferenceUnit));
	}

	private int getDisplayedDocumentWidth() {
		return MathUtils.rint(sheet.getWidth().getValueIn(pixelsReferenceUnit));
	}

	/**
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
		return new Dimension(getDisplayedDocumentWidth() + getDefaultHorizontalScreenPadding()
				- parentScrollPane.getVerticalScrollBar().getWidth(), getDisplayedDocumentHeight()
				+ getDefaultVerticalScreenPadding()
				- parentScrollPane.getHorizontalScrollBar().getHeight());
	}

	/**
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 * 
	 * The approach we take is to draw all the background materials, and then let the Document paint
	 * itself. This enables the Document to be placed in a full screen window, or some other cool
	 * layout.
	 */
	protected void paintComponent(Graphics g) {
		// do whatever JPanel/JComponent likes to do...
		super.paintComponent(g);

		final Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHints(GraphicsUtils.getBestRenderingHints());

		// smooth gradient
		drawGradientBackground(g2d);

		// convert this to pixels
		int widthPixels = getDisplayedDocumentWidth();
		int heightPixels = getDisplayedDocumentHeight();
		// System.out.println("Sheet size is: " + sheet.getWidth() + ", " + sheet.getHeight());
		// System.out.println("Sheet size is: " + widthPixels + ", " + heightPixels);

		int panelWidth = parentFrame.getWidth();
		int panelHeight = parentScrollPane.getHeight();

		paddingHorizontal = Math.max(MathUtils.rint((panelWidth - widthPixels) / 2.0),
				MIN_PADDING_HORIZ);
		paddingVertical = Math.max(MathUtils.rint((panelHeight - heightPixels) / 2.0),
				MIN_PADDING_VERT);

		// draw the jpeg image of the sheet...

		// the dark drop shadow
		g2d.setColor(SHADOW_COLOR);
		final Rectangle docBounds = new Rectangle(paddingHorizontal, paddingVertical, widthPixels,
				heightPixels);
		g2d.fillRect((int) docBounds.getX() + DROP_SHADOW_DISTANCE, (int) docBounds.getY()
				+ DROP_SHADOW_DISTANCE, docBounds.width, docBounds.height);

	}

	public void setParentFrame(JFrame mainFrame) {
		parentFrame = mainFrame;
	}

	public void setParentScrollPane(JScrollPane scrollPane) {
		parentScrollPane = scrollPane;
	}
}
