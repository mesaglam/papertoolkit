package edu.stanford.hci.r3.tools.browse;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.util.DebugUtils;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * <p>
 * Allows you to drag to change the time...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class TimelineCanvas extends PCanvas {

	private Color axisColor;
	private Color tickColor;
	private PPath axisPath;
	private int canvasHeight = 100;

	
	private List<PPath> tickMarksInView = new ArrayList<PPath>();
	
	public TimelineCanvas() {
		useDefaultTheme();
		setPreferredSize(new Dimension(getPreferredSize().width, canvasHeight));

		setupVisualComponents();

		setPanEventHandler(null);
		setZoomEventHandler(null);

		// setAnimatingRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);

		// only drag left and right
		addInputEventListener(new HorizontalPanEventHandler() {
			protected void drag(PInputEvent e) {
				super.drag(e);
				handleDrag();
			}

		});
	}

	private void handleDrag() {
		PBounds viewBounds = getCamera().getViewBounds();
		DebugUtils.println(viewBounds);
		
		// find all the marks that are within the view, or that have scrolled off camera...
		
		for (PPath tick : tickMarksInView) {
			double tickX = tick.getX();
			if (tickX < viewBounds.x) {
				DebugUtils.println(tickX + " LEFT");
			} else if (tickX >= viewBounds.x + viewBounds.width) {
				DebugUtils.println(tickX + " RIGHT");
			} else {
				DebugUtils.println(tickX + " IN");
			}
		}
	}

	private PNode getAxisPath() {
		if (axisPath == null) {
			// HACK: -5 to 3000 is larger than most monitors today... =)
			// Really, this should be resized to the window at all times.
			axisPath = new PPath(new Line2D.Double(-5, canvasHeight / 2, 3000, canvasHeight / 2));
			axisPath.setStroke(new BasicStroke(1));
			axisPath.setStrokePaint(axisColor);
		}
		return axisPath;
	}

	private void setupVisualComponents() {
		getCamera().addChild(getAxisPath());

		// set up the initial vertical tick marks...
		// remove them when they go off camera... add them when they are about to come on camera...?
		for (int i = 0; i < 10; i++) {
			PPath vTickMark = getVerticalTickMarkAtPercentageLocation(i / 10.0 + .25);
			tickMarksInView.add(vTickMark);
			getLayer().addChild(vTickMark);
		}
	}

	/**
	 * @param xLocation
	 *            0.0 to 1.0
	 * @return
	 */
	private PPath getVerticalTickMarkAtPercentageLocation(double xLocation) {
		PPath tick = new PPath(new Line2D.Double(xLocation * 1000, canvasHeight/2 - 10, xLocation * 1000,
				canvasHeight/2 + 10));
		tick.setStroke(new BasicStroke(2));
		tick.setStrokePaint(tickColor);
		return tick;
	}

	public void useDefaultTheme() {
		setBackground(new Color(20, 20, 20));
		axisColor = new Color(180, 180, 180);
		tickColor = new Color(108, 187, 252);
	}

}
