package edu.stanford.hci.r3.pattern;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * <p>
 * This class stores multiple mappings from regions of pattern (and their coordinates in Anoto
 * space) to Sheets and locations on those sheets. We do not need to specify a particular sheet, as
 * one might use one mapping for multiple sheets.
 * </p>
 * 
 * <p>
 * This mapping works both ways. Given a location on the sheet, we should be able to find the
 * pattern coordinate. Given a coordinate, we should be able to find the location on the sheet.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PatternCoordinateToSheetLocationMapping {

}
