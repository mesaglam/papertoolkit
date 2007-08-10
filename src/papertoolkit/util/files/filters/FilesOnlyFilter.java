package papertoolkit.util.files.filters;

import papertoolkit.util.files.Visibility;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * This is a convenience class (for readability) that creates a filter for files only.
 */
public class FilesOnlyFilter extends FileExtensionFilter {

	/**
	 * Do not accept directories.
	 */
	private static final boolean NO_DIRECTORIES = false;

	/**
	 * Accepts files with any extension, no directories, and no hidden files.
	 */
	public FilesOnlyFilter() {
		super(new String[] { "" }, NO_DIRECTORIES, Visibility.BOTH);
	}

	/**
	 * No Directories, No Hidden Files.
	 * 
	 * @param exts
	 *            only these extensions.
	 */
	public FilesOnlyFilter(String[] exts, Visibility visible) {
		super(exts, NO_DIRECTORIES, visible);
	}
}
