package papertoolkit.events;

import papertoolkit.pen.PenSample;
import papertoolkit.units.Size;
import papertoolkit.units.coordinates.PercentageCoordinates;
import papertoolkit.units.coordinates.StreamedPatternCoordinates;

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

	/**
	 * Whether this event should not be processed anymore by handlers deeper in the queue. FALSE by default.
	 * An Event Handler should set it to be consumed if it is NOT OK for other handlers to deal with this
	 * event in "parallel."
	 * 
	 * transient because the consumed flag is useless when we serialize pen events for later replay.
	 */
	private transient boolean consumed = false;

	/**
	 * Was it a pen up or down, or just a regular sample? Regular Sample --> 0 (the default)
	 */
	private PenEventType eventType = PenEventType.SAMPLE;

	/**
	 * Where did the event occur?
	 */
	private PercentageCoordinates locationOnRegion = new PercentageCoordinates(0, 0, new Size());

	/**
	 * Which pen generated this event?
	 */
	private String penID;

	/**
	 * Give a unique name if you would like to identify your pens and the source of your PenEvents.
	 */
	private String penName = "Digital Pen";

	/**
	 * The original sample, if you need to make calculations on the raw streaming coordinates.
	 */
	private PenSample penSample;

	/**
	 * True if the event is dispatched in real-time. False if it is being read from a pen synchronization.
	 */
	private boolean realtimeFlag = true;

	/**
	 * When was this event generated, in system time (milliseconds).
	 */
	private long creationTimestamp;

	/**
	 * Creates a new PenEvent from the Pen Name and Identifier, etc.
	 * 
	 * @param thePenID
	 * @param thePenName
	 * @param sample
	 * @param type
	 *            UP, DOWN, or SAMPLE (most common)
	 * @param isRealtime
	 *            TRUE if the pen is streaming, FALSE if the pen has just been docked in the cradle
	 */
	public PenEvent(String thePenID, String thePenName, PenSample sample, PenEventType type, boolean isRealtime) {
		penID = thePenID;
		// the time the PenEvent was CREATED/DISPATCHED (usually we want to dispatch ASAP after creation)
		// note that the penSample's timestamp may be different!
		// it should be CLOSE to our creationTimestamp, if this is a RealTime event
		// if it is batched, or replayed, the timestamp will be very different!
		creationTimestamp = System.currentTimeMillis();
		penSample = sample;
		penName = thePenName;
		eventType = type;
		realtimeFlag = isRealtime;
	}

	/**
	 * Consume this event.
	 */
	public void consume() {
		consumed = true;
	}

	/**
	 * WARNING: This is a dangerous method to use, if you do not know what you are doing. The original pen
	 * samples have not been converted into the region's local coordinate system, so if the region happens to
	 * have tiled pattern, you may get unexpected results!
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
	public String getPenID() {
		return penID;
	}

	/**
	 * @return
	 */
	public String getPenName() {
		return penName;
	}

	/**
	 * It will give you a percentage location, from which you can derive actual coordinates (by converting to
	 * inches, etc). This coordinate will be duplicated when a PEN_UP happens, because the coordinate will be
	 * set to the last known good coordinate (captured during a regular, non PEN_UP sample).
	 * 
	 * @return the location of the event on the parent region.
	 */
	public PercentageCoordinates getPercentageLocation() {
		return locationOnRegion;
	}

	/**
	 * WARNING: See getOriginalSample(). Do not use this value unless you _know_ what you are doing. It is OK
	 * to use this value if you are doing simple calculations and you are SURE that the samples all come from
	 * thes same pattern tile.
	 * 
	 * @return
	 */
	public StreamedPatternCoordinates getStreamedPatternCoordinate() {
		return new StreamedPatternCoordinates(penSample);
	}

	/**
	 * @return the creation/dispatch timestamp of this event...
	 */
	public long getTimestamp() {
		return creationTimestamp;
	}

	/**
	 * @return a flag to let us know what type of event this is... DOWN, SAMPLE, or UP
	 */
	public PenEventType getType() {
		return eventType;
	}

	/**
	 * @return if this event should not be processed anymore
	 */
	public boolean isConsumed() {
		return consumed;
	}

	/**
	 * @return
	 */
	public boolean isRealTime() {
		return realtimeFlag;
	}

	/**
	 * @return if this event object represents the pen touching down on the page
	 */
	public boolean isTypePenDown() {
		return eventType.equals(PenEventType.DOWN);
	}

	/**
	 * @return if this event object represents the pen lifting up from the page
	 */
	public boolean isTypePenUp() {
		return eventType.equals(PenEventType.UP);
	}

	/**
	 * @param rtFlag
	 */
	public void setIsRealTime(boolean rtFlag) {
		realtimeFlag = rtFlag;
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

	/**
	 * @param modifier
	 *            describes the type of PenEvent (whether the pen just came down, just went up, or is
	 *            currently tracking
	 */
	public void setType(PenEventType modifier) {
		eventType = modifier;
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
