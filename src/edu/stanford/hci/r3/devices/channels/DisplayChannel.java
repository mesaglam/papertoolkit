package edu.stanford.hci.r3.devices.channels;

import edu.stanford.hci.r3.devices.Device;

/**
 * <p>
 * A Local/Remote JFrame and associated Graphics2D object, essentially. Calls to this graphics2D are
 * handled differently depending on whether the device is local or not. If it is a remote device,
 * the commands are replicated across the wire to the actual device. Don't do toooo many, of
 * course... Need to load local images and such.
 * 
 * This class needs to know whether it's local or remote, and it will handle the display
 * accordingly. If it is remote, it will ask the associated action receiver to create a display
 * object, etc.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class DisplayChannel {

	private Device parentDevice;

	public DisplayChannel(Device device) {
		parentDevice = device;
	}
	
}
