package edu.stanford.hci.r3.util.jar;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;

/**
 * <p>
 * 
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class JarClassLoader extends URLClassLoader {

	private URL url;

	public JarClassLoader(File jarFile) throws MalformedURLException {
		this(jarFile.toURI().toURL());
	}

	public JarClassLoader(URL theUrl) {
		super(new URL[] { theUrl });
		url = theUrl;
	}

	/**
	 * @return
	 */
	public String getMainClassName() {
		try {
			URL u = new URL("jar", "", url + "!/");
			JarURLConnection uc = (JarURLConnection) u.openConnection();
			Attributes mainAttributes = uc.getMainAttributes();
			if (mainAttributes != null) {
				return mainAttributes.getValue(Attributes.Name.MAIN_CLASS);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public void invokeClass(String name, String[] args) {
		try {
			Class c = loadClass(name);
			Method m = c.getMethod("main", new Class[] { args.getClass() });
			m.setAccessible(true);
			int mods = m.getModifiers();
			if (m.getReturnType() != void.class || !Modifier.isStatic(mods) || !Modifier.isPublic(mods)) {
				throw new NoSuchMethodException("main");
			}
			m.invoke(null, new Object[] { args });
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
