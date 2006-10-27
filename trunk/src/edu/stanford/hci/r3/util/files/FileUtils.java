package edu.stanford.hci.r3.util.files;

import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import edu.stanford.hci.r3.util.ArrayUtils;
import edu.stanford.hci.r3.util.files.filters.DirectoriesOnlyFilter;
import edu.stanford.hci.r3.util.files.filters.FileExtensionFilter;
import edu.stanford.hci.r3.util.files.filters.FilesOnlyFilter;

/**
 * <p>
 * Utilities for manipulating Paths, Files, Directories, etc.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class FileUtils {

	/**
	 * Either show a save or open dialog.
	 */
	public enum FileChooserType {
		OPEN, SAVE
	}

	private static JFileChooser directoryChooser;

	/**
	 * one that we keep around, for reuse
	 */
	private static JFileChooser fileChooser;

	/**
	 * @param extensions
	 * @return
	 */
	public static JFileChooser createNewFileChooser(String[] extensions) {
		final JFileChooser chooser = new JFileChooser();
		final FileFilter filter = new FileExtensionFilter(extensions);
		chooser.setFileFilter((javax.swing.filechooser.FileFilter) filter);
		return chooser;
	}
	
	/**
	 * @param url
	 * @param result
	 * @throws IOException
	 */
	public static void downloadUrlToFile(URL url, File result) throws IOException {
		IOException exception = null;
		InputStream is = null;
		DataInputStream dis = null;
		FileOutputStream fos = null;
		
		byte[] buf = new byte[1024];
		try {
			is = url.openStream();
			dis = new DataInputStream(new BufferedInputStream(is));
			fos = new FileOutputStream(result);
			int bytesRead;
			bytesRead = dis.read(buf);
			while (bytesRead > 0) {
				fos.write(buf, 0, bytesRead);
				bytesRead = dis.read(buf);
			}
		}
		catch (IOException ioe) {
			exception = ioe;
		}
		finally {
			try {
				if (is != null) is.close();
			} catch (IOException ioe) { }
			
			try {
				if (fos != null) fos.close();
			} catch (IOException ioe) { }
			if (exception != null) throw exception;
		}
	}

	/**
	 * @param possiblyHiddenFile
	 * @return if the file is hidden (either hidden flag, or name starts with a dot)
	 */
	public static boolean isHiddenOrDotFile(final File possiblyHiddenFile) {
		return possiblyHiddenFile.isHidden() || possiblyHiddenFile.getName().startsWith(".");
	}
	/**
	 * Return only directories (that are children of the given path) that are not hidden.
	 * 
	 * @param path
	 * @return
	 */
	public static List<File> listVisibleDirs(File path) {
		final File[] files = path.listFiles(new DirectoriesOnlyFilter(Visibility.VISIBLE));
		return ArrayUtils.convertArrayToList(files);
	}

	
	/**
	 * Return only files (that are children of the given path) that are not hidden.
	 * 
	 * @param path
	 * @param extensionFilter
	 */
	public static List<File> listVisibleFiles(File path, String... extensionFilter) {
		final File[] files = path.listFiles((FileFilter) new FilesOnlyFilter(extensionFilter,
				Visibility.VISIBLE));
		return ArrayUtils.convertArrayToList(files);
	}

	/**
	 * Reads an entire file into the StringBuilder (faster than StringBuffer).
	 * 
	 * @param f
	 * @return the StringBuilder containing all the data
	 */
	public static StringBuilder readFileIntoStringBuffer(File f) {
		return readFileIntoStringBuffer(f, false);
	}
	
	/**
	 * Includes the workaround for bug in setting the user.dir:
	 * http://bugs.sun.com/bugdatabase/view_bug.do;:YfiG?bug_id=4117557
	 * 
	 * @param f
	 *            turn this file into a big string buffer (StringBuilder for efficiency)
	 * @param separateWithNewLines
	 * @return
	 */
	public static StringBuilder readFileIntoStringBuffer(File f, boolean separateWithNewLines) {
		final StringBuilder returnVal = new StringBuilder();
		final String endLine = separateWithNewLines ? "\n" : "";
		try {
			final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(
					f.getAbsoluteFile())));
			String line = "";
			while ((line = br.readLine()) != null) {
				returnVal.append(line + endLine);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (returnVal.length() > 0 && separateWithNewLines) {
			// delete the last newline
			return new StringBuilder(returnVal.substring(0, returnVal.length() - 1));
		} else {
			return returnVal;
		}
	}

	/**
	 * Only allows directory selection.
	 * 
	 * @return the chosen directory, or null if the user canceled.
	 */
	public static File showDirectoryChooser(Component parent, String title) {
		if (directoryChooser == null) {
			directoryChooser = new JFileChooser();
			directoryChooser.setDialogTitle(title);
			directoryChooser.setCurrentDirectory(FileSystemView.getFileSystemView().getDefaultDirectory());
			directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}
		int returnVal = directoryChooser.showOpenDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return directoryChooser.getSelectedFile();
		}
		else {
			return null;
		}
	}

	/**
	 * @param initialPath
	 * @param type
	 * @param parent
	 * @param extensions
	 * @param title
	 * @return
	 */
	public static File showFileChooser(File initialPath, FileChooserType type, Component parent,
			String[] extensions, String title) {
		if (fileChooser == null) {
			fileChooser = createNewFileChooser(extensions);
		} else {
			fileChooser.setFileFilter(new FileExtensionFilter(extensions));
		}
		fileChooser.setCurrentDirectory(initialPath);

		// set a custom title if you like
		if (title != null) {
			fileChooser.setDialogTitle(title);
		}

		// show the dialog and see what people do
		int returnVal = (type == FileChooserType.SAVE) ? fileChooser.showSaveDialog(parent)
				: fileChooser.showOpenDialog(parent);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		} else {
			return null;
		}
	}

	/**
	 * Includes workaround for bug: http://bugs.sun.com/bugdatabase/view_bug.do;:YfiG?bug_id=4117557
	 * 
	 * @param string
	 * @param destFile
	 * @created Jun 5, 2006
	 * @author Ron Yeh
	 */
	public static void writeStringToFile(String string, File destFile) {
		try {
			final FileOutputStream fos = new FileOutputStream(destFile.getAbsoluteFile());
			final BufferedWriter bw = new BufferedWriter(new PrintWriter(fos));
			bw.write(string);
			bw.flush();
			bw.close();
			fos.close(); // should be redundant
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * @param string
	 * @param file
	 * @created Mar 14, 2006
	 * @author Ron Yeh
	 */
	public static void writeStringToFileOnlyIfNew(String string, File file) {
		if (file.exists()) {
			System.err.println("File " + file.getPath() + " already exists. Skipping writing.");
			return;
		}
		writeStringToFile(string, file);
	}
	
}
