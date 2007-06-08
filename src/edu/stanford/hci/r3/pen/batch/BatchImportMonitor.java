package edu.stanford.hci.r3.pen.batch;

import java.io.File;

/**
 * <p>
 * TODO: Rename these all from Batch to Batched
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public interface BatchImportMonitor {

	public String getName();
	
	public void handleBatchedData(File xmlFile);
	
}
