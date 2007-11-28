package papertoolkit.clustering;

import java.util.ArrayList;
import java.util.List;

import papertoolkit.pen.ink.Ink;
import papertoolkit.pen.ink.InkStroke;

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
public class TemporalClustering {

	public static List<InkCluster> clusterWithSimpleThreshold(List<Ink> digitalInk, long milliseconds) {
		List<InkCluster> clusters = new ArrayList<InkCluster>();
		InkCluster currCluster = new InkCluster();
		
		for (Ink ink : digitalInk) {
			for (InkStroke stroke : ink.getStrokes()) {
				if (currCluster.distanceInTime(stroke) > milliseconds) {
					clusters.add(currCluster);
					currCluster = new InkCluster();
				}
				currCluster.addStroke(stroke);
			}
		}
		if (currCluster.getNumInkStrokes() > 0) {
			clusters.add(currCluster);
		}
		return clusters;
	}
}
