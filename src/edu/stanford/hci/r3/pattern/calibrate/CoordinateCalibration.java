package edu.stanford.hci.r3.pattern.calibrate;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.pen.PenSample;
import edu.stanford.hci.r3.pen.batch.BatchedEventHandler;
import edu.stanford.hci.r3.pen.ink.Ink;
import edu.stanford.hci.r3.pen.ink.InkStroke;
import edu.stanford.hci.r3.pen.streaming.PenStreamingConnection;
import edu.stanford.hci.r3.pen.streaming.listeners.PenListener;
import edu.stanford.hci.r3.pen.streaming.listeners.PenStrokeListener;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.MathUtils;
import edu.stanford.hci.r3.util.files.FileUtils;

/**
 * <p>
 * This class helps us create a mapping between the streamed (physical) coordinates and the batched
 * (logical) coordinates for Anoto digital pens. This is important to allow us to mix streaming with
 * batched event handling.
 * </p>
 * <p>
 * Calibrate the Clock First, then Calibrate the X & Y...
 * 
 * This is an an app that takes two strokes and records them. Then, it finds the orientation of the
 * two strokes and calculates the crossing point of the two strokes, recording this as a set of
 * streamed coordinate. It then does the same thing with the batched ink... And calibrates the
 * coordinates.
 * </p>
 * <p>
 * Usage: Make sure the pen is either empty, or you have download all the data off of your pen. You
 * want to make sure that when you synchronize your pen, it will give the same number of strokes
 * that were detected during streaming.
 * 
 * Draw a few strokes on your page. Then turn off streaming. Upload your pen data. The two ink
 * objects are synchronized automatically.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class CoordinateCalibration {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new CoordinateCalibration();
	}

	private Application app;

	private BatchedEventHandler beh;

	private JFrame frame;

	private PenListener listener;

	private JPanel mainPanel;

	private Pen pen;

	private JButton saveStrokesButton;

	private Ink streamedInk = new Ink();

	private long streamedStrokesFileName;

	private PaperToolkit toolkit;

	/**
	 * 
	 */
	public CoordinateCalibration() {
		PaperToolkit.initializeLookAndFeel();
		setupGUI();

		app = new Application("Calibration");
		app.addPen(getPen());
		app.addBatchEventHandler(getBatchedEventHandler());
		toolkit = new PaperToolkit(true, false, false);
		toolkit.startApplication(app);
	}

	private BatchedEventHandler getBatchedEventHandler() {
		if (beh == null) {
			beh = new BatchedEventHandler() {
				@Override
				public void inkArrived(Ink inkOnThisPage) {
					saveBatchedStrokes(inkOnThisPage);
				}

			};
		}
		return beh;
	}

	private Component getPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.add(getSaveStreamedStrokesButton());
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
				@Override
				public void penStroke(InkStroke stroke) {
					streamedInk.addStroke(stroke);
					DebugUtils.println(streamedInk.getNumStrokes() + " strokes collected.");

					final double[] samplesX = stroke.getXSamples();
					final double[] samplesY = stroke.getYSamples();
					DebugUtils.println("STDEV X: " + MathUtils.standardDeviation(samplesX));
					DebugUtils.println("STDEV Y: " + MathUtils.standardDeviation(samplesY));
				}
			};
		}
		return listener;
	}

	private Component getSaveStreamedStrokesButton() {
		if (saveStrokesButton == null) {
			saveStrokesButton = new JButton("Save Streamed Strokes");
			saveStrokesButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					saveStreamedStrokes();
				}

			});
		}
		return saveStrokesButton;
	}

	private void saveBatchedStrokes(Ink inkOnThisPage) {
		FileUtils.writeStringToFile(inkOnThisPage.getAsXML(), new File("data/calibration/"
				+ streamedStrokesFileName + ".batched.xml"));
	}

	/**
	 * 
	 */
	private void saveStreamedStrokes() {
		streamedStrokesFileName = System.currentTimeMillis();
		FileUtils.writeStringToFile(streamedInk.getAsXML(), new File("data/calibration/"
				+ streamedStrokesFileName + ".streamed.xml"));
	}

	/**
	 * 
	 */
	private void setupGUI() {
		frame = new JFrame("Pen Calibration");
		frame.add(getPanel());
		frame.setSize(320, 240);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
