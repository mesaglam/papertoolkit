package papertoolkit.tools.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class InkClustersFrame extends InkFrame {

	private JPanel centerPanel;
	private JRadioButton timeSimpleClusterButton;
	private JRadioButton spaceSimpleClusterButton;
	private JTextField threshOutput;
	private Timer delayedClusterTimer;
	private TimerTask clusterTask;
	private JSlider threshSlider;

	public InkClustersFrame() {
	}

	private static final int MAX_TIME_THRESH = 5000;
	private static final int DEFAULT_TIME_THRESH = 150;

	protected Component getCenterPanel() {
		if (centerPanel == null) {
			centerPanel = new JPanel();
			centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

			timeSimpleClusterButton = new JRadioButton("TimeThreshold");
			timeSimpleClusterButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					threshSlider.setMinimum(0);
					threshSlider.setMaximum(MAX_TIME_THRESH); // milliseconds
					threshSlider.setMajorTickSpacing(1000);
					threshSlider.setValue(DEFAULT_TIME_THRESH);
				}
			});

			spaceSimpleClusterButton = new JRadioButton("SpaceThreshold");
			spaceSimpleClusterButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					threshSlider.setMinimum(0);
					threshSlider.setMaximum(100); // pixels
					threshSlider.setMajorTickSpacing(20);
					threshSlider.setValue(20);
				}
			});

			ButtonGroup group = new ButtonGroup();
			group.add(timeSimpleClusterButton);
			group.add(spaceSimpleClusterButton);

			centerPanel.add(timeSimpleClusterButton);
			centerPanel.add(spaceSimpleClusterButton);


			threshSlider = new JSlider(JSlider.HORIZONTAL, 0, MAX_TIME_THRESH, DEFAULT_TIME_THRESH);
			threshSlider.setMajorTickSpacing(1000);
			threshSlider.setPaintTicks(true);
			threshSlider.setPaintLabels(true);
			threshSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					threshOutput.setText(threshSlider.getValue() + "");

					// if there's a timer, cancel it...
					if (delayedClusterTimer != null) {
						delayedClusterTimer.cancel();
						delayedClusterTimer = null;
					}
					delayedClusterTimer = new Timer();
					clusterTask = new TimerTask() {
						public void run() {
							// switch on the cluster type
							if (spaceSimpleClusterButton.isSelected()) {
								getInkPanel().clusterInSpaceWithSimpleThreshold(threshSlider.getValue());
							} else if (timeSimpleClusterButton.isSelected()) {
								getInkPanel().clusterInTimeWithSimpleThreshold(threshSlider.getValue());
							}
						}
					};
					delayedClusterTimer.schedule(clusterTask, 250L); // a bit later...
				}
			});

			centerPanel.add(threshSlider);

			threshOutput = new JTextField(6);
			threshOutput.setEditable(false);
			threshOutput.setText(threshSlider.getValue() + "");
			centerPanel.add(threshOutput);
			
			timeSimpleClusterButton.setSelected(true);
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

}
