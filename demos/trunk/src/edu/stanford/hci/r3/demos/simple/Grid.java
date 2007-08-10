package edu.stanford.hci.r3.demos.simple;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import papertoolkit.application.Application;
import papertoolkit.events.PenEvent;
import papertoolkit.events.handlers.ClickAdapter;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.paper.layout.FlowPaperLayout;
import papertoolkit.render.SheetRenderer;
import papertoolkit.units.Inches;
import papertoolkit.units.coordinates.Coordinates;


/**
 * <p>
 * You may need to kick the VM up to 256Megabytes. (java -Xmx256M) Actually 512M works better. =)
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Grid {

	private Application application;

	private Sheet sheet;

	/**
	 * 
	 */
	public Grid() {
		application = new Application("A Grid of Patterned Boxes");
	}

	/**
	 * @see papertoolkit.application.Application#initializeAfterConstructor()
	 */
	protected void initializePaperUI() {
		sheet = new Sheet(44, 24);

		// add boxes
		List<Region> regions = new ArrayList<Region>();
		for (int i = 0; i < 1400; i++) {
			final Region r = new Region("Box_" + i, 0, 0, .75, .75);
			r.addEventHandler(new ClickAdapter() {
				@Override
				public void clicked(PenEvent e) {
					System.out.println("Clicked on " + r.getName());
				}
			});
			regions.add(r);
		}

		FlowPaperLayout.layout(sheet, regions, new Coordinates(new Inches(1), new Inches(0.2)),
				new Inches(42), new Inches(24), new Inches(0.2), new Inches(0.2));

		application.addSheet(sheet);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Grid grid = new Grid();
		grid.initializePaperUI();
		SheetRenderer renderer = new SheetRenderer(grid.sheet);
		renderer.renderToPDF(new File("data/Grid/GridSheet.pdf"));
	}
}
