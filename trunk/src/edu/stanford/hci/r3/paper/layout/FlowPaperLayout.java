package edu.stanford.hci.r3.paper.layout;

import java.util.List;

import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.units.coordinates.Coordinates;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * Somewhat similar to Swing's FlowLayout, but it is not a Layout Manager in the sense that it needs
 * to calculate layouts on the fly. It's just a simple utility to lay out your regions more easily.
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
	 * @param targetSheet
	 * @param regions
	 */
	public static void layout(Sheet targetSheet, List<Region> regions) {
		Units width = targetSheet.getWidth();
		Units height = targetSheet.getHeight();

		double currXInches = 0;
		for (Region r : regions) {
			targetSheet.addRegion(r, new Inches(currXInches), new Inches(0));
			currXInches += 1;
		}
	}

	/**
	 * @param sheet
	 * @param regions
	 * @param sheetOffset
	 * @param width
	 * @param height
	 * @param hPadding
	 * @param vPadding
	 */
	public static void layout(Sheet sheet, List<Region> regions, Coordinates sheetOffset,
			Inches width, Inches height, Inches hPadding, Inches vPadding) {
		// keep it around and add it to every region as part of the offset
		double xOffset = sheetOffset.getX().getValueInInches();
		double yOffset = sheetOffset.getY().getValueInInches();

		final double widthOfLayoutAreaInInches = width.getValueInInches();
		final double heightOfLayoutAreaInInches = height.getValueInInches();

		double relInchesX = 0;
		double relInchesY = 0;
		for (Region r : regions) {
			if (relInchesX + r.getWidth().getValueInInches() + hPadding.getValue() > widthOfLayoutAreaInInches) {
				relInchesX = 0;
				relInchesY += r.getHeight().getValueInInches() + vPadding.getValue();
				if (relInchesY > heightOfLayoutAreaInInches) {
					// no more to lay out!
					DebugUtils.println("WARNING: We are stopping the layout because "
							+ "we have exceeded the height of the layout area.");
					break;
				}
			}
			sheet.addRegion(r, //
					new Inches(relInchesX + xOffset), // 
					new Inches(relInchesY + yOffset));//
			relInchesX += r.getWidth().getValueInInches() + hPadding.getValue();
		}
	}
}
