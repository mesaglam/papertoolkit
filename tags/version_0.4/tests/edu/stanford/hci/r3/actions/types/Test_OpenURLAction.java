package edu.stanford.hci.r3.actions.types;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Test_OpenURLAction {
	public static void main(String[] args) {
		try {
			OpenURLAction o = new OpenURLAction(new URL("http://www.yahoo.com/"));
			System.out.println("Invoking now...");
			o.invoke();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
