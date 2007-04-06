package edu.stanford.hci.r3.events;

import edu.stanford.hci.r3.pen.PenSample;
import edu.stanford.hci.r3.units.coordinates.PercentageCoordinates;
import edu.stanford.hci.r3.units.coordinates.StreamedPatternCoordinates;

/**
 * <p>
 * Contains all the information we need to handle pen events. We can serialize
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PenEvent {

	public enum PenEventModifier {
		// a pen down event
		DOWN,

		// a regular sample event
		SAMPLE,

		// a pen up event
		UP
	}

	/**
	 * Whether this event should not be processed anymore by handlers deeper in the queue. FALSE by
	 * default. An Event Handler should set it to be consumed if it is NOT OK for other handlers to
	 * deal with this event in "parallel."
	 * 
	 * transient because the consumed flag is useless when we serialize pen events for later replay.
	 */
	private transient boolean consumed = false;

	/**
	 * Was it a pen up or down, or just a regular sample? Regular Sample --> 0 (the default)
	 */
	private PenEventModifier eventModifier = PenEventModifier.SAMPLE;

	/**
	 * Where did the event occur?
	 */
	private PercentageCoordinates locationOnRegion;

	/**
	 * Which pen generated this event?
	 */
	private int penID;

	/**
	 * Give a unique name if you would like to identify your pens and the source of your PenEvents.
	 */
	private String penName = "Digital Pen";

	/**
	 * The original sample, if you need to make calculations on the raw streaming coordinates.
	 */
	private PenSample penSample;

	/**
	 * When was this event generated, in system time (milliseconds).
	 */
	private long timestamp;

	/**
	 * @param thePenID
	 * @param thePenName
	 * @param time
	 * @param sample
	 */
	public PenEvent(int thePenID, String thePenName, long time, PenSample sample) {
		penID = thePenID;
		timestamp = time;
		penSample = sample;
		penName = thePenName;
	}

	/**
	 * Consume this event.
	 */
	public void consume() {
		consumed = true;
	}

	/**
	 * @return a flag to let us know what type of event this is... DOWN, SAMPLE, or UP
	 */
	public PenEventModifier getModifier() {
		return eventModifier;
	}

	/**
	 * WARNING: This is a dangerous method to use, if you do not know what you are doing. The
	 * original pen samples have not been converted into the region's local coordinate system, so if
	 * the region happens to have tiled pattern, you may get unexpected results!
	 * 
	 * Use getPercentageLocation(...) instead!
	 * 
	 * @return the raw pen sample
	 */
	public PenSample getOriginalSample() {
		return penSample;
	}

	/**
	 * @return Which pen generated this event?
	 */
	public int getPenID() {
		return penID;
	}

	/**
	 * @return
	 */
	public String getPenName() {
		return penName;
	}

	/**
	 * It will give you a percentage location, from which you can derive actual coordinates (by
	 * converting to inches, etc). This coordinate will be duplicated when a PEN_UP happens, because
	 * the coordinate will be set to the last known good coordinate (captured during a regular, non
	 * PEN_UP sample).
	 * 
	 * @return the location of the event on the parent region.
	 */
	public PercentageCoordinates getPercentageLocation() {
		return locationOnRegion;
	}

	/**
	 * WARNING: See getOriginalSample(). Do not use this value unless you _know_ what you are doing.
	 * It is OK to use this value if you are doing simple calculations and you are SURE that the
	 * samples all come from thes same pattern tile.
	 * 
	 * @return
	 */
	public StreamedPatternCoordinates getStreamedPatternCoordinate() {
		return new StreamedPatternCoordinates(penSample);
	}

	/**
	 * @return the timestamp of this event...
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @return if this event should not be processed anymore
	 */
	public boolean isConsumed() {
		return consumed;
	}

	/**
	 * @return if this event object represents the pen touching down on the page
	 */
	public boolean isPenDown() {
		return eventModifier.equals(PenEventModifier.DOWN);
	}

	/**
	 * @return if this event object represents the pen lifting up from the page
	 */
	public boolean isPenUp() {
		return eventModifier.equals(PenEventModifier.UP);
	}

	/**
	 * TODO: Change this to an enum?
	 * 
	 * @param modifier
	 */
	public void setModifier(PenEventModifier modifier) {
		eventModifier = modifier;
	}

	/**
	 * Very low level data in case you want to process it.
	 * 
	 * @param sample
	 */
	public void setOriginalSample(PenSample sample) {
		penSample = sample;
	}

	/**
	 * The event engine will assign the pen name, which is the source of this event.
	 * 
	 * @param name
	 */
	public void setPenName(String name) {
		penName = name;
	}

	/**
	 * @param location
	 */
	public void setPercentageLocation(PercentageCoordinates location) {
		locationOnRegion = location;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "PenEvent {Pen " + penID + " (" + penName + ") " + penSample + "}";
	}
}
