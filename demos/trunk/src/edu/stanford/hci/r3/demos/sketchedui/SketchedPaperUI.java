package edu.stanford.hci.r3.demos.sketchedui;
import java.io.File;
import java.io.IOException;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.events.ContentFilterListener;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.events.filters.InkCollector;
import edu.stanford.hci.r3.events.handlers.ClickAdapter;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.sheets.XMLSheet;
import edu.stanford.hci.r3.tools.design.swing.SheetFrame;
import edu.stanford.hci.r3.util.DebugUtils;


/**
 * Generated code
 * 
 * @author R3 Paper Toolkit
 */
public class SketchedPaperUI extends Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		PaperToolkit p = new PaperToolkit(true /* use app manager */);
		p.loadApplication(new SketchedPaperUI());
	}

	/**
	 * 
	 */
	private XMLSheet sheet;

	/**
	 * 
	 */
	public SketchedPaperUI() {
		super("SketchedPaperUI");
		try {
			sheet = new XMLSheet(new File("data/sketchedui/SketchedPaperUI.xml"));
			
			addSheet(sheet);

			(new SheetFrame(sheet,640,640)).setVisible(true);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		initializePaperUI();
	}
	
	/**
	 * 
	 */
	private void initializePaperUI() {
		
		Region regionRegion1 = sheet.getRegion("Region1");
		setupRegion1(regionRegion1);
		
		Region regionRegion2 = sheet.getRegion("Region2");
		setupRegion2(regionRegion2);
		
		Region regionRegion3 = sheet.getRegion("Region3");
		setupRegion3(regionRegion3);
		
		Region regionRegion4 = sheet.getRegion("Region4");
		setupRegion4(regionRegion4);
		
	}

	
	private void setupRegion1(Region region) {
		final InkCollector inkCollector = new InkCollector();
		inkCollector.addListener(new ContentFilterListener() {
			public void contentArrived() {
				DebugUtils.println("Content arrived in Region1.");
			}
		});
		region.addContentFilter(inkCollector);
		
		
		
	}
	
	private void setupRegion2(Region region) {
		region.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on Region2.");
			}
		});
		
		
		
	}
	
	private void setupRegion3(Region region) {
		region.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on Region3.");
			}
		});
		
		
		
	}
	
	private void setupRegion4(Region region) {
		region.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on Region4.");
			}
		});
		
		
		
	}
	

}
