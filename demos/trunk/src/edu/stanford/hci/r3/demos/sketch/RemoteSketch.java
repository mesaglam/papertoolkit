package edu.stanford.hci.r3.demos.sketch;

import javax.swing.JOptionPane;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.devices.Device;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.components.EndlessProgressDialog;

/**
 * <p>
 * A remote synchronous collaborative sketching tool. Each user (A & B) prints out the SAME sheet
 * and runs the SAME application. The only difference is that each have to specify the other user's
 * machine as a destination for their content.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class RemoteSketch {

	public static void main(String[] args) {
		PaperToolkit.initializeLookAndFeel();
		System.out.println("Welcome to Remote Sketch!");

		String hostName = null;
		boolean hostPingable = false;
		Device device = null;
		EndlessProgressDialog progress = null;

		while (!hostPingable) {
			hostName = JOptionPane
					.showInputDialog("Please enter the hostname or IP Address of your friend's computer.");
			if (hostName == null) {
				DebugUtils.println("You decided not to run Remote Sketch!");
				System.exit(0);
			}
			// check that this host is pingable...
			device = new Device(hostName, "The Other Computer");

			progress = new EndlessProgressDialog(null, "Connecting...",
					"Please wait while we look for the other computer.");
			progress.setAlwaysOnTop(true);

			// if it is not pingable, ask for a new host...
			hostPingable = device.isAlive();
			progress.dispose();
			progress = null;
		}

		
		// TODO: Do not use PING
		// Check instead to see that there is an action receiver running... or something like that
		// Also, Cache Known hosts in a local text file so we can use it for future instances...
		// hostname + ip address...
		
		System.out.println("You chose " + hostName);

	}
}
