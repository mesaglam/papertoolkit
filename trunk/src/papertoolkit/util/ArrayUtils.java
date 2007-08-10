package papertoolkit.util;

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

	/**
	 * Prints an array of Objects to console.
	 * 
	 * @param array
	 */
	public static void printArray(Object[] array) {
		String className = array[0].getClass().toString();
		System.out.print(className.substring(className.lastIndexOf(".") + 1, className.length())
				+ " Array: [");
		for (int i = 0; i < array.length; i++) {
			System.out.print(array[i]);
			if (i != array.length - 1) {
				System.out.print(", ");
			}
		}
		System.out.println("]");
	}

	/**
	 * Displays a matrix of Objects.
	 * 
	 * @param matrix
	 */
	public static void printMatrix(Object[][] matrix) {
		int numRows = matrix[0].length;
		int numCols = matrix.length;
		System.out.println("Matrix of " + matrix[0][0].getClass().getSimpleName() + ": [");
		for (int y = 0; y < numRows; y++) {
			System.out.print("    row " + y + ": [");
			for (int x = 0; x < numCols; x++) {
				System.out.print(matrix[x][y]);
				if (x != numCols - 1) {
					System.out.print(", ");
				}
			}
			System.out.println("]");
		}
		System.out.println("]");
	}

	/**
	 * @param array
	 *            turns an array of ints into a String
	 */
	public static String toString(int[] array) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i : array) {
			sb.append(i + ", ");
		}
		if (sb.length() > 1) {
			sb.delete(sb.length() - 2, sb.length());
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * @param array
	 * @return
	 */
	public static String toString(Object[] array) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (Object o : array) {
			sb.append(o + ", ");
		}
		if (sb.length() > 1) {
			sb.delete(sb.length() - 2, sb.length());
		}
		sb.append("]");
		return sb.toString();
	}
}
