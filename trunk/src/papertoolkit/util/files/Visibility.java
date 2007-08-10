package papertoolkit.util.files;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * Represents whether a File is Hidden/Visible. Used in FileUtils and our custom FileFilters. If
 * BOTH is chosen, then we will accept both hidden & visible types of files/dirs.
 */
public enum Visibility {
	BOTH, 
	INVISIBLE,
	VISIBLE
}
