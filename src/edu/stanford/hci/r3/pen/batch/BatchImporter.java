package edu.stanford.hci.r3.pen.batch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import edu.stanford.hci.r3.demos.batched.monitor.BatchedMonitor;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.SystemUtils;

/**
 * <p>
 * The batched importer will be called by the pen importer (.NET code) every time you synchronize
 * the pen. It sends the information to the BatchServer, which will notify any running applications.
 * 
 * TODO: In the future, applications do not need to be running all the time. They will be notified
 * of new data upon booting.
 * 
 * This class is launched by PaperToolkit\penSynch\bin\BatchImporter.exe. It creates a log file in
 * the same directory, that you can tail using an app such as BareTail.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class BatchImporter {

	private static final boolean DEBUG = true;

	public BatchImporter(String[] args) {
		PrintWriter pw = null;
		try {
			if (DEBUG) {
				JOptionPane.showMessageDialog(null, "Batched Pen Data saved to penSynch/Data/XML/."
						+ "Notifying Registered Listeners...");
			}

			pw = new PrintWriter(new File("BatchImporter.log"));
			pw.println("Running the Batched Pen Data Importer");
			for (String arg : args) {
				pw.println("Argument: " + arg);
			}

			// find all registered listeners, and run them in sequence, with the correct arguments

			// open a socket connection to the batch importer / event handler
			// if an app is running, it will handle the incoming batched ink...
			// if not, this will simply throw an exception and fall through...
			final Socket socket = new Socket("localhost", BatchServer.DEFAULT_PLAINTEXT_PORT);
			final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket
					.getOutputStream()));

			// send over the xml file! =)
			bw.write("XML: " + args[0] + SystemUtils.LINE_SEPARATOR); // the
			// path!
			bw.write(BatchServer.EXIT_COMMAND + SystemUtils.LINE_SEPARATOR);
			bw.flush();
			bw.close();

			// close the socket connection...
			socket.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			pw.println(e.getLocalizedMessage());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			pw.println(e.getLocalizedMessage());
		} catch (IOException e) {
			e.printStackTrace();
			pw.println(e.getLocalizedMessage());
		}
		if (pw != null) {
			pw.flush();
			pw.close();
		}

	}

	/**
	 * Adds a monitor into the configuration file. This monitor
	 */
	public static void registerMonitor(BatchImportMonitor monitor) {
		DebugUtils.println("Registering " + monitor.getName());

		// figure out its path / classpath...

		// save it to our configuration file

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new BatchImporter(args);
	}

	public static void listRegisteredMonitors() {
		// TODO Auto-generated method stub
	}

	public static void removeMonitor(BatchImportMonitor monitor) {
		// TODO Auto-generated method stub
	}
}
