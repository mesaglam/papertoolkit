package edu.stanford.hci.r3.tools.design.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;

import edu.stanford.hci.r3.events.EventHandler;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.events.handlers.ClickAdapter;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.paper.regions.XMLRegion;
import edu.stanford.hci.r3.pen.PenSample;
import edu.stanford.hci.r3.tools.design.util.Regions;

/**
 * 
 * @author Marcello
 */
public class SheetFrame extends JFrame {
	
	private static final long serialVersionUID = 8854448449113342296L;

	/**
	 * Constructs a new JFrame based on a Sheet object.  If the sheet has 
	 * XMLRegions, the meta data will be used to construct JButtons, and 
	 * JTextFields.
	 * @param sheet
	 * @param width
	 * @param height
	 */
	public SheetFrame(Sheet sheet, int width, int height) {
		super(sheet.getName());
		
		double scale = Regions.makeItFit(sheet.getWidth().getValue(), 
										 sheet.getHeight().getValue(), 
										 width, 
										 height);

		setSize((int)(sheet.getWidth().getValue()  * scale),
				(int)(sheet.getHeight().getValue() * scale));
		setLayout(null);
		
		// Loop through regions
		for (Region r : sheet.getRegions()) {
			JComponent c = null;
			
			// Find regions with meta data
			if (r instanceof XMLRegion) {
				final XMLRegion xr = (XMLRegion)r;
				String type = xr.getType();
				if (type!=null) {
					if (type.equals("click")) {
						JButton button = new JButton(xr.getName());
						button.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								long ts = System.currentTimeMillis();
								for (EventHandler eh : xr.getEventHandlers()) {
									if (eh instanceof ClickAdapter) 
										((ClickAdapter)eh).clicked(
												new PenEvent(0,"swing",ts,
													new PenSample(0,0,128,ts)));
								}
							}
						});
						c = button;
					} else if (type.equals("handwriting")) {
						JTextField tf = new JTextField();
						c = tf;
					}
				}
			}
			// Otherwise use a default component
			if (c==null)
				c = new RegionComponent(r);
			
			// Add it
			add(c);
			// Set the bounds
			c.setBounds((int)(r.getOriginX().getValue() * scale), 
						(int)(r.getOriginY().getValue() * scale),
						(int)(r.getWidth().getValue() * scale),
						(int)(r.getHeight().getValue() * scale));
		}
	}

}
