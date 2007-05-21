package edu.stanford.hci.r3.units.coordinates;

import java.text.DecimalFormat;

/**
 * <p>
 * TODO: Make this compatible with the other coordinate types...
 * 
 * Coordinates - a location on Earth represented as latitude (N/S), longitude (E/W), and elevation.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * @author <a href="http://graphics.stanford.edu/~balee/">Brian A. Lee</a> (balee(AT)cs.stanford.edu)
 */
public class GPSCoordinate {

	private static final DecimalFormat FORMATTER = new DecimalFormat("0.000000");

	private double altitude;

	private double latitude;

	private double longitude;

	/**
	 * @param lat
	 * @param lon
	 */
	public GPSCoordinate(double lat, double lon) {
		setLocation(lat, lon, 0);
	}

	/**
	 * @param lat
	 * @param lon
	 * @param ele
	 */
	public GPSCoordinate(double lat, double lon, double ele) {
		setLocation(lat, lon, ele);
	}

	/**
	 * @return
	 */
	public double getAltitude() {
		return altitude;
	}

	/**
	 * @return
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @return
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param lat
	 * @param lon
	 */
	public void setLocation(double lat, double lon) {
		longitude = lon;
		latitude = lat;
	}

	/**
	 * @param lat
	 * @param lon
	 * @param ele
	 */
	public void setLocation(double lat, double lon, double ele) {
		longitude = lon;
		latitude = lat;
		altitude = ele;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + FORMATTER.format(latitude) + ", " + FORMATTER.format(longitude) + ", "
				+ FORMATTER.format(altitude) + ")";
	}
}
