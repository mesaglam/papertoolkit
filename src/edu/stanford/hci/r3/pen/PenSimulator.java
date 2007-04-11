package edu.stanford.hci.r3.pen;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import edu.stanford.hci.r3.components.InkPanel;
import edu.stanford.hci.r3.pen.ink.Ink;
import edu.stanford.hci.r3.pen.ink.InkSimplification;
import edu.stanford.hci.r3.pen.ink.InkStroke;
import edu.stanford.hci.r3.pen.streaming.listeners.PenListener;
import edu.stanford.hci.r3.util.WindowUtils;

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
public class PenSimulator extends PenInput implements MouseListener, MouseMotionListener {

	private InkStroke currentStroke;
	private JFrame frame;
	private Ink ink;

	private InkPanel inputPanel;
	private List<PenListener> listeners = new ArrayList<PenListener>();

	private InkSimplification simplifier = new InkSimplification();

	public PenSimulator() {
		super("Pen Simulator");
	}

	public void addLivePenListener(PenListener penListener) {
		// we don't need to call the super's method, because we just keep our own list of penListeners...
		listeners.add(penListener);
	}

	private InkPanel getInputPanel() {
		if (inputPanel == null) {
			inputPanel = new InkPanel();

			// set the background of the panel
			inputPanel.setOpaque(false);
			inputPanel.setPreferredSize(new Dimension(593, 768));
			inputPanel.addMouseListener(this);
			inputPanel.addMouseMotionListener(this);
			inputPanel.displayInvertedInkColor();

			ink = inputPanel.addNewInk();
		}
		return inputPanel;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			return;
		}

		// DebugUtils.println("Dragging: " + e.getX() + ", " + e.getY());
		PenSample penSample = new PenSample(e.getX(), e.getY(), 128, System.currentTimeMillis());

		for (PenListener l : listeners) {
			l.sample(penSample);
		}
		currentStroke.addSample(penSample);
		inputPanel.repaint();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			return;
		}

		// DebugUtils.println("Pressed: " + e.getX() + ", " + e.getY());
		PenSample sample = new PenSample(e.getX(), e.getY(), 128, System.currentTimeMillis());
		for (PenListener l : listeners) {
			l.penDown(sample);
		}
		currentStroke = new InkStroke();
		currentStroke.addSample(sample);
		ink.addStroke(currentStroke);
		inputPanel.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			// clear the board!
			inputPanel.clear();
			ink = inputPanel.addNewInk();
		} else {
			// DebugUtils.println("Released: " + e.getX() + ", " + e.getY());
			PenSample penSample = new PenSample(e.getX(), e.getY(), 0, System.currentTimeMillis());
			penSample.setPenUp(true);
			for (PenListener l : listeners) {
				l.penUp(penSample);
			}
			currentStroke.addSample(penSample);

			// simplify this stroke!
			simplifier.simplifyStroke(currentStroke);

			inputPanel.repaint();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.stanford.hci.r3.pen.PenInput#removeLivePenListener(edu.stanford.hci.r3.pen.streaming.listeners.PenListener)
	 */
	public void removeLivePenListener(PenListener penListener) {
		listeners.remove(penListener);
	}

	@Override
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

	@Override
	public void stopLiveMode() {
		frame.dispose();
		frame = null;
		liveMode = false;
	}
}
