package edu.stanford.hci.r3.actions.remote;

import java.awt.Color;

import edu.stanford.hci.r3.actions.types.DisplayGraphicsAction;
import edu.stanford.hci.r3.util.WindowUtils;
import edu.stanford.hci.r3.util.networking.ClientServerType;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Test_ActionSender {

	public static void main(String[] args) {
		final ActionSender sender = new ActionSender("localhost", ActionReceiver.DEFAULT_JAVA_PORT,
				ClientServerType.JAVA);

		DisplayGraphicsAction dga = new DisplayGraphicsAction();
		final int w = 1024;
		final int h = 768;
		dga.setFrameSize(w, h);
		dga.setFrameLocation(WindowUtils.getWindowOrigin(w, h, WindowUtils.DESKTOP_CENTER));
		dga.setBringToFront(true);
		dga.maximizeFrame();
		dga.setColor(Color.ORANGE);
		sender.invokeRemoteAction(dga);
	}
}
