package edu.stanford.hci.r3.tools.debug;

import java.io.File;
import java.util.List;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.events.EventHandler;
import edu.stanford.hci.r3.flash.FlashCommunicationServer;
import edu.stanford.hci.r3.flash.FlashListener;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * To help you visualize the event handlers and otherwise debug the paper UI and application. This
 * class contains the bulk of the debugging support, whereas the other classes represent the GUI.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class DebuggingEnvironment {

	private Application app;
	private DebugPCanvas canvas;
	private FlashCommunicationServer flash;
	private DebugFrame frame;

	/**
	 * @param paperApp
	 */
	public DebuggingEnvironment(Application paperApp) {
		app = paperApp;

		DebugUtils.println("Starting to debug " + app);

		// ---------------------------------
		// out first try used Piccolo
		// startPiccoloDebugView(paperApp);
		// ---------------------------------

		// now, we'll use Flash as our GUI
		startFlashDebuger();
	}

	/**
	 * 
	 */
	private void sendAppLayout() {
		List<Sheet> sheets = app.getSheets();
		DebugUtils.println("Number of Sheets: " + sheets.size());
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
				msg.append("<region name='" + r.getName() + "'x=\"" + rX + "\" y=\"" + rY
						+ "\" w=\"" + rWidth + "\" h=\"" + rHeight + "\">");

				List<EventHandler> eventHandlers = r.getEventHandlers();
				for (EventHandler eh : eventHandlers) {
					// DebugUtils.println(eh.toString());

					String eventType = eh.getClass().getSuperclass().getSimpleName();
					String container = eh.getClass().toString();
					container = container.substring(0, container.indexOf("$"));

					msg.append("<eventhandler name='" + eventType + "' location='" + container
							+ "'/>");
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

	public void showFlashView() {
		// start the Flash GUI
		File r3RootPath = PaperToolkit.getToolkitRootPath();
		final File eventVizHTML = new File(r3RootPath, "flash/bin/EventViz.html");
		flash.openFlashGUI(eventVizHTML);
		flash.removeAllFlashClientListeners(); // HACK: for now...
		flash.addFlashClientListener(new FlashListener() {
			@Override
			public void messageReceived(String command) {
				if (command.equals("eventvizclient connected")) {
					DebugUtils.println("Flash Client Connected!");
					sendApplicationLayout();
				} else if (command.equals("load most recent pattern mappings")) {
					app.getHostToolkit().loadMostRecentPatternMappings();
				}
			}
		});
	}

	/**
	 * 
	 */
	private void startFlashDebuger() {
		// Start the local messaging server
		flash = new FlashCommunicationServer();
		showFlashView();
	}

	/**
	 * @param paperApp
	 */
	private void startPiccoloDebugView(Application paperApp) {
		// set up a GUI
		frame = new DebugFrame(paperApp.getName());
		canvas = frame.getCanvas();

		// add visual components to GUI
		canvas.addVisualComponents(paperApp);
	}

	/**
	 * @param msg
	 * @param r
	 */
	public void visualize(String msg, Region r) {
		flash.sendMessage("<showMe msg='" + msg + "' regionName='" + r.getName() + "'/>");
	}

}
