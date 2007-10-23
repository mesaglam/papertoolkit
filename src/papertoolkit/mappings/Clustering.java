package papertoolkit.mappings;

import java.util.ArrayList;
import java.util.List;

import papertoolkit.util.DebugUtils;

public class Clustering {

	public static interface Cluster {
		public void addItem(Cluster item);

		public long getCenter();

		public long getDifference(Cluster c);

		public long getDistance(Cluster c);

		public List<Cluster> getItems();

		// traverses down the tree
		public int numItems();

	}

	public static class LeafNode implements Cluster {
		private long center = 0L;

		/**
		 * Always start with one item.
		 * 
		 * @param value
		 */
		public LeafNode(long value) {
			center = value;
		}

		public void addItem(Cluster item) {
			center = item.getCenter();
		}

		public long getCenter() {
			return center;
		}

		public long getDifference(Cluster c) {
			return c.getCenter() - center;
		}

		public long getDistance(Cluster c) {
			return Math.abs(c.getCenter() - center);
		}

		public List<Cluster> getItems() {
			List<Cluster> items = new ArrayList<Cluster>();
			items.add(this);
			return items;
		}

		@Override
		public int numItems() {
			return 1;
		}
	}

	public static class TreeNode implements Cluster {
		private long center = 0L;
		private List<Cluster> items;

		private long min = Long.MAX_VALUE;
		private long max = Long.MIN_VALUE;
		
		/**
		 * Always start with one item.
		 * 
		 * @param leafItem
		 */
		public TreeNode() {
			items = new ArrayList<Cluster>();
		}

		public void addItem(Cluster item) {
			items.add(item);
			final int n = items.size();
			center = (long) (((n - 1) / n * (double) center) + ((double) item.getCenter() / n));
			
			if (center < min) {
				min = center;
			}
			if (center > max) {
				max = center;
			}
		}

		public long getCenter() {
			return center;
		}

		public long getDifference(Cluster c) {
			return c.getCenter() - center;
		}

		public long getDistance(Cluster c) {
			return Math.abs(c.getCenter() - center);
		}

		@Override
		public List<Cluster> getItems() {
			return items;
		}

		@Override
		public int numItems() {
			int num = 0;
			for (Cluster c : items) {
				num += c.numItems();
			}
			return num;
		}

		public long getMin() {
			return min;
		}

		public long getMax() {
			return max;
		}
	}

	private List<Cluster> clusters = new ArrayList<Cluster>();
	private List<Long> leafItems;

	public Clustering(List<Long> items) {
		leafItems = items;
		DebugUtils.println("Starting with " + items.size() + " items.");
	}

	public List<Long> getListOfValues() {
		List<Long> values = new ArrayList<Long>();

		List<Cluster> clustersToExamine = new ArrayList<Cluster>();
		clustersToExamine.addAll(clusters);

		// do it backwards

		while (clustersToExamine.size() > 0) {
			Cluster c = clustersToExamine.remove(clustersToExamine.size() - 1);
			if (c instanceof LeafNode) {
				values.add(0, c.getCenter());
			} else {
				List<Cluster> items = c.getItems();
				clustersToExamine.addAll(items);
			}
		}

		return values;
	}

	public List<Cluster> getClusters() {
		return clusters;
	}

	private void onePass() {
		// DecimalFormat df = new DecimalFormat("0.##");

		long minDistance = Long.MAX_VALUE;
		int numWithMinDist = 0;

		// iteratively go until we have only one cluster!

		// scan to get min distance between clusters
		Cluster lastCluster = null;
		for (Cluster c : clusters) {
			if (lastCluster != null) {
				long dist = lastCluster.getDistance(c);
				final double secondsDiff = dist / 1000.0;
				if (secondsDiff > 60) {
					final double minutesDiff = secondsDiff / 60;
					if (minutesDiff > 60) {
						final double hoursDiff = minutesDiff / 60;
						if (hoursDiff > 24) {
							final double daysDiff = hoursDiff / 24;
							// DebugUtils.println(df.format(daysDiff) + " DAYS");
						} else {
							// DebugUtils.println(df.format(hoursDiff) + " hours");
						}
					} else {
						// DebugUtils.println(df.format(minutesDiff) + " mins");
					}
				} else {
					// DebugUtils.println(df.format(secondsDiff) + " s");
				}

				// keep min distance
				if (dist < minDistance) {
					numWithMinDist = 1;
					minDistance = dist;
					// DebugUtils.println(minDistance);
				} else if (dist == minDistance) {
					numWithMinDist++;
				}
			}
			lastCluster = c;
		}

		// DebugUtils.println("Min Distance: " + minDistance + " Num With: " + numWithMinDist);

		// make clusters
		List<Cluster> newClusters = new ArrayList<Cluster>();
		lastCluster = null; // reset this
		boolean currItemJustGroupedWithPrevious = false;
		boolean currItemJustAddedToNewCluster = false;

		for (Cluster c : clusters) {
			if (lastCluster != null) {
				// check the distance; if it's the min, then add it to the previous cluster
				// and start over (to spread out the clusters)
				long dist = lastCluster.getDistance(c); // don't use Longs... cuz == doesn't work

				if (dist <= minDistance) {
					if (c instanceof LeafNode) {
						TreeNode newC = new TreeNode();
						newC.addItem(c); // turn it into a treenode for the first time!
						lastCluster.addItem(newC);
					} else {
						lastCluster.addItem(c);
					}

					lastCluster = null;
					currItemJustGroupedWithPrevious = true;
				} else {

				}
			}

			if (!currItemJustGroupedWithPrevious) {
				if (c instanceof LeafNode) {
					TreeNode newC = new TreeNode();
					newC.addItem(c); // turn it into a treenode for the first time!
					c = newC;
				}
				currItemJustAddedToNewCluster = true;
				newClusters.add(c);
				lastCluster = c;
			}

			// one of the two has to be true
			if (currItemJustAddedToNewCluster || currItemJustGroupedWithPrevious) {

			} else {
				// bad
				DebugUtils.println("Lost Item: " + c.getCenter());
			}

			currItemJustAddedToNewCluster = false;
			currItemJustGroupedWithPrevious = false;
		}

		clusters = newClusters;
	}

	public int performClustering() {
		// assume leafitems are already sorted

		// add leaf clusters....
		for (long l : leafItems) {
			clusters.add(new LeafNode(l));
		}

		DebugUtils.println("Performing Clustering on: " + clusters.size() + " items.");

		int numPasses = 0;

		while (clusters.size() > 1) {
			// DebugUtils.println("Total Clusters: " + clusters.size());
			onePass();

			numPasses++;
			// rinse, repeat...
		}

		// DebugUtils.println("Total Clusters: " + clusters.size());
		DebugUtils.println("We took " + numPasses + " passes to cluster " + clusters.get(0).numItems()
				+ " items.");
		return numPasses;
	}
}
