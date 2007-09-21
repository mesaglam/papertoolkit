package papertoolkit;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * <p>
 * To test the redirection of System.out. This is moving into the Toolkit monitoring code...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class _Test_SystemOut extends BufferedOutputStream {

	public static void main(String[] args) {
		_Test_SystemOut test = new _Test_SystemOut(System.out);
		System.setOut(new PrintStream(test));
		System.out.println("Hello World");
		// DebugUtils.println("Lala");
	}

	private PrintStream oldOut;
	private String lastStringPrinted;
	private StringBuilder buffer = new StringBuilder();

	public _Test_SystemOut(PrintStream out) {
		super(out);
		oldOut = out;
	}

	@Override
	public void close() throws IOException {
		oldOut.close();
	}

	@Override
	public synchronized void flush() throws IOException {
		oldOut.flush();
	}

	@Override
	public void write(byte[] b) throws IOException {
		oldOut.write(b);
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
		oldOut.print(lastStringPrinted);

		if (!element.contains("java.io.PrintStream.")) {
			// we don't want to pass any newline characters, so we should escape them
			// since the following methods take regexps, we need a crapload of \ symbols (yay Java)
			String printed = buffer.toString().replaceAll("\\n", "\\\\n");
			printed = printed.replaceAll("\\r", "\\\\r");
			oldOut.print(printed + " :: " + element);
			buffer = new StringBuilder();
		}
	}

	@Override
	public synchronized void write(int b) throws IOException {
		oldOut.write(b);
	}
}
