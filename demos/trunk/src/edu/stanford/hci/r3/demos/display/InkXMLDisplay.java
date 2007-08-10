package edu.stanford.hci.r3.demos.display;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import papertoolkit.pen.ink.Ink;
import papertoolkit.tools.components.InkPanel;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.WindowUtils;


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
public class InkXMLDisplay {

	/**
	 * 
	 */
	private static InkPanel inkPanel;

	/**
	 * @return
	 */
	private static InkPanel getInkPanel() {
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

		final JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(f);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			Ink i = new Ink(fc.getSelectedFile());
			DebugUtils.println(i.getNumStrokes() + " strokes loaded.");
			getInkPanel().addInk(i);
		}
	}
}
