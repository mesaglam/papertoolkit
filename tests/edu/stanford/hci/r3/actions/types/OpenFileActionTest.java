package edu.stanford.hci.r3.actions.types;

import java.io.File;

/**
 * 
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class OpenFileActionTest {
	public static void main(String[] args) {
		openLocalFile();
	}

	private static void openLocalFile() {
		OpenFileAction oaction = new OpenFileAction(new File("data/testFiles/ButterflyNetCHI2006.pdf"));
		oaction.invoke();
	}
}
