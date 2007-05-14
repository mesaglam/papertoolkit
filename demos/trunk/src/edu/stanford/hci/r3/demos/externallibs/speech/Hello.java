package edu.stanford.hci.r3.demos.externallibs.speech;
import java.util.Locale;

import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;

/**
 * Simple program showing how to use FreeTTS using only the Java Speech API (JSAPI).
 */
public class Hello {

	public static void main(String[] args) {

		String voiceName = "kevin16";
		System.out.println();
		System.out.println("Using voice: " + voiceName);

		try {
			/*
			 * Find a synthesizer that has the general domain voice we are looking for. NOTE: this
			 * uses the Central class of JSAPI to find a Synthesizer. The Central class expects to
			 * find a speech.properties file in user.home or java.home/lib.
			 * 
			 * If your situation doesn't allow you to set up a speech.properties file, you can
			 * circumvent the Central class and do a very non-JSAPI thing by talking to
			 * FreeTTSEngineCentral directly. See the WebStartClock demo for an example of how to do
			 * this.
			 */
			SynthesizerModeDesc desc = new SynthesizerModeDesc(null, // engine name
					"general", // mode name
					Locale.US, // locale
					null, // running
					null); // voice
			Synthesizer synthesizer = Central.createSynthesizer(desc);

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
				System.err.println("Synthesizer does not have a voice named " + voiceName + ".");
				System.exit(1);
			}
			synthesizer.getSynthesizerProperties().setVoice(voice);

			/*
			 * Tell the synthesizer to speak and wait for it to complete.
			 */
			synthesizer.speakPlainText("Site564. Notes and Photos Saved.", null);
			synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);

			
			/*
			 * Clean up and leave.
			 */
			synthesizer.deallocate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
