package papertoolkit.demos.devices;

import papertoolkit.actions.types.OpenURLAction;
import papertoolkit.devices.Device;

/**
 * <p>
 * The Device framework allows you to create an interaction that spans multiple handheld devices. In this
 * case, the "handheld devices" are actually simulated, and all run on the same computer. In this Hello World
 * demo, we create a new Device (which defaults to the local computer) and asks it to open a URL with the
 * default browser.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * @lastWorkedOn 21 August 2007
 */
public class LocalDevice {

	private Device device;

	public LocalDevice() {
		// a device object handles the action sending and receiving automatically
		// a no parameter device defaults to the localhost...
		// we need a nice way to connect devices at runtime... so that they will automatically swap out/in
		// ActionSenders and Receivers...
		device = new Device();
		device.invoke(new OpenURLAction("http://www.yahoo.com/"));
		device.disconnect();
	}

	public static void main(String[] args) {
		new LocalDevice();
	}
}
