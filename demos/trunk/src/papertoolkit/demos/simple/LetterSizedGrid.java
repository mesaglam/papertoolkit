package papertoolkit.demos.simple;

import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import papertoolkit.PaperToolkit;
import papertoolkit.application.Application;
import papertoolkit.events.PenEvent;
import papertoolkit.events.handlers.ClickAdapter;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.paper.layout.FlowPaperLayout;
import papertoolkit.paper.regions.TextRegion;
import papertoolkit.pen.Pen;
import papertoolkit.render.SheetRenderer;
import papertoolkit.units.Inches;
import papertoolkit.units.coordinates.Coordinates;


/**
 * <p>
 * You may need to kick the VM up to 256Megabytes. (java -Xmx256M) Actually 512M works better. =)
 * Click on a grid, and it tells you which box you clicked on. Additionally, you can use up to four
 * pens at the SAME TIME!!! In fact, if your other pens are not connected, it's OK.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class LetterSizedGrid extends Application {

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
	@SuppressWarnings("unused")
	private static void renderPDF() {
		LetterSizedGrid grid = new LetterSizedGrid();
		SheetRenderer renderer = new SheetRenderer(grid.sheet);
		renderer.renderToPDF(new File("data/Grid/LetterSizedGridSheet.pdf"));
	}

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static void runApp() {
		LetterSizedGrid grid = new LetterSizedGrid();
		PaperToolkit p = new PaperToolkit();
		p.startApplication(grid);
	}

	private Sheet sheet;

	/**
	 * 
	 */
	public LetterSizedGrid() {
		super("A Letter-Sized Grid of Pattern Boxes");
		initializeAfterConstructor();
	}

	/**
	 * Called automatically by the super's constructor.
	 */
	protected void initializeAfterConstructor() {
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
		sheet.addConfigurationPath(new File("data/Grid/"));
		addSheet(sheet);

		// Add Two Pens
		// If the other pens aren't alive, keep on going with the local pen...
		final Pen pen1 = new Pen("Brown Pen (local)");
		final Pen pen2 = new Pen("Green Pen", "171.66.32.119"); // VeracruzX
		addPenInput(pen1);
		addPenInput(pen2);
	}
}
