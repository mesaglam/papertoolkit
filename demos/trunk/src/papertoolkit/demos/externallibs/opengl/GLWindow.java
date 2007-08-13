package papertoolkit.demos.externallibs.opengl;

import javax.media.opengl.GLJPanel;
import javax.swing.JFrame;

public class GLWindow {

	public static void main(String[] args) {
		newDisplayJPanel();
	}

	private static void newDisplayJPanel() {
		JFrame frame = new JFrame("Test");
		GLJPanel gljp = new GLJPanel();
		frame.setContentPane(gljp);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(640, 480);
		frame.setVisible(true);
	}

}
