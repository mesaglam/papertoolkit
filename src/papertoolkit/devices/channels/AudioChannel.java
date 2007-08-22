package papertoolkit.devices.channels;

import java.io.File;

import papertoolkit.actions.types.PlaySoundAction;
import papertoolkit.actions.types.TextToSpeechAction;
import papertoolkit.devices.Device;


/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class AudioChannel {

	private Device parentDevice;

	public AudioChannel(Device device) {
		parentDevice = device;
	}

	/**
	 * @param sound
	 */
	public void playSoundFile(File sound) {
		PlaySoundAction action = new PlaySoundAction(sound);
		parentDevice.invoke(action);
	}

	/**
	 * @param text
	 */
	public void readTextOutLoud(String text) {
		TextToSpeechAction action = new TextToSpeechAction(text);
		parentDevice.invoke(action);
	}
}
