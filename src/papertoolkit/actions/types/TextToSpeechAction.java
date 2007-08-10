package papertoolkit.actions.types;

import java.beans.PropertyVetoException;
import java.util.Locale;

import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;

import papertoolkit.actions.Action;


/**
 * <p>
 * Speaks some text, if the receiving machine has FreeTTS installed (or some other JSAPI engine).
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class TextToSpeechAction implements Action {

	private static final String DEFAULT_FREETTS_VOICE = "kevin16";

	private static TextToSpeechAction instance;

	/**
	 * @return
	 */
	public static TextToSpeechAction getInstance() {
		if (instance == null) {
			instance = new TextToSpeechAction();
			instance.initialize();
		}
		return instance;
	}

	private boolean initialized;

	/**
	 * Speak this text...
	 */
	private String savedText;

	private Synthesizer synthesizer;

	private String voiceName = DEFAULT_FREETTS_VOICE;

	/**
	 * 
	 */
	public TextToSpeechAction() {
		this("");
	}

	/**
	 * 
	 */
	public TextToSpeechAction(String wordsToSay) {
		savedText = wordsToSay;
		// leave everything else null until it is invoked!
	}

	/**
	 * Clean up resources.
	 */
	public void close() {
		try {
			synthesizer.deallocate();
		} catch (EngineException e) {
			e.printStackTrace();
		} catch (EngineStateError e) {
			e.printStackTrace();
		}
		initialized = false;
	}

	/**
	 * 
	 */
	public void initialize() {
		if (initialized) {
			return; // already initialized
		}

		initialized = true;
		try {
			voiceName = DEFAULT_FREETTS_VOICE;
			SynthesizerModeDesc desc = new SynthesizerModeDesc(null, // engine name
					"general", // mode name
					Locale.US, // locale
					null, // running
					null); // voice
			synthesizer = Central.createSynthesizer(desc);

			// Get the synthesizer ready to speak
			synthesizer.allocate();
			synthesizer.resume();

			// Choose the voice.
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
	 * Remote invocations should use this method. Local invocations may also use this, but it might
	 * be more efficient to keep a TextToSpeechAction around, initialize it, and then call
	 * speak(String) repeatedly.
	 * 
	 * @see papertoolkit.actions.Action#invoke()
	 */
	public void invoke() {
		initialize();
		speak();
		close();
	}

	/**
	 * 
	 */
	public void speak() {
		speak(savedText);
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
}
