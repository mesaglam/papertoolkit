package edu.stanford.hci.r3.demos.batched.checkboxes;

import javax.swing.JFrame;

import edu.stanford.hci.r3.PaperToolkit;

/**
 * <p></p>
 * <p><span class="BSDLicense">
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span></p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class CheckboxGUI extends JFrame {

	public CheckboxGUI() {
		PaperToolkit.initializeLookAndFeel();
		
	}
	
	public static void main(String[] args) {
		CheckboxGUI gui = new CheckboxGUI();
		gui.setVisible(true);
		gui.pack();
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
