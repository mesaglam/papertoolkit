package edu.stanford.hci.r3.util.graphics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;

/**
 * <p>
 * Utilities for picking good colors, taken from various websites who claim to have good knowledge of colors
 * (such as Munsell or ColorBrewer).
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * @date Mar 17, 2004 Original Version
 * @date April 25, 2007 Cleaned Up and Included in R3
 */
public class ColorUtils {

	// represents the little swatch that colorizes the buttons
	public static class ColorIcon implements Icon {

		public Color color;

		private int height;

		private int width;

		public ColorIcon(int w, int h, Color c) {
			this.width = w;
			this.height = h;
			this.color = c;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.Icon#getIconHeight()
		 */
		public int getIconHeight() {
			return height;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.Icon#getIconWidth()
		 */
		public int getIconWidth() {
			return width;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
		 */
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(color);
			g.fillRect(x, y, width, height);
		}

		/**
		 * @param c
		 */
		public void setColor(Color c) {
			color = c;
		}

		/**
		 * @param R
		 * @param G
		 * @param B
		 */
		public void setColor(int R, int G, int B) {
			color = new Color(R, G, B);
		}

	}

	// Anything that wants to listen for the Dialog's output should implement
	// the following interface (it's really a callback)
	public static interface ColorListener {
		public abstract void setColor(Color c);
	}

	// a default version of ColorListener
	public static class ColorPrinter implements ColorListener {
		/*
		 * (non-Javadoc)
		 * 
		 * @see edu.stanford.hci.r3.util.graphics.ColorUtils.ColorListener#setColor(java.awt.Color)
		 */
		public void setColor(Color c) {
			System.out.println(c);
		}
	}

	// attached to buttons, calls the associated callback method (ColorListener)
	public static class ColorSetter implements ActionListener {
		private final Map<JButton, Color> buttonToColor = new HashMap<JButton, Color>();

		private ColorListener cl = null;

		private JDialog dialog = null;

		public ColorSetter(ColorListener cl, JDialog d) {
			this.cl = cl;
			this.dialog = d;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent ae) {
			JButton b = (JButton) ae.getSource();
			cl.setColor((Color) buttonToColor.get(b));
			dialog.dispose();
		}

		public Object put(JButton b, Color c) {
			return buttonToColor.put(b, c);
		}

	}

	public static final Color azure = new Color(224, 238, 238);

	public static final Color babyblue = new Color(109, 110, 192);

	public static final Color beige = new Color(245, 245, 220);

	public static final Color blue = new Color(13, 13, 190);

	public static final Color bluegray = new Color(88, 99, 99);

	public static final Color brown = new Color(162, 136, 99);

	// cb_ implies they were gotten from ColorBrewer
	public static final Color cb_blue = new Color(128, 177, 211);

	public static final Color cb_cyan = new Color(141, 211, 199);

	public static final Color cb_lavender = new Color(190, 186, 218);

	public static final Color cb_orange = new Color(253, 180, 98);

	public static final Color cb_red = new Color(251, 128, 114);

	public static final Color cb_yellow = new Color(255, 255, 179);

	public static final Color cornsilk = new Color(238, 232, 205);

	public static final Color cyan = new Color(13, 190, 190);

	public static final Color darkolive = new Color(128, 131, 86);

	public static final Color gray = new Color(220, 220, 220);

	public static final Color green = new Color(13, 190, 13);

	public static final Color honeydew = new Color(224, 238, 224);

	public static final Color indigo = new Color(58, 69, 146);

	public static final Color lace = new Color(253, 245, 230);

	public static final Color lemonchiffon = new Color(255, 250, 205);

	public static final Color magenta = new Color(190, 13, 190);

	public static final Color mblue = new Color(93, 109, 93);

	public static final Color mgray = new Color(93, 76, 56);

	public static final Color mgreen = new Color(129, 145, 93);

	public static final Color mpink = new Color(198, 141, 113);

	public static final Color myellow = new Color(186, 141, 72);

	public static final Color olive = new Color(175, 176, 72);

	public static final Color orange = new Color(228, 107, 33);

	public static final Color papaya = new Color(255, 239, 213);

	public static final Color peachpink = new Color(254, 144, 143);

	public static final Color red = new Color(190, 13, 13);

	public static final Color rose = new Color(171, 47, 79);

	public static final Color seashell = new Color(238, 229, 222);

	public static final Color volcanic = new Color(13, 25, 25);

	public static final Color yellow = new Color(190, 190, 13);

	public static final Color yellowgreen = new Color(149, 196, 58);

	/**
	 * 16 good colors...
	 * 
	 * @param parent
	 * @return
	 */
	public static Color chooseBackgroundColor(Frame parent) {
		JDialog d = new JDialog(parent, "Select a Background Color", true);
		Container c = d.getContentPane();
		c.setLayout(new GridLayout(3, 2));

		Color[] goodBackgroundColors = getGoodBackgroundColors();
		for (int i = 0; i < goodBackgroundColors.length; i++) {
			ColorIcon ci = new ColorIcon(60, 40, goodBackgroundColors[i]);
			JButton b = new JButton(ci);
			c.add(b);
		}
		d.pack();
		d.setVisible(true);
		return null;
	}

	/**
	 * @param parent
	 * @return
	 */
	public static void chooseColor(Frame parent, ColorListener cl) {
		JDialog d = new JDialog(parent, "Select a Color", true);
		ColorSetter cs = new ColorSetter(cl, d);
		Container c = d.getContentPane();
		Color[] goodColors = getGoodForegroundColors();
		int numRows = (int) Math.sqrt(goodColors.length) + 1;
		c.setLayout(new GridLayout(numRows, numRows));
		for (int i = 0; i < goodColors.length; i++) {
			ColorIcon ci = new ColorIcon(60, 40, goodColors[i]);
			JButton b = new JButton(ci);
			cs.put(b, goodColors[i]);
			b.addActionListener(cs);
			c.add(b); // add to contentpane
		}
		d.pack();
		d.setVisible(true);
	}

	/**
	 * @param parent
	 * @param colors
	 * @param cl
	 */
	public static void chooseColor(Frame parent, List<Color> colors, ColorListener cl) {
		JDialog d = new JDialog(parent, "Select a Color", true);
		ColorSetter cs = new ColorSetter(cl, d);
		Container c = d.getContentPane();
		int length = colors.size();
		int numRows = (int) Math.sqrt(length);
		c.setLayout(new GridLayout(numRows, numRows));
		for (Color myColor : colors) {
			ColorIcon ci = new ColorIcon(60, 40, myColor);
			JButton b = new JButton(ci);
			cs.put(b, myColor);
			b.addActionListener(cs);
			c.add(b);
		}
		d.pack();
		d.setVisible(true);
	}

	public static Color chooseEdgeColor(Frame parent) {
		JDialog d = new JDialog(parent, "Select Edge Color", true);
		Container c = d.getContentPane();
		c.setLayout(new GridLayout(3, 4));

		Color[] goodForegroundColors = getGoodForegroundColors();
		
		for (int i = 0; i < goodForegroundColors.length; i++) {
			ColorIcon ci = new ColorIcon(60, 40, goodForegroundColors[i]);
			JButton b = new JButton(ci);
			c.add(b);
		}
		d.pack();
		d.setVisible(true);
		return null;
	}

	/**
	 * Shows color chooser
	 * 
	 * @param parent
	 * @return
	 */
	public static Color chooseNodeColor(Frame parent) {
		JDialog d = new JDialog(parent, "Select Node Color", true);
		Container c = d.getContentPane();
		c.setLayout(new GridLayout(3, 2));

		Color[] goodDotColors = getGoodDotColors();
		for (int i = 0; i < goodDotColors.length; i++) {
			ColorIcon ci = new ColorIcon(60, 40, goodDotColors[i]);
			JButton b = new JButton(ci);
			c.add(b);
		}
		d.pack();
		d.setVisible(true);
		return null;
	}

	/**
	 * @param n
	 * @return a color from the foreground list
	 */
	public static Color getForegroundColor(int n) {
		Color[] goodForegroundColors = getGoodForegroundColors();
		return goodForegroundColors[n % goodForegroundColors.length];
	}

	private static Color[] getGoodBackgroundColors() {
		return new Color[] { seashell, cornsilk, azure, beige, papaya, honeydew, lemonchiffon, lace, gray };
	}

	private static Color[] getGoodDotColors() {
		return new Color[] { olive, brown, bluegray };
	}

	private static Color[] getGoodForegroundColors() {
		return new Color[] { green, indigo, rose, babyblue, darkolive, mblue, mgray, mgreen, mpink, orange,
				cb_cyan, cb_yellow, cb_lavender, cb_red, cb_blue, cb_orange, myellow, new Color(25, 25, 25)};
	}

	/**
	 * @param n
	 * @return
	 */
	public static List<Color> getUniqueColors(int n) {
		return getUniqueColors(n, null);
	}

	/**
	 * @param n
	 *            how many unique colors to make
	 * @return an array of unique colors
	 */
	public static List<Color> getUniqueColors(int n, Set<Color> excludedColors) {
		final List<Color> result = new ArrayList<Color>();

		int i = 0;
		Color[] goodForegroundColors = getGoodForegroundColors();
		while (result.size() < n) { // while not enough colors
			Color candidate = null;
			if (i < goodForegroundColors.length) {
				candidate = goodForegroundColors[i];
				i++;
			} else {
				candidate = makeRandomColor();
			}

			
			// oops, we don't want this one!
			if ((excludedColors != null) && (excludedColors.contains(candidate))) {
				continue;
			}

			result.add(candidate);
		}
		return result;

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HashSet<Color> exclude = new HashSet<Color>();
		// exclude.add(red);
		// exclude.add(green);
		// exclude.add(blue);
		chooseColor(null, getUniqueColors(14, exclude), new ColorPrinter());

		// GoodColorChooser gcc = new GoodColorChooser();
		// gcc.chooseBackgroundColor(null);
		// gcc.chooseEdgeColor(null);
		// gcc.chooseNodeColor(null);
	}

	/**
	 * Make an icon with a color swatch.
	 * 
	 * @param w
	 * @param h
	 * @param R
	 * @param G
	 * @param B
	 * @return
	 */
	public static ColorIcon makeColorIcon(int w, int h, int R, int G, int B) {
		return new ColorIcon(w, h, new Color(R, G, B));
	}

	/**
	 * @return a totally random :) Color
	 */
	public static Color makeRandomColor() {
		int R = (int) Math.round(Math.random() * 255);
		int G = (int) Math.round(Math.random() * 255);
		int B = (int) Math.round(Math.random() * 255);
		return new Color(R, G, B);
	}
}
