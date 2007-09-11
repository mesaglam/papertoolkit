package papertoolkit.pen;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import papertoolkit.pen.ink.Ink;
import papertoolkit.pen.ink.InkSimplification;
import papertoolkit.pen.ink.InkStroke;
import papertoolkit.pen.streaming.listeners.PenListener;
import papertoolkit.tools.components.InkPanel;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.WindowUtils;

/**
 * <p>
 * Opens a JFrame/JPanel that the user can draw on with his mouse or Tablet stylus. This will simulate a
 * digital pen. We can make the pen activate by holding the left mouse button down.
 * </p>
 * <p>
 * This also contains a simple r-theta based ink simplification algorithm. Basically, if the distance of the
 * current point is sufficiently far from the last two points, in terms of distance (r) or direction (theta),
 * then we trigger a new pen sample. This algorithm might be applied to actual pen input in the future, to
 * achieve ink simplification/beautification, etc.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PenSimulator extends InputDevice {

	private InkStroke currentStroke;
	private JFrame frame;
	private Ink ink;
	private InkPanel inputPanel;
	private InkSimplification simplifier = new InkSimplification();
	private JPanel offsetPanel;
	private JTextField xField;
	private JTextField yField;

	public PenSimulator() {
		super("Pen Simulator");
	}

	public void addLivePenListener(PenListener penListener) {
		super.addLivePenListener(penListener);
	}

	/**
	 * @return a JPanel that you can click and drag on to simulate pen input.
	 */
	private InkPanel getInputPanel() {
		if (inputPanel == null) {
			inputPanel = new InkPanel();
			inputPanel.setRecenterFlag(false);

			// set the background of the panel
			inputPanel.setOpaque(false);
			inputPanel.setPreferredSize(new Dimension(593, 768));
			inputPanel.addMouseListener(getMouseAdapter());
			inputPanel.addMouseMotionListener(getMouseMotionAdapter());
			inputPanel.displayInvertedInkColor();

			ink = inputPanel.addNewInk();
		}
		return inputPanel;
	}

	private MouseMotionListener getMouseMotionAdapter() {
		return new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				final int offsetX = getOffsetX();
				final int offsetY = getOffsetY();
				
				inputPanel.set2DOffset(-offsetX, -offsetY);

				if (SwingUtilities.isRightMouseButton(e)) {
					return;
				}
				PenSample penSample = new PenSample(offsetX + e.getX(), offsetY + e.getY(), 128,
						System.currentTimeMillis());
				for (PenListener l : getPenListeners()) {
					l.sample(penSample);
				}
				currentStroke.addSample(penSample);
				inputPanel.repaint();
			}

		};
	}

	private MouseListener getMouseAdapter() {
		return new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				inputPanel.set2DOffset(-getOffsetX(), -getOffsetY());

				if (SwingUtilities.isRightMouseButton(e)) {
					return;
				}
				PenSample sample = new PenSample(getOffsetX() + e.getX(), getOffsetY() + e.getY(), 128,
						System.currentTimeMillis());
				for (PenListener l : getPenListeners()) {
					l.penDown(sample);
				}
				currentStroke = new InkStroke();
				currentStroke.addSample(sample);
				ink.addStroke(currentStroke);
				inputPanel.repaint();
			}

			public void mouseReleased(MouseEvent e) {
				inputPanel.set2DOffset(-getOffsetX(), -getOffsetY());

				if (SwingUtilities.isRightMouseButton(e)) {
					// clear the board!
					inputPanel.clear();
					ink = inputPanel.addNewInk();
				} else {
					PenSample penSample = new PenSample(getOffsetX() + e.getX(), getOffsetY() + e.getY(), 0,
							System.currentTimeMillis());
					penSample.setPenUp(true);
					for (PenListener l : getPenListeners()) {
						l.penUp(penSample);
					}
					currentStroke.addSample(penSample);

					// simplify this stroke!
					simplifier.simplifyStroke(currentStroke);

					inputPanel.repaint();
				}
			}
		};
	}

	protected int getOffsetY() {
		if (yField == null) {
			return 0;
		}
		try {
			int yOff = Integer.parseInt(yField.getText());
			return yOff;
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	protected int getOffsetX() {
		if (xField == null) {
			return 0;
		}
		try {
			int xOff = Integer.parseInt(xField.getText());
			return xOff;
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see papertoolkit.pen.InputDevice#startLiveMode()
	 */
	public void startLiveMode() {
		// start up a JFrame w/ GUI to get mouse input...
		frame = new JFrame("Pen Simulator");
		frame.setLayout(new BorderLayout());
		frame.add(getOffsetPanel(), BorderLayout.NORTH);
		frame.add(getInputPanel(), BorderLayout.CENTER);

		// if you exit your pen, we might as well close the entire paper application
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocation(WindowUtils.getWindowOrigin(frame, WindowUtils.DESKTOP_CENTER));
		frame.setVisible(true);
		liveMode = true;
	}

	private Component getOffsetPanel() {
		if (offsetPanel == null) {
			offsetPanel = new JPanel();
			offsetPanel.add(new JLabel("X Offset: "));
			xField = new JTextField(20);
			xField.setText("0");
			offsetPanel.add(xField);
			offsetPanel.add(new JLabel("Y Offset: "));
			yField = new JTextField(20);
			yField.setText("0");
			offsetPanel.add(yField);
		}
		return offsetPanel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see papertoolkit.pen.InputDevice#stopLiveMode()
	 */
	public void stopLiveMode() {
		frame.dispose();
		frame = null;
		liveMode = false;
	}
}
