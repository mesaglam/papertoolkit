package papertoolkit.demos.batched.checkboxes;

/**
 * <p>
 * This app demonstrates two features of the toolkit: 1) the batched data upload & event handlers,
 * and 2) creating sheets and regions on the fly, saving them out to disk, and reloading them for
 * future use.
 * </p>
 * <p>
 * The idea is that in Design Mode, a person can draw out a number of checkboxes or inking regions,
 * which will be saved to disk. This is all done in Streaming Mode. The Checkboxes are small
 * squares/rectangles, whereas inking regions are large rectangles.
 * </p>
 * <p>
 * In Use Mode, a person can check these check boxes in any order and as many times as they wish.
 * Then, when the pen is synched, this data will be reported.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Checkboxes {

	/**
	 * This represents the Model of this demonstration app. It will maintain the number and location
	 * of check boxes...
	 */
	public Checkboxes() {

	}

	public static void main(String[] args) {
		new CheckboxGUI(new Checkboxes());
	}

}
