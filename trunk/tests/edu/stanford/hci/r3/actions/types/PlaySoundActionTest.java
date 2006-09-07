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
public class PlaySoundActionTest {
	public static void main(String[] args) {
		// PlaySoundAction action = new PlaySoundAction(new File("data/testFiles/Tweety.wav"));
		PlaySoundAction action = new PlaySoundAction(new File(
				"data/testFiles/CodeMonkey_MonoClip.mp3"));
		action.invoke();
		System.out.println("You should see this immediately, regardless of "
				+ "how long the sound clip is.");
	}
}
