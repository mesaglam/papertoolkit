package edu.stanford.hci.r3.pen;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.stanford.hci.r3.util.DebugUtils;

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
	private JPanel inputPanel;

	@Override
	public void startLiveMode() {
		// start up a JFrame w/ GUI to get mouse input...
		frame = new JFrame("Pen Simulator");
		frame.setContentPane(getInputPanel());
		// if you exit your pen, we might as well close the entire paper application
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		liveMode = true;
	}

	private JPanel getInputPanel() {
		if (inputPanel == null) {
			inputPanel = new JPanel();
			inputPanel.setPreferredSize(new Dimension(850, 1100));
			inputPanel.addMouseListener(this);
			inputPanel.addMouseMotionListener(this);
		}
		return inputPanel;
	}

	@Override
	public void stopLiveMode() {
		frame.dispose();
		frame = null;
		liveMode = false;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		DebugUtils.println("Pressed: " + e.getX() + ", " + e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		DebugUtils.println("Released: " + e.getX() + ", " + e.getY());
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		DebugUtils.println("Dragging: " + e.getX() + ", " + e.getY());
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
}
