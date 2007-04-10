package edu.stanford.hci.r3.pen.ink;

import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.stanford.hci.r3.components.InkPanel;
import edu.stanford.hci.r3.util.WindowUtils;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Test_InkPanel {

	/**
	 * 
	 */
	private static InkPanel inkPanel;

	/**
	 * @return
	 */
	private static Container getInkPanel() {
		if (inkPanel == null) {
			inkPanel = new InkPanel();
		}
		return inkPanel;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setContentPane(getInkPanel());
		f.setSize(1024, 768);
		f.setLocation(WindowUtils.getWindowOrigin(f, WindowUtils.DESKTOP_CENTER));
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}
