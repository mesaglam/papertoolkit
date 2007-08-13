package papertoolkit.demos.unfinished;

import papertoolkit.PaperToolkit;
import papertoolkit.application.Application;
import papertoolkit.events.PenEvent;
import papertoolkit.events.handlers.ClickHandler;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;

public class MapApp extends Application {

	private Sheet s;

	public MapApp() {
		super("Maps!");

		
		Region r = new Region("Click", 2, 3, 4, 4);
		r.addEventHandler(new ClickHandler() {
			@Override
			public void clicked(PenEvent e) {
				// device.display(....)
				
			}

			@Override
			public void pressed(PenEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void released(PenEvent e) {
				// TODO Auto-generated method stub
				
			}
		});

		
		
		
		
	}

	public static void main(String[] args) {
		
		Application map = new MapApp();
		
		PaperToolkit.runApplication(map);
		
		
		
	}
}
