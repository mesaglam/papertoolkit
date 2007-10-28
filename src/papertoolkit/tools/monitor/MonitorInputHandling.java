package papertoolkit.tools.monitor;

import java.util.List;

import papertoolkit.PaperToolkit;
import papertoolkit.application.Application;
import papertoolkit.events.EventDispatcher;
import papertoolkit.events.EventHandler;
import papertoolkit.events.PenEvent;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.pen.InputDevice;
import papertoolkit.pen.PenSample;
import papertoolkit.pen.streaming.listeners.PenListener;
import papertoolkit.util.DebugUtils;

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
public class MonitorInputHandling {

	private EventHandler lastEventHandler;
	private long lastEventHandlerTimestamp = 0L;
	private ToolkitMonitoringService monitoringService;

	public MonitorInputHandling(ToolkitMonitoringService toolkitMonitoringService) {
		monitoringService = toolkitMonitoringService;

		PaperToolkit toolkit = toolkitMonitoringService.getToolkit();

		// instrument the event dispatcher
		EventDispatcher eventDispatcher = toolkit.getEventDispatcher();
		eventDispatcher.setMonitor(this);

		List<Application> loadedApps = toolkit.getLoadedApps();
		for (Application app : loadedApps) {
			// instrument all pens
			List<InputDevice> penInputDevices = app.getPenInputDevices();
			for (final InputDevice dev : penInputDevices) {
				dev.addLivePenListener(new PenListener() {
					public void penDown(PenSample sample) {
						MonitorInputHandling.this.penDown(dev, sample);
					}

					public void penUp(PenSample sample) {
						MonitorInputHandling.this.penUp(dev, sample);
					}

					public void sample(PenSample sample) {
						// don't do anything here (for now), because it's too much info

						// send it...
					}
				});
			}

			// instrument all event handlers!
		}
	}

	/**
	 * Report when event handlers change...
	 * 
	 * @param handler
	 * @param event
	 */
	public void eventHandled(EventHandler handler, PenEvent event) {
		// intercept system outs too!
		// using System.setOut and the DebugUtils trick

		DebugUtils.println("Sending Event Handled Information: " + handler);

		if (handler != null) {
			// show a new group entry if it has changed...
			// otherwise, send all pen downs and ups to be displayed in the datagrid
			if (lastEventHandler != handler) {
				// || (event.getTimestamp() - lastEventHandlerTimestamp) > 3000

				// assume handlers are anonymous subclasses
				// get the super class's name
				List<Region> parentRegions = handler.getParentRegions();

				if (parentRegions.size() == 0) {
					DebugUtils.println("No Parent Regions");
					return;
				}

				// really, the handler name should be sufficient, but we'll send this duplicate info for
				// now...
				Region firstRegion = parentRegions.get(0);
				double rX = firstRegion.getOriginX().getValueInInches();
				double rY = firstRegion.getOriginY().getValueInInches();
				double rW = firstRegion.getWidth().getValueInInches();
				double rH = firstRegion.getHeight().getValueInInches();

				final Sheet parentSheet = firstRegion.getParentSheet();

				StringBuilder sheetAndRegions = new StringBuilder();
				sheetAndRegions.append("<eventHandledOnSheet sheet=\"" + parentSheet.getName() + "\" >");
				for (Region r : parentSheet.getRegions()) {
					sheetAndRegions.append("<region name=\"" + r.getName() + "\" rX=\""
							+ r.getOriginX().getValueInInches() + "\" rY=\""
							+ r.getOriginY().getValueInInches() + "\" rW=\""
							+ r.getWidth().getValueInInches() + "\" rH=\"" + r.getHeight().getValueInInches()
							+ "\" />");
				}
				sheetAndRegions.append("</eventHandledOnSheet>");

				monitoringService.outputToClients("<eventHandler component=\"" + firstRegion.getName()
						+ "\" handlerName=\"" + handler.getClass().getSuperclass().getSimpleName()
						+ "\" time=\"" + event.getTimestamp() + "\" rX=\"" + rX + "\" rY=\"" + rY
						+ "\" rW=\"" + rW + "\" rH=\"" + rH + "\" />");

				monitoringService.outputToClients(sheetAndRegions.toString());

				lastEventHandler = handler;
				lastEventHandlerTimestamp = event.getTimestamp();
			} else {
				// same handler... send ink over instead!

				monitoringService.outputToClients("<penSampleHandled xInches=\""
						+ event.getPercentageLocation().getX().getValueInInches() + "\" yInches=\""
						+ event.getPercentageLocation().getY().getValueInInches() + "\" />");

			}
		} else {
			// TODO: Dump these into the pen data panel somewhere
			// monitoringService.outputToClients("Unhandled Pen Strokes: " + event);
		}
	}

	/**
	 * @param dev
	 * @param sample
	 */
	public void penDown(InputDevice dev, PenSample sample) {
		monitoringService.outputToClients("Pen Down: " + dev + " :: " + sample);
	}

	/**
	 * @param dev
	 * @param sample
	 */
	public void penUp(InputDevice dev, PenSample sample) {
		monitoringService.outputToClients("Pen Up: " + dev + " :: " + sample);
	}
}
