/**
 * 
 */
package edu.stanford.hci.r3.actions;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class RetrieveURLAction {

	private URL url;

	/**
	 * @param aURL
	 */
	public RetrieveURLAction(URL aURL) {
		setURL(aURL);
	}

	public void invoke() {
		DebugUtils.println("Invoke called");
		if (url == null) {
			System.out.println("RetrieveURLAction :: url is null. Returning from invocation...");
			return;
		} else {
			URI uri;
			try {
				uri = url.toURI();
				System.out.println(uri);
				Desktop.getDesktop().open(new File("data/testFiles/ButterflyNetCHI2006.pdf"));
				Desktop.getDesktop().browse(uri);
				DebugUtils.println("Browsed");
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(this + " invoked");
		}
	}

	public void setURL(URL urlToOpen) {
		url = urlToOpen;
	}

	public String toActionString() {
		return "Display URL " + url.toString();
	}

	public String toString() {
		return "Retrieve URL: " + url.toString();
	}
}
