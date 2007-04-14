package edu.stanford.hci.r3.demos.flash;

import sun.security.krb5.Config;
import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.config.Configuration;
import edu.stanford.hci.r3.config.Constants;
import edu.stanford.hci.r3.flash.whiteboard.FlashWhiteboard;
import edu.stanford.hci.r3.pen.Pen;

/**
 * <p>
 * An example of how you might use R3 to create a Wizard of Oz interface to capture pen input, and provide
 * feedback to an external device that looks like an advanced AI has "recognized" the text and drawings.
 * 
 * This doesn't really need to use the R3 App approach, but rather just attaches a Pen Listener to a pen
 * device, and sends that data over to the Flash Whiteboard component that we have. Later on, when we start
 * adding buttons, we may need to use the R3 app approach.
 * 
 * We'll try doing this with multiple pens.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class WizardOfOz {

	private PaperToolkit paperToolkit;
	private Pen pen;
	private Application app;
	private FlashWhiteboard whiteboard;

	public WizardOfOz() {
		paperToolkit = new PaperToolkit();
		// DebugUtils.println(paperToolkit.getProperty("unusedProperty"));

		pen = new Pen("Home", "solaria.stanford.edu");

		whiteboard = new FlashWhiteboard(Constants.Ports.FLASH_COMMUNICATION_SERVER);
		whiteboard.addPen(pen);

		app = new Application("Wizard of Oz Monitoring");
		app.addPenInput(pen);
		paperToolkit.startApplication(app);
	}

	public static void main(String[] args) {
		new WizardOfOz();
	}
}
