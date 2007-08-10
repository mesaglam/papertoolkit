package papertoolkit.util.files.filters;

import java.io.File;
import java.io.FileFilter;

import papertoolkit.util.files.FileUtils;
import papertoolkit.util.files.Visibility;


/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * We assume that directories that start with '.' (like .cvs, .svn, .ssh) are hidden directories.
 */
public class DirectoriesOnlyFilter implements FileFilter {

	/**
	 * Should we use the visibility flag?
	 */
	private boolean useVisibility = true;

	/**
	 * Which visibility will we accept? Defaults to Visible Directories.
	 */
	private Visibility visibility = Visibility.VISIBLE;

	/**
	 * If flag==false, we match directories of either visibility (we don't check the flag).
	 * 
	 * @param useVisibilityFlag
	 */
	public DirectoriesOnlyFilter(boolean useVisibilityFlag) {
		useVisibility = useVisibilityFlag;
	}

	/**
	 * Will accept only directories that match the Visibility flag.
	 * 
	 * @param v
	 */
	public DirectoriesOnlyFilter(Visibility v) {
		visibility = v;
	}

	/**
	 * Tests whether we will accept this file or not.
	 * 
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File f) {
		if (!f.isDirectory()) {
			// this is a file; we keep only directories
			return false;
		}

		if (!useVisibility) {
			// we don't check the flag
			return true;
		}

		// we check the visibility flag here
		// only true if visibility matches the specification
		// invisible && hidden works
		// visible && !hidden works
		// both && (hidden || !hidden) works
		if (visibility == Visibility.BOTH) {
			return true;
		}
		return (visibility == Visibility.INVISIBLE) == FileUtils.isHiddenOrDotFile(f);
	}
}
