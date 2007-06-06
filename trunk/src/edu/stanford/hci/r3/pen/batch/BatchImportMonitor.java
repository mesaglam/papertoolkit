package edu.stanford.hci.r3.pen.batch;

import java.io.File;

public interface BatchImportMonitor {

	public String getName();
	
	public void handleBatchedData(File xmlFile);
	
}
