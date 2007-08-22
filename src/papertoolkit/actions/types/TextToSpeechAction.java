package papertoolkit.actions.types;

import java.beans.PropertyVetoException;
import java.io.File;
import java.util.Locale;

import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;

import papertoolkit.PaperToolkit;
import papertoolkit.actions.Action;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.files.FileUtils;

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

	/**
	 * The default Free TTS voice name. In the future, the voice will be customizable.
	 */
	private static final String DEFAULT_FREETTS_VOICE = "kevin16";

	/**
	 * Probably more efficient if local implementations only use one instance...
	 */
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

	/**
	 * Allows us to track whether we have allocated a synthesizer object...
	 */
	private boolean initialized;

	/**
	 * 
	 */
	private Synthesizer synthesizer;

	/**
	 * Speak this text...
	 */
	private String textToSpeak;

	/**
	 * 
	 */
	private String voiceName = DEFAULT_FREETTS_VOICE;

	/**
	 * Say Nothing by Default...
	 */
	public TextToSpeechAction() {
		this("");
	}

	/**
	 * Nothing is allocated until the first invocation...
	 */
	public TextToSpeechAction(String wordsToSay) {
		textToSpeak = wordsToSay;
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

		checkThatJavaSpeechIsInstalled();
		
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
	 * 
	 */
	private void checkThatJavaSpeechIsInstalled() {
		// make sure speech.properties exists...
		// e.g., C:\Program Files\Java\jre1.6.0_01
		String pathToJava = System.getProperty("java.home");

		// if it does not... then we copy it over automatically!
		File speechProperties = new File(pathToJava, "lib/speech.properties");
		if (!speechProperties.exists()) {
			DebugUtils.println(speechProperties.getPath()
					+ " needs to be present for PaperToolkit to use Java Speech.");
			File speechPropsSourceFile = PaperToolkit.getToolkitFile("lib/freetts/speech.properties");
			if (speechPropsSourceFile.exists()) {
				FileUtils.copy(speechPropsSourceFile, speechProperties);
				// copy it for you...
				DebugUtils.println("Copying the speech.properties file into your JRE's lib directory: " + speechProperties.getAbsolutePath());
			}
		}
	}

	/**
	 * Remote invocations should use this method. Local invocations may also use this, but it might be more
	 * efficient to keep a TextToSpeechAction around, initialize it, and then call speak(String) repeatedly.
	 * 
	 * @see papertoolkit.actions.Action#invoke()
	 */
	public void invoke() {
		initialize();
		speak();
		close();
	}

	/**
	 * @param t
	 */
	public void setText(String t) {
		textToSpeak = t;
	}

	/**
	 * Speak the default text...
	 */
	public void speak() {
		speak(textToSpeak);
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

	public String toString() {
		return "Say [" + textToSpeak + "] using text-to-speech.";
	}
}
