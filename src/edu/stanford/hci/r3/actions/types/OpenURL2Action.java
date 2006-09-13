package edu.stanford.hci.r3.actions.types;

import java.net.URL;

import net.sf.wraplog.SystemLogger;
import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingExecutionException;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import edu.stanford.hci.r3.actions.R3Action;

/**
 * <p>
 * Uses BrowserLauncher to launch a URL. Use this if the other OpenURLAction does not work (quickly)
 * for you.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class OpenURL2Action implements R3Action {

	public static final String FIREFOX = "FIREFOX";

	public static final String IE = "IE";

	private String browser;

	private URL url;

	/**
	 * @param theURL
	 */
	public OpenURL2Action(URL theURL) {
		this(theURL, null);
	}

	/**
	 * This one works the best on my desktop machine. I have no idea why...
	 * 
	 * @param theURL
	 * @param targetBrowser
	 */
	public OpenURL2Action(URL theURL, String targetBrowser) {
		url = theURL;
		browser = targetBrowser;
	}

	public void invoke() {
		try {
			BrowserLauncher b = new BrowserLauncher(new SystemLogger());
			// List browserList = b.getBrowserList();
			// System.out.println(browserList);
			if (browser == null) {
				// slow on my desktop. =\ Maybe it's my desktop's fault
				b.openURLinBrowser(url.toString());
			} else {
				b.openURLinBrowser(browser, url.toString());
			}
		} catch (BrowserLaunchingInitializingException e) {
			e.printStackTrace();
		} catch (UnsupportedOperatingSystemException e) {
			e.printStackTrace();
		} catch (BrowserLaunchingExecutionException e) {
			e.printStackTrace();
		}
	}

}
