package edu.stanford.hci.r3.demos.simple;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.application.Application;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.events.handlers.ClickAdapter;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.units.PatternDots;
import edu.stanford.hci.r3.util.DebugUtils;

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
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("Clicked on R1: " + e);
			}
		});
		r2.addEventHandler(new ClickAdapter() {
			@Override
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
