package edu.stanford.hci.r3.demos.batched.monitor;

import java.io.File;

import javax.swing.JOptionPane;

import papertoolkit.pen.synch.BatchedDataImportMonitor;
import papertoolkit.pen.synch.BatchedDataImporter;


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
public class BatchedMonitor implements BatchedDataImportMonitor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BatchedDataImporter.registerMonitor(new BatchedMonitor());
		BatchedDataImporter.removeMonitor(new BatchedMonitor());
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
