package edu.stanford.hci.r3.other;

import edu.stanford.hci.r3.util.DebugUtils;

public class Test_MathFunctions {
	public static void main(String[] args) {
		DebugUtils.println(Math.atan(1) + " " + Math.atan2(1, 1));
		DebugUtils.println(Math.atan(10) + " " + Math.atan2(10, 1));
		DebugUtils.println(Math.atan(-1) + " " + Math.atan2(-1, 1));
		DebugUtils.println(Math.atan(-1) + " " + Math.atan2(1, -1));
		DebugUtils.println(Math.atan(0) + " " + Math.atan2(0.000001, -1));
		DebugUtils.println(Math.atan(0) + " " + Math.atan2(-0.000001, -1));
	}
}
