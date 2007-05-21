package edu.stanford.hci.r3.units.conversion;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Experimental: Basically interpolates/extrapolates coordinates... based on given examples.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class CoordinateConverter {

	private Map<Double, Double> AtoB = new HashMap<Double, Double>();
	private Map<Double, Double> BtoA = new HashMap<Double, Double>();

	private Set<Double> As = new HashSet<Double>();
	private Set<Double> Bs = new HashSet<Double>();

	public CoordinateConverter() {

	}

	/**
	 * @param a
	 * @param b
	 */
	public void addExampleMapping(double a, double b) {
		AtoB.put(a, b);
		BtoA.put(b, a);
		As.add(a);
		Bs.add(b);
	}

	public double convertAtoB(double a) {
		double[] closestAndFarthestValues = getClosestAndFarthestValues(As, a);

		double closestA = closestAndFarthestValues[0];
		double farthestA = closestAndFarthestValues[1];

		double closestB = AtoB.get(closestA);
		double farthestB = AtoB.get(farthestA); // farthest

		double dA = closestA - farthestA;
		double dB = closestB - farthestB;
		double bOverA = dB / dA; 
		
		// interpolate to get B
		double dAFromTarget = a - closestA;
		double dBFromTarget = bOverA * (dAFromTarget); // == b - closestB
		return closestB + dBFromTarget;
	}

	public double convertBtoA(double b) {
		double[] closestAndFarthestValues = getClosestAndFarthestValues(Bs, b);

		double closestB = closestAndFarthestValues[0];
		double farthestB = closestAndFarthestValues[1];

		double closestA = BtoA.get(closestAndFarthestValues[0]);
		double farthestA = BtoA.get(closestAndFarthestValues[1]); // farthest

		double dB = closestB - farthestB;
		double dA = closestA - farthestA;
		double aOverB = dA / dB; 

		// interpolate to get A
		double dBFromTarget = b - closestB;
		double dAFromTarget = aOverB * (dBFromTarget); // == a - closestA
		return closestA + dAFromTarget;
	}

	private double[] getClosestAndFarthestValues(Set<Double> testSet, double target) {
		double minDiff = Double.MAX_VALUE;
		double maxDiff = Double.MIN_VALUE;

		double minResult = Double.MAX_VALUE;
		double maxResult = Double.MIN_VALUE;

		for (double test : testSet) {
			double diff = test - target;
			double absDiff = Math.abs(diff);

			if (absDiff < minDiff) {
				minDiff = absDiff;
				minResult = test;
			}

			if (absDiff > maxDiff) {
				maxDiff = absDiff;
				maxResult = test;
			}
		}
		return new double[] { minResult, maxResult }; // closest and farthest!
	}
}
