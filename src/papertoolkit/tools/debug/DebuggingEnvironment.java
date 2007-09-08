package papertoolkit.tools.debug;

import java.io.File;
import java.util.List;

import papertoolkit.PaperToolkit;
import papertoolkit.application.Application;
import papertoolkit.events.EventHandler;
import papertoolkit.flash.FlashCommunicationServer;
import papertoolkit.flash.FlashListener;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;

/**
 * <p>
 * To help you visualize the event handlers and otherwise debug the paper UI and application. This class
 * contains the bulk of the debugging support, whereas the other classes represent the GUI.
 * 
 * NOTE: Removed a Utils.java file in this package, at revision 629. That included an attempt by Marcello to
 * automatically determine the source surrounding a called method, through introspection with the debugger.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class DebuggingEnvironment {

	public static String escapeLiteral(String s) {
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}

	private Application app;
	private FlashCommunicationServer flash;

	/**
	 * @param paperApp
	 */
	public DebuggingEnvironment(Application app) {
		// Start the local messaging server
		flash = new FlashCommunicationServer();
		setApp(app);
	}

	/**
	 * 
	 */
	private void sendAppLayout() {
		List<Sheet> sheets = app.getSheets();
		// DebugUtils.println("Number of Sheets: " + sheets.size());
		// assume one sheet for now...
		StringBuilder msg = new StringBuilder();
		msg.append("<app>");
		for (Sheet s : sheets) {
			msg.append("<sheet w=\"" + s.getWidth().getValueInInches() + "\" h=\""
					+ s.getHeight().getValueInInches() + "\">");
			for (Region r : s.getRegions()) {
				double rX = r.getOriginX().getValueInInches();
				double rY = r.getOriginY().getValueInInches();
				double rWidth = r.getWidth().getValueInInches();
				double rHeight = r.getHeight().getValueInInches();
				msg.append("<region name='" + r.getName() + "'x=\"" + rX + "\" y=\"" + rY + "\" w=\""
						+ rWidth + "\" h=\"" + rHeight + "\">");

				List<EventHandler> eventHandlers = r.getEventHandlers();
				for (EventHandler eh : eventHandlers) {
					// DebugUtils.println(eh.toString());

					String eventType = eh.getClass().getSuperclass().getSimpleName();
					String container = eh.getClass().toString();
					container = container.substring(0, container.indexOf("$"));

					msg.append("<eventhandler name='" + eventType + "' location='" + container + "'/>");
				}
				msg.append("</region>");
			}
			msg.append("</sheet>");
		}
		msg.append("</app>");
		flash.sendMessage(msg.toString());
	}

	/**
	 * 
	 */
	private void sendApplicationLayout() {
		flash.sendMessage("<loadingapplication/>");
		sendAppLayout();
	}

	/**
	 * @param paperApp
	 */
	private void setApp(Application paperApp) {
		app = paperApp;
		app.setDebuggingEnvironment(this);
		// DebugUtils.println("Starting to debug " + app);
	}

	/**
	 * start the Flash GUI
	 */
	public void showFlashView() {
		File r3RootPath = PaperToolkit.getToolkitRootPath();
		final File eventVizHTML = new File(r3RootPath, "flash/bin/EventViz.html");
		flash.openFlashHTMLGUI(eventVizHTML);
		flash.removeAllFlashClientListeners(); // HACK: for now...
		flash.addFlashClientListener(new FlashListener() {
			public boolean messageReceived(String command, String...args) {
				if (command.equals("eventvizclient connected")) {
					// DebugUtils.println("Flash Client Connected!");
					sendApplicationLayout();
					return CONSUMED;
				} else if (command.equals("load most recent pattern mappings")) {
					app.getHostToolkit().loadMostRecentPatternMappings();
					return CONSUMED;
				} else {
					return NOT_CONSUMED;
				}
			}
		});
	}

	/**
	 * @param msg
	 * @param r
	 */
	public void visualize(String msg, Region r, String code) {
		String message = "<showMe msg='" + msg + "' regionName='" + r.getName() + "'";
		if (code != null)
			message += "><code>" + escapeLiteral(code) + "</code></showMe>";
		else
			message += "/>";
		flash.sendMessage(message);
	}

}
