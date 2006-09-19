package edu.stanford.hci.r3.devices;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * Applications can also include devices, which can supply input and receive output. Since devices
 * may not be attached to the local machine where you are running the program, we need to assign a
 * hostname to each device.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Device {

	private String hostNameOrIPAddr;

	private String name;

	/**
	 * 
	 */
	public Device(String theHostNameOrIPAddr, String descriptiveName) {
		hostNameOrIPAddr = theHostNameOrIPAddr;
		name = descriptiveName;
	}

	/**
	 * Pings the device to see if will be able to talk to it.
	 * 
	 * @return
	 */
	public boolean isDeviceAlive() {
		try {
			DebugUtils.println("Checking if Device is Alive and Reachable...");
			InetAddress address = InetAddress.getByName(hostNameOrIPAddr);
			DebugUtils.println("Device Hostname: " + address.getHostName());
			DebugUtils.println("Device Address: " + address.getHostAddress());
			return address.isReachable(5); // 5 seconds
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Not Reachable");
		return false;
	}
}
