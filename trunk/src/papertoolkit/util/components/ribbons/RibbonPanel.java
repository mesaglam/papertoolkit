package papertoolkit.util.components.ribbons;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import papertoolkit.util.graphics.GraphicsUtils;
import papertoolkit.util.layout.SpringLayoutUtils;


/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * @created Feb 16, 2006
 */
public class RibbonPanel extends JPanel implements RibbonConstants {

	/**
	 * 
	 */
	private String displayName;

	/**
	 * 
	 */
	private JPanel toolPanel;

	/**
	 * @param name
	 */
	public RibbonPanel(String name) {
		displayName = name;
		super.setLayout(new BorderLayout());
		super.add(getToolPanel(), BorderLayout.CENTER);
		super.add(new RibbonPanelHeader(displayName), BorderLayout.SOUTH);
		setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 1));
		setBackground(Color.GRAY);
	}

	/**
	 * Re-directs the call to the toolPanel
	 * 
	 * @see java.awt.Container#add(java.awt.Component)
	 */
	public Component add(Component comp) {
		return getToolPanel().add(comp);
	}

	/**
	 * Re-directs the call to the toolPanel
	 * 
	 * @see java.awt.Container#add(java.awt.Component, int)
	 */
	public Component add(Component comp, int index) {
		return getToolPanel().add(comp, index);
	}

	/**
	 * Re-directs the call to the toolPanel
	 * 
	 * @see java.awt.Container#add(java.awt.Component, java.lang.Object)
	 */
	public void add(Component comp, Object constraints) {
		getToolPanel().add(comp, constraints);
	}

	/**
	 * Re-directs the call to the toolPanel
	 * 
	 * @see java.awt.Container#add(java.awt.Component, java.lang.Object, int)
	 */
	public void add(Component comp, Object constraints, int index) {
		getToolPanel().add(comp, constraints, index);
	}

	/**
	 * @return
	 * 
	 * @created Feb 16, 2006
	 * @author Ron Yeh
	 */
	public JPanel getToolPanel() {
		if (toolPanel == null) {
			toolPanel = new JPanel() {
				private final GradientPaint gradient = new GradientPaint(0, 0, PANEL_BEGIN_COLOR, 0,
						getHeight(), PANEL_END_COLOR, false);

				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					final Graphics2D g2d = (Graphics2D) g;
					g2d.setRenderingHints(GraphicsUtils.getBestRenderingHints());
					// paint the background gradient
					g2d.setPaint(gradient);
					g2d.fillRect(0, 0, getWidth(), getHeight());
				}
			};
			toolPanel.setLayout(new SpringLayout());
		}
		return toolPanel;
	}

	/**
	 * Lays out a panel using SpringLayout.
	 * 
	 * @param rows
	 * @param cols
	 */
	public void layoutComponents(int rows, int cols) {
		SpringLayoutUtils.makeCompactGrid(getToolPanel(), rows, cols, 5, 5, 5, 5);
	}

	/**
	 * Default Layout. One Row.
	 */
	public void layoutComponents() {
		SpringLayoutUtils.makeCompactGrid(getToolPanel(), 1, toolPanel.getComponentCount(), 5, 5, 5, 5);
	}
}
