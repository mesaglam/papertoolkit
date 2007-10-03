package papertoolkit.events;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import papertoolkit.application.Application;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.tools.debug.DebuggingEnvironment;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.files.FileUtils;

/**
 * <p>
 * This is the super interface of all the other event handlers. These are the pen & paper analogues to Java
 * Swing's EventListener architecture. We changed it to an abstract class in ver. 0.5, as now handlers should
 * have access to their parent regions.... Event Handlers can be added to multiple regions (is this unique?)
 * </p>
 * <p>
 * We have merged Filters into Event Handlers. Filters are both similar to and different from EventHandlers.
 * They are similar in the sense that they can be added to regions. When ink is written on a region, the data
 * will be sent to a filter that is attached to that region.
 * </p>
 * <p>
 * Filters are different that event handlers because they transform incoming pen data into something
 * qualitatively different, instead of just telling a piece of code that an event has arrived. For example,
 * one could implement a filter that inverts pen samples' y value on a page (dunno why, but it's a fun
 * example). Then, you can add an event handler to this filter. The event handler would then get PenEvents
 * with upside down pen samples.
 * </p>
 * <p>
 * A more intricate filter would be an ink container. It can collect all ink that passes through the filter,
 * and tell a handler that new ink has arrived. Whenever the handler needs the ink, it can request it from the
 * ink container. Because of the custom nature of filters, it might be that each filter has a custom event
 * handler. For example, an InkContainer might have an InkContainer listener. We can implement these as inner
 * classes.
 * </p>
 * <p>
 * Filters can change the incoming event stream. For example, it might only care about events that affect
 * certain parts of the region. Events that are not close to these parts will be ignored.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public abstract class EventHandler {

	/**
	 * This is used in debugging visualizations, for traversing up the list to figure out where the event
	 * handler should be positioned.
	 */
	protected List<Region> parentRegions = new ArrayList<Region>();

	/**
	 * @param r
	 */
	public void addParentRegion(Region r) {
		parentRegions.add(r);
	}

	public final String getName() {
		return toString();
	}

	/**
	 * @return
	 */
	public List<Region> getParentRegions() {
		return parentRegions;
	}

	/**
	 * if this event should be consumed (i.e., lower priority event handlers should not see this event), we
	 * should set the event.consumed property to true
	 */
	public abstract void handleEvent(PenEvent event);

	/**
	 * @param message
	 */
	public void showMe(String message) {
		for (Region r : parentRegions) {
			// DebugUtils.println(r);

			Sheet s = r.getParentSheet();
			Application a = s.getParentApplication();
			DebugUtils.println("Hosted in: " + a.getName());

			Thread currThread = Thread.currentThread();
			StackTraceElement[] trace = currThread.getStackTrace();
			String code = null;
			for (StackTraceElement ste : trace) {
				String method = ste.getMethodName();
				String className = ste.getClassName();
				if (method.equals("showMe") || className.startsWith("java."))
					continue;
				String filename = "src/" + className.replace('.', '/');
				int innerclass = filename.indexOf('$');
				if (innerclass >= 0)
					filename = filename.substring(0, innerclass);
				filename += ".java";
				int line = ste.getLineNumber() - 1;
				List<String> lines = FileUtils.readFileIntoLines(new File(filename));
				code = filename + ":" + ste.getLineNumber() + "\n";
				for (int i = Math.max(0, line - 3); i < Math.min(line + 4, lines.size() - 1); i++) {
					code += (i + 1) + ": " + lines.get(i) + "\n";
				}
				break;
			}

			DebuggingEnvironment d = a.getDebuggingEnvironment();
			if (d != null) {
				d.visualize(message, r, code);
			}
		}
	}

	/**
	 * @return the Event Handler's Name, a short CamelCase string describing the purpose of the handler.
	 */
	public abstract String toString();

}
