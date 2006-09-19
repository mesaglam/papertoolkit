package edu.stanford.hci.r3.devices;

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

}
