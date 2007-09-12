package papertoolkit.demos.gesture;

import java.awt.Color;
import java.io.File;

import javax.swing.filechooser.FileSystemView;

import papertoolkit.PaperToolkit;
import papertoolkit.application.Application;
import papertoolkit.events.PenEvent;
import papertoolkit.events.handlers.GestureHandler;
import papertoolkit.events.handlers.MarkingGestureHandler;
import papertoolkit.external.ExternalCommunicationServer;
import papertoolkit.external.ExternalListener;
import papertoolkit.external.flash.FlashPenListener;
import papertoolkit.external.flash.FlashWhiteboard;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.pen.InputDevice;
import papertoolkit.pen.Pen;
import papertoolkit.pen.gesture.dollar.DollarRecognizer.RecognitionResult;
import papertoolkit.pen.ink.InkStroke;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.files.FileUtils;

/**
 * <p>
 * Uses the Application infrastructure to test Gesture Recognition on regions...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class GestureRecognitionApp {

	private ExternalCommunicationServer flash;

	public GestureRecognitionApp() {
		Application app = PaperToolkit.createApplication();
		final Pen pen = new Pen();
		app.addPenInput(pen);

		Sheet sheet = app.createSheet(5, 8);
		Region region = sheet.createRegion(0, 0, 5, 8);
		region.addEventHandler(new GestureHandler() {
			public void gestureArrived(PenEvent lastSample, RecognitionResult result, InkStroke stroke) {
				DebugUtils.println(result);
				flash.sendMessage("<recognized templateName=\""+result.getName()+"\"/>");
			}
		});

		Sheet sheet2 = app.createSheet(5, 8);
		Region region2 = sheet2.createRegion();
		region2.addEventHandler(new MarkingGestureHandler() {
			public void handleMark(PenEvent e, MarkDirection dir) {
				DebugUtils.println(dir);
				flash.sendMessage("<recognized templateName=\""+dir.toString()+"\"/>");
			}
		});

		connectWithFlashGUI();
		pen.addLivePenListener(new FlashPenListener(flash));

		app.run();
	}

	private void connectWithFlashGUI() {
		// start the local server for sending ink over to the Flash client app
		flash = new ExternalCommunicationServer(7890);
		flash.addFlashClientListener(getFlashListener());
		final File gestureRecHTML = new File("flash/GestureRecognizer/bin/GestureRecognizerDisplay.html");
		DebugUtils.println(gestureRecHTML.getAbsolutePath());
		flash.openFlashHTMLGUI(gestureRecHTML);
	}

	private ExternalListener getFlashListener() {
		return new ExternalListener() {
			public boolean messageReceived(String command, String... args) {
				if (command.equals("exit")) {
					return CONSUMED;
				} else {
					DebugUtils.println("Flash Whiteboard Unhandled command: " + command);
					return NOT_CONSUMED;
				}
			}

		};
	}

	public static void main(String[] args) {
		new GestureRecognitionApp();
	}
}
