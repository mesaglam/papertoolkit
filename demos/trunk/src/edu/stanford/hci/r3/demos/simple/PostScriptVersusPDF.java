package edu.stanford.hci.r3.demos.simple;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.paper.layout.FlowPaperLayout;
import papertoolkit.render.SheetRenderer;
import papertoolkit.units.Inches;
import papertoolkit.units.coordinates.Coordinates;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.files.FileUtils;


/**
 * <p>
 * This class demonstrates how you can render a PS and/or a PDF file of your Sheet. Warning: For some reason,
 * Adobe Reader 7 Renders the PDF files produced by this toolkit much faster than Adobe Reader 8, or even
 * Acrobat PRO 7/8.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PostScriptVersusPDF {

	public PostScriptVersusPDF() {
		Sheet sheet = new Sheet(8.5, 11);

		List<Region> postIts = new ArrayList<Region>();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 4; j++) {
				String name = "PostIt_" + ((i * 4) + j);
				DebugUtils.println(name);
				Region region = new Region(name, 0, 0, 2.5, 2.5);
				region.setActive(true);
				postIts.add(region);
			}
		}

		FlowPaperLayout.layout(sheet, postIts, //
				new Coordinates(new Inches(0.25), new Inches(0.15)), //
				new Inches(8), new Inches(11), //
				new Inches(0), new Inches(0.2));

		SheetRenderer sheetRenderer = new SheetRenderer(sheet);
		sheetRenderer.renderToPDF(new File(FileUtils.getDesktopDirectory(), "Sheet.pdf"));
		sheetRenderer.renderToPostScript(new File(FileUtils.getDesktopDirectory(), "Sheet.ps"));
		sheetRenderer.setRenderActiveRegionsWithPattern(false);
		sheetRenderer.renderToPDF(new File(FileUtils.getDesktopDirectory(), "SheetNoPattern.pdf"));
		sheetRenderer.renderToPostScript(new File(FileUtils.getDesktopDirectory(), "SheetNoPattern.ps"));
	}

	public static void main(String[] args) {
		new PostScriptVersusPDF();
	}
}
