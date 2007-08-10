package papertoolkit.actions.types;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.media.*;

import papertoolkit.actions.R3Action;

import com.sun.media.codec.audio.mp3.JavaDecoder;


/**
 * <p>
 * Play a sound file. It must be available on the local file system, wherever this action ends up.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PlaySoundAction implements R3Action {

	private List<PlaySoundListener> notifyOnStop = new ArrayList<PlaySoundListener>();

	public static interface PlaySoundListener {
		public void soundStopped();
	}

	public void addStopListener(PlaySoundListener psl) {
		notifyOnStop.add(psl);
	}

	static {
		// register the MP3 plugin for JMF.
		JavaDecoder.main(new String[] {});
		System.out.println("Disregard the InvocationTargetException. "
				+ "It is printed out by the JavaDecoder while registering the mp3 plugin.");
	}

	private Player player;

	/**
	 * The file to play (from the local file system).
	 */
	private File sound;

	/**
	 * @param soundFile
	 */
	public PlaySoundAction(File soundFile) {
		sound = soundFile;
	}

	/**
	 * @see papertoolkit.actions.R3Action#invoke()
	 */
	public void invoke() {
		// play it!
		try {
			player = Manager.createRealizedPlayer(sound.toURI().toURL());
			player.addControllerListener(new ControllerListener() {
				public void controllerUpdate(ControllerEvent ce) {
					if (ce instanceof StopEvent) {
						StopEvent se = (StopEvent) ce;
						System.out.println("PlaySoundAction Stopped at: "
								+ se.getMediaTime().getSeconds() + " seconds");
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
		} catch (MalformedURLException e) {
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

	public void unpause() {
		player.start();
	}
}
