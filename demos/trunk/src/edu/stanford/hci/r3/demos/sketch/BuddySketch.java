package edu.stanford.hci.r3.demos.sketch;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.actions.R3Action;
import edu.stanford.hci.r3.actions.remote.*;
import edu.stanford.hci.r3.actions.types.ProcessInformationAction;
import edu.stanford.hci.r3.devices.Device;
import edu.stanford.hci.r3.networking.ClientServerType;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.pen.ink.Ink;
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

	private static final String DISPLAY_PHOTO = "DISPLAYPHOTO";

	private static final String DISPLAY_INK = "DISPLAYINK";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new BuddySketch();
	}

	private ActionReceiver actionReceiver;

	/**
	 * The frame.
	 */
	private BuddySketchGUI buddySketchGUI;

	/**
	 * This has got to be the worst name ever. It's a wonder I got into this business.
	 */
	private BuddySketchPaperUI buddySketchPUI;

	/**
	 * The device we want to connect to...
	 */
	private Device device = null;

	private Pen myPen;

	/**
	 * 
	 */
	public BuddySketch() {
		super("Buddy Sketch Application");
		System.out.println("Welcome to Buddy Sketch!");
		PaperToolkit.initializeLookAndFeel();
		setUserChoosesPDFDestinationFlag(false);

		// set up the GIGAprint application
		buddySketchPUI = new BuddySketchPaperUI(this);
		addSheet(buddySketchPUI, new File("data/Sketch/BuddySketchUI.patternInfo.xml"));

		addLocalPen();

		if (false) {
			renderToPDF();
		} else {
			// load it up and start!
			PaperToolkit r3 = new PaperToolkit();
			r3.useApplicationManager(true);
			r3.startApplication(this);
		}
	}

	/**
	 * 
	 */
	private void addLocalPen() {
		myPen = new Pen("Local Pen");
		addPen(myPen);
	}

	/**
	 * 
	 */
	private void chooseOtherComputer() {

		HostNameWithComment[] availableHosts = loadIPAddressesFromPrivateFile();
		if (availableHosts.length == 0) {
			availableHosts = loadIPAddressesFromFile();
		}

		HostNameWithComment hostNameWComment = null;
		boolean hostAddressable = false;
		EndlessProgressDialog progress = null;
		String hostName = null;
		while (!hostAddressable) {
			hostNameWComment = (HostNameWithComment) JOptionPane.showInputDialog(null,
					"Please enter the hostname or " + "IP Address of your friend's computer.",
					"Connect to which computer?", JOptionPane.QUESTION_MESSAGE, null, availableHosts,
					availableHosts[0]);
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
	 * @param imgFile
	 */
	public void displayImage(File imgFile) {
		// display it in my own window...
		buddySketchGUI.displayPhoto(imgFile);

		// tell the OTHER device to display it too!
		ProcessInformationAction a = new ProcessInformationAction(DISPLAY_PHOTO, imgFile);
		device.invokeAction(a);
	}

	/**
	 * @return
	 */
	private ActionReceiverConnectionListener getConnectionListener() {
		return new ActionReceiverConnectionListener() {
			public void newConnectionFrom(String hostName, String ipAddr) {
				DebugUtils.println("New Connection From: " + hostName + " " + ipAddr);
			}
		};
	}

	/**
	 * @see edu.stanford.hci.r3.Application#initializeBeforeStarting()
	 */
	@Override
	protected void initializeBeforeStarting() {
		startLocalActionReceiver();

		// pause here at the dialog box! =)
		chooseOtherComputer();

		// start up my GUI... where my Ink & Photos display and also where the other person's Ink &
		// Photos display
		buddySketchGUI = new BuddySketchGUI(this);

		// connect!
		device.connect();
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

	/**
	 * @return
	 */
	private HostNameWithComment[] loadIPAddressesFromPrivateFile() {
		List<HostNameWithComment> hostNames = new ArrayList<HostNameWithComment>();
		try {
			File f = new File("data/Sketch/BuddySketchPrivateHostNames.txt");
			if (f.exists()) {
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
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return hostNames.toArray(new HostNameWithComment[] {});
	}

	/**
	 * Disregard the input...
	 * 
	 * @see edu.stanford.hci.r3.Application#renderToPDF(java.io.File, java.lang.String)
	 */
	public void renderToPDF() {
		buddySketchPUI.getRenderer().renderToPDF(new File("data/Sketch/BuddySketchUI.pdf"));
	}

	/**
	 * @param newInkOnly
	 */
	public void sendInkToGUI(Ink newInkOnly) {
		buddySketchGUI.addInkToCanvas(newInkOnly);

		// tell the OTHER device to display this ink too!
		ProcessInformationAction a = new ProcessInformationAction(DISPLAY_INK, newInkOnly);
		device.invokeAction(a);
	}

	/**
	 * 
	 */
	private void startLocalActionReceiver() {
		actionReceiver = new ActionReceiver(ActionReceiver.DEFAULT_JAVA_PORT, ClientServerType.JAVA,
				new String[] { "localhost", "*" }); // trust everyone!

		actionReceiver.setConnectionListener(getConnectionListener());

		// invokes the actions
		actionReceiver.addActionHandler(new ActionHandler() {

			/**
			 * @see edu.stanford.hci.r3.actions.remote.ActionHandler#receivedAction(edu.stanford.hci.r3.actions.R3Action)
			 */
			@Override
			public void receivedAction(R3Action action) {
				if (action instanceof ProcessInformationAction) {
					ProcessInformationAction p = (ProcessInformationAction) action;
					Object msgVal = p.getInformation();
					String msgName = p.getName();

					if (msgName.equals(DISPLAY_PHOTO)) {
						DebugUtils.println("Displaying Photo!");
						File f = (File) msgVal;
						DebugUtils.println("Photo Exists? " + f.exists());

						// display it in my own window...
						buddySketchGUI.displayBuddyPhoto(f);

					} else if (msgName.equals(DISPLAY_INK)) {
						Ink i = (Ink) msgVal;
						buddySketchGUI.addBuddyInkToCanvas(i);
					}
				} else {
					action.invoke();
				}
			}

			/**
			 * @see edu.stanford.hci.r3.actions.remote.ActionHandler#receivedActionText(java.lang.String)
			 */
			@Override
			public void receivedActionText(String line) {
				super.receivedActionText(line);
			}

		});
	}
}
