package edu.stanford.hci.r3.util.files;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import edu.stanford.hci.r3.util.ArrayUtils;
import edu.stanford.hci.r3.util.files.filters.DirectoriesOnlyFilter;
import edu.stanford.hci.r3.util.files.filters.FileExtensionFilter;
import edu.stanford.hci.r3.util.files.filters.FilesOnlyFilter;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * Utilities for manipulating Paths, Files, Directories, etc.
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
					f)));
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


}
