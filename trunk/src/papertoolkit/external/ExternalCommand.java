package papertoolkit.external;

/**
 * <p>
 * A command that the external client will send us.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public abstract class ExternalCommand {

	private String name;

	public ExternalCommand() {
		this("ExternalCommand");
	}

	public ExternalCommand(String n) {
		name = n;
	}

	/**
	 * Override this with a unique name, if you want it to be identifiable...
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	public abstract void invoke(String... args);
}
