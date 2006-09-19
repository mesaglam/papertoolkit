package edu.stanford.hci.r3.actions.remote;

import java.net.MalformedURLException;
import java.net.URL;

import edu.stanford.hci.r3.actions.types.OpenURL2Action;
import edu.stanford.hci.r3.networking.ClientServerType;

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
public class Test_ActionSenderAndReceiver {

	public static void main(String[] args) {
		// start the receiver tray app
		ActionReceiverTrayApp.main(null);

		// 10 seconds later...
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		final ActionSender sender = new ActionSender("localhost", ActionReceiver.DEFAULT_JAVA_PORT,
				ClientServerType.JAVA);

		try {
			sender.invokeRemoteAction(new OpenURL2Action(new URL("http://www.flickr.com/"),
					OpenURL2Action.FIREFOX));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
