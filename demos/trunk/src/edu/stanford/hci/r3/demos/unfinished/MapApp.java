package edu.stanford.hci.r3.demos.unfinished;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.application.Application;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.events.handlers.ClickHandler;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;

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
