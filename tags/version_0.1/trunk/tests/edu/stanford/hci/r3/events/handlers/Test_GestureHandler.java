package edu.stanford.hci.r3.events.handlers;

import static java.lang.Math.atan2;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;

import org.junit.Test;

/**
 * <p>
 * Name all tests like Test_X so that the names do not clash with the usage of the actual class in
 * eclipse's autocomplete.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Test_GestureHandler {

	public static void main(String[] args) {

	}

	@Test
	public void testAngles() {
		// atan2(dy,dx)
		// degrees go from +180 to -180, with 0 degrees pointing EAST
		// rads probably go from +PI to -PI, with 0 going EAST
		System.out.println(toDegrees(atan2(5, 2)));
		System.out.println(toDegrees(atan2(0, 2)));
		System.out.println(toDegrees(atan2(5, 20)));
		System.out.println(toDegrees(atan2(4, 3)));
		System.out.println(toDegrees(atan2(3, 3 * sqrt(3))));
		System.out.println(toDegrees(atan2(5, -2)));
		System.out.println(toDegrees(atan2(0, -2)));
		System.out.println(toDegrees(atan2(-0, -2)));
		System.out.println(toDegrees(atan2(-2, -500)));
		System.out.println(toDegrees(atan2(-2, 500)));
	}
}
