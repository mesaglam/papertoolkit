package edu.stanford.hci.r3.flash;

import java.io.File;

import javax.swing.filechooser.FileSystemView;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.files.FileUtils;

/**
 * <p>
 * Bypasses Flash's Security Sandboxing for local SWF files... This is designed for Windows Systems...
 * 
 * TODO: A different implementation for Mac OS X is needed.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class FlashSecurityRegistration {

	public static void main(String[] args) {
		// Find the FlashPlayer trust directory for this user...
		String pathBeneathHomeDirectory = "Application Data/Macromedia/Flash Player/#Security/FlashPlayerTrust";
		File desktopDirectory = FileSystemView.getFileSystemView().getHomeDirectory().getAbsoluteFile();
		File homeDirectory = desktopDirectory.getParentFile();
		File flashPlayerTrustDir = new File(homeDirectory, pathBeneathHomeDirectory);

		// If it does not exist, we create the directory
		if (!flashPlayerTrustDir.exists()) {
			flashPlayerTrustDir.mkdirs();
		}

		// We add the cfg file (papertoolkit.cfg) listing the trusted directories
		// e.g., PaperToolkit/flash/bin
		File flashBinDir = new File(PaperToolkit.getToolkitRootPath(), "flash/bin");
		String pathToAuthorizeForFlash = flashBinDir.getAbsolutePath();
		
		File destFile = new File(flashPlayerTrustDir, "papertoolkit.cfg");
		FileUtils.writeStringToFile(pathToAuthorizeForFlash, destFile);
		DebugUtils.println("Whitelisted [PaperToolkit/flash/bin] to avoid Flash's Security Sandboxing for Local SWF files...");
	}
}
