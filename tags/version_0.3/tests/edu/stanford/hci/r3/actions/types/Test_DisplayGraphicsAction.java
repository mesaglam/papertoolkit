package edu.stanford.hci.r3.actions.types;

import java.io.File;

import edu.stanford.hci.r3.PaperToolkit;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Test_DisplayGraphicsAction {

	private static final File XML_FILE = new File("data/testFiles/output/DisplayAction.xml");

	public static void main(String[] args) {
		serializeDisplayAction();
		unserializeDisplayAction();
	}

	/**
	 * Serializing Generic JPanels won't work... Maybe we can serialize a named class? Nope. That
	 * doesn't work either... Instead, try serializing an object that embodies the g2d calls. It
	 * takes a g2d and renders to it! I think we should take the RobotAction approach and serialize
	 * commands instead.
	 */
	private static void serializeDisplayAction() {
		DisplayGraphicsAction displayAction = new DisplayGraphicsAction();
		displayAction.fillRect(10, 20, 50, 30);
		displayAction.setExitOnClose(true);
		displayAction.drawImage(new File("data/testFiles/dragon.jpg"), 10, 20);
		PaperToolkit.toXML(displayAction, XML_FILE);
	}

	/**
	 * 
	 */
	private static void unserializeDisplayAction() {
		DisplayGraphicsAction dga = (DisplayGraphicsAction) PaperToolkit.fromXML(XML_FILE);
		dga.invoke();
	}

}
