package papertoolkit.tools.services;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * <p>
 * Monitor System Out.printlns and report them to SideCar...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class MonitorSystemOut extends BufferedOutputStream {

	private ToolkitMonitoringService monitor;
	/**
	 * Our original System.out, that we are now redirecting...
	 */
	private PrintStream systemOut;

	private String lastStringPrinted;

	private StringBuilder buffer = new StringBuilder();

	public MonitorSystemOut(ToolkitMonitoringService toolkitMonitoringService) {
		super(System.out);
		systemOut = System.out;
		monitor = toolkitMonitoringService;
		System.setOut(new PrintStream(this));
	}
	
	@Override
	public void close() throws IOException {
		systemOut.close();
	}

	@Override
	public synchronized void flush() throws IOException {
		systemOut.flush();
	}

	@Override
	public void write(byte[] b) throws IOException {
		systemOut.write(b);
	}

	/**
	 * This seems to be the method that is called with System.out.println... We need to figure out what was
	 * printed, and where!
	 * 
	 * @see java.io.BufferedOutputStream#write(byte[], int, int)
	 */
	@Override
	public synchronized void write(byte[] b, int off, int len) throws IOException {
		lastStringPrinted = new String(b, off, len);
		final Thread currThread = Thread.currentThread();
		final int actualOffset = 9;
		final StackTraceElement[] ste = currThread.getStackTrace();
		String element = ste[actualOffset].toString();
		element = "[ " + element + " ]";

		// add it to our string builder
		buffer.append(lastStringPrinted);
		systemOut.print(lastStringPrinted);

		if (!element.contains("java.io.PrintStream.")) {
			// we don't want to pass any newline characters, so we should escape them
			// since the following methods take regexps, we need a crapload of \ symbols (yay Java)
			String printed = buffer.toString().replaceAll("\\n", "\\\\n");
			printed = printed.replaceAll("\\r", "\\\\r");
			systemOut.print(printed + " :: " + element);
			buffer = new StringBuilder();
		}
	}

	@Override
	public synchronized void write(int b) throws IOException {
		systemOut.write(b);
	}
	
}
