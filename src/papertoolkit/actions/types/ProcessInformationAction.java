package papertoolkit.actions.types;

import papertoolkit.actions.R3Action;
import papertoolkit.util.DebugUtils;

/**
 * <p>
 * Allows you to pass some information. Nothing happens if you invoke() it. The sole purpose of this
 * class is to pass an object to the other device.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class ProcessInformationAction implements R3Action {

	/**
	 * The message to pass. It can be of any object.
	 */
	private Object information;

	private String name;

	/**
	 * @param msgValue
	 */
	public ProcessInformationAction(String messageName, Object msgValue) {
		name = messageName;
		information = msgValue;
	}

	public Object getInformation() {
		return information;
	}

	public String getName() {
		return name;

	}

	/**
	 * @see papertoolkit.actions.R3Action#invoke()
	 */
	public void invoke() {
		DebugUtils.println("Got a message: " + name);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Message: " + name;
	}
}
