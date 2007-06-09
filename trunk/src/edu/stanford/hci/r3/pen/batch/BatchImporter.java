package edu.stanford.hci.r3.pen.batch;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import javax.swing.JOptionPane;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.SystemUtils;
import edu.stanford.hci.r3.util.files.FileUtils;

/**
 * <p>
 * The batched importer will be called by the pen importer (.NET code) every time you synchronize the pen. It
 * sends the information to the BatchServer, which will notify any running applications.
 * 
 * TODO: In the future, applications do not need to be running all the time. They will be notified of new data
 * upon booting.
 * 
 * This class is launched by PaperToolkit\penSynch\bin\BatchImporter.exe. It creates a log file in the same
 * directory, that you can tail using an app such as BareTail.
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
			final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

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
	 * Adds a monitor into the configuration file. This monitor will be run every time a digital pen is
	 * docked.
	 */
	public static void registerMonitor(BatchImportMonitor monitor) {
		DebugUtils.println("Registering " + monitor.getName());

		// this is the name of the class
		DebugUtils.println(monitor.getClass());

		// this should be the run dir
		DebugUtils.println(monitor.getClass().getResource("/"));

		// create a new instance of this, and run the handler....
		// do it by name...
		try {

			Class<?> c = Class.forName("edu.stanford.hci.r3.demos.batched.monitor.BatchedMonitor");
			Object newInstance = c.newInstance();

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// figure out its path / classpath...

		// save it to our configuration file

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// new BatchImporter(args);
		listRegisteredMonitors();
	}

	/**
	 * We will register our Batched Pen Monitors in PaperToolkit/penSynch/RegisteredBatchedMonitors/.
	 * Currently, we accept *.bat, *.exe, *.jar, and *.javarun files.
	 * 
	 * *.javarun is our own specification, that allows you to specify the absolute path of the CLASSPATH,
	 * working directory, and the main class. Currently, we do not support OS X outside of javarun, but you
	 * can feel free to contribute to the toolkit if you wish.
	 * 
	 * *.jar files should contain a main class, and should be self-contained.
	 */
	public static void listRegisteredMonitors() {
		// find all files in PaperToolkit/penSynch/RegisteredBatchedMonitors/
		File monitorsDir = new File(PaperToolkit.getToolkitRootPath(), "/penSynch/RegisteredBatchedMonitors/");
		List<File> monitors = FileUtils.listVisibleFiles(monitorsDir);
		for (File f : monitors) {
			// DebugUtils.println(f.getAbsolutePath());
			String fileName = f.getName().toLowerCase();
			if (fileName.endsWith(".jar")) {
				// this works if your JRE install has associated JAR files with the double-click action
				DebugUtils.println("Running JAR: " + fileName);
				SystemUtils.runJar(f, new String[] { '"' + new File("penSynch/RegisteredBatchedMonitors/Examples/Example.txt").getAbsolutePath() + '"' });
			} else if (fileName.endsWith(".bat") || fileName.endsWith(".exe")) {
				DebugUtils.println("Running BAT/EXE: " + fileName);
				SystemUtils.run(f, new String[] { '"' + new File("penSynch/RegisteredBatchedMonitors/Examples/Example.txt").getAbsolutePath() + '"' });
			}
		}
	}

	/**
	 * @param monitor
	 */
	public static void removeMonitor(BatchImportMonitor monitor) {
		// TODO Auto-generated method stub
	}
}
