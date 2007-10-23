package papertoolkit.util.files.filters;

import java.io.File;

import papertoolkit.util.files.FileUtils;
import papertoolkit.util.files.Visibility;

/**
 * @author ronyeh
 * 
 * A Java FileFilter that excludes hidden files and directories, and also excludes things that match a
 * filename pattern (case sensitive)
 */
public class FileExcludeHiddenAndPatternFilter extends FileExtensionFilter {

	// if the file name contains this pattern (and the pattern != null) exclude it
	private String patternToExclude;

	/**
	 * @param exts
	 *            the extensions to accept
	 * @param pattToExclude
	 *            the pattern to exclude
	 */
	public FileExcludeHiddenAndPatternFilter(String[] exts, String pattToExclude) {
		super(exts, true, Visibility.VISIBLE);
		patternToExclude = pattToExclude;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File f) {
		if (FileUtils.isHiddenOrDotFile(f)) {
			// System.out.println("Hidden File! accept(file)");
			return false; // exclude hidden files
		} else if (patternToExclude != null && f.getName().contains(patternToExclude)) {
			return false; // exclude ones that match the exclusion pattern
		} else {
			return super.accept(f);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	public boolean accept(File fileDir, String name) {
		File childFile = new File(fileDir, name);
		if (FileUtils.isHiddenOrDotFile(childFile)) {
			// System.out.println("Hidden File! accept(fileDir, name)");
			return false; // exclude hidden files
		} else if (childFile.getName().toLowerCase().equals(patternToExclude)) {
			return false; // exclude ones that match the exclusion pattern
		} else {
			return super.accept(fileDir, name);
		}
	}
}
