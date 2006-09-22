package edu.stanford.hci.r3.demos.sketch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.devices.Device;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.components.EndlessProgressDialog;

/**
 * <p>
 * A remote synchronous collaborative sketching tool. Each user (A & B) prints out the SAME sheet
 * and runs the SAME application. The only difference is that each have to specify the other user's
 * machine as a destination for their content.
 * 
 * This is Task 2 of the GIGAprints CHI Studies.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class BuddySketch extends Application {

	/**
	 * <p>
	 * </p>
	 */
	private static class HostNameWithComment {
		public String comment;

		public String hostName;

		/**
		 * @param ipAddr
		 * @param c
		 */
		public HostNameWithComment(String ipAddr, String c) {
			hostName = ipAddr;
			comment = c;
		}

		public String toString() {
			return hostName + "    " + comment;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new BuddySketch();
	}

	/**
	 * The device we want to connect to...
	 */
	private Device device = null;

	private BuddySketchPaperUI paperUI;

	/**
	 * 
	 */
	public BuddySketch() {
		super("Buddy Sketch Application");
		PaperToolkit.initializeLookAndFeel();
		System.out.println("Welcome to Buddy Sketch!");

		// set up the GIGAprint application
		paperUI = new BuddySketchPaperUI();
		addSheet(paperUI);

		// load it up and start!
		PaperToolkit r3 = new PaperToolkit();
		r3.useApplicationManager(true);
		r3.loadApplication(this);
	}

	/**
	 * 
	 */
	private void connectToTheOtherComputer() {
		final HostNameWithComment[] availableHosts = loadIPAddressesFromFile();

		HostNameWithComment hostNameWComment = null;
		boolean hostAddressable = false;
		EndlessProgressDialog progress = null;
		String hostName = null;
		while (!hostAddressable) {
			hostNameWComment = (HostNameWithComment) JOptionPane.showInputDialog(null,
					"Please enter the hostname or " + "IP Address of your friend's computer.",
					"Connect to which computer?", JOptionPane.QUESTION_MESSAGE, null,
					availableHosts, availableHosts[0]);
			if (hostNameWComment == null) {
				DebugUtils.println("You decided not to run Remote Sketch!");
				System.exit(0);
			}
			hostName = hostNameWComment.hostName;
			if (hostNameWComment.hostName.equals("")) {
				hostName = "localhost"; // buddy sketch with ourselves! =)
			}

			// check that this host is pingable...
			device = new Device(hostName, hostNameWComment.comment);

			progress = new EndlessProgressDialog(null, "Connecting...",
					"Please wait while we look for the other computer.");
			progress.setAlwaysOnTop(true);

			// if it is not pingable, ask for a new host...
			hostAddressable = device.isAlive();
			progress.dispose();
			progress = null;
		}

		System.out.println("You chose " + hostNameWComment);
	}

	/**
	 * @see edu.stanford.hci.r3.Application#initializeAfterConstructor()
	 */
	@Override
	protected void initializeAfterConstructor() {

	}

	/**
	 * @see edu.stanford.hci.r3.Application#initializeBeforeStarting()
	 */
	@Override
	protected void initializeBeforeStarting() {
		connectToTheOtherComputer();
	}

	/**
	 * @return
	 */
	private HostNameWithComment[] loadIPAddressesFromFile() {
		List<HostNameWithComment> hostNames = new ArrayList<HostNameWithComment>();
		try {
			File f = new File("data/Sketch/BuddySketchHostNames.txt");
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (!line.contains("//")) {
					continue; // next!
				}
				String ipAddr = line.substring(0, line.indexOf("//")).trim();
				if (ipAddr.length() == 0) {
					// not a well formed IP or hostname
					continue;
				}

				String comment = line.substring(line.indexOf("//"));
				hostNames.add(new HostNameWithComment(ipAddr, comment));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return hostNames.toArray(new HostNameWithComment[] {});
	}
}
