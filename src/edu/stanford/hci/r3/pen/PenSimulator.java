package edu.stanford.hci.r3.pen;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import edu.stanford.hci.r3.components.InkPanel;
import edu.stanford.hci.r3.pen.streaming.listeners.PenListener;
import edu.stanford.hci.r3.util.WindowUtils;

/**
 * <p>
 * Opens a JFrame/JPanel that the user can draw on with his mouse or Tablet stylus. This will simulate a
 * digital pen. We can make the pen activate either by toggling (via left-cli
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class PenSimulator extends PenInput implements MouseListener, MouseMotionListener {

	private JFrame frame;
	private InkPanel inputPanel;
	private List<PenListener> listeners = new ArrayList<PenListener>();

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
			inputPanel.setPreferredSize(new Dimension(593, 768));
			inputPanel.addMouseListener(this);
			inputPanel.addMouseMotionListener(this);
		}
		return inputPanel;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// DebugUtils.println("Dragging: " + e.getX() + ", " + e.getY());
		for (PenListener l : listeners) {
			l.sample(new PenSample(e.getX(), e.getY(), 128, System.currentTimeMillis()));
		}
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
		// DebugUtils.println("Pressed: " + e.getX() + ", " + e.getY());
		for (PenListener l : listeners) {
			l.penDown(new PenSample(e.getX(), e.getY(), 128, System.currentTimeMillis()));
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// DebugUtils.println("Released: " + e.getX() + ", " + e.getY());
		for (PenListener l : listeners) {
			l.penUp(new PenSample(e.getX(), e.getY(), 128, System.currentTimeMillis()));
		}
	}

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
