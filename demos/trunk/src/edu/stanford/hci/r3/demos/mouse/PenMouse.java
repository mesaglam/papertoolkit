package edu.stanford.hci.r3.demos.mouse;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;

import papertoolkit.pen.Pen;
import papertoolkit.pen.PenSample;
import papertoolkit.pen.streaming.listeners.PenListener;


/**
 * <p>
 * Simulate a mouse with the pen... Move the mouse w/ your real mouse first... and then the pen will move the
 * mouse relatively from penDown to penUp...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class PenMouse {

	public static void main(String[] args) {
		new PenMouse();
	}

	private double lastX;

	private double lastY;
	
	private Point mouseLocation;

	private Pen pen;

	private Robot robot;

	public PenMouse() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}

		pen = new Pen();
		pen.addLivePenListener(new PenListener() {
			public void penDown(PenSample sample) {
				lastX = sample.getX();
				lastY = sample.getY();
				robot.mousePress(InputEvent.BUTTON1_MASK);
			}

			public void penUp(PenSample sample) {
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
			}

			public void sample(PenSample sample) {

				double dX = sample.getX() - lastX;
				double dY = sample.getY() - lastY;

				mouseLocation = MouseInfo.getPointerInfo().getLocation();
				robot.mouseMove((int) (mouseLocation.x + dX), (int) (mouseLocation.y + dY));

				lastX = sample.getX();
				lastY = sample.getY();
			}
		});
		pen.startLiveMode();
	}
}
