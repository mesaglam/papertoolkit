package papertoolkit.util.components.ribbons;

import static papertoolkit.util.components.ribbons.RibbonConstants.CATEGORY_LABEL_FONT;
import static papertoolkit.util.components.ribbons.RibbonConstants.NOT_SELECTED_COLOR;
import static papertoolkit.util.components.ribbons.RibbonConstants.SELECTED_COLOR;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import papertoolkit.util.graphics.GraphicsUtils;


/**
 * <p>
 * A tab that can be clicked.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * @created Feb 23, 2006
 */
public class RibbonCategoryLabel extends JPanel {

	private boolean isSelected;

	private JLabel labelText;

	private CardLayout layout;

	private String name;

	private RibbonToolbar parentToolbar;

	private JPanel toolPanel;

	/**
	 * @param categoryName
	 * @param toolbar
	 */
	public RibbonCategoryLabel(String categoryName, RibbonToolbar toolbar) {
		parentToolbar = toolbar;
		name = categoryName;
		labelText = new JLabel(name);
		labelText.setFont(CATEGORY_LABEL_FONT);
		add(labelText);
		addMouseListener(getClickListener());
		setOpaque(false);
	}

	/**
	 * @return The Tab to click on. It changes on mousePressed, so it feels more responsive.
	 */
	private MouseAdapter getClickListener() {
		return new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				System.out.println("Clicked on Category Label: " + name);
				if (layout != null) {
					layout.show(toolPanel, name);
					parentToolbar.setSelectedLabel(RibbonCategoryLabel.this);
				}
			}
		};
	}

	/**
	 * Paints a nice gradient like in Office 12. =)
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		final Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHints(GraphicsUtils.getBestRenderingHints());

		// dimensions
		final int w = getWidth();
		final int h = getHeight();

		if (isSelected) {
			g2d.setColor(SELECTED_COLOR);
			g2d.fillRoundRect(0, 0, w, h + 14, 14, 14);
		} else {
			g2d.setColor(NOT_SELECTED_COLOR);
			g2d.fillRoundRect(0, 0, w, h + 14, 14, 14);

		}
	}

	/**
	 * @param cardLayout
	 * @param tools
	 */
	public void setCardLayoutInfo(CardLayout cardLayout, JPanel tools) {
		layout = cardLayout;
		toolPanel = tools;
	}

	/**
	 * 
	 * @param selected
	 */
	public void setSelected(boolean selected) {
		isSelected = selected;

		if (isSelected) {
			labelText.setForeground(Color.BLACK);
		} else {
			labelText.setForeground(Color.GRAY);
		}
		repaint();
	}

}
