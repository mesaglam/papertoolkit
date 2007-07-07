package edu.stanford.hci.r3.demos.devices;

import edu.stanford.hci.r3.actions.remote.ActionReceiver;
import edu.stanford.hci.r3.actions.types.OpenURLAction;
import edu.stanford.hci.r3.devices.Device;

/**
 * <p>
 * Uses the Device framework to create an interaction that spans multiple handheld devices. In this case, the
 * "handheld devices" are actually simulated, and all run on the same computer.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class LocalDevice {

	private ActionReceiver receiver;
	private Device device;

	public LocalDevice() {
		receiver = new ActionReceiver();

		device = new Device();

		device.invokeAction(new OpenURLAction("http://www.yahoo.com/"));

		receiver.stop();
		device.stop();
	}

	public static void main(String[] args) {
		new LocalDevice();
	}
}
