package papertoolkit.demos.sketchedui;

import java.io.File;
import java.io.IOException;

import papertoolkit.PaperToolkit;
import papertoolkit.application.Application;
import papertoolkit.events.PenEvent;
import papertoolkit.events.handlers.ClickAdapter;
import papertoolkit.events.handlers.InkHandler;
import papertoolkit.paper.Region;
import papertoolkit.paper.sheets.XMLSheet;
import papertoolkit.pen.ink.InkStroke;
import papertoolkit.tools.design.swing.SheetFrame;
import papertoolkit.util.DebugUtils;


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

			(new SheetFrame(sheet, 640, 640)).setVisible(true);
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
		final InkHandler inkCollector = new InkHandler() {
			public void handleInkStroke(PenEvent event, InkStroke mostRecentStroke) {
				DebugUtils.println("Content arrived in Region1.");
			}
		};
		region.addEventHandler(inkCollector);

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
