package papertoolkit.tools.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import papertoolkit.PaperToolkit;
import papertoolkit.pen.ink.Ink;
import papertoolkit.pen.synch.PenSynch;
import papertoolkit.pen.synch.PenSynchManager;
import papertoolkit.util.DebugUtils;

public class InkRendererFrame extends InkFrame {

	public static void main(String[] args) {

		PaperToolkit.initializeLookAndFeel();

		// read in batched data...
		// (get most recent synch)
		PenSynchManager synchManager = new PenSynchManager();
		PenSynch penSynch = synchManager.getMostRecentPenSynch();
		List<Ink> importedInk = penSynch.getImportedInk();

		for (Ink ink : importedInk) {
			DebugUtils.println(ink.getNumStrokes());
		}

		InkFrame inkFrame = new InkRendererFrame();
		inkFrame.setInk(importedInk);
	}

	private JRadioButton catmullRenderingButton;
	private JPanel centerPanel;
	private JCheckBox debugButton;
	private JRadioButton hybridRenderingButton;

	private JRadioButton linearRenderingButton;
	private JRadioButton quadraticRenderingButton;

	public InkRendererFrame() {

	}

	protected Component getCenterPanel() {
		if (centerPanel == null) {
			centerPanel = new JPanel();
			centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

			debugButton = new JCheckBox("Show Pen Samples");
			debugButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					updateDebugFlag();
					getInkPanel().repaint();
				}

			});

			hybridRenderingButton = new JRadioButton("Hybrid");
			hybridRenderingButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getInkPanel().useHybridRendering();
					updateDebugFlag();
					getInkPanel().repaint();
				}
			});

			catmullRenderingButton = new JRadioButton("Catmull-Rom");
			catmullRenderingButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getInkPanel().useCatmullRomRendering();
					updateDebugFlag();
					getInkPanel().repaint();
				}
			});

			linearRenderingButton = new JRadioButton("Linear");
			linearRenderingButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getInkPanel().useLinearRendering();
					updateDebugFlag();
					getInkPanel().repaint();
				}
			});

			quadraticRenderingButton = new JRadioButton("Quadratic Bezier");
			quadraticRenderingButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getInkPanel().useQuadraticRendering();
					updateDebugFlag();
					getInkPanel().repaint();
				}
			});

			ButtonGroup group = new ButtonGroup();
			group.add(hybridRenderingButton);
			group.add(catmullRenderingButton);
			group.add(linearRenderingButton);
			group.add(quadraticRenderingButton);

			centerPanel.add(debugButton);

			centerPanel.add(hybridRenderingButton);
			centerPanel.add(catmullRenderingButton);
			centerPanel.add(linearRenderingButton);
			centerPanel.add(quadraticRenderingButton);

			catmullRenderingButton.setSelected(true);
		}
		return centerPanel;
	}

	public InkClustersPanel getInkPanel() {
		if (inkPanel == null) {
			inkPanel = new InkClustersPanel();
			inkPanel.setBackground(Color.BLACK);
		}
		return (InkClustersPanel) inkPanel;
	}

	private void updateDebugFlag() {
		getInkPanel().setDebugRendering(debugButton.isSelected());
	}
}
