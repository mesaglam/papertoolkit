package edu.stanford.hci.r3.util.files;

/**
 * <p>
 * For sorting List<File>...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 *
 */
public enum SortDirection {

	// for alphabetical sorts
	A_TO_Z, Z_TO_A, 

	// for time-based sorts
	NEW_TO_OLD, OLD_TO_NEW,
	
	// for size-based sorts
	SMALL_TO_LARGE, LARGE_TO_SMALL
}
