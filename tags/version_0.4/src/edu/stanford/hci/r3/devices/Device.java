package edu.stanford.hci.r3.devices;

import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import edu.stanford.hci.r3.actions.R3Action;
import edu.stanford.hci.r3.actions.remote.ActionReceiver;
import edu.stanford.hci.r3.actions.remote.ActionSender;
import edu.stanford.hci.r3.actions.types.OpenFileAction;
import edu.stanford.hci.r3.actions.types.OpenURLAction;
import edu.stanford.hci.r3.actions.types.PlaySoundAction;
import edu.stanford.hci.r3.actions.types.TextToSpeechAction;
import edu.stanford.hci.r3.devices.channels.ActionChannel;
import edu.stanford.hci.r3.devices.channels.AudioChannel;
import edu.stanford.hci.r3.devices.channels.DisplayChannel;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.networking.ClientServerType;

/**
 * <p>
 * Applications can also include devices, which can supply input and receive output. Since devices
 * may not be attached to the local machine where you are running the program, we need to assign a
 * hostname to each device.
 * </p>
 * <p>
 * Devices work closely with the Actions API. Basically, devices are constructs that allow us to
 * send events and actions to remote machines...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Device {

	// ////////////////////////////////////////////////////////////////////////////////////////
	// The series of doXXX methods are convenience methods for the application to execute local
	// actions from the actions.* package. When called, they implicitly ask the local device
	// to invoke Actions.
	// ////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * For making sure the sounds are not overlapped.
	 */
	// private static LinkedList<PlaySoundAction> queuedSounds = new LinkedList<PlaySoundAction>();
	//

	/**
	 * Plays a sound file. Returns the object in case you need to stop it.
	 */
	// public static PlaySoundAction doPlaySound(File soundFile) {
	// final PlaySoundAction playSoundAction = new PlaySoundAction(soundFile);
	// playSoundAction.addStopListener(new PlaySoundAction.PlaySoundListener() {
	// public void soundStopped() {
	// queuedSounds.remove(playSoundAction); // remove myself
	// if (queuedSounds.size() > 0) {
	// // if there are any left... play the next guy
	// queuedSounds.getFirst().invoke();
	// }
	// }
	// });
	// queuedSounds.addLast(playSoundAction);
	// if (queuedSounds.size() == 1) {
	// playSoundAction.invoke();
	// }
	//
	// return playSoundAction;
	// }

	/**
	 * 
	 */
	private static PlaySoundAction playSoundAction;

	/**
	 * Plays the sound file on the local device.
	 * 
	 * @param soundFile
	 * @return the playSoundAction object, so you can stop the audio if you wish.
	 */
	public static PlaySoundAction doPlaySound(File soundFile) {
		if (playSoundAction != null) {
			playSoundAction.stop();
		}
		playSoundAction = new PlaySoundAction(soundFile);
		playSoundAction.invoke();
		return playSoundAction;
	}

	/**
	 * 
	 */
	private ActionChannel actionChannel;

	/**
	 * 
	 */
	private AudioChannel audioChannel;

	/**
	 * 
	 */
	private DisplayChannel displayChannel;

	/**
	 * 
	 */
	private String hostNameOrIPAddr;

	/**
	 * 
	 */
	private String name;

	/**
	 * 
	 */
	private ActionSender sender;

	/**
	 * Provide an IP address of a remote device that is listening for actions.
	 */
	public Device(String theHostNameOrIPAddr, String descriptiveName) {
		hostNameOrIPAddr = theHostNameOrIPAddr;
		name = descriptiveName;
	}

	/**
	 * Once we have connected, we can start sending this device commands....
	 */
	public void connect() {
		sender = new ActionSender(hostNameOrIPAddr, ActionReceiver.DEFAULT_JAVA_PORT,
				ClientServerType.JAVA);
	}

	/**
	 * 
	 */
	public void disconnect() {
		sender.disconnect();
		sender = null;
	}

	/**
	 * @return
	 */
	public ActionChannel getActionChannel() {
		if (actionChannel == null) {
			actionChannel = new ActionChannel(this);
		}
		return actionChannel;
	}

	/**
	 * The audio channel provides convenience functions to easily play audio or read text on this
	 * device. But, if you need to access the sender directly, you can always call the lower level
	 * invokeAction(...).
	 * 
	 * @return a channel that allows us to send audio to this device...
	 */
	public AudioChannel getAudioChannel() {
		if (audioChannel == null) {
			audioChannel = new AudioChannel(this);
		}
		return audioChannel;
	}

	/**
	 * Access the display.
	 * 
	 * @return
	 */
	public DisplayChannel getDisplayChannel() {
		if (displayChannel == null) {
			displayChannel = new DisplayChannel(this);
		}
		return displayChannel;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Send actions directly to the REMOTE device that this Device object represents.
	 * 
	 * @param action
	 */
	public void invokeAction(R3Action action) {
		if (sender != null) {
			sender.invokeRemoteAction(action);
		} else {
			DebugUtils.println("Sender is null.");
		}
	}

	/**
	 * Instead of pinging, we check if a device is "reachable" by making sure it has a host address.
	 * 
	 * @return
	 */
	public boolean isAlive() {
		try {
			DebugUtils.println("Checking if Device is Alive and Reachable...");
			final InetAddress address = InetAddress.getByName(hostNameOrIPAddr);
			DebugUtils.println("Device Hostname: " + address.getHostName());
			DebugUtils.println("Device Address: " + address.getHostAddress());
			return true;
		} catch (UnknownHostException e) {
			DebugUtils.println(e);
		}
		return false;
	}

	/**
	 * @param textToSpeak
	 */
	public static void doSpeakText(String textToSpeak) {
		TextToSpeechAction.getInstance().speak(textToSpeak);
	}

	/**
	 * Opens a URL on the local machine.
	 */
	public static void doOpenURL(String urlString) {
		try {
			final URL u = new URL(urlString);
			new OpenURLAction(u).invoke();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Opens a file on the local device, using the default file editor.
	 * 
	 * @param file
	 * @return 
	 */
	public static OpenFileAction doOpenFile(File file) {
		final OpenFileAction ofa = new OpenFileAction(file);
		ofa.invoke();
		return ofa;
	}
}
