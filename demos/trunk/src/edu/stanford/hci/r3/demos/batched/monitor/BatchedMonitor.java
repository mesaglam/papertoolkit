package edu.stanford.hci.r3.demos.batched.monitor;

import java.io.File;

import javax.swing.JOptionPane;

import edu.stanford.hci.r3.pen.batch.BatchImportMonitor;
import edu.stanford.hci.r3.pen.batch.BatchImporter;

/**
 * <p>
 * Demonstrates how we can register a monitor for batched data. This monitor will be called every
 * time someone docks the pen...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class BatchedMonitor implements BatchImportMonitor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BatchImporter.registerMonitor(new BatchedMonitor());
		BatchImporter.listRegisteredMonitors();
		BatchImporter.removeMonitor(new BatchedMonitor());
		BatchImporter.listRegisteredMonitors();
		
		new BatchedMonitor().handleBatchedData(new File("."));
	}

	@Override
	public void handleBatchedData(File xmlFile) {
		JOptionPane.showMessageDialog(null, "Handling Batched Data...");
	}

	@Override
	public String getName() {
		return "Batched Monitor Demo";
	}
}
