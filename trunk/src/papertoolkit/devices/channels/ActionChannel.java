package papertoolkit.devices.channels;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import papertoolkit.actions.types.OpenFileAction;
import papertoolkit.actions.types.OpenURLAction;
import papertoolkit.actions.types.RobotAction;
import papertoolkit.devices.Device;


/**
 * <p>
 * The idea of channels is to provide a nice API for sending actions to a remote device. One could
 * circumvent this to directly use the actions package, but the Device object will only know about
 * its channels (for now).
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class ActionChannel {

	private Device parentDevice;

	/**
	 * @param device
	 */
	public ActionChannel(Device device) {
		parentDevice = device;
	}

	/**
	 * @param urlString
	 */
	public void openURL(String urlString) {
		try {
			OpenURLAction a = new OpenURLAction(new URL(urlString));
			parentDevice.invokeAction(a);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param typeThis
	 */
	public void typeString(String typeThis) {
		RobotAction a = new RobotAction();
		a.typeString(typeThis);
		parentDevice.invokeAction(a);
	}

	/**
	 * @param f
	 */
	public void openFile(File f) {
		OpenFileAction a = new OpenFileAction(f);
		parentDevice.invokeAction(a);
	}
}
