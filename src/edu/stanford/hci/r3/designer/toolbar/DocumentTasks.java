package edu.stanford.hci.r3.designer.toolbar;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import edu.stanford.hci.r3.util.components.ribbons.RibbonPanel;

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
public class DocumentTasks {

	private RibbonPanel filePanel;

	private RibbonPanel measurementPanel;

	private JButton measuringBoxButton;

	private JButton measuringTapeButton;

	private JButton openDocumentButton;

	private RibbonPanel getFilePanel() {
		if (filePanel == null) {
			filePanel = new RibbonPanel("File");
			filePanel.add(getOpenDocument());
			filePanel.layoutComponents(1, 1);
		}
		return filePanel;
	}

	/**
	 * @return
	 */
	private Component getMeasureBoxes() {
		if (measuringBoxButton == null) {
			measuringBoxButton = new JButton("Measuring Box");
			measuringBoxButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					System.out.println("Box");
				}
			});
		}
		return measuringBoxButton;
	}

	private Component getMeasureLines() {
		if (measuringTapeButton == null) {
			measuringTapeButton = new JButton("Measuring Tape");
			measuringTapeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					System.out.println("Tape");
				}
			});
		}
		return measuringTapeButton;
	}

	/**
	 * @return
	 */
	private RibbonPanel getMeasurementPanel() {
		if (measurementPanel == null) {
			measurementPanel = new RibbonPanel("Measurement");
			measurementPanel.add(getMeasureLines());
			measurementPanel.add(getMeasureBoxes());
			measurementPanel.layoutComponents(1, 2);
		}
		return measurementPanel;
	}

	private Component getOpenDocument() {
		if (openDocumentButton == null) {
			openDocumentButton = new JButton("Open");
		}
		return openDocumentButton;
	}

	/**
	 * @return
	 */
	public List<RibbonPanel> getPanels() {
		final List<RibbonPanel> panels = new ArrayList<RibbonPanel>();
		panels.add(getFilePanel());
		panels.add(getMeasurementPanel());
		return panels;
	}
}
