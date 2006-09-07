package edu.stanford.hci.r3.actions.types;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import edu.stanford.hci.r3.actions.R3Action;

/**
 * <p>
 * Uses the default browser to launch the specified URL. WARNING: This seems really really slow on
 * Java 6. It is slow (a minute??) on both Firefox and IE7. It used to work well through JDIC on
 * Java 5. We may implement an alternative.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class OpenURLAction implements R3Action {

	private URL url;

	public OpenURLAction(URL theURL) {
		url = theURL;
	}

	public void invoke() {
		try {
			System.out.println();
			final URI toURI = url.toURI();
			System.out.println("Got the URI");
			Desktop.getDesktop().browse(toURI);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

}
