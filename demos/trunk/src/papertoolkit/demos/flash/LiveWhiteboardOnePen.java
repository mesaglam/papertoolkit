package papertoolkit.demos.flash;

import java.awt.Color;
import java.util.List;

import papertoolkit.flash.whiteboard.FlashWhiteboard;
import papertoolkit.pen.Pen;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.graphics.ColorUtils;


/**
 * <p>
 * Shows how to use the Flash components to assemble a Live Whiteboard that will get data from the local pen
 * device in real time.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class LiveWhiteboardOnePen {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Open Four Whiteboards attached to One Pen
		// new LiveWhiteboardOnePen(4);
		
		// Open One Whiteboard attached to One Pen
		new LiveWhiteboardOnePen();
	}

	/**
	 * Just use one pen (the local one)
	 */
	public LiveWhiteboardOnePen() {
		this(1);
	}

	/**
	 * Open possibly more than whiteboard UI with a single pen.
	 * 
	 * @param numWhiteboards
	 */
	public LiveWhiteboardOnePen(int numWhiteboards) {
		if (numWhiteboards < 1) {
			numWhiteboards = 1;
		}

		// in R3, there are two ways to make real-time pen and paper applications... One is to use
		// the Application framework. A second one, which we will use here, is to just attach a pen
		// listener to a live (local) Pen.
		Pen pen = new Pen(); // local pen

		List<Color> uniqueColors = ColorUtils.getUniqueColors(numWhiteboards);
		DebugUtils.println(uniqueColors);

		for (int i = 0; i < numWhiteboards; i++) {
			// load the Flash component that listens for real-time ink!
			// basically, just open the HTML page that contains the flash component! =)
			FlashWhiteboard whiteboard = new FlashWhiteboard(8989 + i);
			whiteboard.addPen(pen);
			whiteboard.setBackgroundColor(Color.BLACK);
			// whiteboard.setInkColor(new Color(55, 55, 128));
			whiteboard.setInkColor(Color.WHITE);
			whiteboard.setSwatchColor(uniqueColors.get(i));
			whiteboard.setTitle("Whiteboard" + i);
			whiteboard.setRotation(-90);
			whiteboard.load();
		}
	}

}
