package edu.stanford.hci.r3.actions.types;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.media.CannotRealizeException;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Manager;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.StopEvent;

import com.sun.media.codec.audio.mp3.JavaDecoder;

import edu.stanford.hci.r3.actions.R3Action;

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

	static {
		// register the MP3 plugin for JMF.
		JavaDecoder.main(new String[] {});
		System.out.println("Disregard the InvocationTargetException. "
				+ "It is printed out by the JavaDecoder while registering the mp3 plugin.");
	}

	/**
	 * The file to play (from the local file system).
	 */
	private File sound;

	private Player player;

	/**
	 * @param soundFile
	 */
	public PlaySoundAction(File soundFile) {
		sound = soundFile;
	}

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
	public void stop() {
		player.stop();
		player.close();
	}
}
