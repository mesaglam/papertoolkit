package papertoolkit.tools.design.swing;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import papertoolkit.events.EventHandler;
import papertoolkit.events.PenEvent;
import papertoolkit.events.PenEventType;
import papertoolkit.events.handlers.ClickHandler.ClickAdapter;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.paper.regions.XMLRegion;
import papertoolkit.pen.PenSample;
import papertoolkit.util.graphics.GraphicsUtils;

/**
 * A JFrame based on a Sheet object. This allows you to quickly generate a Swing interface from an existing
 * paper interface.
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author Marcello
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class SheetFrame extends JFrame {

	/**
	 * Constructs a new JFrame based on a Sheet object. If the sheet has XMLRegions, the meta data will be
	 * used to construct JButtons, and JTextFields.
	 * 
	 * @param sheet
	 * @param width
	 * @param height
	 */
	public SheetFrame(Sheet sheet, int width, int height) {
		super(sheet.getName());

		double scale = GraphicsUtils.getScaleToFitFirstBoxInSecond(sheet.getWidth().getValue(), sheet
				.getHeight().getValue(), width, height);

		JPanel panel = new JPanel(null);
		panel.setPreferredSize(new Dimension((int) (sheet.getWidth().getValue() * scale), (int) (sheet
				.getHeight().getValue() * scale)));
		getContentPane().add(panel);
		pack();

		setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		// Loop through regions
		for (Region r : sheet.getRegions()) {
			JComponent c = null;

			// Find regions with meta data
			if (r instanceof XMLRegion) {
				final XMLRegion xr = (XMLRegion) r;
				String type = xr.getType();
				if (type != null) {
					if (type.equals("click")) {
						JButton button = new JButton(xr.getName());
						button.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								long ts = System.currentTimeMillis();
								for (EventHandler eh : xr.getEventHandlers()) {
									if (eh instanceof ClickAdapter)
										((ClickAdapter) eh).clicked(new PenEvent("0_Simulated", "swing", new PenSample(0,
												0, 128, ts), PenEventType.DOWN, true));
								}
							}
						});
						c = button;
					} else if (type.equals("handwriting")) {
						JTextField tf = new JTextField();
						tf.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								// TODO: Gotta figure out what happens here
							}
						});
						c = tf;
					}
				}
			}
			// Otherwise use a default component
			if (c == null) {
				c = new RegionComponent(r);
			}

			// Add it
			panel.add(c);
			// Set the bounds
			c.setBounds((int) (r.getOriginX().getValue() * scale), (int) (r.getOriginY().getValue() * scale),
					(int) (r.getWidth().getValue() * scale), (int) (r.getHeight().getValue() * scale));
			setVisible(true);
		}
	}

}
