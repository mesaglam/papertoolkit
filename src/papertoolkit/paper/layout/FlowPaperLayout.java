package papertoolkit.paper.layout;

import java.util.ArrayList;
import java.util.List;

import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.units.Inches;
import papertoolkit.units.Units;
import papertoolkit.units.coordinates.Coordinates;
import papertoolkit.util.DebugUtils;


/**
 * <p>
 * Somewhat similar to Swing's FlowLayout, but it is not a Layout Manager in the sense that it needs to
 * calculate layouts on the fly. It's just a simple utility to lay out your regions more easily.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class FlowPaperLayout {

	/**
	 * @param sheet
	 * @param currRegionGroup
	 * @param xOffsetOfGroup
	 * @param yOffsetOfGroup
	 */
	private static void addRegionGroupToSheet(Sheet sheet, RegionGroup currRegionGroup,
			Inches xOffsetOfGroup, Inches yOffsetOfGroup) {
		List<Region> regions = currRegionGroup.getRegions();
		double offsetXInInches = currRegionGroup.getXOffsetInInches() + xOffsetOfGroup.getValue();
		double offsetYInInches = currRegionGroup.getYOffsetInInches() + yOffsetOfGroup.getValue();
		for (Region r : regions) {
			Coordinates regionOffset = currRegionGroup.getRegionOffset(r);
			sheet.addRegion(r, new Inches(offsetXInInches + regionOffset.getX().getValueInInches()),
					new Inches(offsetYInInches + regionOffset.getY().getValueInInches()));
		}
	}

	/**
	 * @param targetSheet
	 * @param regions
	 */
	public static void layout(Sheet targetSheet, List<Region> regions) {
		Units width = targetSheet.getWidth();
		Units height = targetSheet.getHeight();
		layout(targetSheet, regions, new Coordinates(new Inches(0), new Inches(0)), width, height,
				new Inches(0), new Inches(0));
	}

	/**
	 * Pass in a sheet and a list of regions... this will lay it out for you automatically.
	 * 
	 * TODO: Allow CENTER, LEFT, or RIGHT alignments. (Center for now)
	 * 
	 * @param sheet
	 * @param regions
	 * @param sheetOffset
	 *            distance from the upper left corner of the sheet
	 * @param width
	 *            of the content area
	 * @param height
	 *            of the content area
	 * @param hPadding
	 * @param vPadding
	 */
	public static void layout(Sheet sheet, List<Region> regions, //
			Coordinates sheetOffset, // upper left corner
			Units width, Units height, // 
			Units hPadding, Units vPadding) {
		// keep it around and add it to every region as part of the offset
		final double xOffset = sheetOffset.getX().getValueInInches();
		final double yOffset = sheetOffset.getY().getValueInInches();

		final double widthOfLayoutAreaInInches = width.getValueInInches();
		final double heightOfLayoutAreaInInches = height.getValueInInches();

		double relInchesX = 0;
		double relInchesY = 0;

		final double hPaddingInInches = hPadding.getValueInInches();

		double maxHeightOfRow = 0;

		// keep the regions around, so that we can align the current row
		final List<Region> currentRow = new ArrayList<Region>();

		int count = 0;
		for (final Region currRegion : regions) {

			final double currRegionWidthInInches = currRegion.getWidth().getValueInInches();
			final double currRegionHeightInInches = currRegion.getHeight().getValueInInches();

			// we will exceed the given width, so we have to wrap to the next row...
			if (relInchesX + currRegionWidthInInches + hPaddingInInches > widthOfLayoutAreaInInches) {

				// center this row
				centerRow(sheet, currentRow, widthOfLayoutAreaInInches, relInchesX);

				// clear the cache of items for this row
				currentRow.clear();

				relInchesX = 0;
				// move to the next row by shifting Y by the maximum height of this row...
				relInchesY += maxHeightOfRow + vPadding.getValueInInches();

				// reset this value
				maxHeightOfRow = 0;

				// we exceed the given height, so there are no more to lay out!
				if (relInchesY > heightOfLayoutAreaInInches) {
					DebugUtils.println("WARNING: We are stopping the layout after " + count
							+ " items because we have exceeded the height of the layout area.");
					break;
				}
			}

			maxHeightOfRow = Math.max(maxHeightOfRow, currRegionHeightInInches);

			sheet.addRegion(currRegion, // add it to the sheet
					new Inches(relInchesX + xOffset), // absolute X
					new Inches(relInchesY + yOffset)); // absolute Y

			currentRow.add(currRegion); // add it to the current row

			relInchesX += currRegionWidthInInches + hPaddingInInches;
			count++;
		}
		
		centerRow(sheet, currentRow, widthOfLayoutAreaInInches, relInchesX);

		// align the last row that we laid out

		DebugUtils.println("Laid out " + count + " items.");
	}

	/**
	 * @param sheet
	 * @param currentRow
	 * @param extraSlackBetweenItems
	 */
	private static void centerRow(Sheet sheet, final List<Region> currentRow, double widthOfLayoutAreaInInches, double relInchesX) {
		final double slackSpace = widthOfLayoutAreaInInches - relInchesX;
		final int numItemsInThisRow = currentRow.size();
		final double extraSlackBetweenItems = slackSpace / (numItemsInThisRow - 1);
		double totalSlack = 0;

		for (Region regionToAdjust : currentRow) {
			Coordinates regionOffset = sheet.getRegionOffset(regionToAdjust);
			sheet.setRegionOffset(regionToAdjust, new Inches(regionOffset.getX().getValueInInches()
					+ totalSlack), regionOffset.getY());
			totalSlack += extraSlackBetweenItems;
		}
	}

	/**
	 * @param sheet
	 * @param regionGroups
	 * @param sheetOffset
	 * @param width
	 * @param height
	 * @param hPadding
	 * @param vPadding
	 */
	public static void layoutRegionGroups(Sheet sheet, List<RegionGroup> regionGroups,
			Coordinates sheetOffset, Units width, Units height, Units hPadding, Units vPadding) {
		// keep it around and add it to every region as part of the offset
		final double xOffset = sheetOffset.getX().getValueInInches();
		final double yOffset = sheetOffset.getY().getValueInInches();

		final double widthOfLayoutAreaInInches = width.getValueInInches();
		final double heightOfLayoutAreaInInches = height.getValueInInches();

		double relInchesX = 0;
		double relInchesY = 0;

		final double hPaddingInInches = hPadding.getValueInInches();

		double maxHeightOfRow = 0;

		// for aligning the current row
		final List<RegionGroup> currentRow = new ArrayList<RegionGroup>();

		int count = 0;
		for (final RegionGroup currRegionGroup : regionGroups) {

			final double currRegionWidthInInches = currRegionGroup.getWidth().getValueInInches();
			final double currRegionHeightInInches = currRegionGroup.getHeight().getValueInInches();

			// we will exceed the given width, so we have to wrap to the next row...
			if (relInchesX + currRegionWidthInInches + hPaddingInInches > widthOfLayoutAreaInInches) {

				// center this row
				final double slackSpace = widthOfLayoutAreaInInches - relInchesX;
				final int numItemsInThisRow = currentRow.size();
				final double extraSlackBetweenItems = slackSpace / (numItemsInThisRow - 1);
				double totalSlack = 0;
				for (RegionGroup regionToAdjust : currentRow) {
					for (Region r : regionToAdjust.getRegions()) {
						Coordinates regionOffset = sheet.getRegionOffset(r);
						sheet.setRegionOffset(r, new Inches(regionOffset.getX().getValueInInches()
								+ totalSlack), regionOffset.getY());
					}
					totalSlack += extraSlackBetweenItems;
				}

				// clear the cache of items for this row
				currentRow.clear();

				relInchesX = 0;
				// move to the next row by shifting Y by the maximum height of this row...
				relInchesY += maxHeightOfRow + vPadding.getValueInInches();

				// reset this value
				maxHeightOfRow = 0;

				// we exceed the given height, so there are no more to lay out!
				if (relInchesY > heightOfLayoutAreaInInches) {
					DebugUtils.println("WARNING: We are stopping the layout after " + count
							+ " items because we have exceeded the height of the layout area.");
					break;
				}
			}
			maxHeightOfRow = Math.max(maxHeightOfRow, currRegionHeightInInches);

			addRegionGroupToSheet(sheet, currRegionGroup, new Inches(relInchesX + xOffset), new Inches(
					relInchesY + yOffset));

			currentRow.add(currRegionGroup); // add it to the current row
			relInchesX += currRegionWidthInInches + hPaddingInInches;
			count++;
		}
		DebugUtils.println("Laid out " + count + " items.");
	}
}
