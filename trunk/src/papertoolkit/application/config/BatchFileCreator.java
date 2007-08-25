package papertoolkit.application.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import papertoolkit.util.classpath.EclipseProjectClassPath;
import papertoolkit.util.files.FileUtils;


/**
 * <p>
 * This is a utility generating the correct classpath for a WindowsXP *.bat file. The classpath is
 * determined from the eclipse project settings (.classpath).
 * 
 * Run this whenever the classpath changes.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class BatchFileCreator {

	/**
	 * Classpath Delimiter on Windows.
	 */
	private static final String DELIM = ";";

	/**
	 * May 29, 2006
	 */
	public static void main(String[] args) {
		System.out.println("Creating Batch File...");

		final File currentWorkingDir = new File(".");
		final File classpathFile = new File(".classpath");

		System.out.println(currentWorkingDir.getAbsolutePath());
		System.out.println(classpathFile.getAbsolutePath());

		final BatchFileCreator creator = new BatchFileCreator(classpathFile);
		final String classPathString = creator.getClassPathString();

		File batchFile = new File("PenServer.bat");
		FileUtils.writeStringToFile("java -classpath " + classPathString
				+ " edu.stanford.hci.r3.pen.streaming.PenServerTrayApp \n pause", batchFile);

		File batchFile2 = new File("ActionReceiver.bat");
		FileUtils.writeStringToFile("java -classpath " + classPathString
				+ " edu.stanford.hci.r3.actions.remote.ActionReceiverTrayApp \n pause", batchFile2);

		System.out.println(classPathString);
	}

	/**
	 * 
	 */
	private EclipseProjectClassPath eclipseProjectClassPath;

	/**
	 * @param classpathFile
	 */
	public BatchFileCreator(File classpathFile) {
		eclipseProjectClassPath = new EclipseProjectClassPath(classpathFile);
	}

	/**
	 * @return
	 */
	private String getClassPathString() {
		StringBuilder classPathString = new StringBuilder();
		List<String> classpaths = eclipseProjectClassPath.parseFile();
		for (String cp : classpaths) {
			classPathString.append(cp + DELIM);
		}
		return classPathString.substring(0, classPathString.length() - 1);
	}

}