package edu.stanford.hci.r3.demos.grid;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.events.handlers.ClickHandler;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.paper.layout.FlowPaperLayout;
import edu.stanford.hci.r3.render.SheetRenderer;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.units.coordinates.Coordinates;

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
		application = new Application("A Grid of Pattern Boxes");
	}

	/**
	 * @see edu.stanford.hci.r3.Application#initializePaperUI()
	 */
	protected void initializePaperUI() {
		sheet = new Sheet(44, 24);

		// add boxes
		List<Region> regions = new ArrayList<Region>();
		for (int i = 0; i < 1400; i++) {
			final Region r = new Region("Box_" + i, 0, 0, .75, .75);
			r.addEventHandler(new ClickHandler() {

				@Override
				public void clicked(PenEvent e) {
					System.out.println("Clicked on " + r.getName());
				}

				@Override
				public void pressed(PenEvent e) {
					
				}

				@Override
				public void released(PenEvent e) {
					
				}
				
			});
			regions.add(r);
		}

		FlowPaperLayout.layout(sheet, regions, new Coordinates(new Inches(1), new Inches(0.2)),
				new Inches(42), new Inches(24), new Inches(0.2), new Inches(0.2));

		application.addSheet(sheet);
	}

	public static void main(String[] args) {
		Grid grid = new Grid();
		grid.initializePaperUI();
		SheetRenderer renderer = new SheetRenderer(grid.sheet);
		renderer.renderToPDF(new File("data/Grid/GridSheet.pdf"));
	}
}
