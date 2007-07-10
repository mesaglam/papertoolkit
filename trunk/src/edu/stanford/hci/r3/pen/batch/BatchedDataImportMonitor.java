package edu.stanford.hci.r3.pen.batch;

import java.io.File;

/**
 * 
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public interface BatchedDataImportMonitor {

	public String getName();
	
	public void handleBatchedData(File xmlFile);
	
}
