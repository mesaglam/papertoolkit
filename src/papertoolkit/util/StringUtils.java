package papertoolkit.util;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Manipulate and analyze Strings.
 * 
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class StringUtils {

	/**
	 * Measures the size of the input string, taking into account the current font and newlines.
	 * 
	 * @return
	 */
	public static Dimension getStringSize(String textToMeasure, Font displayFont) {
		final FontRenderContext fontRenderContext = new FontRenderContext(null, true, true);

		// break it up by lines (in case there are newlines "\n")
		final String[] strings = textToMeasure.split("\n");

		double totalHeight = 0;
		double maxWidth = 0;

		for (String s : strings) {
			Rectangle2D stringBounds = displayFont.getStringBounds(s, fontRenderContext);
			maxWidth = Math.max(maxWidth, stringBounds.getWidth());
			totalHeight += stringBounds.getHeight();
		}

		final Dimension dimension = new Dimension();
		dimension.setSize(maxWidth, totalHeight);
		return dimension;
	}

	/**
	 * @param number,
	 *            e.g., 15
	 * @param numTotalDigitsInString,
	 *            e.g., 4
	 * @return number as a String, with prepended 0s, e.g., 0015
	 */
	public static String padNumberToNDigits(int number, int numTotalDigitsInString) {
		String numString = number + "";
		while (numString.length() < numTotalDigitsInString) {
			numString = "0" + numString; // prepend it with a zero
		}
		return numString;
	}

	/**
	 * Repeat a string numTimes.
	 * 
	 * @param string
	 * @param numTimes
	 * @return stringstringstringstring.... numTimes over.
	 */
	public static String repeat(String string, int numTimes) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < numTimes; i++) {
			sb.append(string);
		}
		return sb.toString();
	}

	/**
	 * Splits a string into multiple lines, each with at most maxChars characters. We will try our best to
	 * split after spaces.
	 * 
	 * @param string
	 * @param maxCharsPerLine
	 * @return a List of Strings representing the lines.
	 */
	public static List<String> splitString(String string, int maxCharsPerLine) {
		final List<String> lines = new ArrayList<String>();
		final String[] items = string.split(" ");
		final StringBuilder currString = new StringBuilder();
		for (String item : items) {
			if (currString.length() + item.length() > maxCharsPerLine) {
				lines.add(currString.toString());
				currString.setLength(0); // clear buffer
			}
			currString.append(item + " ");
		}
		lines.add(currString.toString());
		return lines;
	}

}
