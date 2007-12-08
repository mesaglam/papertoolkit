package papertoolkit.tools.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import papertoolkit.clustering.InkCluster;
import papertoolkit.clustering.SpatialClustering;
import papertoolkit.clustering.TemporalClustering;

public class InkClustersPanel extends InkPanel {

	private List<InkCluster> clusters;

	public InkClustersPanel() {

	}

	public void clusterInSpaceWithSimpleThreshold(int spaceThresh) {
		clusters = SpatialClustering.clusterWithSimpleThreshold(inkWell, spaceThresh); // pixels / ink units (anoto dots)...
		repaint();
	}

	public void clusterInTimeWithSimpleThreshold(long timeThresh) {
		// iterate through all the ink, and apply the time threshold (millis)
		clusters = TemporalClustering.clusterWithSimpleThreshold(inkWell, timeThresh); // seconds
		repaint();
	}

	protected void drawInk(final Graphics2D g2d) {
		super.drawInk(g2d);

		Color oldColor = g2d.getColor();
		
		g2d.setColor(new Color(228, 228, 255, 200));
		// iterate through all the clusters and draw bounding boxes
		if (clusters != null) {
			for (InkCluster c : clusters) {
				Rectangle2D boundingBoxOfInkStroke = c.getBoundingBoxOfInkStrokes();
				g2d.draw(boundingBoxOfInkStroke.getBounds());
			}
		}
		g2d.setColor(oldColor);
	}
}
