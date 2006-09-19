package edu.stanford.hci.r3.demos.grid;

import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.events.handlers.ClickAdapter;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.paper.layout.FlowPaperLayout;
import edu.stanford.hci.r3.paper.regions.TextRegion;
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
public class LetterSizedGrid extends Application {

	private Sheet sheet;

	/**
	 * 
	 */
	public LetterSizedGrid() {
		super("A Letter-Sized Grid of Pattern Boxes");
	}

	/**
	 * 
	 */
	protected void initialize() {
		sheet = new Sheet(8.5, 11); // Letter Sized

		final Font font = new Font("Tahoma", Font.PLAIN, 18);

		// add boxes
		final List<Region> regions = new ArrayList<Region>();
		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < 5; col++) {
				final String rname = "Box_" + col + "_" + row;
				final Region r = new TextRegion(rname, rname, font, new Inches(0), new Inches(0),
						new Inches(1), new Inches(1));
				r.addEventHandler(new ClickAdapter() {
					@Override
					public void clicked(PenEvent e) {
						System.out.println("Clicked on " + r.getName());
						System.out.println(e.getStreamedPatternCoordinate());
					}
				});
				regions.add(r);
			}
		}
		// add the regions to the sheet...
		FlowPaperLayout.layout(sheet, regions, new Coordinates(new Inches(0.5), new Inches(0.5)),
				new Inches(7.5), new Inches(10), new Inches(0.5), new Inches(0.8));

		// must go before the addSheet for now...
		sheet.registerConfigurationPath(new File("data/Grid/"));
		addSheet(sheet);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		runApp();
		// renderPDF();
	}

	/**
	 * 
	 */
	private static void runApp() {
		LetterSizedGrid grid = new LetterSizedGrid();
		PaperToolkit p = new PaperToolkit();
		p.startApplication(grid);
	}

	/**
	 * 
	 */
	private static void renderPDF() {
		LetterSizedGrid grid = new LetterSizedGrid();
		SheetRenderer renderer = new SheetRenderer(grid.sheet);
		renderer.renderToPDF(new File("data/Grid/LetterSizedGridSheet.pdf"));
	}
}
