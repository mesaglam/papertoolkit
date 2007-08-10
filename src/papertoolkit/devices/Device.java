package papertoolkit.devices;

import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import papertoolkit.actions.Action;
import papertoolkit.actions.remote.ActionReceiver;
import papertoolkit.actions.remote.ActionSender;
import papertoolkit.actions.types.OpenFileAction;
import papertoolkit.actions.types.OpenURLAction;
import papertoolkit.actions.types.PlaySoundAction;
import papertoolkit.actions.types.TextToSpeechAction;
import papertoolkit.devices.channels.ActionChannel;
import papertoolkit.devices.channels.AudioChannel;
import papertoolkit.devices.channels.DisplayChannel;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.networking.ClientServerType;


/**
 * <p>
 * Applications can also include devices, which can supply input and receive output. Since devices may not be
 * attached to the local machine where you are running the program, we need to assign a hostname to each
 * device.
 * </p>
 * <p>
 * Devices work closely with the Actions API. Basically, devices are constructs that allow us to send events
 * and actions to remote machines...
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
	 * @param textToSpeak
	 */
	public static void doSpeakText(String textToSpeak) {
		TextToSpeechAction.getInstance().speak(textToSpeak);
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
	 * A descriptive name for this device.
	 */
	private String name;

	/**
	 * This sends commands to a remote receiver.
	 */
	private ActionSender sender;

	/**
	 * 
	 */
	public Device() {
		this("localhost", "This Computer");
		start();
	}

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
	public void start() {
		sender = new ActionSender(hostNameOrIPAddr, ActionReceiver.DEFAULT_JAVA_PORT, ClientServerType.JAVA);
	}

	/**
	 * Disconnects from the sender. This device becomes useless afterward, until you reconnect.
	 */
	public void stop() {
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
	 * The audio channel provides convenience functions to easily play audio or read text on this device. But,
	 * if you need to access the sender directly, you can always call the lower level invokeAction(...).
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
	public void invokeAction(Action action) {
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
}
