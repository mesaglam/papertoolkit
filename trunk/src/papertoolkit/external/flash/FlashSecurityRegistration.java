package papertoolkit.external.flash;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import papertoolkit.PaperToolkit;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.files.FileUtils;


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

	private List<File> paths = new ArrayList<File>();

	public static void main(String[] args) {
		new FlashSecurityRegistration().registerPaths();
	}

	public FlashSecurityRegistration() {
		this("papertoolkit.cfg");
	}

	private String fileName;
	
	public FlashSecurityRegistration(String fName) {
		fileName = fName;
	}

	public void addPathToRegister(File path) {
		paths.add(path);
	}

	public void registerPaths() {
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

		StringBuilder pathsToRegister = new StringBuilder();
		pathsToRegister.append(pathToAuthorizeForFlash + "\n");
		for (File p : paths) {
			pathsToRegister.append(p.getAbsolutePath() + "\n");
		}
		
		File destFile = new File(flashPlayerTrustDir, fileName);
		FileUtils.writeStringToFile(pathsToRegister.toString(), destFile);
		DebugUtils.println("Whitelisted [" + pathsToRegister.substring(0, pathsToRegister.length()-1)
				+ "] to avoid Flash's Security Sandboxing for Local SWF files...");
	}
}
