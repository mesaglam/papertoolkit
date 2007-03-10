package edu.stanford.hci.r3.tools.debugging;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.events.EventHandler;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

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
public class DebugPCanvas extends PCanvas {

	private Color defaultLineColor;
	private PNode sheetContainer;
	private Color defaultFillColor;

	public DebugPCanvas() {
		sheetContainer = new PNode();
		getLayer().addChild(sheetContainer);
		useDefaultTheme();
	}

	public void useDefaultTheme() {
		setBackground(new Color(50, 50, 50));
		defaultLineColor = new Color(240, 240, 240);
		defaultFillColor = new Color(140, 140, 140);
	}

	public void addVisualComponents(Application paperApp) {
		sheetContainer.setPaint(defaultLineColor);

		sheetContainer.setOffset(30, 40);

		List<Sheet> sheets = paperApp.getSheets();
		DebugUtils.println("Number of Sheets: " + sheets.size());

		Sheet sheet = sheets.get(0);

		double wInches = sheet.getWidth().getValueInInches();
		double hInches = sheet.getHeight().getValueInInches();

		double pixelsPerInch = 72;

		PPath sheetRect = new PPath(new Rectangle2D.Double(0, 0, wInches * pixelsPerInch, hInches
				* pixelsPerInch));

		sheetRect.setStrokePaint(defaultLineColor);
		// rectangle.setPaint(defaultLineColor);

		List<Region> regions = sheet.getRegions();
		for (Region r : regions) {
			double xLoc = r.getOriginX().getValueInInches() * pixelsPerInch;
			double yLoc = r.getOriginY().getValueInInches() * pixelsPerInch;

			double rWidth = r.getWidth().getValueInInches() * pixelsPerInch;
			double rHeight = r.getHeight().getValueInInches() * pixelsPerInch;

			PPath regionRect = new PPath(new Rectangle2D.Double(xLoc, yLoc, rWidth, rHeight));
			regionRect.setStrokePaint(defaultLineColor);
			regionRect.setPaint(defaultFillColor);

			sheetRect.addChild(regionRect);

			List<EventHandler> eventHandlers = r.getEventHandlers();

			for (EventHandler eh : eventHandlers) {
				DebugUtils.println(eh.getClass().getName());

				int xOffset = 750;

				PPath connector = new PPath(new Line2D.Double(xLoc + rWidth, yLoc + 20, xOffset,
						yLoc + 20));
				connector.setStrokePaint(defaultLineColor);

				PPath handlerCircle = new PPath(new Ellipse2D.Double(0, 0, 20, 20));
				handlerCircle.setStrokePaint(defaultLineColor);
				handlerCircle.setOffset(xOffset, yLoc + 10);

				PText label = new PText(eh.getClass().getName());
				label.setTextPaint(defaultLineColor);
				label.setOffset(25, 2);

				// or should we add to the regionRect?
				handlerCircle.addChild(label);
				sheetRect.addChild(connector);
				sheetRect.addChild(handlerCircle);
			}

		}

		sheetContainer.addChild(sheetRect);
		repaint();
	}
}
