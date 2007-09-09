package papertoolkit.tools.services;

import papertoolkit.PaperToolkit;
import papertoolkit.events.EventDispatcher;
import papertoolkit.events.EventHandler;
import papertoolkit.events.PenEvent;

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
public class ToolkitMonitor {

	private PaperToolkit toolkit;
	private EventDispatcher dispatcher;
	private ToolkitMonitoringService monitoringService;

	public ToolkitMonitor(PaperToolkit paperToolkit, EventDispatcher eventDispatcher,
			ToolkitMonitoringService toolkitMonitoringService) {
		toolkit = paperToolkit;
		dispatcher = eventDispatcher;
		monitoringService = toolkitMonitoringService;
	}

	public void eventHandled(EventHandler handler, PenEvent event) {
		monitoringService.outputToClients("Event Handled: " + handler.toString() + " :: " + event);
	}
}
