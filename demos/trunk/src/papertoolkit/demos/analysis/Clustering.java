package papertoolkit.demos.analysis;

import java.util.List;

import papertoolkit.pen.ink.Ink;
import papertoolkit.pen.synch.PenSynch;
import papertoolkit.pen.synch.PenSynchManager;
import papertoolkit.tools.components.InkClustersFrame;
import papertoolkit.tools.components.InkFrame;
import papertoolkit.tools.components.InkPanel;
import papertoolkit.util.DebugUtils;

public class Clustering {

	public static void main(String[] args) {

		// read in batched data...
		// (get most recent synch)
		PenSynchManager synchManager = new PenSynchManager();
		PenSynch penSynch = synchManager.getMostRecentPenSynch();
		List<Ink> importedInk = penSynch.getImportedInk();

		for (Ink ink : importedInk) {
			DebugUtils.println(ink.getNumStrokes());
		}

		// Visualize This in a GUI Frame... InkFrame?
		InkFrame inkFrame = new InkClustersFrame();
		inkFrame.setInk(importedInk);
		InkPanel inkPanel = inkFrame.getInkPanel();
		
		// cluster in time
		// spit out cluster info
		

		// cluster in space
		// spit out cluster info

		// Visualize in a GUI Panel
		// Each Cluster has a Bounding Box...

		// System.exit(0);
	}
}
