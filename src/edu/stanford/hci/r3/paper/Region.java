package edu.stanford.hci.r3.paper;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.events.ContentFilter;
import edu.stanford.hci.r3.events.EventHandler;
import edu.stanford.hci.r3.render.RegionRenderer;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.util.graphics.GraphicsUtils;

/**
 * <p>
 * A region is defined by a Shape that outlines the region. Internally, the region remembers the
 * Units that it is represented in. Thus, you may use whatever units pleases you.
 * </p>
 * <p>
 * Also, a region can be INPUT(REALTIME|BATCH), OUTPUT(REALTIME|INTERMITTENT), or STATIC. This is
 * entirely determined by the type(s) of attachments (input event handlers, input filters, realtime
 * outputs, print output) you add to a region. If you do not add anything, it is by default a STATIC
 * region. For example, if you add an input event handler for a streaming pen, it becomes an
 * INPUT(REALTIME) region. Regions that handle interactive input will automatically be overlaid with
 * pattern when rendered to PDF or to a printer.
 * </p>
 * <p>
 * Regardless of whether the Shape is closed or not, we assume that all regions are closed Shapes. A
 * rectangular region is represented using this class's Region(4 args) constructors.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Region {

	private static final Color LIGHT_LIGHT_GRAY = new Color(240, 240, 240);

	/**
	 * String constant for a warning message.
	 */
	private static final String WARNING_POST = " (see Region.java)";

	/**
	 * String constant for a warning message.
	 */
	private static final String WARNING_PRE = "WARNING: Using Default Renderer for ";

	/**
	 * If the region is active (i.e., NOT STATIC), we will overlay pattern over it.
	 */
	private boolean active = false;

	/**
	 * Filters events and passes them to other event handlers (which are usually customized to the
	 * event filter)...
	 */
	private List<ContentFilter> contentFilters = new ArrayList<ContentFilter>();

	/**
	 * All Regions can have event handlers that listen for pen events. If the event handler list is
	 * non empty, the region should also be set to active. We can do this automatically. If the
	 * region is not set to active, no pattern will be rendered when a renderer processes this
	 * region.
	 */
	private List<EventHandler> eventHandlers = new ArrayList<EventHandler>();

	/**
	 * If we want to have a fully transparent background for this Rectangular Region, we will set
	 * the opacity to 0.0 (default). Otherwise, we will fill it with this color, with the correct
	 * alpha (set opacity to > 0.0).
	 */
	private Color fillColor = LIGHT_LIGHT_GRAY;

	/**
	 * The name of the region (e.g., Public/Private Button). Useful for debugging. Initialized with
	 * a simple default.
	 */
	private String name;

	/**
	 * Should range from 0.0 to 1.0. 1.0 means the fillColor will be fully opaque. Any smaller means
	 * that it will be translucent. 0.0 means that you will not see the fillColor. By Default,
	 * regions are totally transparent. A good practice would be to set the region to 66% opacity
	 * with a fill. This means that you will usually lighten up the background image so that you can
	 * layer pattern on top of it.
	 */
	private double opacity = 0;

	/**
	 * This is used only to interpret the shape's true physical size. The value of the units object
	 * doesn't matter. Only the type of the unit matters.
	 */
	protected Units referenceUnits;

	/**
	 * Internal horizontal scale of the region. When rendering, we will multiply the shape by this
	 * scale. This is only a RECOMMENDATION and not a requirement of the renderer, however, as some
	 * regions may not make sense if scaled after the fact. However, we will try to make sure most
	 * of our calculations respect this scaling factor.
	 */
	protected double scaleX = 1.0;

	/**
	 * Internal vertical scale of the region. When rendering, we will multiply the shape by this
	 * scale.
	 */
	protected double scaleY = 1.0;

	/**
	 * This is the shape of the region. It is stored as unscaled coordinates, to be interpreted by
	 * the referenceUnits object. Upon rendering, client code SHOULD but is not REQUIRED to respect
	 * the scaleX and scaleY parameters to adjust the width and height.
	 */
	private Shape shape;

	/**
	 * If we are rendering region outlines, we will render the stroke color in this color.
	 */
	private Color strokeColor = Color.BLACK;

	/**
	 * By default, regions are visible (they tend to be images, pattern, etc). However, if you would
	 * like to create an invisible region, go ahead. We're not gonna stop you.=)
	 */
	private boolean visible = true;

	/**
	 * For our American friends.
	 * 
	 * @param xInches
	 * @param yInches
	 * @param wInches
	 * @param hInches
	 */
	public Region(String name, double xInches, double yInches, double wInches, double hInches) {
		this(name, new Rectangle2D.Double(xInches, yInches, wInches, hInches), Inches.ONE);
	}

	/**
	 * 
	 * @param name
	 * @param s
	 *            the shape that defines this region.
	 * @param u
	 *            the reference unit for interpreting the shape's coordinates
	 */
	public Region(String theName, Shape s, Units u) {
		shape = s;
		referenceUnits = u;
		name = theName;
	}

	/**
	 * A protected constructor so subclasses can assign the shape whichever way they please. A
	 * Region doesn't really make sense without a shape, so use this constructor carefully (i.e.,
	 * make sure to assign a sensible shape).
	 * 
	 * @param u
	 */
	protected Region(String theName, Units u) {
		referenceUnits = u;
		name = theName;
	}

	/**
	 * We will convert all the units to x's type. Thus, if you pass in an Inch, Centimeter, Foot,
	 * Points, we will convert everything to Inches. It's OK to keep the x object around, because we
	 * only use it to interpret the shape object.
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public Region(String name, Units x, Units y, Units w, Units h) {
		this(name, new Rectangle2D.Double(x.getValue(), y.getValueIn(x), // assume a Rectangle2D
				w.getValueIn(x), h.getValueIn(x)), x);
	}

	/**
	 * A Content Filter will grab input and "change" the meaning of the input in some way. The
	 * canonical example is the HandwritingRecognizer, as it will collect ink and then change it
	 * into ASCII text.
	 * 
	 * @param filter
	 */
	public void addContentFilter(ContentFilter filter) {
		contentFilters.add(filter);
		active = true;
	}

	/**
	 * Keeps track of this event handler. The PaperToolkit will dispatch events to these, whenever
	 * the event deals with this region.
	 * 
	 * @param handler
	 */
	public void addEventHandler(EventHandler handler) {
		eventHandlers.add(handler);
		
		// tell the event handler that we are one of its parent regions
		// this allows code in event handling to determine which regions it might affect, at runtime! :)
		handler.addParentRegion(this);
		
		active = true;
	}

	/**
	 * @return
	 */
	public List<ContentFilter> getEventFilters() {
		return contentFilters;
	}

	/**
	 * @return the event engine will access the event handlers, to invoke events.
	 */
	public List<EventHandler> getEventHandlers() {
		return eventHandlers;
	}

	/**
	 * @return
	 */
	public Color getFillColor() {
		return fillColor;
	}

	/**
	 * @return the height of the region (or its rectangular bounding box)
	 */
	public Units getHeight() {
		return referenceUnits.getUnitsObjectOfSameTypeWithValue(shape.getBounds2D().getHeight()
				* scaleY);
	}

	/**
	 * @return
	 */
	public String getIsActiveString() {
		return " [" + (isActive() ? "ACTIVE" : "STATIC") + "]";
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return how opaque this region's background will be. If it's 0, we will not render the
	 *         background fillColor at all.
	 */
	public double getOpacity() {
		return opacity;
	}

	/**
	 * @return
	 */
	public Units getOriginX() {
		return referenceUnits.getUnitsObjectOfSameTypeWithValue(shape.getBounds2D().getX());
	}

	/**
	 * @return
	 */
	public Units getOriginY() {
		return referenceUnits.getUnitsObjectOfSameTypeWithValue(shape.getBounds2D().getY());
	}

	/**
	 * Subclasses should override this, to customize rendering. Otherwise, you'll just get gray
	 * boxes, which is what RegionRenderer does.
	 * 
	 * @return the renderer for this region
	 */
	public RegionRenderer getRenderer() {
		// subclasses should override this method
		// otherwise, you will get a warning
		if (!getClass().getSimpleName().equals("Region")) {
			System.out.println(WARNING_PRE + this + WARNING_POST);
		}
		return new RegionRenderer(this);
	}

	/**
	 * @return
	 */
	public double getScaleX() {
		return scaleX;
	}

	/**
	 * @return
	 */
	public double getScaleY() {
		return scaleY;
	}

	/**
	 * Subclasses can modify the internal shape object.
	 * 
	 * @return the internal shape, at your peril.
	 */
	public Shape getShape() {
		return shape;
	}

	public Color getStrokeColor() {
		return strokeColor;
	}

	/**
	 * TODO: We should make sure that Units objects immutable if possible.
	 * 
	 * @return a pointer to the actual units object.
	 */
	public Units getUnits() {
		return referenceUnits;
	}

	/**
	 * @return a bounds object that represents the UNSCALED internal shape.
	 */
	public Rectangle2D getUnscaledBounds2D() {
		return shape.getBounds2D();
	}

	/**
	 * @return
	 */
	public Units getUnscaledBoundsHeight() {
		return referenceUnits.getUnitsObjectOfSameTypeWithValue(shape.getBounds2D().getHeight());
	}

	/**
	 * @return
	 */
	public Units getUnscaledBoundsWidth() {
		return referenceUnits.getUnitsObjectOfSameTypeWithValue(shape.getBounds2D().getWidth());
	}

	/**
	 * @return a copy of the internal shape as a Java2D GeneralPath. You should use this with
	 *         getScaleX/Y to determine the true shape. Alternatively, use getScaledShapeCopy()
	 */
	public Shape getUnscaledShapeCopy() {
		return new GeneralPath(shape);
	}

	/**
	 * @return the width of the region (or its rectangular bounding box).
	 */
	public Units getWidth() {
		return referenceUnits.getUnitsObjectOfSameTypeWithValue(shape.getBounds2D().getWidth()
				* scaleX);
	}

	/**
	 * @return if this region is an active (NON-STATIC) region. This means that upon rendering to
	 *         PDF/Printer, pattern will be displayed over this region.
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @return whether the region will be visible. TODO: The renderer SHOULD respect this flag.
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * For De-Serialization through XStream (just like in Java deserialization), we need to fill in
	 * some fields, especially if they are null due to old serialized versions, or because of
	 * transient variables.
	 * 
	 * For fields that are unexpectedly null, signal a warning.
	 * 
	 * @return
	 */
	private Object readResolve() {
		if (eventHandlers == null) {
			System.err.println("Region.java:: [" + getName()
					+ "]'s eventHandlers list was unexpectedly null upon "
					+ "deserialization with XStream. Perhaps you need to "
					+ "reserialize your Regions?");
			eventHandlers = new ArrayList<EventHandler>();
		}
		if (contentFilters == null) {
			System.err.println("Region.java:: [" + getName()
					+ "]'s eventFilters list was unexpectedly null upon "
					+ "deserialization with XStream. Perhaps you need to "
					+ "reserialize your Regions?");
			contentFilters = new ArrayList<ContentFilter>();
		}

		return this;
	}

	/**
	 * Resets to the default scaling factors.
	 */
	public void resetScale() {
		setScale(1.0, 1.0);
	}

	/**
	 * Scales the points in the current region by a horizontal and vertical multiplier.
	 * 
	 * @param sX
	 * @param sY
	 */
	public void scaleRegion(double sX, double sY) {
		scaleX = scaleX * sX;
		scaleY = scaleY * sY;
	}

	/**
	 * Resizes the region uniformly in x and y. We actually just store the number and scale it
	 * whenever we need to render the final region. The Lazy Approach. =) This is nice because we
	 * can scale the region multiple times without getting aliasing effects.
	 * 
	 * @param scale
	 */
	public void scaleRegionUniformly(double scale) {
		scaleRegion(scale, scale);
	}

	/**
	 * @param isRegionActive
	 *            determines whether this will be an active region or not
	 */
	public void setActive(boolean isRegionActive) {
		active = isRegionActive;
	}

	/**
	 * @param theFillColor
	 */
	public void setFillColor(Color theFillColor) {
		fillColor = theFillColor;
	}

	/**
	 * @param theName
	 *            the name of the region. Name it something useful, like "Blue Button for Changing
	 *            Pen Colors"
	 */
	public void setName(String theName) {
		name = theName;
	}

	/**
	 * @param theOpacityFrom0To1
	 *            bounds checked from 0.0 to 1.0
	 */
	public void setOpacity(double theOpacityFrom0To1) {
		if (theOpacityFrom0To1 > 1) {
			theOpacityFrom0To1 = 1;
		} else if (theOpacityFrom0To1 < 0) {
			theOpacityFrom0To1 = 0;
		}
		opacity = theOpacityFrom0To1;
	}

	/**
	 * Replaces the scaling factors.
	 * 
	 * @param newScaleX
	 * @param newScaleY
	 */
	public void setScale(double newScaleX, double newScaleY) {
		scaleX = newScaleX;
		scaleY = newScaleY;
	}

	/**
	 * Subclasses can use this method to set the shape after constructing the object.
	 * 
	 * @param s
	 */
	protected void setShape(Shape s) {
		shape = s;
	}

	public void setStrokeColor(Color strokeCol) {
		strokeColor = strokeCol;
	}

	/**
	 * @param v
	 *            whether the region will be visible
	 */
	public void setVisible(boolean v) {
		visible = v;
	}

	/**
	 * Please override for more interesting output. This will print the name of the class along with
	 * all the segments of the shape.
	 * 
	 * @see java.lang.Object#toString()
	 * @return the String representation of this Region
	 */
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		final String className = shape.getClass().getName();
		sb.append(getName() + " of type ");
		sb.append(className.substring(className.lastIndexOf(".") + 1) + ": {");

		final PathIterator pathIterator = shape.getPathIterator(AffineTransform.getScaleInstance(
				scaleX, scaleY));

		sb.append(GraphicsUtils.getPathAsString(pathIterator));

		sb.append("} in " + referenceUnits.getUnitName());
		sb.append(getIsActiveString());
		return sb.toString();
	}

}
