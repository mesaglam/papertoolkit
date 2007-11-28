package papertoolkit.demos.analysis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import papertoolkit.clustering.GenericHierarchicalClustering;
import papertoolkit.clustering.GenericHierarchicalClustering.Cluster;
import papertoolkit.clustering.GenericHierarchicalClustering.LeafNode;
import papertoolkit.clustering.GenericHierarchicalClustering.TreeNode;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.MathUtils;
import papertoolkit.util.files.FileUtils;
import papertoolkit.util.graphics.ImageUtils;

public class Timestamps {

	File desktopDirectory = FileUtils.getDesktopDirectory();

	public Timestamps() {

		// read timestamps from a file and create a JPEG visualizing the timeline
		// read it into a list...
		List<Long> times = FileUtils.readFileIntoLinesOfLongs(new File(desktopDirectory, "Timestamps.txt"));

		// cluster them!
		GenericHierarchicalClustering clustering = new GenericHierarchicalClustering(times);
		int heightOfTree = clustering.performClustering();
		List<Long> listOfValues = clustering.getListOfValues();
		FileUtils.writeListToFile(listOfValues, new File(desktopDirectory, "TimestampsClustered.txt"));
		DebugUtils.println(listOfValues.size());

		// render this clustering
		// make it at least 25 pixels + numpasses * 2 (for each grouping)

		// create a jpeg (1024 x 25)
		final int width = 1024;
		final int height = 25 + 15 * 12;
		final int paddingX = 10;
		BufferedImage writable = ImageUtils.createWritableBuffer(width + 2 * paddingX, height);
		Graphics2D g = writable.createGraphics();

		long min = times.get(0);
		long max = times.get(times.size() - 1);
		long span = max - min;

		List<Cluster> clusters = clustering.getClusters();

		int numClustersToProcess = clusters.size();
		int treeDepth = 0;
		while (numClustersToProcess > 0) {
			treeDepth++;

			List<Cluster> newClusters = new ArrayList<Cluster>();
			for (Cluster c : clusters) {
				long timestamp = c.getCenter(); // plot the center as a 2 pixel high black box...

				long offset = timestamp - min;
				double fraction = offset / (double) span;
				int xLocation = MathUtils.rint(fraction * width);

				if (c instanceof LeafNode) {
					g.setColor(new Color(.25f, .25f, .25f, .75f));
					g.fillRect(paddingX + xLocation, treeDepth*15, 1, 15);
				} else {
					g.setColor(new Color(.85f, .25f, .25f, .75f));
					g.fillRect(paddingX + xLocation, treeDepth*15, 2, 10);
					TreeNode treeNode = (TreeNode) c;
					long minJunction = treeNode.getMin();
					long maxJunction = treeNode.getMax();

					long offMin = minJunction - min;
					double fractMin = offMin / (double) span;
					int xLocMin = MathUtils.rint(fractMin * width);

					long offMax = maxJunction - min;
					double fractMax = offMax / (double) span;
					int xLocMax = MathUtils.rint(fractMax * width);
					g.fillRect(paddingX + xLocMin, treeDepth*15, xLocMax-xLocMin, 1);

					newClusters.addAll(c.getItems());
				}
			}
			clusters = newClusters;
			
			numClustersToProcess = newClusters.size();
		}
		DebugUtils.println(treeDepth);
		
		// write out the file
		ImageUtils.writeImageToPNG(writable, new File(desktopDirectory, "TimestampsClusteredVis.png"));
	}

	private void drawTimestampsOnAnAxis(List<Long> times) {
		// assume first is min, last is max
		long min = times.get(0);
		long max = times.get(times.size() - 1);
		long span = max - min;

		// create a jpeg (1024 x 25)
		final int width = 1024;
		final int height = 25;
		final int paddingX = 10;
		BufferedImage writable = ImageUtils.createWritableBuffer(width + 2 * paddingX, height);

		// plot a 3px line centered on the location
		Graphics2D g = writable.createGraphics();
		g.setColor(new Color(.7f, .7f, .85f, .55f));

		for (Long timestamp : times) {
			long offset = timestamp - min;
			double fraction = offset / (double) span;
			int xLocation = MathUtils.rint(fraction * width);
			g.fillRect(paddingX + xLocation, 0, 1, height);
		}

		// write out the file
		ImageUtils.writeImageToPNG(writable, new File(desktopDirectory, "TimestampsVis.png"));
	}

	private void readTimestampsFromPhotosAndWriteToFile() {
		String path1 = "C:/Documents and Settings/Ron Yeh/My Documents/Projects/Data/"
				+ "BNetDataLosTuxtlas05/Users/Ron Yeh/Photos/Los Tuxtlas/";

		String path2 = "C:/Documents and Settings/Ron Yeh/My Documents/Projects/Data/"
				+ "BNetDataLosTuxtlas05/Users/Ron Yeh/Photos/Los Tuxtlas/March 19, 2005";

		List<File> list = FileUtils.listVisibleFilesRecursivelyExcludingPattern(new File(path1),
				new String[] { "jpg" }, "thumbnails");

		List<Long> timestamps = new ArrayList<Long>();

		for (File f : list) {
			System.out.print(f.getName() + " ");

			// get the timestamp
			long timestamp = ImageUtils.readTimeFrom(f);
			// DebugUtils.println(timestamp + " " + new Date(timestamp));

			timestamps.add(timestamp);

			// how to convert from a timestamp into a list of files, or an index into audio, or into other
			// related timestamps?
		}

		DebugUtils.println("");
		DebugUtils.println(timestamps.get(0) + " to " + timestamps.get(timestamps.size() - 1));
		Collections.sort(timestamps);
		DebugUtils.println(timestamps.get(0) + " to " + timestamps.get(timestamps.size() - 1));
		DebugUtils.println(timestamps.size() + " photos read.");

		// drop a file w/ the timestamps onto the desktop
		File desktopDirectory = FileUtils.getDesktopDirectory();
		FileUtils.writeListToFile(timestamps, new File(desktopDirectory, "Timestamps.txt"));
	}

	public static void main(String[] args) {
		new Timestamps();
	}
}
