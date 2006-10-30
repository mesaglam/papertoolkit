package edu.stanford.hci.r3.development;

import java.util.List;

import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.paper.sheets.PDFSheet;

/**
 * <p>
 * Makes coding easy by generating templates and boilerplate for you.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class CodeGenerator {

	/**
	 * How many tabs to put before any line.
	 */
	private int indent = 0;

	private StringBuilder stringBuilder;

	public CodeGenerator() {
	}

	/**
	 * @param line
	 */
	private void addLine(String line) {
		for (int i = 0; i < indent; i++) {
			stringBuilder.append("\t"); // indent!
		}
		stringBuilder.append(line + "\n");
	}

	/**
	 * 
	 */
	private void decreaseIndent() {
		if (indent == 0) {
			return;
		}
		indent--;
	}

	/**
	 * Generates boilerplate code for initializing your paper UIs after you have read them in from
	 * an xml file.
	 * 
	 * @param sheet
	 */
	public void generateInitializePaperUI(Sheet sheet) {
		stringBuilder = new StringBuilder();
		List<Region> regions = sheet.getRegions();
		addLine("private void initializePaperUI() {");
		increaseIndent();
		for (Region r : regions) {
			String regionName = r.getName(); // remove underscores
			String regionNameNoUnderscores = regionName.replace("_", "");
			final String regionIdentifier = "region" + regionNameNoUnderscores;
			addLine("Region " + regionIdentifier + " = sheet.getRegion(\"" + regionName + "\");");
			addLine("setup" + regionNameNoUnderscores + "(" + regionIdentifier + ");");
			addLine("");
		}
		decreaseIndent();
		addLine("}");
		System.out.println("Code for Generating a Paper UI");
		System.out.println("------------------------------");
		System.out.print(stringBuilder);
		System.out.println("------------------------------");
	}

	/**
	 * @param sheet
	 */
	public void generateSetupRegionMethods(PDFSheet sheet) {
		stringBuilder = new StringBuilder();
		List<Region> regions = sheet.getRegions();
		for (Region r : regions) {
			String regionName = r.getName().replace("_", ""); // remove underscores
			addLine("private void setup" + regionName + "(Region region) {");
			increaseIndent();
			addLine("region.addEventHandler(new ClickAdapter() {");
			increaseIndent();
			addLine("@Override");
			addLine("public void clicked(PenEvent e) {");
			increaseIndent();
			addLine("DebugUtils.println(\"You clicked on " + regionName + ".\");");
			decreaseIndent();
			addLine("}");
			decreaseIndent();
			addLine("});");
			decreaseIndent();
			addLine("}");
		}

		System.out.println("Code for Setting up Regions with Event Handlers");
		System.out.println("------------------------------");
		System.out.print(stringBuilder);
		System.out.println("------------------------------");
	}

	/**
	 * 
	 */
	private void increaseIndent() {
		indent++;
	}
}
