package edu.stanford.hci.r3.events;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.tools.debug.DebuggingEnvironment;
import edu.stanford.hci.r3.tools.debug.Utils;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * This is the super interface of all the other event handlers. These are the pen & paper analogues
 * to Java Swing's EventListener architecture. We changed it to an abstract class in ver. 0.5, as
 * now handlers should have access to their parent regions.... Event Handlers can be added to
 * multiple regions (is this unique?)
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
	 * This is used in debugging visualizations, for traversing up the list to figure out where the
	 * event handler should be positioned.
	 */
	protected List<Region> parentRegions = new ArrayList<Region>();

	public void addParentRegion(Region r) {
		parentRegions.add(r);
	}

	public List<Region> getParentRegions() {
		return parentRegions;
	}

	/**
	 * if this event should be consumed (i.e., lower priority event handlers should not see this
	 * event), we should set the event.consumed property to true
	 */
	public abstract void handleEvent(PenEvent event);

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
				String filename = "src/"+className.replace('.', '/');
				int innerclass = filename.indexOf('$');
				if (innerclass>=0)
					filename = filename.substring(0,innerclass);
				filename += ".java";
				try {
					int line = ste.getLineNumber()-1;
					List<String> lines = Utils.getLines(new File(filename));
					code = filename+":"+ste.getLineNumber()+"\n";
					for (int i=Math.max(0, line-3); 
							 i<Math.min(line+4, lines.size()-1); 
							 i++)
						code += (i+1)+": "+lines.get(i)+"\n";
					
				} catch (IOException ex) {
					ex.printStackTrace();
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
	 * @return the Event Handler's Name
	 */
	public abstract String toString();

}
