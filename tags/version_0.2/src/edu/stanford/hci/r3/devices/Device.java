package edu.stanford.hci.r3.devices;

import java.net.InetAddress;
import java.net.UnknownHostException;

import edu.stanford.hci.r3.actions.R3Action;
import edu.stanford.hci.r3.actions.remote.ActionReceiver;
import edu.stanford.hci.r3.actions.remote.ActionSender;
import edu.stanford.hci.r3.devices.channels.ActionChannel;
import edu.stanford.hci.r3.devices.channels.AudioChannel;
import edu.stanford.hci.r3.devices.channels.DisplayChannel;
import edu.stanford.hci.r3.networking.ClientServerType;
import edu.stanford.hci.r3.util.DebugUtils;

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
	 * 
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
	 * Access the display directly.
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
	 * Send actions directly to the device.
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
			InetAddress address = InetAddress.getByName(hostNameOrIPAddr);
			DebugUtils.println("Device Hostname: " + address.getHostName());
			DebugUtils.println("Device Address: " + address.getHostAddress());
			return true;
		} catch (UnknownHostException e) {
			DebugUtils.println(e);
		}
		return false;
	}
}
