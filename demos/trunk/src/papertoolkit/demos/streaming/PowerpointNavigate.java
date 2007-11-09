package papertoolkit.demos.streaming;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import papertoolkit.pen.Pen;
import papertoolkit.pen.PenSample;
import papertoolkit.pen.streaming.listeners.PenListener;


/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PowerpointNavigate {

	private static PenSample down;

	private static Robot robot = null;

	private static PenSample up;

	/**
	 * @return
	 */
	private static PenListener getDebugPenListener() {
		return new PenListener() {
			public void penDown(PenSample sample) {
				System.out.println("Pen Down: " + sample);
				down = sample;
			}

			public void penUp(PenSample sample) {
				
				// Recognize the Stroke; if it's an O then Blank...
				
				
				
				// Otherwise... default to checking stroke
				
				
				System.out.println("Pen Up: " + sample);
				if (up.y > down.y) {
					System.out.println("Dragged Down");
					robot.keyPress(KeyEvent.VK_PAGE_DOWN);
					robot.keyRelease(KeyEvent.VK_PAGE_DOWN);
				} else {
					System.out.println("Dragged Up");
					robot.keyPress(KeyEvent.VK_PAGE_UP);
					robot.keyRelease(KeyEvent.VK_PAGE_UP);
				}
			}

			public void sample(PenSample sample) {
				System.out.println(sample);
				// last sample will be checked by the penUp method
				up = sample;
			}
		};
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}

		Pen pen = new Pen();
		pen.startLiveMode();
		pen.addLivePenListener(getDebugPenListener());
	}

}
