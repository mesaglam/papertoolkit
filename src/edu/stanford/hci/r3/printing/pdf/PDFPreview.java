package edu.stanford.hci.r3.printing.pdf;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;

import javax.swing.JFrame;

import com.adobe.acrobat.Viewer;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * Uses an (admittedly old) Acrobat Java component to preview a PDF File. The performance of the
 * component is pretty poor (e.g., no double buffering), and it doesn't render Acrobat 7 files
 * correctly. Use at your own peril.
 * 
 * Life goes on...
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PDFPreview {

	/**
	 * The file we are viewing.
	 */
	private File file;

	/**
	 * Contains the Acrobat Viewer.
	 */
	private JFrame frame;

	/**
	 * The Acrobat Viewer.
	 */
	private Viewer viewer;

	/**
	 * @param f
	 */
	public PDFPreview(File f) {
		file = f;

		frame = new JFrame("PDF Viewer");
		frame.setLayout(new BorderLayout());
		try {
			viewer = new Viewer();
			viewer.activate();
			viewer.setDocumentInputStream(new FileInputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
		frame.add(viewer, BorderLayout.CENTER);
		frame.setSize(1024, 768);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				// System.out.println("Deactivating...");
				viewer.deactivate();
			}
		});
	}

	/**
	 * @return the Acrobat Viewer's Container.
	 */
	public JFrame getFrame() {
		return frame;
	}

	/**
	 * Exits the Java Application (VM) whenever the user closes the PDF Viewer. This is set to OFF
	 * by default, because you may ask for a PDFPreview within your application.
	 */
	public void setExitApplicationOnClose() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
