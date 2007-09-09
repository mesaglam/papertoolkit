package papertoolkit.demos.simple;

import papertoolkit.PaperToolkit;
import papertoolkit.application.Application;
import papertoolkit.events.PenEvent;
import papertoolkit.events.handlers.ClickHandler.ClickAdapter;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.units.PatternDots;
import papertoolkit.util.DebugUtils;

/**
 * <p>
 * 
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class OverlappingRegions extends Application {

	private Region r1;
	private Region r2;
	private Sheet s;

	public OverlappingRegions() {
		super("Overlapping Regions");

		r1 = new Region("R1", 0, 0, 1, 1);
		r2 = new Region("R2", 0, 0, 2, 2);

		s = new Sheet(5, 8);
		
		s.addRegion(r2, new PatternDots(0), new PatternDots(0), new PatternDots(1000000000), new PatternDots(1000000000));
		s.addRegion(r1, new PatternDots(0), new PatternDots(0), new PatternDots(1000000000), new PatternDots(1000000000));

		r1.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("Clicked on R1: " + e);
			}
		});
		r2.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("Clicked on R2: " + e);
			}
		});
		
		addSheet(s);
		
		PaperToolkit r3 = new PaperToolkit();
		r3.startApplication(this);
	}

	public static void main(String[] args) {
		new OverlappingRegions();
	}
}
