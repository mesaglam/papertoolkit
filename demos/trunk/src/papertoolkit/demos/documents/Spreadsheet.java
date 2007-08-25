package papertoolkit.demos.documents;

import java.io.File;

import papertoolkit.PaperToolkit;
import papertoolkit.application.Application;
import papertoolkit.events.PenEvent;
import papertoolkit.events.handlers.InkHandler;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.paper.sheets.PDFSheet;
import papertoolkit.pen.ink.InkStroke;
import papertoolkit.util.DebugUtils;


/**
 * <p>
 * 
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class Spreadsheet extends Application {

	private Sheet sheet;
	private Region region;

	public Spreadsheet() {
		super("Spreadsheet");
		addSheet(makeSheet());
	}

	private Sheet makeSheet() {
		sheet = new PDFSheet(new File("data/Documents/DataSheet.pdf"));
		sheet.addRegion(createRegion());
		return sheet;
	}

	private Region createRegion() {
		region = new Region("Data", 0, 0, 8.5, 11);
		final InkHandler inkCollector = new InkHandler() {
			public void handleInkStroke(PenEvent event, InkStroke mostRecentStroke) {
				DebugUtils.println(getNumStrokesCollected());
			}
		};
		region.addEventHandler(inkCollector);
		return region;
	}

	public static void main(String[] args) {
		Spreadsheet spreadsheet = new Spreadsheet();
		new PaperToolkit().loadApplication(spreadsheet);
	}

}
