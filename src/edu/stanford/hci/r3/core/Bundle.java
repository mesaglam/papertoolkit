package edu.stanford.hci.r3.core;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * A Collection of Sheets (see Sheet.java). A collection of pages/sheets of interactive paper. A
 * bundle contains multiple Sheets.
 */
public class Bundle {

	private List<Sheet> sheets = new ArrayList<Sheet>();

	public Bundle() {

	}

	/**
	 * @param s
	 */
	public void addSheet(Sheet s) {
		sheets.add(s);
	}
}
