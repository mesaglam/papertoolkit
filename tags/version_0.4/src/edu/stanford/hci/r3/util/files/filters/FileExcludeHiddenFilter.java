package edu.stanford.hci.r3.util.files.filters;

import java.io.File;

import edu.stanford.hci.r3.util.files.FileUtils;
import edu.stanford.hci.r3.util.files.Visibility;

/**
 * <p>
 * A Java FileFilter that excludes hidden files and directories
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 *
 */
public class FileExcludeHiddenFilter extends FileExtensionFilter {

	public FileExcludeHiddenFilter() {
		super(ACCEPT_ALL, true, Visibility.VISIBLE);
	}

	public FileExcludeHiddenFilter(String[] exts) {
		super(exts, true, Visibility.VISIBLE);
	}

	public FileExcludeHiddenFilter(String[] exts, boolean dir) {
		super(exts, dir, Visibility.VISIBLE);
	}

	public boolean accept(File f) {
		if (FileUtils.isHiddenOrDotFile(f)) {
			// System.out.println("Hidden File! accept(file)");
			return false; // exclude hidden files
		} else {
			return super.accept(f);
		}
	}

	public boolean accept(File fileDir, String name) {
		if (FileUtils.isHiddenOrDotFile(new File(fileDir, name))) {
			// System.out.println("Hidden File! accept(fileDir, name)");
			return false; // exclude hidden files
		} else {
			return super.accept(fileDir, name);
		}
	}

}
