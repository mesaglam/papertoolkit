package papertoolkit.actions.types;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.media.*;

import papertoolkit.actions.Action;

import com.sun.media.codec.audio.mp3.JavaDecoder;

/**
 * <p>
 * Play a sound file. It must be available on the local file system, wherever this action ends up.
 * 
 * NOTE: to successfully use this class, you should download and install JMF and the Performance pack for your
 * OS: http://java.sun.com/products/java-media/jmf/2.1.1/download.html Otherwise, many errors will be
 * thrown... including: something about the AudioCodec not being found.
 * 
 * You should ALSO download and install Java's MP3 plugin
 * (http://java.sun.com/products/java-media/jmf/mp3/download.html). Don't ask my why Java hasn't made all this
 * part of the JRE yet... :-(
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PlaySoundAction implements Action {

	public static interface PlaySoundListener {
		public void soundStopped();
	}

	static {
		// register the MP3 plugin for JMF.
		JavaDecoder.main(new String[] {});
		// System.out.println("Disregard the InvocationTargetException. "
		// + "It is printed out by the JavaDecoder while registering the mp3 plugin.");
	}

	private List<PlaySoundListener> notifyOnStop = new ArrayList<PlaySoundListener>();

	/**
	 * 
	 */
	private Player player;

	/**
	 * The file to play (from the local file system).
	 */
	private URL sound;

	/**
	 * @param soundFile
	 */
	public PlaySoundAction(File soundFile) {
		try {
			sound = soundFile.toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param soundURL
	 */
	public PlaySoundAction(String soundURL) {
		try {
			sound = new URL(soundURL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public PlaySoundAction(URL soundURL) {
		sound = soundURL;
	}

	public void addStopListener(PlaySoundListener psl) {
		notifyOnStop.add(psl);
	}

	/**
	 * @see papertoolkit.actions.Action#invoke()
	 */
	public void invoke() {
		// play it!
		try {
			player = Manager.createRealizedPlayer(sound);
			player.addControllerListener(new ControllerListener() {
				public void controllerUpdate(ControllerEvent ce) {
					if (ce instanceof StopEvent) {
						StopEvent se = (StopEvent) ce;
						System.out.println("PlaySoundAction Stopped at: " + se.getMediaTime().getSeconds()
								+ " seconds");
						stop();
						for (PlaySoundListener psl : notifyOnStop) {
							psl.soundStopped();
						}
					}
				}
			});
			player.start();
		} catch (NoPlayerException e) {
			e.printStackTrace();
		} catch (CannotRealizeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public void pause() {
		player.stop();
	}

	/**
	 * Stops the audio player and disposes resources.
	 */
	public void stop() {
		player.stop();
		player.close();
	}

	public String toString() {
		return "Play Sound at: " + sound;
	}

	public void unpause() {
		player.start();
	}
}
