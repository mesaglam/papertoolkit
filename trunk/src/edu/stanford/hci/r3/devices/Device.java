package edu.stanford.hci.r3.devices;

import java.net.InetAddress;
import java.net.UnknownHostException;

import edu.stanford.hci.r3.actions.remote.ActionReceiver;
import edu.stanford.hci.r3.actions.remote.ActionSender;
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
	private String hostNameOrIPAddr;

	/**
	 * 
	 */
	private String name;

	private ActionSender sender;

	/**
	 * 
	 */
	public Device(String theHostNameOrIPAddr, String descriptiveName) {
		hostNameOrIPAddr = theHostNameOrIPAddr;
		name = descriptiveName;
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

	/**
	 * 
	 */
	public void connect() {
		
		
		sender = new ActionSender(hostNameOrIPAddr, ActionReceiver.DEFAULT_JAVA_PORT,
				ClientServerType.JAVA);
	}
}
