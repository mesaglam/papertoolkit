package papertoolkit.util.components.ribbons;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import papertoolkit.util.graphics.GraphicsUtils;
import papertoolkit.util.layout.StackedLayout;


/**
 * <p>
 * Intended to provide a context sensitive menu/toolbar like MS Office 2007. The software should be
 * able to pick a tab based on what you are doing. The developer must call the right functions to
 * show the right tabs (this class has no smarts).
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * @created Feb 16, 2006
 */
public class RibbonToolbar extends JPanel implements RibbonConstants {

	/**
	 * 
	 */
	private final CardLayout cardLayout = new CardLayout();

	/**
	 * Put the categories here...
	 */
	private JPanel categories;

	/**
	 * Enables us to index the JPanel by the category name.
	 */
	private HashMap<String, JPanel> categoriesToPanels = new HashMap<String, JPanel>();

	/**
	 * 
	 */
	private List<RibbonCategoryLabel> categoryTabList = new ArrayList<RibbonCategoryLabel>();

	/**
	 * These tools can appear or disappear based on context.
	 */
	private List<RibbonPanel> tools = new ArrayList<RibbonPanel>();

	/**
	 * Put the ribbon tool panels here...
	 */
	private JPanel toolsPanel;

	/**
	 * 
	 */
	public RibbonToolbar() {
		setLayout(new BorderLayout());
		add(getCategories(), BorderLayout.NORTH);
		add(getTools(), BorderLayout.CENTER);
	}

	/**
	 * @param categoryNames
	 */
	public void addCategories(String... categoryNames) {
		for (String name : categoryNames) {
			addCategory(name);
		}
	}

	/**
	 * @param categoryName
	 * 
	 * @created Feb 23, 2006
	 * @author Ron Yeh
	 */
	public void addCategory(String categoryName) {
		// the clickable label at the top
		final RibbonCategoryLabel labelledTab = new RibbonCategoryLabel(categoryName, this);
		categories.add(labelledTab, "LeftTall");
		categoryTabList.add(labelledTab);

		// select the first one!
		if (categoryTabList.size() == 1) {
			labelledTab.setSelected(true);
		} else {
			labelledTab.setSelected(false);
		}

		final JPanel categoryPanel = getNewCategoryPanel();

		// put it into the card panel
		getTools().add(categoryName, categoryPanel);
		labelledTab.setCardLayoutInfo(cardLayout, getTools());

		// the corresponding panel of tools
		categoriesToPanels.put(categoryName, categoryPanel);
	}

	/**
	 * @param panelName
	 * @param panels
	 */
	public void addToolsToCategoryPanel(String panelName, List<RibbonPanel> panels) {
		for (RibbonPanel rp : panels) {
			addToolToCategoryPanel(panelName, rp);
		}
	}

	/**
	 * @param categoryName
	 * @param rp
	 * 
	 * @created Feb 23, 2006
	 * @author Ron Yeh
	 */
	private void addToolToCategoryPanel(String categoryName, RibbonPanel rp) {
		final JPanel panel = categoriesToPanels.get(categoryName);
		// System.out.println(categoriesToPanels.keySet());
		panel.add(rp);
		panel.invalidate();
	}

	/**
	 * @return
	 * 
	 * @created Feb 23, 2006
	 * @author Ron Yeh
	 */
	private Component getCategories() {
		if (categories == null) {
			categories = new JPanel() {
				/**
				 * the background gradient. Is it OK to just have one for all of the categories?
				 */
				final GradientPaint gradient = new GradientPaint(0, 0, new Color(202, 198, 195),
						getWidth(), 0, new Color(234, 234, 234), false);

				/**
				 * Paints a nice gradient like in Office 12. =)
				 */
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					final Graphics2D g2d = (Graphics2D) g;
					g2d.setRenderingHints(GraphicsUtils.getBestRenderingHints());
					g2d.setPaint(gradient);
					g2d.fillRect(0, 0, getWidth(), getHeight());
				}

			};
			categories.setLayout(new StackedLayout(StackedLayout.HORIZONTAL));
		}
		return categories;
	}

	/**
	 * @return a new JPanel that serves as one card.
	 * 
	 * @created Feb 23, 2006
	 * @author Ron Yeh
	 */
	private JPanel getNewCategoryPanel() {
		final JPanel jp = new JPanel() {

			/**
			 * Paints a nice gradient like in Office 12. =)
			 */
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				final Graphics2D g2d = (Graphics2D) g;

				g2d.setColor(HEADER_COLOR);
				g2d.fillRect(0, 0, getWidth(), HEADER_HEIGHT);

				// paint the background panel gradient
				final GradientPaint midToBottomGradient = new GradientPaint(0, HEADER_HEIGHT,
						PANEL_BEGIN_COLOR, 0, getHeight(), PANEL_END_COLOR, false);

				g2d.setPaint(midToBottomGradient);
				g2d.fillRect(0, HEADER_HEIGHT, getWidth(), getHeight());
			}
		};

		jp.setLayout(new BoxLayout(jp, BoxLayout.X_AXIS));
		return jp;
	}

	/**
	 * @return
	 * 
	 * @created Feb 23, 2006
	 * @author Ron Yeh
	 */
	private JPanel getTools() {
		if (toolsPanel == null) {
			toolsPanel = new JPanel();
			toolsPanel.setLayout(cardLayout);
		}
		return toolsPanel;
	}

	/**
	 * @param rp
	 * 
	 * @created Feb 16, 2006
	 * @author Ron Yeh
	 */
	public void registerTool(RibbonPanel rp) {
		tools.add(rp);
	}

	/**
	 * @param labelToSelect
	 * @created Mar 31, 2006
	 * @author Ron Yeh
	 */
	public void setSelectedLabel(RibbonCategoryLabel labelToSelect) {
		for (RibbonCategoryLabel tab : categoryTabList) {
			if (tab.equals(labelToSelect)) {
				tab.setSelected(true);
			} else {
				tab.setSelected(false);
			}
		}
	}
}
