package edu.stanford.hci.r3.actions.types;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import edu.stanford.hci.r3.util.graphics.GraphicsUtils;

public class TestPanel extends JPanel {
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		final Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHints(GraphicsUtils.getBestRenderingHints());
		g2d.setColor(Color.ORANGE);
		g2d.drawRect(10, 10, 50, 80);
	}

}
