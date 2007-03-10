package edu.stanford.hci.r3.tools.debugging;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.stanford.hci.r3.util.WindowUtils;

/**
 * <p>
 * 
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class DebugFrame extends JFrame {

	private DebugPCanvas canvas;

	public DebugFrame(String nameOfApp) {
		super("Debugging " + nameOfApp);
		setSize(1024, 768);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		setupContents();

		
		
		setLocation(WindowUtils.getWindowOrigin(this, WindowUtils.DESKTOP_CENTER));
		
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setVisible(true);
	}

	private void setupContents() {
		setLayout(new BorderLayout());
		
		canvas = new DebugPCanvas();
		add(canvas, BorderLayout.CENTER);
	}
	
	public DebugPCanvas getCanvas() {
		return canvas;
	}
}
