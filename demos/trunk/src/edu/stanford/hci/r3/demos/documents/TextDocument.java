package edu.stanford.hci.r3.demos.documents;

import java.io.File;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.events.filters.InkCollector;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.paper.sheets.PDFSheet;
import edu.stanford.hci.r3.util.DebugUtils;

public class TextDocument extends Application {

	public static void main(String[] args) {
		TextDocument spreadsheet = new TextDocument();
		new PaperToolkit(true).loadApplication(spreadsheet);
	}

	private Region region;

	private Sheet sheet;

	public TextDocument() {
		super("TextDocument");
		addSheet(createSheet());
	}

	private Region createRegion() {
		region = new Region("Text", 0, 0, 8.5, 11);
		final InkCollector inkCollector = new InkCollector() {
			public void contentArrived() {
				DebugUtils.println(getNumStrokesCollected());
			}
		};
		region.addContentFilter(inkCollector);
		return region;
	}

	private Sheet createSheet() {
		sheet = new PDFSheet(new File("data/Documents/Midsummer.pdf"));
		sheet.addRegion(createRegion());
		return sheet;
	}

}
