package edu.stanford.hci.r3.events;

import edu.stanford.hci.r3.pen.streaming.PenSample;
import edu.stanford.hci.r3.units.coordinates.PercentageCoordinates;
import edu.stanford.hci.r3.units.coordinates.StreamedPatternCoordinates;

/**
 * <p>
 * Contains all the information we need to handle pen events.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PenEvent {

	/**
	 * What type of event is this? DOWN, UP, OTHER?
	 */
	protected static final int PEN_DOWN_MODIFIER = 1;

	protected static final int PEN_UP_MODIFIER = 2;

	/**
	 * Whether this event should not be processed anymore by handlers deeper in the queue.
	 */
	private boolean consumed = true;

	/**
	 * Was it a pen up or down, or just a regular sample? Regular Sample --> 0 (the default)
	 */
	private int eventModifier = 0;

	/**
	 * Where did the event occur?
	 */
	private PercentageCoordinates locationOnRegion;

	/**
	 * Which pen generated this event?
	 */
	private int penID;

	/**
	 * The original sample, if you need to make calculations.
	 */
	private PenSample penSample;

	/**
	 * When was this event generated, in system time (milliseconds).
	 */
	private long timestamp;

	/**
	 * @param penID
	 * @param time
	 */
	public PenEvent(int thePenID, long time) {
		penID = thePenID;
		timestamp = time;
	}

	/**
	 * Consume this event.
	 */
	public void consume() {
		consumed = true;
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
	 * It will give you a percentage location, from which you can derive actual coordinates (by
	 * converting to inches, etc).
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
	 * @return
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @return
	 */
	public boolean isConsumed() {
		return consumed;
	}

	/**
	 * @return
	 */
	public boolean isPenDown() {
		return eventModifier == PEN_DOWN_MODIFIER;
	}

	/**
	 * @return
	 */
	public boolean isPenUp() {
		return eventModifier == PEN_UP_MODIFIER;
	}

	/**
	 * @param modifier
	 */
	public void setModifier(int modifier) {
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
	 * @param location
	 */
	public void setPercentageLocation(PercentageCoordinates location) {
		locationOnRegion = location;
	}
}