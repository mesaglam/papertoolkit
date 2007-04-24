package edu.stanford.hci.r3.pattern.coordinates;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * Represents a logical page address, which is available when we import batched pen strokes.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PageAddress {

	/**
	 * Parses strings of the form A.B.C.D
	 */
	private static final Pattern PATTERN_ADDRESS = Pattern.compile("(.*?)\\.(.*?)\\.(.*?)\\.(.*)");

	private int book;

	private int page;

	private int segment;

	private int shelf;

	public PageAddress(int theSegment, int theShelf, int theBook, int thePage) {
		segment = theSegment;
		shelf = theShelf;
		book = theBook;
		page = thePage;
	}

	/**
	 * @param pageAddress
	 */
	public PageAddress(String pageAddress) {
		// extract the segment, shelf, book, and page from the pageAddress
		final Matcher matcherPageAddress = PATTERN_ADDRESS.matcher(pageAddress);
		if (matcherPageAddress.find()) {
			String segmentStr = matcherPageAddress.group(1);
			String shelfStr = matcherPageAddress.group(2);
			String bookStr = matcherPageAddress.group(3);
			String pageStr = matcherPageAddress.group(4);

			// DebugUtils.println(segmentStr + "_" + shelfStr + "_" + bookStr + "_" + pageStr);

			segment = Integer.parseInt(segmentStr);
			shelf = Integer.parseInt(shelfStr);
			book = Integer.parseInt(bookStr);
			page = Integer.parseInt(pageStr);
		} else {
			System.err.println("PageAddress: " + pageAddress
					+ " is not a valid logical page address.");
			// do nothing, and leave all the fields blank
		}
	}

	/**
	 * @return the book
	 */
	public int getBook() {
		return book;
	}

	/**
	 * @return the page
	 */
	public int getPage() {
		return page;
	}

	/**
	 * @return the segment
	 */
	public int getSegment() {
		return segment;
	}

	/**
	 * @return the shelf
	 */
	public int getShelf() {
		return shelf;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return segment + "." + shelf + "." + book + "." + page;
	}

}
