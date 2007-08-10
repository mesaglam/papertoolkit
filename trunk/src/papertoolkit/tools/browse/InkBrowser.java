package papertoolkit.tools.browse;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import papertoolkit.util.WindowUtils;


/**
 * <p>
 * Allows you to browse through all your synched pen data... and do interesting things with it,
 * like... create a Paper or Graphical UI! =)
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class InkBrowser extends JFrame {

	public static void main(String[] args) {
		new InkBrowser();
	}

	private TimelineCanvas timeline;
	private ContentCanvas content;

	public InkBrowser() {
		super("Ink Browser");
		setSize(1024, 768);
		setLocation(WindowUtils.getWindowOrigin(this, WindowUtils.DESKTOP_CENTER));
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		setupContents();

		setVisible(true);
	}

	private void setupContents() {
		setLayout(new BorderLayout());

		content = new ContentCanvas();
		timeline = new TimelineCanvas();

		add(content, BorderLayout.CENTER);
		add(timeline, BorderLayout.SOUTH);
	}

}
