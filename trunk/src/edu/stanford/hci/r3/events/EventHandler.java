package edu.stanford.hci.r3.events;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.paper.Region;

/**
 * <p>
 * This is the super interface of all the other event handlers. These are the pen & paper analogues
 * to Java Swing's EventListener architecture. We changed it to an abstract class in ver. 0.5, as
 * now handlers should have access to their parent regions.... Event Handlers can be added to
 * multiple regions (is this unique?)
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public abstract class EventHandler {

	private List<Region> parentRegions = new ArrayList<Region>();

	public void addParentRegion(Region r) {
		parentRegions.add(r);
	}

	public List<Region> getParentRegions() {
		return parentRegions;
	}

	/**
	 * if this event should be consumed (i.e., lower priority event handlers should not see this
	 * event), we should set the event.consumed property to true
	 */
	public abstract void handleEvent(PenEvent event);

	/**
	 * @return the Event Handler's Name
	 */
	public abstract String toString();
}
