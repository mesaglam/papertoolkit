package papertoolkit.tools.services;

import java.util.List;

import papertoolkit.events.EventHandler;
import papertoolkit.events.PenEvent;
import papertoolkit.paper.Region;
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

	private EventHandler lastEventHandler;
	private long lastEventHandlerTimestamp = 0L;

	/**
	 * Report when event handlers change...
	 * 
	 * @param handler
	 * @param event
	 */
	public void eventHandled(EventHandler handler, PenEvent event) {
		// intercept system outs too!
		// using System.setOut and the DebugUtils trick

		if (handler != null) {
			// show a new group entry if it has changed...
			// otherwise, send all pen downs and ups to be displayed in the datagrid
			if (lastEventHandler != handler || (event.getTimestamp() - lastEventHandlerTimestamp) > 3000) {

				// assume handlers are anonymous subclasses
				// get the super class's name
				List<Region> parentRegions = handler.getParentRegions();

				monitoringService.outputToClients("<eventHandler component=\"" + parentRegions.toString()
						+ "\" handlerName=\"" + handler.getClass().getSuperclass().getSimpleName()
						+ "\" time=\"" + event.getTimestamp() + "\"/>");
				lastEventHandler = handler;
				lastEventHandlerTimestamp = event.getTimestamp();
			}
		} else {
			// TODO: Dump these into the pen data panel somewhere
			// monitoringService.outputToClients("Unhandled Pen Strokes: " + event);
		}
	}

	public void penDown(InputDevice dev, PenSample sample) {
		monitoringService.outputToClients("Pen Down: " + dev + " :: " + sample);
	}

	public void penUp(InputDevice dev, PenSample sample) {
		monitoringService.outputToClients("Pen Up: " + dev + " :: " + sample);
	}
}
