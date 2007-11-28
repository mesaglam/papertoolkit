package papertoolkit.printing;

import javax.print.DocPrintJob;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;

import papertoolkit.util.SystemUtils;
import papertoolkit.util.files.FileUtils;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt"> BSD
 * License</a>.
 * </p>
 * 
 * Monitor a Print Job
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> ( ronyeh(AT)cs.stanford.edu )
 */
public class PrintJobWatcher {

	// true iff it is safe to close the print job's input stream
	private boolean done = false;

	/**
	 * @param job
	 */
	public PrintJobWatcher(DocPrintJob job) {
		// Add a listener to the print job
		job.addPrintJobListener(new PrintJobAdapter() {
			public void printJobCanceled(PrintJobEvent pje) {
				allDone();
			}

			public void printJobCompleted(PrintJobEvent pje) {
				allDone();
			}

			public void printJobFailed(PrintJobEvent pje) {
				allDone();
			}

			public void printJobNoMoreEvents(PrintJobEvent pje) {
				allDone();
			}

			void allDone() {
				synchronized (PrintJobWatcher.this) {
					done = true;
					PrintJobWatcher.this.notify();
				}
			}
		});
	}

	/**
	 * Blocks on waiting for the print job...
	 */
	public synchronized void waitForDone() {
		try {
			System.out.println("Checking Printer Status...");
			while (!done) {
				System.out.println(FileUtils.getCurrentTimeForUseInAFileName()
						+ " Waiting for the Print Job to Complete...");
				wait();
			}
			System.out.println("Print Job Completed.");
		} catch (InterruptedException e) {
		}
	}
}