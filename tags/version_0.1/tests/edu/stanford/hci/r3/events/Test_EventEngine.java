package edu.stanford.hci.r3.events;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.util.components.SuperJTextField;

/**
 * <p>
 * A simple gui to add pens to the event listener! The pen servers can be anywhere!
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Test_EventEngine {

	public static void main(String[] args) {
		JFrame f = new JFrame("EventEngine Test");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout());

		final EventEngine e = new EventEngine();

		final JTextField serverNameTextField = new SuperJTextField("localhost", 30);
		JButton connectButton = new JButton("Connect and Register Pen");
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final String server = serverNameTextField.getText();
				Pen p = new Pen();
				p.startLiveMode(server);
				e.register(p);
			}
		});

		mainPanel.add(serverNameTextField);
		mainPanel.add(connectButton);
		f.setContentPane(mainPanel);
		f.pack();
		f.setVisible(true);

		// now hang out and listen for events!

		// really, the application, when loaded by the paper toolkit, should start all the pens in
		// live mode or connect them to the appropriate remote machines.
	}
}
