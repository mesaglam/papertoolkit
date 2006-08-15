package edu.stanford.hci.r3.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class ArrayUtils {

	/**
	 * @param <T>
	 *            type of the objects...
	 * @param arrayOfObjects
	 *            the array we want to convert into the List<T>
	 * @return an arraylist of <T>s
	 */
	public static <T> List<T> convertArrayToList(T[] arrayOfObjects) {
		final ArrayList<T> list = new ArrayList<T>();
		Collections.addAll(list, arrayOfObjects);
		return list;
	}

	/**
	 * @param array
	 */
	public static void printArray(double[] array) {
		System.out.print("Double Array: [");
		for (int i = 0; i < array.length; i++) {
			System.out.print(array[i]);
			if (i != array.length - 1) {
				System.out.print(", ");
			}
		}
		System.out.println("]");
	}

}
