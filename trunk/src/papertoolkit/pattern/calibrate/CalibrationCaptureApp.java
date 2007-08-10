package papertoolkit.pattern.calibrate;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import papertoolkit.PaperToolkit;
import papertoolkit.application.Application;
import papertoolkit.pen.Pen;
import papertoolkit.pen.ink.Ink;
import papertoolkit.pen.ink.InkStroke;
import papertoolkit.pen.streaming.listeners.PenListener;
import papertoolkit.pen.streaming.listeners.PenStrokeListener;
import papertoolkit.pen.synch.BatchedEventHandler;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.MathUtils;
import papertoolkit.util.WindowUtils;
import papertoolkit.util.files.FileUtils;


/**
 * <p>
 * This class helps us create a mapping between the streamed (physical) coordinates and the batched (logical)
 * coordinates for Anoto digital pens. This is important to allow us to mix streaming with batched event
 * handling.
 * </p>
 * <p>
 * Calibrate the Clock First, then Calibrate the X & Y...
 * 
 * This is an an app that takes two strokes and records them. Then, it finds the orientation of the two
 * strokes and calculates the crossing point of the two strokes, recording this as a set of streamed
 * coordinate. It then does the same thing with the batched ink... And calibrates the coordinates.
 * </p>
 * <p>
 * For time-sensitive applications, this also allows us to calibrate the clock of the pen to the system's
 * clock.
 * </p>
 * <p>
 * Usage: Make sure the pen is either empty, or you have download all the data off of your pen. You want to
 * make sure that when you synchronize your pen, it will give the same number of strokes that were detected
 * during streaming.
 * 
 * Draw a few strokes on your page. Then turn off streaming. Upload your pen data. The two ink objects are
 * synchronized automatically.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class CalibrationCaptureApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new CalibrationCaptureApp();
	}

	private Application app;

	private BatchedEventHandler beh;

	private CalibrationEngine calibrate = new CalibrationEngine();

	private JButton calibrateButton;

	private Ink currStreamedInk;

	private JFrame frame;

	private PenListener listener;

	private JPanel mainPanel;

	private Pen pen;

	private Ink savedBatchedInk;

	private Ink savedStreamedInk;

	private JButton saveStrokesButton;

	private long streamedStrokesFileName;

	private JLabel streamedStrokesInfoLabel;

	private PaperToolkit toolkit;

	/**
	 * 
	 */
	public CalibrationCaptureApp() {
		PaperToolkit.initializeLookAndFeel();
		setupGUI();

		app = new Application("Calibration");
		app.addPenInput(getPen());
		app.addBatchEventHandler(getBatchedEventHandler());
		toolkit = new PaperToolkit(true, false, false);
		toolkit.startApplication(app);
	}

	/**
	 * 
	 */
	private void alignStreamedAndBatchedStrokes() {
		calibrate.alignInkStrokes(savedStreamedInk, savedBatchedInk);
	}

	/**
	 * @return
	 */
	private BatchedEventHandler getBatchedEventHandler() {
		if (beh == null) {
			beh = new BatchedEventHandler("Calibration") {
				public void inkArrived(Ink inkOnThisPage) {
					saveBatchedStrokes(inkOnThisPage);
				}

			};
		}
		return beh;
	}

	/**
	 * @return the button to align the two sets of strokes
	 */
	private Component getCalibrateButton() {
		if (calibrateButton == null) {
			calibrateButton = new JButton("Calibrate (Align Streamed & Batched Strokes)");
			calibrateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			calibrateButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					alignStreamedAndBatchedStrokes();
				}
			});
		}
		return calibrateButton;
	}

	/**
	 * @return
	 */
	private Component getPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
			mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
			mainPanel.add(getSaveStreamedStrokesButton());
			mainPanel.add(Box.createVerticalStrut(10));
			mainPanel.add(getStreamedStrokesInfoLabel());
			mainPanel.add(Box.createVerticalStrut(10));
			mainPanel.add(getCalibrateButton());
		}
		return mainPanel;
	}

	/**
	 * @return
	 */
	private Pen getPen() {
		if (pen == null) {
			pen = new Pen();
			pen.addLivePenListener(getPenListener());
		}
		return pen;
	}

	/**
	 * @return
	 */
	private PenListener getPenListener() {
		if (listener == null) {
			listener = new PenStrokeListener() {
				public void penStroke(InkStroke stroke) {
					if (currStreamedInk == null) {
						currStreamedInk = new Ink();
					}

					currStreamedInk.addStroke(stroke);
					DebugUtils.println(currStreamedInk.getNumStrokes() + " strokes collected.");

					final double[] samplesX = stroke.getXSamples();
					final double[] samplesY = stroke.getYSamples();
					DebugUtils.println("STDEV X: " + MathUtils.standardDeviation(samplesX));
					DebugUtils.println("STDEV Y: " + MathUtils.standardDeviation(samplesY));
				}
			};
		}
		return listener;
	}

	/**
	 * @return
	 */
	private Component getSaveStreamedStrokesButton() {
		if (saveStrokesButton == null) {
			saveStrokesButton = new JButton("Save Streamed Strokes");
			saveStrokesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			saveStrokesButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					saveStreamedStrokes();
				}

			});
		}
		return saveStrokesButton;
	}

	/**
	 * @return
	 */
	private Component getStreamedStrokesInfoLabel() {
		if (streamedStrokesInfoLabel == null) {
			streamedStrokesInfoLabel = new JLabel("No Saved Strokes");
			streamedStrokesInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		}
		return streamedStrokesInfoLabel;
	}

	/**
	 * Will only work for the last page you wrote on.
	 * 
	 * @param inkOnThisPage
	 */
	private void saveBatchedStrokes(Ink inkOnThisPage) {
		savedBatchedInk = inkOnThisPage;
		FileUtils.writeStringToFile(inkOnThisPage.toXMLString(), new File("data/calibration/"
				+ streamedStrokesFileName + ".batched.xml"));
	}

	/**
	 * 
	 */
	private void saveStreamedStrokes() {
		savedStreamedInk = currStreamedInk;
		currStreamedInk = null;
		streamedStrokesFileName = System.currentTimeMillis();
		FileUtils.writeStringToFile(savedStreamedInk.toXMLString(), new File("data/calibration/"
				+ streamedStrokesFileName + ".streamed.xml"));
		streamedStrokesInfoLabel.setText(savedStreamedInk.getNumStrokes() + " strokes saved.");
	}

	/**
	 * 
	 */
	private void setupGUI() {
		frame = new JFrame("Pen Calibration");
		frame.add(getPanel());
		frame.pack();
		frame.setLocation(WindowUtils.getWindowOrigin(frame, WindowUtils.DESKTOP_CENTER));
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
