package edu.stanford.hci.r3.pattern.coordinates;

import edu.stanford.hci.r3.units.PatternDots;
import edu.stanford.hci.r3.units.coordinates.BatchedPatternCoordinates;
import edu.stanford.hci.r3.units.coordinates.StreamedPatternCoordinates;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * Given a coordinate in physical (streamed) anoto space, it returns a coordinate in logical (batched) space.
 * Given a coordinate in logical space, it returns a coordinate in physical space.
 * 
 * It does this by finding the pattern package that contains the coordinate. Or, you can pick one
 * explicitly...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class CoordinateTranslator {

	/**
	 * TODO Move this out to the 
	 * @param args
	 */
	public static void main(String[] args) {
		CoordinateTranslator translator = new CoordinateTranslator(new PageAddress("48.0.12.8"),
				new PatternDots(4097107), new PatternDots(8389499), 15, -2, 1330, 0);
		// StreamedPatternCoordinates streamed = translator.translate(new BatchedPatternCoordinates(
		// "48.0.12.10", 60, 156.25));
		StreamedPatternCoordinates streamed = translator.translate(new BatchedPatternCoordinates("48.0.12.9",
				57.875, 251.5));

		DebugUtils.println(streamed);

		BatchedPatternCoordinates batched = translator.translate(streamed);
		DebugUtils.println(batched);
	}

	private PageAddress evenPage;

	private PatternDots evenPageOriginX;

	private PatternDots evenPageOriginY;

	private double oddPageOffsetX;

	private double oddPageOffsetY;

	private double perPageOffsetX;

	private double perPageOffsetY;

	/**
	 * 
	 */
	public CoordinateTranslator(PageAddress lowestEvenPage, PatternDots lowestEvenPageOriginX,
			PatternDots lowestEvenPageOriginY, double oddPgOffsetX, double oddPgOffsetY, double perPgOffsetX,
			double perPgOffsetY) {
		evenPage = lowestEvenPage;
		evenPageOriginX = lowestEvenPageOriginX;
		evenPageOriginY = lowestEvenPageOriginY;
		oddPageOffsetX = oddPgOffsetX;
		oddPageOffsetY = oddPgOffsetY;
		perPageOffsetX = perPgOffsetX;
		perPageOffsetY = perPgOffsetY;
	}

	/**
	 * Assume that the first three numbers in the page address (segment, shelf, book) are the same
	 * 
	 * @param batchedCoord
	 * @return
	 */
	public StreamedPatternCoordinates translate(BatchedPatternCoordinates batchedCoord) {

		// find the difference in page numbers between the batched and our evenPage
		final PageAddress batchedAddress = batchedCoord.getPageAddress();
		final int pageDiff = batchedAddress.getPage() - evenPage.getPage();
		final double offsetX = pageDiff * perPageOffsetX;
		final double offsetY = pageDiff * perPageOffsetY;

		if (batchedAddress.isOddPage()) {
			return new StreamedPatternCoordinates(new PatternDots(evenPageOriginX.getValue() + offsetX
					+ batchedCoord.getXVal() + oddPageOffsetX), new PatternDots(evenPageOriginY.getValue()
					+ offsetY + batchedCoord.getYVal() + oddPageOffsetY));
		} else {
			return new StreamedPatternCoordinates(new PatternDots(evenPageOriginX.getValue() + offsetX
					+ batchedCoord.getXVal()), new PatternDots(evenPageOriginY.getValue() + offsetY
					+ batchedCoord.getYVal()));
		}
	}

	/**
	 * @param streamedCoord
	 * @return
	 */
	public BatchedPatternCoordinates translate(StreamedPatternCoordinates streamedCoord) {

		final double xVal = streamedCoord.getXVal();
		final double yVal = streamedCoord.getYVal();

		final double offsetX = xVal - evenPageOriginX.getValue();
		final double offsetY = yVal - evenPageOriginY.getValue();

		final boolean nonZeroOffsetX = perPageOffsetX != 0;
		final boolean nonZeroOffsetY = perPageOffsetY != 0;

		double pageDiff = 0;
		if (nonZeroOffsetX) {
			pageDiff = offsetX / perPageOffsetX;
		} else if (nonZeroOffsetY) {
			pageDiff = offsetY / perPageOffsetY;
		}

		final int finalPage = (int) (evenPage.getPage() + pageDiff);
		final PageAddress address = new PageAddress(evenPage.getSegment(), evenPage.getShelf(), evenPage
				.getBook(), finalPage);

		final double xCoordOnPage = nonZeroOffsetX ? offsetX % perPageOffsetX : offsetX;
		final double yCoordOnPage = nonZeroOffsetY ? offsetY % perPageOffsetY : offsetY;

		if (address.isOddPage()) {
			return new BatchedPatternCoordinates(address, new PatternDots(xCoordOnPage - oddPageOffsetX),
					new PatternDots(yCoordOnPage - oddPageOffsetY));
		} else {
			return new BatchedPatternCoordinates(address, new PatternDots(xCoordOnPage), new PatternDots(
					yCoordOnPage));
		}
	}

}
