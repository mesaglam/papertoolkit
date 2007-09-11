package papertoolkit.pen;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import papertoolkit.pen.ink.Ink;
import papertoolkit.pen.ink.InkSimplification;
import papertoolkit.pen.ink.InkStroke;
import papertoolkit.pen.streaming.listeners.PenListener;
import papertoolkit.tools.components.InkPanel;
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
				if (SwingUtilities.isRightMouseButton(e)) {
					return;
				}

				// DebugUtils.println("Dragging: " + e.getX() + ", " + e.getY());
				PenSample penSample = new PenSample(e.getX(), e.getY(), 128, System.currentTimeMillis());
				for (PenListener l : penListeners) {
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
				if (SwingUtilities.isRightMouseButton(e)) {
					return;
				}

				// DebugUtils.println("Pressed: " + e.getX() + ", " + e.getY());
				PenSample sample = new PenSample(e.getX(), e.getY(), 128, System.currentTimeMillis());
				for (PenListener l : penListeners) {
					l.penDown(sample);
				}
				currentStroke = new InkStroke();
				currentStroke.addSample(sample);
				ink.addStroke(currentStroke);
				inputPanel.repaint();
			}

			public void mouseReleased(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					// clear the board!
					inputPanel.clear();
					ink = inputPanel.addNewInk();
				} else {
					// DebugUtils.println("Released: " + e.getX() + ", " + e.getY());
					PenSample penSample = new PenSample(e.getX(), e.getY(), 0, System.currentTimeMillis());
					penSample.setPenUp(true);
					for (PenListener l : penListeners) {
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


	/*
	 * (non-Javadoc)
	 * 
	 * @see papertoolkit.pen.InputDevice#startLiveMode()
	 */
	public void startLiveMode() {
		// start up a JFrame w/ GUI to get mouse input...
		frame = new JFrame("Pen Simulator");
		frame.setContentPane(getInputPanel());

		// if you exit your pen, we might as well close the entire paper application
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocation(WindowUtils.getWindowOrigin(frame, WindowUtils.DESKTOP_CENTER));
		frame.setVisible(true);
		liveMode = true;
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
