package papertoolkit.tools.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import papertoolkit.pen.ink.Ink;

/**
 * <p>
 * An Easy Way to Browse Ink Objects... =D
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class InkFrame extends JFrame {

	private JPanel controlPanel;
	private List<Ink> digitalInk = new ArrayList<Ink>();

	protected InkPanel inkPanel;
	private JButton leftButton;
	private int nextInkIndex = 0;
	private JButton rightButton;

	public InkFrame() {
		setTitle("Digital Ink Viewer");
		setSize(1024, 700);
		setLayout(new BorderLayout());
		add(getInkPanel(), BorderLayout.CENTER);
		add(getControlPanel(), BorderLayout.SOUTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	private void checkAndLoadInk() {
		// wrap
		if (nextInkIndex >= digitalInk.size()) {
			nextInkIndex = 0;
		}
		if (nextInkIndex < 0) {
			nextInkIndex = digitalInk.size() - 1;
		}

		// load the ink into the panel
		inkPanel.addInk(digitalInk.get(nextInkIndex));
	}

	private Component getControlPanel() {
		if (controlPanel == null) {
			controlPanel = new JPanel();
			controlPanel.setLayout(new BorderLayout());

			leftButton = new JButton("Previous");
			leftButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					inkPanel.clear();
					previous();
				}

			});
			rightButton = new JButton("Next");
			rightButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					inkPanel.clear();
					next();
				}

			});

			controlPanel.add(leftButton, BorderLayout.WEST);
			controlPanel.add(getCenterPanel(), BorderLayout.CENTER);
			controlPanel.add(rightButton, BorderLayout.EAST);
		}
		return controlPanel;
	}

	protected Component getCenterPanel() {
		// fill it in with something interesting
		return new JPanel();
	}

	public InkPanel getInkPanel() {
		if (inkPanel == null) {
			inkPanel = new InkPanel();
			inkPanel.setBackground(Color.BLACK);
		}
		return inkPanel;
	}

	private void next() {
		if (digitalInk.size() == 0) {
			return;
		}
		checkAndLoadInk();

		nextInkIndex++;
	}

	private void previous() {
		if (digitalInk.size() == 0) {
			return;
		}
		checkAndLoadInk();

		nextInkIndex--;
	}

	public void setInk(List<Ink> importedInk) {
		nextInkIndex = digitalInk.size();
		for (Ink i : importedInk) {
			digitalInk.add(i);
		}
		next();
	}
}
