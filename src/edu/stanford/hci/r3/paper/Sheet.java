package edu.stanford.hci.r3.paper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.designer.acrobat.RegionConfiguration;
import edu.stanford.hci.r3.render.SheetRenderer;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.units.Size;
import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.units.coordinates.Coordinates;

/**
 * <p>
 * Represents one sheet of interactive/augmented paper. This sheet can be large (like a GIGAprint)
 * or it can be smaller, like an 8.5x11" print.
 * </p>
 * <p>
 * The Sheet can be rendered in many different contexts. It can be rendered to a PDF/PS file, or
 * printed to a Java2D printer. It can also be rendered to the screen for a quick preview. Each of
 * these options has different advantages/disadvantages. For example, when rendering to the screen
 * or Java2D, no dot pattern is drawn by default. This is because doing so would be inefficient.
 * </p>
 * <p>
 * The dimensions of the sheet is kept in Units objects, and is only normalized (usually to Points)
 * when necessary (i.e., when rendering a PDF).
 * </p>
 * <p>
 * On Coordinate Systems: To maintain the analogue with GUI development, we will choose the origin
 * (0,0) to be the top-left corner of the document (I can hear the screams of PDF/Postscript
 * enthusiasts already--I'm sorry). We will make it easy to flip the coordinate systems to a more
 * Postscript-friendly way later. Possibly, we'll have a call like setCoordinateSystem(GUI |
 * POSTSCRIPT).
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Sheet {

	private static final String INDENT = "   ";

	/**
	 * Any time we load regions from a configuration file, we will keep track of that path. Later
	 * on, we can use this information to find other files, such as the .patternInfo.xml files or
	 * even the patterned pdf file.
	 */
	private Set<File> configurationPaths = new HashSet<File>();

	/**
	 * Internal datastructure for indexing a region by its name.
	 */
	private Map<String, Region> regionNameToRegionObject = new HashMap<String, Region>();

	/**
	 * A list of all the regions contained on this sheet. This is the master list. We may keep other
	 * lists for convenience or efficiency.
	 */
	private List<Region> regions = new ArrayList<Region>();

	private Map<Region, Coordinates> regionsAndRelativeLocations = new HashMap<Region, Coordinates>();

	/**
	 * Represents the rectangular size of this Sheet.
	 */
	private Size size = new Size();

	/**
	 * Defaults to US Letter.
	 */
	public Sheet() {
		this(new Inches(8.5), new Inches(11.0));
	}

	/**
	 * A convenience method for our American friends. =)
	 * 
	 * @param widthInches
	 * @param heightInches
	 */
	public Sheet(double widthInches, double heightInches) {
		this(new Inches(widthInches), new Inches(heightInches));
	}

	/**
	 * Choose your own units.
	 * 
	 * @param width
	 * @param height
	 */
	public Sheet(Units w, Units h) {
		setSize(w, h);
	}

	/**
	 * @param r
	 *            a region to be added to this sheet.
	 */
	public void addRegion(Region r) {
		regions.add(r);
		regionNameToRegionObject.put(r.getName(), r);
	}

	/**
	 * @param r
	 * @param xOffset
	 *            from the top left corner of the sheet
	 * @param yOffset
	 *            from the top left corner of the sheet
	 */
	public void addRegion(Region r, Units xOffset, Units yOffset) {
		addRegion(r);
		regionsAndRelativeLocations.put(r, new Coordinates(xOffset, yOffset));
	}

	/**
	 * This file must be an XStream-serialized RegionConfiguration object. This can be produced by
	 * hand, programmatically, or by the R3 Acrobat plugin.
	 * 
	 * @param regionConfigurationFile
	 *            read in this file and add all the regions to this Sheet.
	 */
	public void addRegions(File regionConfigurationFile) {
		final RegionConfiguration rc = (RegionConfiguration) PaperToolkit
				.fromXML(regionConfigurationFile);

		// make sure the size of the sheet matches this object...
		// otherwise, raise a warning flag
		if (!rc.getHeight().equals(getHeight()) || !rc.getWidth().equals(getWidth())) {
			System.err.println("Sheet::addRegions(regionConfigurationFile): Warning! "
					+ "This region configuration file was made for a sheet of a different size. "
					+ "RegionConfiguration: [" + rc.getWidth() + "," + rc.getHeight() + "] versus "
					+ "This Sheet: [" + getWidth() + "," + getHeight() + "]");
			System.err.println("We will proceed.... but you have been warned. =)");
		}

		final List<Region> theRegions = rc.getRegions();
		for (Region r : theRegions) {
			addRegion(r);
		}

		registerConfigurationPath(regionConfigurationFile.getParentFile());
	}

	/**
	 * @param r
	 * @return whether this sheet contains a given region r.
	 */
	public boolean containsRegion(Region r) {
		return regions.contains(r);
	}

	/**
	 * @return directories to look in for files like the patterned pdf, patternInfo.xml, or
	 *         regions.xml files.
	 */
	public Set<File> getConfigurationPaths() {
		return configurationPaths;
	}

	/**
	 * @return the height of this sheet.
	 */
	public Units getHeight() {
		return size.getHeight();
	}

	/**
	 * @param regionName
	 *            This will only work if the names are unique.
	 * @return the region with this name...
	 */
	public Region getRegion(String regionName) {
		return regionNameToRegionObject.get(regionName);
	}

	/**
	 * @return
	 */
	public List<String> getRegionNames() {
		final List<String> names = new ArrayList<String>();
		for (Region r : regions) {
			names.add(r.getName());
		}
		return names;
	}

	/**
	 * @param r
	 *            if it doesn't exist, that implies a 0,0 offset.
	 * @return
	 */
	public Coordinates getRegionOffset(Region r) {
		final Coordinates coordinates = regionsAndRelativeLocations.get(r);
		if (coordinates == null) {
			return new Coordinates(new Inches(0), new Inches(0));
		}
		return coordinates;
	}

	/**
	 * @return the internal list of regions
	 */
	public List<Region> getRegions() {
		return regions;
	}

	/**
	 * @return a new Renderer for this object.
	 */
	public SheetRenderer getRenderer() {
		return new SheetRenderer(this);
	}

	/**
	 * @return a copy of the Size object (so you can't modify the size of this sheet)
	 */
	public Size getSize() {
		return size.clone();
	}

	/**
	 * @return
	 */
	public Units getWidth() {
		return size.getWidth();
	}

	/**
	 * @param configPath
	 *            a directory that we should be aware of, for automatically loading things such as
	 *            the patternInfo.xml file.
	 */
	public void registerConfigurationPath(File configPath) {
		// register the fact that we loaded a configuration file from this directory
		configurationPaths.add(configPath);
		// DebugUtils.println("Configuration Paths: " + configurationPaths);
	}

	/**
	 * @param width
	 * @param height
	 */
	protected void setSize(Units width, Units height) {
		size.setSize(width, height);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();

		// the indent
		String i = INDENT;

		sb.append("Sheet {\n");
		sb.append(i + "Size: " + getWidth() + " x " + getHeight() + "\n");

		sb.append(i + "Regions: " + regions.size() + " {\n");

		// indent twice
		i = INDENT + INDENT;

		for (Region r : regions) {
			sb.append(i + r.toString() + "\n");
		}

		i = INDENT;

		sb.append(i + "}\n");
		sb.append("}\n");
		return sb.toString();
	}

	/**
	 * @return
	 */
	public String toXML() {
		return PaperToolkit.toXML(this);
	}
}
