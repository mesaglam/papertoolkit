package papertoolkit.demos.debug;


import papertoolkit.application.Application;
import papertoolkit.events.PenEvent;
import papertoolkit.events.handlers.ClickHandler.ClickAdapter;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.paper.Sheet.SheetSize;
import papertoolkit.util.DebugUtils;

/**
 * <p>
 * This application has a single region with a click handler. If you do not wish to print this region, you can
 * bind it to a pre-patterned notebook by inspecting the system tray icon at runtime.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class BindRegionsApp {

	public BindRegionsApp() {
		Application application = new Application("Bind Me");
		Sheet sheet = new Sheet(SheetSize.A5);
		Region r = new Region("Button", 2, 1);
		application.addSheet(sheet);
		sheet.addRegion(r);
		r.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("Clicked at: " + e.getPercentageLocation());
			}
		});
		application.run();
	}
	
	public static void main(String[] args) {
		new BindRegionsApp();
	}
}
