package edu.stanford.hci.r3.events.replay;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.filter.VisibilityFilter;
import prefuse.action.layout.AxisLayout;
import prefuse.data.Table;
import prefuse.data.expression.AndPredicate;
import prefuse.data.query.RangeQueryBinding;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.AxisRenderer;
import prefuse.render.Renderer;
import prefuse.render.RendererFactory;
import prefuse.render.ShapeRenderer;
import prefuse.visual.VisualItem;
import prefuse.visual.VisualTable;
import prefuse.visual.expression.VisiblePredicate;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.events.EventType;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * Allows developers to load a saved event stream (either from batched or streaming modes) and
 * replay them. You can also load ALL events, and browse through them.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class EventBrowser {

	/**
	 * Folder where the event data is stored.
	 */

	/**
	 * The event data we read in.
	 */

	private EventBrowserView view;

	private Visualization vis;

	private Table dataTable;

	private Display display;

	/**
	 * HSQLDB access, perhaps.
	 */

	private static final String penEventsGroup = "PenEventsGroup";

	public EventBrowser() {
		vis = new Visualization();
		VisualTable vt = vis.addTable(penEventsGroup, getPenEventsTable());
		vis.setRendererFactory(new RendererFactory() {
			AbstractShapeRenderer sr = new ShapeRenderer();

			Renderer arY = new AxisRenderer(Constants.RIGHT, Constants.TOP);

			Renderer arX = new AxisRenderer(Constants.CENTER, Constants.FAR_BOTTOM);

			public Renderer getRenderer(VisualItem item) {
				return item.isInGroup("ylab") ? arY : item.isInGroup("xlab") ? arX : sr;
			}
		});

		// --------------------------------------------------------------------
		// STEP 2: create actions to process the visual data

		// set up dynamic queries, search set
		RangeQueryBinding eventsQuery = new RangeQueryBinding(vt, "Timestamp");

        AxisLayout yaxis = new AxisLayout("Events Group", "Timestamp",
                Constants.Y_AXIS, VisiblePredicate.TRUE);

		
        AndPredicate filter = new AndPredicate(eventsQuery.getPredicate());

        
        ActionList update = new ActionList();
        update.add(new VisibilityFilter(penEventsGroup, filter));
        update.add(yaxis);
        update.add(new RepaintAction());
        vis.putAction("update", update);

		
		display = new Display(vis);
		view = new EventBrowserView(display);
	}

	/**
	 * @return
	 */
	private Table getPenEventsTable() {
		if (dataTable == null) {
			dataTable = new Table();
			dataTable.addColumn("Timestamp", Long.class);
			dataTable.addColumn("Type", EventType.class);

			importData(dataTable);
			
			final int numRows = dataTable.getRowCount();
			final int numCols = dataTable.getColumnCount();
			for (int r = 0; r < numRows; r++) {
				StringBuilder sb = new StringBuilder();
				for (int c = 0; c < numCols; c++) {
					sb.append(dataTable.get(r, c) + "\t");
				}
				DebugUtils.println(sb);
			}
		}
		return dataTable;
	}

	private void importData(Table data) {
		data.addRows(1);
		data.set(0, 0, 123L);
		data.set(0, 1, EventType.STREAMING);
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		PaperToolkit.initializeLookAndFeel();
		new EventBrowser();
	}
}
