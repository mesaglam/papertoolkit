package edu.stanford.hci.r3.devices;

import javax.swing.JFrame;

/**
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Test_Device {
	
	private static Device device;

	/**
	 * Sep 20, 2006
	 */
	public static void main(String[] args) {
		connectToADevice();
	}

	private static void connectToADevice() {
		device = new Device("localhost", "My Computer");
		if (device.isAlive()) {
			device.connect();
		}
		
		JFrame f = new JFrame("Test");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(640,480);
		f.setVisible(true);
	}
}
