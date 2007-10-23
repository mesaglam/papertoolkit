package papertoolkit.util.files;

import java.awt.Component;
import java.awt.Desktop;
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
import java.net.URI;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import papertoolkit.util.ArrayUtils;
import papertoolkit.util.SystemUtils;
import papertoolkit.util.files.filters.DirectoriesOnlyFilter;
import papertoolkit.util.files.filters.FileExcludeHiddenAndPatternFilter;
import papertoolkit.util.files.filters.FileExcludeHiddenFilter;
import papertoolkit.util.files.filters.FileExtensionFilter;
import papertoolkit.util.files.filters.FilesOnlyFilter;
import papertoolkit.util.graphics.ImageUtils;

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
	 * @param htmlFile
	 */
	public static void browseToLocalHTMLFile(File htmlFile) {
		final URI uri = htmlFile.toURI();
		try {
			Desktop.getDesktop().browse(uri);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param srcFileOrDir
	 * @param targetFileOrDir
	 */
	public static void copy(File srcFileOrDir, File targetFileOrDir) {
		try {
			copy(srcFileOrDir, targetFileOrDir, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * A flexible copy function. It will do slightly different things depending on what is passed into the
	 * parameters. It can copy a file to another file, into a directory, or a directory into another
	 * directory.
	 * 
	 * @param sourceFileOrDir
	 * @param targetFileOrDir
	 * @param b
	 * @throws IOException
	 */
	public static void copy(File sourceFileOrDir, File targetFileOrDir, boolean visibleFilesOnly)
			throws IOException {
		// directories can be copied over a target directory
		if (sourceFileOrDir.isDirectory()) {
			if (!targetFileOrDir.exists()) {
				targetFileOrDir.mkdirs();
			}

			if (!targetFileOrDir.isDirectory()) {
				System.err.println("Error: Trying to copy a directory into a file.");
			} else {
				copyDirectory(sourceFileOrDir, targetFileOrDir, visibleFilesOnly);
			}
		}
		// files can be copied over a target file (or into a directory)
		else {
			if (targetFileOrDir.isDirectory()) {
				targetFileOrDir = new File(targetFileOrDir, sourceFileOrDir.getName());
			}
			copyFile(sourceFileOrDir, targetFileOrDir, visibleFilesOnly);
		}
	}

	/**
	 * Copies all files from one directory to another.
	 * 
	 * @param destDir
	 * @param srcDir
	 * @param visibleFilesOnly
	 * @throws IOException
	 */
	private static void copyDirectory(File srcDir, File destDir, boolean visibleFilesOnly) throws IOException {
		if (visibleFilesOnly && (isHiddenOrDotFile(srcDir))) {
			// this source directory does not fit the flag, because it is hidden
			return;
		}

		// System.out.println("Copying from: " + srcDir.getCanonicalPath() + " to "
		// + destDir.getCanonicalPath());
		final File[] srcItems = srcDir.listFiles();
		for (int i = 0; i < srcItems.length; i++) {
			final File srcFile = srcItems[i];
			// if we want only visible files, but this file is hidden...
			if (visibleFilesOnly && isHiddenOrDotFile(srcFile)) {
				continue;
			}

			// create the dest
			final File dest = new File(destDir, srcFile.getName());
			if (srcFile.isDirectory()) {
				dest.mkdir();
				copyDirectory(srcFile, dest, visibleFilesOnly);
			} else {
				copyFile(srcFile, dest, visibleFilesOnly);
			}
		}
	}

	/**
	 * Copy a file from source to dest.
	 * 
	 * @param source
	 * @param dest
	 * @param visibleFilesOnly
	 * @throws IOException
	 */
	private static void copyFile(File source, File dest, boolean visibleFilesOnly) throws IOException {
		if (visibleFilesOnly && isHiddenOrDotFile(source)) {
			// this source file does not fit the flag
			return;
		}

		if (dest.exists()) {
			System.err.println("Destination File Already Exists: " + dest);
		}

		FileChannel in = null, out = null;
		try {
			in = new FileInputStream(source).getChannel();
			out = new FileOutputStream(dest).getChannel();
			in.transferTo(0, in.size(), out);

			// an alternate way...
			// long size = in.size();
			// final MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0, size);
			// out.write(buf);

		} finally {
			if (in != null) {
				in.close();
				// System.out.println("Closed In");
			}
			if (out != null) {
				out.close();
				// System.out.println("Closed Out");
			}
		}
	}

	/**
	 * @param extensions
	 *            should NOT have a . in front of them... i.e., xml, and NOT .xml
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
		} catch (IOException ioe) {
			exception = ioe;
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException ioe) {
			}

			try {
				if (fos != null)
					fos.close();
			} catch (IOException ioe) {
			}
			if (exception != null)
				throw exception;
		}
	}

	/**
	 * @return The current time, with symbols replaced with underscores, so that we can use it in file names.
	 *         This is great for logs that have to be tagged with dates.
	 */
	public static String getCurrentTimeForUseInAFileName() {
		String time = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		// remove symbols that break Windows file names
		time = time.replaceAll(":", "_");
		time = time.replaceAll(",", "");
		return time;
	}

	/**
	 * Return a string that can be used as the main part of the file name. This represents the current
	 * timestamp.
	 * 
	 * @return
	 */
	public static String getCurrentTimeForUseInASortableFileName() {
		final Calendar c = Calendar.getInstance();
		final int month = c.get(Calendar.MONTH) + 1; // Calendar.January == 0
		final int date = c.get(Calendar.DATE);
		final int year = c.get(Calendar.YEAR);

		final int twentyFourHour = c.get(Calendar.HOUR_OF_DAY);
		final int minute = c.get(Calendar.MINUTE);
		final int seconds = c.get(Calendar.SECOND);

		final String monthStr = padToTwoDigitsWithZeroes(month);
		final String dateStr = padToTwoDigitsWithZeroes(date);
		final String hourStr = padToTwoDigitsWithZeroes(twentyFourHour);
		final String minuteStr = padToTwoDigitsWithZeroes(minute);
		final String secondsStr = padToTwoDigitsWithZeroes(seconds);

		return year + "_" + monthStr + "_" + dateStr + "__" + //
				hourStr + "_" + minuteStr + "_" + secondsStr;
	}

	/**
	 * @return on a Windows System, the user's Desktop Directory
	 */
	public static File getDesktopDirectory() {
		return FileSystemView.getFileSystemView().getHomeDirectory();
	}

	/**
	 * @return on a Windows System, the My Documents Directory
	 */
	public static File getMyDocumentsDirectory() {
		return FileSystemView.getFileSystemView().getDefaultDirectory();
	}

	/**
	 * break a path down into individual elements and add to a list. example : if a path is /a/b/c/d.txt, the
	 * breakdown will be [d.txt,c,b,a]
	 * 
	 * @author David M. Howard
	 * @param f
	 *            input file
	 * @return a List collection with the individual elements of the path in reverse order
	 */
	private static List<String> getPathList(File f) {
		List<String> l = new ArrayList<String>();
		File r;
		try {
			r = f.getCanonicalFile();
			while (r != null) {
				l.add(r.getName());
				r = r.getParentFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
			l = null;
		}
		return l;
	}

	/**
	 * get relative path of File 'f' with respect to 'home' directory example : home = /a/b/c f = /a/d/e/x.txt
	 * s = getRelativePath(home,f) = ../../d/e/x.txt
	 * 
	 * home = "/a/b/c" file = "/a/b/c/d/e.txt" relative path = "d/e.txt"
	 * 
	 * home = "/a/b/c" file = "/a/d/f/g.txt" relative path = "../../d/f/g.txt"
	 * 
	 * @author David M. Howard
	 * @param home
	 *            base path, should be a directory, not a file, or it doesn't make sense
	 * @param f
	 *            file to generate path for
	 * @return path from home to f as a string
	 */
	public static String getRelativePath(File home, File f) {
		List<String> homelist;
		List<String> filelist;
		String s;

		homelist = getPathList(home);
		filelist = getPathList(f);
		s = matchPathLists(homelist, filelist);

		return s;
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
	 * TODO/BUG: Doesn't use FileExcludeHiddenFilter??
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
	 * @param path
	 * @return
	 */
	public static List<File> listVisibleFilesRecursively(File path) {
		final FileFilter filter = (FileFilter) new FileExcludeHiddenFilter();

		final ArrayList<File> files = new ArrayList<File>();
		final ArrayList<File> dirsToProcess = new ArrayList<File>();

		dirsToProcess.add(path);

		while (dirsToProcess.size() != 0) {
			File thisPath = dirsToProcess.remove(0);

			// list it, and add all files to the files arraylist
			// add all directories to dirsToProcess
			File[] theseFiles = thisPath.listFiles(filter);
			if (theseFiles != null) {
				for (File f : theseFiles) {
					if (f.isDirectory()) {
						dirsToProcess.add(f);
					} else {
						files.add(f);
					}
				}
			}
		}
		return files;
	}

	/**
	 * Lists files starting from a given path. These files must be visible, and match the extension filter,
	 * but NOT match the excluding pattern filter.
	 * 
	 * Yes, this method name is extremely long. Feel free to suggest a better one. :)
	 * 
	 * @author ronyeh
	 * 
	 * @param path
	 * @param extensionFilter
	 * @param patternToExclude
	 *            if this string appears as a file or directory name ANYWHERE in the absolute path of the file
	 *            being examined, it is excluded. This pattern is case-sensitive.
	 * @return
	 */
	public static List<File> listVisibleFilesRecursivelyExcludingPattern(File path, String[] extensionFilter,
			String patternToExclude) {

		// eliminates hidden files.
		final FileFilter filter = (FileFilter) new FileExcludeHiddenAndPatternFilter(extensionFilter,
				patternToExclude);

		// the list of files we will return
		final List<File> files = new ArrayList<File>();

		// special case
		// If the path doesn't fit the pattern, exclude it
		if (path.getName().contains(patternToExclude)) {
			return files;
		}

		// the algorithm is actually iterative, unlike what is implied by the method name

		// this stores all the directories we still need to look at
		final List<File> dirsToProcess = new ArrayList<File>();

		// add the root path
		dirsToProcess.add(path);
		while (dirsToProcess.size() != 0) {
			final File currentPath = dirsToProcess.remove(0);

			// list it, and add all files to the files arraylist
			// add all directories to dirsToProcess
			final File[] currentFiles = currentPath.listFiles(filter);
			if (currentFiles == null) {
				continue;
			}
			for (File f : currentFiles) {
				if (f.isDirectory()) {
					dirsToProcess.add(f);
				} else {
					files.add(f);
				}
			}
		}
		return files;
	}

	/**
	 * @param path
	 * @param extensionFilter
	 * @return a List of Files (guaranteed to be files, because if it's a dir, it will drill down)
	 */
	public static List<File> listVisibleFilesRecursively(File path, String[] extensionFilter) {

		final FileFilter filter = (FileFilter) new FileExcludeHiddenFilter(extensionFilter);

		final ArrayList<File> files = new ArrayList<File>();
		final ArrayList<File> dirsToProcess = new ArrayList<File>();

		dirsToProcess.add(path);

		while (dirsToProcess.size() != 0) {
			File thisPath = dirsToProcess.remove(0);

			// list it, and add all files to the files arraylist
			// add all directories to dirsToProcess
			File[] theseFiles = thisPath.listFiles(filter);
			if (theseFiles != null) {
				for (File f : theseFiles) {
					if (f.isDirectory()) {
						dirsToProcess.add(f);
					} else {
						files.add(f);
					}
				}
			}
		}
		return files;
	}

	/**
	 * figure out a string representing the relative path of 'f' with respect to 'r'
	 * 
	 * @author David M. Howard
	 * 
	 * @param r
	 *            home path
	 * @param f
	 *            path of file
	 */
	private static String matchPathLists(List<String> r, List<String> f) {
		int i;
		int j;
		String s;
		// start at the beginning of the lists
		// iterate while both lists are equal
		s = "";
		i = r.size() - 1;
		j = f.size() - 1;

		// first eliminate common root
		while ((i >= 0) && (j >= 0) && (r.get(i).equals(f.get(j)))) {
			i--;
			j--;
		}

		// for each remaining level in the home path, add a ..
		for (; i >= 0; i--) {
			s += ".." + File.separator;
		}

		// for each level in the file path, add the path
		for (; j >= 1; j--) {
			s += f.get(j) + File.separator;
		}

		// file name
		s += f.get(j);
		return s;
	}

	/**
	 * Works with positive numbers... and has a corner case where value=0 and numDigits=1 will fail.
	 * 
	 * @param value
	 * @param numDigits
	 * @return
	 */
	private static String padToTwoDigitsWithZeroes(int value) {
		return (value < 10) ? "0" + value : "" + value;
	}

	public static List<Long> readFileIntoLinesOfLongs(File file) {
		List<String> lines = readFileIntoLines(file);
		List<Long> dest = new ArrayList<Long>();
		for (String s : lines) {
			dest.add(Long.parseLong(s));
		}
		return dest;
	}
	
	/**
	 * Reads a file into a list of Strings
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static List<String> readFileIntoLines(File file) {
		List<String> lines = new ArrayList<String>();
		try {
			FileInputStream fis = new FileInputStream(file);
			BufferedReader read = new BufferedReader(new InputStreamReader(fis));

			String line;
			while ((line = read.readLine()) != null) {
				lines.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
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
			final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f
					.getAbsoluteFile())));
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
		} else {
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
		int returnVal = (type == FileChooserType.SAVE) ? fileChooser.showSaveDialog(parent) : fileChooser
				.showOpenDialog(parent);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		} else {
			return null;
		}
	}

	/**
	 * @param files
	 * @return
	 */
	public static void sortByLastModified(List<File> files, final SortDirection direction) {
		// SystemUtils.tic();
		Collections.sort(files, new Comparator<File>() {
			public int compare(File a, File b) {
				long aTime = a.lastModified();
				long bTime = b.lastModified();
				long diff = aTime - bTime;
				if (diff == 0) {
					return 0;
				} else if (diff < 0) {
					if (SortDirection.OLD_TO_NEW == direction) {
						return -1;
					} else {
						return 1;
					}

				} else {
					if (SortDirection.OLD_TO_NEW == direction) {
						return 1;
					} else {
						return -1;
					}
				}
			}
		});
		// SystemUtils.toc();
	}

	/**
	 * For JPEGs....
	 * 
	 * This is a little bit slow, as it takes 4.3 seconds for about 670 files. Can we speed this up through a
	 * cache? After implementing the cache, it takes about 875 ms to sort 670 files. This is because we only
	 * read each file once, and save the timestamp in memory.
	 * 
	 * @param files
	 * @param direction
	 */
	public static HashMap<File, Long> sortPhotosByCaptureDate(List<File> files, final SortDirection direction) {
		// cache of file --> timestamp mappings.
		final HashMap<File, Long> exifTimes = new HashMap<File, Long>();

		SystemUtils.tic();
		Collections.sort(files, new Comparator<File>() {
			public int compare(File a, File b) {

				long aTime;
				long bTime;

				// begin cache
				if (exifTimes.containsKey(a)) {
					aTime = exifTimes.get(a);
				} else {
					aTime = ImageUtils.readTimeFrom(a);
					exifTimes.put(a, aTime);
				}
				if (exifTimes.containsKey(b)) {
					bTime = exifTimes.get(b);
				} else {
					bTime = ImageUtils.readTimeFrom(b);
					exifTimes.put(b, bTime);
				}
				// end cache

				long diff = aTime - bTime;
				if (diff == 0) {
					return 0;
				} else if (diff < 0) {
					if (SortDirection.OLD_TO_NEW == direction) {
						return -1;
					} else {
						return 1;
					}

				} else {
					if (SortDirection.OLD_TO_NEW == direction) {
						return 1;
					} else {
						return -1;
					}
				}
			}
		});
		SystemUtils.toc();
		return exifTimes;
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

	public static void writeListToFile(List<?> stuff, File file) {
		// create a big string, with each item on one line...
		StringBuilder b = new StringBuilder();
		for (Object o : stuff) {
			b.append(o.toString() + "\n");
		}
		writeStringToFile(b.toString(), file);
	}
}
