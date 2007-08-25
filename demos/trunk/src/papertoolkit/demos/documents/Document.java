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
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Document extends Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Document spreadsheet = new Document();
		new PaperToolkit().loadApplication(spreadsheet);
	}

	private Region region;

	private Sheet sheet;

	/**
	 * 
	 */
	public Document() {
		super("Word Document");
		addSheet(makeSheet());
	}

	/**
	 * @return
	 */
	private Region createRegion() {
		region = new Region("Text", 0, 0, 8.5, 11);
		final InkHandler inkCollector = new InkHandler() {
			public void handleInkStroke(PenEvent event, InkStroke mostRecentStroke) {
				DebugUtils.println(getNumStrokesCollected());
			}
		};
		region.addEventHandler(inkCollector);
		return region;
	}

	/**
	 * @return
	 */
	private Sheet makeSheet() {
		sheet = new PDFSheet(new File("data/Documents/Midsummer.pdf"));
		sheet.addRegion(createRegion());
		return sheet;
	}

}
