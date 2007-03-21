package edu.stanford.hci.r3.flash.whiteboard;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javax.swing.SwingUtilities;

import edu.stanford.hci.r3.PaperToolkit;

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
public class FlashWhiteboard {

	public FlashWhiteboard() {
		File r3RootPath = PaperToolkit.getToolkitRootPath();
		final File whiteBoardHTML = new File(r3RootPath, "flash/bin/Whiteboard.html");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					Desktop.getDesktop().browse(whiteBoardHTML.toURI());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void main(String[] args) {
		new FlashWhiteboard();
	}
}
