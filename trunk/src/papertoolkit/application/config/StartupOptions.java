package papertoolkit.application.config;

/**
 * <p>
 * Pass this in to the PaperToolkit constructor...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class StartupOptions {

	/**
	 * Use the dark GUI theme (default behavior). Make sure you initialize PaperToolkit first, otherwise your
	 * GUI will look weird (mix between PaperToolkit's theme and Swing's theme).
	 */
	private boolean useLookAndFeel = true;

	/**
	 * Automatically load the handwriting recognition server on startup. Otherwise, we'll just load it
	 * whenever we notice that there is a handwriting recognizer in your application (default behavior).
	 */
	private boolean useHandwritingRecognitionServer = false;

	public StartupOptions() {
		// nothing
	}

	public void setParamApplyGUILookAndFeel(boolean flag) {
		useLookAndFeel = flag;
	}

	public boolean getParamApplyGUILookAndFeel() {
		return useLookAndFeel;
	}

	public void setParamTurnOnHandwritingRecognitionServer(boolean flag) {
		useHandwritingRecognitionServer = flag;
	}

	public boolean getParamTurnOnHandwritingRecognitionServer() {
		return useHandwritingRecognitionServer;
	}
}
