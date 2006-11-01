package edu.stanford.hci.r3.design.gui.toolbar;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import com.sun.org.apache.xml.internal.resolver.helpers.FileURL;

import edu.stanford.hci.r3.util.components.ribbons.RibbonPanel;
import edu.stanford.hci.r3.util.files.FileUtils;

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

	private JButton newDocumentButton;

	private JButton openDocumentButton;

	private JButton importFileButton;

	/**
	 * 
	 * @return
	 */
	private RibbonPanel getFilePanel() {
		if (filePanel == null) {
			filePanel = new RibbonPanel("File");
			filePanel.add(getNewDocument());
			filePanel.add(getOpenDocument());
			filePanel.add(getImportFile());
			filePanel.layoutComponents();
		}
		return filePanel;
	}

	private Component getImportFile() {
		importFileButton = new JButton("Import File");
		importFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				System.out.println("Import File");
				JFileChooser chooser = FileUtils.createNewFileChooser(new String[] {"jpg", "pdf"});
				chooser.showOpenDialog(null);
			}
		});
		return importFileButton;
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

	/**
	 * @return
	 */
	private Component getNewDocument() {
		if (newDocumentButton == null) {
			newDocumentButton = new JButton("New", makeIcon("New"));
			newDocumentButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					System.out.println("New Document");
				}
			});

		}
		return newDocumentButton;
	}

	/**
	 * This should open a File Chooser, allowing a user to open a PDF file. This designer will allow us to
	 * place regions onto this PDF file.
	 * 
	 * @return
	 */
	private Component getOpenDocument() {
		if (openDocumentButton == null) {
			openDocumentButton = new JButton("Open", makeIcon("Open"));
			openDocumentButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					System.out.println("Open Document");
					FileUtils.createNewFileChooser(new String[] {"jpg", "pdf"});
				}
			});

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

	private ImageIcon makeIcon(String iconName) {
		return new ImageIcon(DocumentTasks.class.getResource("/icons/" + iconName + ".png"));
	}
}
