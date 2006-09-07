package edu.stanford.hci.r3.actions.types;

/**
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class RobotActionTest {
	public static void main(String[] args) {
		RobotAction r = new RobotAction();
		r.setAutoDelay(10);
		r.mouseMove(0, 0);
		for (int i = 0; i < 100; i++) {
			r.mouseMove(i * 4, i * 10);
		}
		System.out.println(r.getNumCommands() + " Commands");
		r.invoke();
	}
}
