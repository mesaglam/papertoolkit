package papertoolkit.tools.monitor;

import java.util.List;

import papertoolkit.application.Application;
import papertoolkit.events.EventHandler;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;

public class MonitorPaperUIInfo {

	private ToolkitMonitoringService service;
	private List<Application> apps;

	public MonitorPaperUIInfo(ToolkitMonitoringService toolkitMonitoringService, List<Application> loadedApps) {
		service = toolkitMonitoringService;
		apps = loadedApps;

		// right now, we sort of assume one app... but what the hey
		for (Application app : apps) {
			List<Sheet> sheets = app.getSheets();
			for (Sheet sheet : sheets) {
				List<Region> regions = sheet.getRegions();
				for (Region region : regions) {
					List<EventHandler> eventHandlers = region.getEventHandlers();
					for (EventHandler handler : eventHandlers) {
						// right now, assume every region has a handler...
						service.outputToClients("<addHandler handlerName=\"" + handler.getName() //
								+ "\" regionName=\"" + region.getName() //
								+ "\" sheetName=\"" + sheet.getName() //
								+ "\" />");
					}
				}
			}
		}
	}
}
