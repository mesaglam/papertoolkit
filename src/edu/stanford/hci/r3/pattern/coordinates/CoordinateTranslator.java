package edu.stanford.hci.r3.pattern.coordinates;

/**
 * <p>
 * Given a coordinate in physical (streamed) anoto space, it returns a coordinate in logical (batched) space.
 * Given a coordinate in logical space, it returns a coordinate in physical space.
 * 
 * It does this by finding the pattern package that contains the coordinate. Or, you can pick one
 * explicitly...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class CoordinateTranslator {

	// Batched --> Streaming
	// 48.0.12.8
	// Figure out which pattern package
	// Figure out the page number...
	// Start from the min X & min Y
	// add offsets X & Y * page number
	// adjust for even or odd!
	// Done!

}
