package edu.stanford.hci.r3.events.replay;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import prefuse.Display;
import prefuse.data.query.NumberRangeModel;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.WindowUtils;
import edu.stanford.hci.r3.util.components.JRangeSlider;

/**
 * <p>
 * The GUI for the Event Browser. It contains a JFrame with some cool visualizations that allow us
 * to
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class EventBrowserView extends JFrame {

	private JPanel commandButtonsPanel;

	private JPanel eventSliderPanel;

	private JPanel eventVizView;

	private BoundedRangeModel rangeModel;

	private JRangeSlider rangeSlider;

	private Display display;

	public EventBrowserView(Display disp) {
		display = disp;
		setTitle("Event Browser");
		setSize(800, 320);
		setupComponents();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(WindowUtils.getWindowOrigin(this, WindowUtils.DESKTOP_CENTER));
		setVisible(true);
	}

	private Component getCommandButtons() {
		if (commandButtonsPanel == null) {
			commandButtonsPanel = new JPanel();
			commandButtonsPanel.add(new JButton("Import"));
			commandButtonsPanel.add(new JButton("Export"));
			commandButtonsPanel.add(new JButton("Play"));
		}
		return commandButtonsPanel;
	}

	private Component getEventSliderPanel() {
		if (eventSliderPanel == null) {
			eventSliderPanel = new JPanel();
			eventSliderPanel.setLayout(new BorderLayout());
			eventSliderPanel.add(getEventVizView(), BorderLayout.SOUTH);
			eventSliderPanel.add(getRangeSlider(), BorderLayout.SOUTH);
		}
		return eventSliderPanel;
	}

	private Component getEventVizView() {
		if (eventVizView == null) {
			eventVizView = new JPanel();
			eventVizView.setLayout(new BorderLayout());
			eventVizView.add(display, BorderLayout.CENTER);
		}
		return eventVizView;
	}

	private BoundedRangeModel getRangeModel() {
		if (rangeModel == null) {
			rangeModel = new NumberRangeModel(0, 1000, 0, 1000);
		}
		return rangeModel;
	}

	private Component getRangeSlider() {
		if (rangeSlider == null) {
			rangeSlider = new EventSlider(getRangeModel(), JRangeSlider.HORIZONTAL,
					JRangeSlider.LEFTRIGHT_TOPBOTTOM);
			rangeSlider.setBackground(Color.DARK_GRAY);
			rangeSlider.setForeground(new Color(202, 217, 253));
			rangeSlider.setThumbColor(new Color(249, 248, 244));
			rangeSlider.setMinExtent(1); // between the two handles
			rangeSlider.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					DebugUtils.println("Pressed");
				}

				public void mouseReleased(MouseEvent e) {
					DebugUtils.println("Released");
				}
			});

		}
		return rangeSlider;
	}

	private void setupComponents() {
		final Container contentPane = getContentPane();
		contentPane.add(getEventSliderPanel(), BorderLayout.CENTER);
		contentPane.add(getCommandButtons(), BorderLayout.SOUTH);
	}
}
