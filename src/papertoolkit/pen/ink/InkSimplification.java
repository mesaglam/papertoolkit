package papertoolkit.pen.ink;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import papertoolkit.pen.PenSample;
import papertoolkit.util.MathUtils;


/**
 * <p>
 * Handles real-time or batched simplification of ink strokes. This can make ink look better, or speed up the
 * rendering of ink (by minimizing the number of samples to be drawn).
 * </p>
 * <p>
 * The stroke simplification algorithm this smooths out little bumps
 * 
 * If the stroke is in the same general direction (within the 90 degree cone of the most recent velocity
 * vectors), then we only add new points if they are far enough away...
 * 
 * If the stroke is turning in a different direction, then the threshold is much smaller...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class InkSimplification {

	private int maxNumSamples = 3;

	private LinkedList<PenSample> recentSamples = new LinkedList<PenSample>();

	public InkSimplification() {

	}

	/**
	 * @param sample
	 * @return true if we should KEEP this sample. false if we should just disregard it, as it is too close to
	 *         the recent samples.
	 */
	public boolean processNewSample(PenSample sample) {
		recentSamples.add(sample);
		if (recentSamples.size() <= maxNumSamples) {
			// just return true, so we can collect more samples
			return true;
		} else {
			// check the bounding box in r,theta space...
			// if it's big enough, we return true

			double maxDR = Double.MIN_VALUE;
			double maxDT = Double.MIN_VALUE;

			// this is the new "origin"
			PenSample firstSample = recentSamples.getFirst();
			double firstX = firstSample.x;
			double firstY = firstSample.y;

			double firstTS = firstSample.timestamp;

			List<Double> dThetas = new ArrayList<Double>();

			for (PenSample s : recentSamples) {
				// DebugUtils.println(s);
				if (s.equals(firstSample)) {
					continue;
				}

				double dX = s.getX() - firstX;
				double dY = s.getY() - firstY;
				double dT = s.getTimestamp() - firstTS;

				double dR = Math.sqrt(dX * dX + dY * dY);
				double dTheta = Math.atan2(dY, dX);
				dThetas.add(dTheta);
				// System.out.println(theta + " ");
				maxDR = Math.max(dR, maxDR); // min should be 0
				maxDT = Math.max(dT, maxDT);
			}

			// by default, keep the sample
			boolean shouldKeepNewSample = true;

			// if the samples are too close to each other, then throw them out...
			// DebugUtils.println("maxR: " + maxR);
			if (maxDR < 30) {
				shouldKeepNewSample = false;
			}

			// however, if we've turned enough, we should keep the new sample
			double stdevTheta = MathUtils.standardDeviation(dThetas.toArray(new Double[0]));
			// DebugUtils.println("stdevTheta: " + stdevTheta);
			if (stdevTheta > 1) {
				shouldKeepNewSample = true;
			}

			// however, if the person is writing really slowly, then keep the sample
			// DebugUtils.println("maxDT: " + maxDT);
			if (maxDT > 100) {
				shouldKeepNewSample = true;
			}

			// yet, we should still throw away the samples that are WAY too close to each other... :)
			if (maxDR < 5) {
				shouldKeepNewSample = false;
			}

			if (shouldKeepNewSample) {
				// remove the oldest sample
				recentSamples.removeFirst();
			} else {
				// remove the newest sample
				recentSamples.removeLast();
			}

			return shouldKeepNewSample;
		}
	}

	/**
	 * 
	 */
	public void reset() {
		recentSamples.clear();
	}

	/**
	 * @param currentStroke
	 */
	public void simplifyStroke(InkStroke currentStroke) {
		reset();

		List<PenSample> samples = currentStroke.getSamples();
		List<PenSample> newSamples = new ArrayList<PenSample>();

		for (PenSample s : samples) {
			boolean shouldKeep = processNewSample(s);
			if (shouldKeep) {
				newSamples.add(s);
			}
		}

		// we should ALWAYS keep the last sample
		PenSample lastSample = samples.get(samples.size() - 1);
		if (!newSamples.contains(lastSample)) {
			newSamples.add(lastSample);
		}

		currentStroke.setSamples(newSamples);
	}
}
