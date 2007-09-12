package papertoolkit.tools.services;

import papertoolkit.events.EventHandler;
import papertoolkit.events.PenEvent;
import papertoolkit.pen.InputDevice;
import papertoolkit.pen.PenSample;

/**
 * <p>
 * All of the methods in this class are called by various toolkit objects to broadcast information to
 * listeners.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class ToolkitMonitor {

	private ToolkitMonitoringService monitoringService;

	public ToolkitMonitor(ToolkitMonitoringService toolkitMonitoringService) {
		monitoringService = toolkitMonitoringService;
	}

	public void eventHandled(EventHandler handler, PenEvent event) {
		if (handler != null) {
			monitoringService.outputToClients("Event Handled: " + handler + " :: " + event);
		} else {
			monitoringService.outputToClients("Unhandled Event: " + event);
		}
	}

	public void penDown(InputDevice dev, PenSample sample) {
		monitoringService.outputToClients("Pen Down: " + dev + " :: " + sample);
	}

	public void penUp(InputDevice dev, PenSample sample) {
		monitoringService.outputToClients("Pen Up: " + dev + " :: " + sample);
	}
}
