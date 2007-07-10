package edu.stanford.hci.r3.util.classpath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class EclipseProjectClassLoader extends ClassLoader {

	private HashMap<String, Class<?>> classes = new HashMap<String, Class<?>>();

	private List<File> jarClassPaths = new ArrayList<File>();
	private List<File> dirClassPaths = new ArrayList<File>();

	/**
	 * @param projectDir
	 */
	public EclipseProjectClassLoader(File projectDir) {
		// determine the paths we will look in
		EclipseProjectClassPath cp = new EclipseProjectClassPath(new File(projectDir, ".classpath"));
		List<String> paths = cp.parseFile();
		for (String path : paths) {
			File classpath = new File(projectDir, path);
			if (classpath.isDirectory()) { // is a directory with *.class files
				dirClassPaths.add(classpath);
			} else if (classpath.getAbsolutePath().toLowerCase().endsWith(".jar")) { // is a JAR
				// file
				jarClassPaths.add(classpath);
			} else {
				DebugUtils.println("Unrecognized Classpath: " + classpath.getAbsolutePath());
			}
		}
	}

	/**
	 * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
	 */
	@Override
	protected synchronized Class<?> loadClass(String className, boolean resolveIt)
			throws ClassNotFoundException {

		Class<?> result = null;
		byte[] classData;

		DebugUtils.println("Load Class: " + className);
		result = classes.get(className);
		if (result != null) {
			DebugUtils.println("Returning Cached Result.");
			return result;
		}

		try {
			result = super.findSystemClass(className);
			DebugUtils.println("Returning System class (in CLASSPATH)");
			return result;
		} catch (ClassNotFoundException cnfe) {
			DebugUtils.println("Not a System Class");
		}

		// security check
		if (className.startsWith("java.")) {
			// cannot load java.* from our database...
			throw new ClassNotFoundException();
		}

		classData = getClassImplementationFromDirectory(className);

		if (classData == null) {
			// we didn't find it in the directories
			// try our jar files
			result = getClassImplementationFromJARFiles(className);
		} else {
			// we found it in the directory... define the class object now
			result = defineClass(className, classData, 0, classData.length);
		}
		
		// we didn't find it in the filesystem (dir or jar)
		if (classData == null && result == null) {
			throw new ClassNotFoundException();
		}

		if (resolveIt) {
			resolveClass(result);
		}

		classes.put(className, result);
		DebugUtils.println("Returning Newly Loaded Class");
		return result;
	}

	private Class<?> getClassImplementationFromJARFiles(String className) {
		// try to load it from the JAR files we know of...
		for (File jarFile : jarClassPaths) {
			JarClassLoader jcl;
			try {
				jcl = new JarClassLoader(jarFile);
				Class<?> loadedClass = null;
				try {
					loadedClass = jcl.loadClass(className);
				} catch (ClassNotFoundException e) {
					// next!
					continue;
				}

				// if we actually found the class (no exception)...
				return loadedClass;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * @param className
	 * @return
	 */
	private byte[] getClassImplementationFromDirectory(String className) {
		DebugUtils.println("Fetching the implementation of " + className);
		byte[] result;

		// Find the File in our JARs or bin/
		for (File path : dirClassPaths) {
			File classFile = new File(path, className.replace(".", "/") + ".class");
			if (classFile.exists()) {
				DebugUtils.println("Loading " + classFile.getAbsolutePath());

				FileInputStream fi;
				try {
					fi = new FileInputStream(classFile);
					result = new byte[fi.available()];
					fi.read(result);
					return result;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
