package edu.stanford.hci.r3.flash.whiteboard;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import edu.stanford.hci.r3.PaperToolkit;

public class FlashWhiteboard {

	public FlashWhiteboard() {
		File r3RootPath = PaperToolkit.getToolkitRootPath();
		File whiteBoardHTML = new File(r3RootPath, "flash/bin/Whiteboard.html");
		try {
			Desktop.getDesktop().browse(whiteBoardHTML.toURI());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		new FlashWhiteboard();
	}
}
