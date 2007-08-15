package papertoolkit.tools.design.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import papertoolkit.events.EventHandler;
import papertoolkit.events.PenEvent;
import papertoolkit.events.PenEventType;
import papertoolkit.paper.Region;
import papertoolkit.pen.PenSample;
import papertoolkit.units.Percentage;
import papertoolkit.units.Pixels;
import papertoolkit.units.coordinates.PercentageCoordinates;


/**
 * This class is used as an emulation layer for Region objects.  It displays 
 * strokes drawn with the pen.  It also handles mouse input and generates the
 * appropriate PenEvents as necessary allowing you to test your application 
 * without using pen and paper.
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author Marcello
 *
 */
public class RegionComponent extends JComponent implements MouseMotionListener, MouseListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8845814285246446932L;
	private Region region;
	
	private List<Shape> inks = new ArrayList<Shape>();
	

	public RegionComponent(Region r) {
		this.region = r;
		setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(120,120,120)));
		addMouseMotionListener(this);
		addMouseListener(this);
		r.addEventHandler(new EventHandler() {

			boolean penDown = false;
			
			double lastX,lastY;
			
			public void handleEvent(PenEvent event) {

				PercentageCoordinates pl = event.getPercentageLocation();

				double x = pl.getPercentageInXDirection() * getWidth() / 100;
				double y = pl.getPercentageInYDirection() * getHeight() / 100;
				
				double force = event.getOriginalSample().getForce() / 128;
				
				System.out.println("x="+x+",y="+y+",force="+force);

				if (event.getOriginalSample().getForce()>0) {
					if (penDown) {
						// Dragged
						add(new Line2D.Double(x,y,lastX,lastY));
					} else {
						penDown = true;
						
						// Pressed
						add(new Line2D.Double(x,y,x,y));
					}
				} else {
					if (penDown) {
						penDown = false;
						// Released
					} else {
						// Moved
					}
				}
				lastX = x;
				lastY = y;
			}

			public String toString() {
				return "[RegionComponent EventHandler]";
			}
		});
	}
	
	/**
	 * Returns the region.
	 * @return
	 */
	public Region getRegion() {
		return region;
	}

	public void mouseDragged(MouseEvent e) {
		fireEvent(getEvent(e.getX(),e.getY(),true,true));
	}
	
	protected void add(Shape s) {
		synchronized (inks) {
			inks.add(s);
		}
		Rectangle r = s.getBounds();
		repaint(r.x-1,r.y-1,r.width+4,r.height+4);
		//repaint();
	}
	
	/**
	 * Draws strokes
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(Color.WHITE);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON);
		synchronized (inks) {
			for (Shape s : inks)
				g2d.draw(s);
		}
	}

	public void mouseMoved(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {
		fireEvent(getEvent(e.getX(),e.getY(),true,false));
	}

	public void mouseReleased(MouseEvent e) {
		fireEvent(getEvent(e.getX(),e.getY(),false,false));
	}
	/**
	 * Converts fraction to a percentage.
	 * @param x
	 * @param max
	 * @return
	 */
	private Percentage getPercentage(double x, double max) {
		return new Percentage(x*100.0/max,new Pixels(max));
	}
	/**
	 * Creates an event based on mouse coordinates relative to the region.
	 * @param x
	 * @param y
	 * @param down
	 * @param sample
	 * @return
	 */
	private PenEvent getEvent(int x, int y, boolean down, boolean sample) {
		long ts = System.currentTimeMillis();
		PenEvent pe = 
			new PenEvent(0,"Swing",ts, new PenSample(x,y,down ? 128 : 0,ts));
		if (!sample)
			pe.setType(down ? PenEventType.DOWN : PenEventType.UP);
			
		pe.setPercentageLocation(
			new PercentageCoordinates(getPercentage(x,getWidth()),
									  getPercentage(y,getHeight())));
		
		return pe;
	}
	
	/**
	 * Fires a PenEvent to all handlers
	 * @param pe
	 */
	private void fireEvent(PenEvent pe) {
		for (EventHandler eh : region.getEventHandlers()) {
			eh.handleEvent(pe);
			if (pe.isConsumed())
				return;
		}
	}

}
