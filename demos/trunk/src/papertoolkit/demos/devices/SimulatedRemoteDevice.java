package papertoolkit.demos.devices;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import papertoolkit.PaperToolkit;
import papertoolkit.actions.Action;
import papertoolkit.actions.types.OpenURL2Action;
import papertoolkit.actions.types.OpenURLAction;
import papertoolkit.actions.types.PlaySoundAction;
import papertoolkit.actions.types.RobotAction;
import papertoolkit.actions.types.RunAppAction;
import papertoolkit.actions.types.RunJavaAppAction;
import papertoolkit.actions.types.TextToSpeechAction;
import papertoolkit.devices.Device;
import papertoolkit.util.WindowUtils;

/**
 * <p>
 * Make sure you have looked at LocalDevice first. This class simulates a remote device using a local device.
 * We can send interesting Actions and data to this "remote" device. The remote device can also respond with
 * actions. This will trigger a local event handler.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class SimulatedRemoteDevice {

	private Device device;
	private JFrame frame;
	private JPanel mainPanel;
	private ArrayList<Action> items;
	private JComboBox dropDown;

	/**
	 * This class tests the different actions... on the local device
	 */
	public SimulatedRemoteDevice() {
		device = new Device();

		// add some action items...
		items = new ArrayList<Action>();
		items.add(new OpenURLAction("http://www.google.com/"));
		items.add(new OpenURL2Action("http://www.yahoo.com/"));
		items.add(new RunAppAction("notepad")); // wordpad doesn't work, but write.exe does!
		items.add(getCloseRobotAction()); // closes the window! =)
		items.add(new TextToSpeechAction("Your data has been uploaded to the website."));
		items.add(new RunJavaAppAction(SimulatedRemoteDeviceTestApp.class));
		items.add(new PlaySoundAction("http://hci.stanford.edu/paper/sound/CodeMonkey_MonoClip.mp3"));
		setupJFrame();

	}

	private Action getCloseRobotAction() {
		RobotAction r = new RobotAction();
		r.keyPress(KeyEvent.VK_ALT);
		r.keyPress(KeyEvent.VK_F4);
		r.keyRelease(KeyEvent.VK_F4);
		r.keyRelease(KeyEvent.VK_ALT);
		return r;
	}

	private void setupJFrame() {
		// open up a JFrame with options you can pick from
		frame = new JFrame("Device Architecture Test");

		frame.add(getMainPanel());
		frame.setIconImage(PaperToolkit.getPaperToolkitIcon());
		frame.setSize(640, 200);
		frame.setLocation(WindowUtils.getWindowOrigin(frame, WindowUtils.DESKTOP_CENTER));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	private Component getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(getDropDown(), BorderLayout.NORTH);
			mainPanel.add(getInvokeButton(), BorderLayout.CENTER);
			mainPanel.add(getExitButton(), BorderLayout.SOUTH);
		}
		return mainPanel;
	}

	private Component getInvokeButton() {
		JButton button = new JButton("Invoke this Action on the \"Remote\" Device");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Action selectedAction = (Action) dropDown.getSelectedItem();
				device.invoke(selectedAction);
			}
		});
		return button;
	}

	private Component getDropDown() {
		dropDown = new JComboBox();
		// when you pick an option, it invokes the associated actions
		for (Action a : items) {
			dropDown.addItem(a);
		}
		return dropDown;
	}

	private Component getExitButton() {
		JButton button = new JButton("Exit");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				device.disconnect();
				System.exit(0);
			}
		});
		return button;
	}

	public static void main(String[] args) {
		new SimulatedRemoteDevice();
	}
}
