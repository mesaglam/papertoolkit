package edu.stanford.hci.r3.designer.toolbar;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.util.components.ribbons.RibbonPanel;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class UserTestTasks {

	private RibbonPanel recordDataPanel;

	public List<RibbonPanel> getPanels() {
		final List<RibbonPanel> panels = new ArrayList<RibbonPanel>();
		panels.add(getRecordDataPanel());
		return panels;
	}

	private RibbonPanel getRecordDataPanel() {
		if (recordDataPanel == null) {
			recordDataPanel = new RibbonPanel("Record Data");
		}
		return recordDataPanel;
	}

}
