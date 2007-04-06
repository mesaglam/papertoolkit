package edu.stanford.hci.r3.demos.biomap;

import java.beans.PropertyVetoException;
import java.util.Locale;

import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;

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
public class AudioFeedback {
	private String voiceName;

	private Synthesizer synthesizer;

	public AudioFeedback() {
		try {
			voiceName = "kevin16";
			SynthesizerModeDesc desc = new SynthesizerModeDesc(null, // engine name
					"general", // mode name
					Locale.US, // locale
					null, // running
					null); // voice
			synthesizer = Central.createSynthesizer(desc);

			/*
			 * Get the synthesizer ready to speak
			 */
			synthesizer.allocate();
			synthesizer.resume();

			/*
			 * Choose the voice.
			 */
			desc = (SynthesizerModeDesc) synthesizer.getEngineModeDesc();
			Voice[] voices = desc.getVoices();
			Voice voice = null;
			for (Voice v : voices) {
				if (v.getName().equals(voiceName)) {
					voice = v;
					break;
				}
			}
			if (voice == null) {
				System.err.println("Synthesizer does not have a voice named " + voiceName
						+ ". Choosing the first available voice.");
				voice = voices[0];
			}
			synthesizer.getSynthesizerProperties().setVoice(voice);

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (EngineException e) {
			e.printStackTrace();
		} catch (AudioException e) {
			e.printStackTrace();
		} catch (EngineStateError e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param wordsToSpeak
	 */
	public void speak(String wordsToSpeak) {
		// Tell the synthesizer to speak and wait for it to complete.
		try {
			synthesizer.speakPlainText(wordsToSpeak, null);
			synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		/*
		 * Clean up and leave.
		 */
		try {
			synthesizer.deallocate();
		} catch (EngineException e) {
			e.printStackTrace();
		} catch (EngineStateError e) {
			e.printStackTrace();
		}
	}
}
