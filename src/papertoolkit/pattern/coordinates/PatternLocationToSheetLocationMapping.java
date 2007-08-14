package papertoolkit.pattern.coordinates;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import papertoolkit.PaperToolkit;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.pattern.coordinates.conversion.PatternCoordinateConverter;
import papertoolkit.pattern.coordinates.conversion.TiledPatternCoordinateConverter;
import papertoolkit.pen.PenSample;
import papertoolkit.units.PatternDots;
import papertoolkit.units.coordinates.PercentageCoordinates;
import papertoolkit.units.coordinates.StreamedPatternCoordinates;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.MathUtils;
import papertoolkit.util.files.FileUtils;

/**
 * <p>
 * This class stores mappings from regions of pattern (and their coordinates in Anoto space) to Sheets and
 * locations on those sheets. This mapping works both ways. Given a location on the sheet, we should be able
 * to find the pattern coordinate. Given a coordinate, we should be able to find the location on the sheet.
 * </p>
 * <p>
 * An instance of this object should be built when a PDF is rendered with pattern. At that moment, regions on
 * a sheet are bound to specific physical coordinates. Each application should store this mapping per sheet.
 * </p>
 * <p>
 * The SheetRenderer class uses this class to save the mapping to disk, so that a future instance can load the
 * mapping and run the application.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PatternLocationToSheetLocationMapping {

	// files end with .patternInfo.xml
	private static final String[] PATTERN_INFO_EXTENSION_FILTER = new String[] { "patternInfo.xml" };

	/**
	 * Binds regions to pattern bounds, specified in logical (batched) and physical (streamed) coordinates.
	 */
	private Map<Region, PatternCoordinateConverter> regionToPatternBounds = new HashMap<Region, PatternCoordinateConverter>();

	/**
	 * The mapping is bound to one sheet.
	 */
	private Sheet sheet;

	/**
	 * One mapping object per sheet. Create this object after you have added all the regions that you need to
	 * the sheet. This class will maintain a mapping of Regions to physical (streaming) and logical (batched)
	 * pen coordinates.
	 * 
	 * This constructor will try to load the pattern configuration automatically. If you want to set the
	 * mapping manually, you will need to load the map with TiledPatternCoordinateConverters so that active
	 * regions can be accessed.
	 * 
	 * @param s
	 */
	public PatternLocationToSheetLocationMapping(Sheet s) {
		sheet = s;
		final List<Region> regions = s.getRegions();
		warnIfThereAreNoRegions(regions);
		initializeMap(regions);
		loadConfigurationFromAutomaticallyDiscoveredXMLFiles();
	}

	/**
	 * These XML files are created automatically when you render a patterned PDF.
	 * 
	 * @param s
	 * @param patternInfoFile
	 */
	public PatternLocationToSheetLocationMapping(Sheet s, File patternInfoFile) {
		sheet = s;
		final List<Region> regions = s.getRegions();
		warnIfThereAreNoRegions(regions);
		initializeMap(regions);
		loadConfigurationFromXML(patternInfoFile);
	}

	/**
	 * Checks whether this mapping contains the pen sample (streamed coordinates). If it does, it returns the
	 * TiledPatternCoordinateConverter object for that sample. If not, it returns null.
	 * 
	 * @param sample
	 * @return
	 */
	public List<PatternCoordinateConverter> getCoordinateConvertersForSample(PenSample sample) {
		List<PatternCoordinateConverter> coordinateConverters = new ArrayList<PatternCoordinateConverter>();

		for (Region r : regionToPatternBounds.keySet()) {
			PatternCoordinateConverter converter = regionToPatternBounds.get(r);
			final StreamedPatternCoordinates coord = new StreamedPatternCoordinates(sample);
			if (converter.contains(coord)) {
				// DebugUtils.println("Sample is on: " + r.getName());

				// where are we on this region?
				final PercentageCoordinates relativeLocation = converter.getRelativeLocation(coord);

				// currently, this is a FALLBACK HACK to check whether we are actually outside the
				// region later on, we must fix this and catch it earlier in the process
				if (relativeLocation.getPercentageInXDirection() > 100
						|| relativeLocation.getPercentageInYDirection() > 100) {
					DebugUtils.println("FALLBACK HACK. It's actually outside the bounds. "
							+ "Going on to check the next region...");
					continue;
				} else {
					coordinateConverters.add(converter);
				}
			}
		}
		return coordinateConverters;
	}

	/**
	 * @param r
	 *            find the coordinate converter for this region.
	 * @return the converter that enables you to figure out where on the region a sample falls.
	 */
	public PatternCoordinateConverter getPatternBoundsOfRegion(Region r) {
		// DebugUtils.println("Pattern Bounds: " + regionToPatternBounds.size() + " " + r.getName() + " "
		// + regionToPatternBounds);
		return regionToPatternBounds.get(r);
	}

	/**
	 * An advanced method, to allow us to inspect the region <--> pattern mapping. We can tell if the pattern
	 * map has an area of 0, which probably means it is uninitialized!
	 * 
	 * @return
	 */
	public Map<Region, PatternCoordinateConverter> getRegionToPatternMapping() {
		return regionToPatternBounds;
	}

	/**
	 * @return
	 */
	public Sheet getSheet() {
		return sheet;
	}

	/**
	 * For all ACTIVE regions, we need a coordinate converter that will enable us to find out where the pen is
	 * on the region. We should be able to add new regions to a sheet after the fact, so we should have the
	 * ability to reinitialize this map later on.
	 * 
	 * @param regions
	 */
	public void initializeMap(List<Region> regions) {
		regionToPatternBounds.clear();
		for (final Region r : regions) {
			if (r.isActive()) {
				// put in an empty one for now. It should be updated later... otherwise,
				// this active region will not be accessible by the end user
				// note: I introduced a bug here a while back, by changing the name I gave to the
				// TiledPatternCoordinateConverter (_UninitializedMapping). Watch out if you want to
				// customize this name!
				regionToPatternBounds.put(r, new TiledPatternCoordinateConverter(r.getName()));
			}
		}
	}

	/**
	 * @param configurationPaths
	 */
	private void loadConfigurationFromAutomaticallyDiscoveredXMLFiles() {
		// check to see if the pattern configuration file exists.
		// if it does, load it automatically...
		final Set<File> configurationPaths = sheet.getConfigurationPaths();
		if (configurationPaths.size() == 0) {
			DebugUtils.println("This Sheet does not have any pattern configuration paths. "
					+ "Either add one so that we can automatically look for *.patternInfo.xml "
					+ "files, or add patternInfo files explicitly.");
			return;
		}

		DebugUtils.println("This Sheet has configuration paths for *.patternInfo.xml files.");

		for (File path : configurationPaths) {
			// System.out.println(path);
			List<File> patternInfoFiles = FileUtils.listVisibleFiles(path, PATTERN_INFO_EXTENSION_FILTER);
			// System.out.println(patternInfoFiles.size());
			for (File f : patternInfoFiles) {
				// DebugUtils.println("Trying to automatically load " + f.getName());
				loadConfigurationFromXML(f);
			}
		}
	}

	/**
	 * On Nov 9, 2006, I changed this to use the interface... I'll need to run some regression tests to make
	 * sure I didn't break anything.
	 * 
	 * @param xmlFile
	 */
	@SuppressWarnings("unchecked")
	public void loadConfigurationFromXML(File xmlFile) {
		// for each region, there should be a specification of the pattern information
		final HashMap<RegionID, PatternCoordinateConverter> regionIDToPattern = (HashMap<RegionID, PatternCoordinateConverter>) PaperToolkit
				.fromXML(xmlFile);
		for (Region r : regionToPatternBounds.keySet()) {
			final RegionID xmlKey = new RegionID(r);
			// System.out.println("Found Key: " + regionIDToPattern.containsKey(xmlKey) + " for " +
			// r.getName());

			// loads the information into our map
			if (regionIDToPattern.containsKey(xmlKey)) {
				// DebugUtils.println("Loaded Pattern Configuration for " + xmlKey);
				regionToPatternBounds.put(r, regionIDToPattern.get(xmlKey));
			}
		}
	}

	/**
	 * For debugging.
	 */
	public void printMapping() {
		System.out.println(this);
	}

	/**
	 * Due to xstream's inability to serial/unserialize really complicated classes, we will save only a
	 * regionName+origin --> pattern info mapping
	 * 
	 * @param xmlFile
	 */
	public void saveConfigurationToXML(File xmlFile) {
		try {
			// create a new map that goes from name+origin to pattern bounds mapping
			// only save active regions... because we don't render pattern for nonactive regions
			final HashMap<RegionID, PatternCoordinateConverter> regionIDToPattern = new HashMap<RegionID, PatternCoordinateConverter>();
			final Set<Region> regions = regionToPatternBounds.keySet();
			for (Region r : regions) {
				if (!r.isActive()) {
					continue;
				}
				final RegionID rid = new RegionID(r);
				regionIDToPattern.put(rid, regionToPatternBounds.get(r));
			}
			if (xmlFile.exists()) {
				DebugUtils.println("Overwriting XML File: " + xmlFile.getName());
				xmlFile.delete();
			}
			PaperToolkit.toXML(regionIDToPattern, new FileOutputStream(xmlFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * For a region on our sheet, update the coordinate information so that it can be accessed at runtime by
	 * our EventEngine. The coordinateInfo object will translate incoming samples to coordinates relative to
	 * the region.
	 * 
	 * @param r
	 * @param coordinateInfo
	 */
	public void setPatternInformationOfRegion(Region r, PatternCoordinateConverter coordinateInfo) {
		if (regionToPatternBounds.containsKey(r) || sheet.containsRegion(r)) {
			// updating an already-known region OR
			// adding a new region (probably added to the sheet after this object was constructed)
			regionToPatternBounds.put(r, coordinateInfo);
		} else {
			System.err.println("PatternLocationToSheetLocationMapping.java: Region unknown. "
					+ "Please add it to the sheet before updating this mapping.");
			// TODO... should we add it automatically, if the user has not?
		}
	}

	/**
	 * A convenience function for mapping a contiguous (single-tile) patterned area to a region object.
	 * 
	 * @param region
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void setPatternInformationOfRegion(Region region, //
			PatternDots x, PatternDots y, PatternDots width, PatternDots height) {
		setPatternInformationOfRegion(region, new TiledPatternCoordinateConverter(region.getName(), x
				.getValue(), y.getValue(), //
				MathUtils.rint(width.getValue()), MathUtils.rint(height.getValue())));
	}

	/**
	 * @param s
	 */
	public void setSheet(Sheet s) {
		sheet = s;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (final Region r : regionToPatternBounds.keySet()) {
			sb.append(r.getName() + " --> ");
			final PatternCoordinateConverter bounds = regionToPatternBounds.get(r);
			sb.append(bounds + ", ");
		}
		String string = sb.toString();
		if (string.length() > 2) {
			return string.substring(0, string.length() - 2); // chop off the last ", "
		} else {
			return string;
		}
	}

	/**
	 * @param regions
	 */
	private void warnIfThereAreNoRegions(final List<Region> regions) {
		// at this point, the sheet has to have regions...
		// for now, warn, if there are no regions
		if (regions.size() == 0) {
			DebugUtils.println("There aren't any regions. Did you perhaps add the "
					+ "regions _after_ you added the sheet to the application?");
		}
	}
}
