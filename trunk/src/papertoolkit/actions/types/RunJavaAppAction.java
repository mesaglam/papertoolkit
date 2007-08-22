package papertoolkit.actions.types;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import papertoolkit.actions.Action;

/**
 * <p>
 * Runs the main method of a java class. The class has to be in your classpath at runtime of the toolkit...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class RunJavaAppAction implements Action {

	/**
	 * e.g. MyClass.class
	 */
	private Class<?> classToRun;

	/**
	 * @param classWithMainFunction
	 */
	public RunJavaAppAction(Class<?> classWithMainFunction) {
		classToRun = classWithMainFunction;
	}

	/**
	 * Invokes the main method of the given class.
	 * 
	 * @see papertoolkit.actions.Action#invoke()
	 */
	public void invoke() {
		try {
			Method method = classToRun.getMethod("main", new Class[] { String[].class });
			method.invoke(null, new Object[] { new String[] {} });
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

	public String toString() {
		return "Run Java Application: " + classToRun.getName() + ".main";
	}
}
