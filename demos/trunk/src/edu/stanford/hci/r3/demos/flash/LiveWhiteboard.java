package edu.stanford.hci.r3.demos.flash;

import java.awt.Color;

import edu.stanford.hci.r3.flash.whiteboard.FlashWhiteboard;
import edu.stanford.hci.r3.pen.Pen;

/**
 * <p>
 * Shows how to use the Flash components to assemble a Live Whiteboard that will get data from the local pen
 * device in real time. We can do multiple Whiteboards at the same time!
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class LiveWhiteboard {

	public static void main(String[] args) {
		new LiveWhiteboard();
	}

	private FlashWhiteboard flashWhiteboard1;
	private FlashWhiteboard flashWhiteboard2;
	private FlashWhiteboard flashWhiteboard3;
	private FlashWhiteboard flashWhiteboard4;

	public LiveWhiteboard() {
		// in R3, there are two ways to make real-time pen and paper applications... One is to use
		// the Application framework. A second one, which we will use here, is to just attach a pen
		// listener to a live (local) Pen.

		// Connect to two pens, one local, and one accessible through the network
		// Pen pen1 = new Pen(); // local pen
		// Pen pen2 = new Pen("Laptop", "171.66.51.122");

		// Connect to two remote pens, both accessible through the network (and hosted 
		// on computers behind a NAT box)
		Pen pen1 = new Pen("Pen 1", "solaria.stanford.edu"); // remote pen
		pen1.setPenServerPort(11100); // forwarded through the nat to machine 1

		Pen pen2 = new Pen("Pen 2", "solaria.stanford.edu"); // remote pen
		pen2.setPenServerPort(11105); // forwarded through the nat to machine 2

		// load the Flash component that listens for real-time ink!
		// basically, just open the HTML page that contains the flash component! =)
		// we pick different ports, so the data can be streamed separately
		flashWhiteboard1 = new FlashWhiteboard(8989);
		flashWhiteboard1.addPen(pen1);
		flashWhiteboard1.setSwatchColor(Color.RED);
		flashWhiteboard1.setTitle("Red Pen");
		flashWhiteboard1.load();

		flashWhiteboard2 = new FlashWhiteboard(8990);
		flashWhiteboard2.addPen(pen1);
		flashWhiteboard2.setSwatchColor(Color.BLUE);
		flashWhiteboard2.setTitle("Blue Pen");
		flashWhiteboard2.load();

		flashWhiteboard3 = new FlashWhiteboard(8991);
		flashWhiteboard3.addPen(pen2);
		flashWhiteboard3.setSwatchColor(Color.GREEN);
		flashWhiteboard3.setTitle("Green Pen");
		flashWhiteboard3.load();

		flashWhiteboard4 = new FlashWhiteboard(8992);
		flashWhiteboard4.addPen(pen2);
		flashWhiteboard4.setSwatchColor(Color.ORANGE);
		flashWhiteboard4.setTitle("Orange Pen");
		flashWhiteboard4.load();
	}

}
