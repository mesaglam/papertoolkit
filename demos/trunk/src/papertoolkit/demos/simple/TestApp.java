package papertoolkit.demos.simple;

import papertoolkit.application.Application;
import papertoolkit.events.PenEvent;
import papertoolkit.events.handlers.InkHandler;
import papertoolkit.events.handlers.ClickHandler.ClickAdapter;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.pen.ink.InkStroke;
import papertoolkit.util.DebugUtils;

public class TestApp {

	public static void main(String[] args) {

		Application application = new Application("Test");
		Sheet s = application.createSheet();
		s.setName("Main Sheet");
		
		Region mainCanvas = s.createRegion(1, 1, 6.5, 7);
		mainCanvas.setName("MainCanvas");
		mainCanvas.addEventHandler(new InkHandler() {
			public void handleInkStroke(PenEvent event, InkStroke mostRecentStroke) {
				DebugUtils.println("Stroke");
			}
		});
		
		Region sketch = s.createRegion(3, 8.5, 0.5, 0.5);
		sketch.setName("Sketch");
		sketch.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("Clicked Sketch");
			}
		});
		
		Region train = s.createRegion(5, 8.5, 0.5, 0.5);
		train.setName("Train");
		train.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("Clicked Train");
			}
		});

		Region retrieve = s.createRegion(7, 8.5, 0.5, 0.5);
		retrieve.setName("Retrieve");
		retrieve.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("Clicked Retrieve");
			}
		});
		application.run();
	}
}
