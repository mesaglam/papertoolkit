package edu.stanford.hci.r3.util.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.util.MathUtils;

/**
 * <p>
 * For awesome splash screens. =) This is to be used with Java 6's SplashScreen facility.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class SplashScreenUtils {

	/**
	 * OS X-like indeterminate progress bar.
	 * 
	 * @param maxNumMilliseconds
	 * @param center
	 */
	public static void animateSplashScreen(final long maxNumMilliseconds, final Point2D center) {
		// if there's no splashscreen, then return
		final SplashScreen splashScreen = SplashScreen.getSplashScreen();
		if (splashScreen == null) {
			return;
		}

		// determine some parameters
		final double radius = 26;
		final double numBars = 12;

		// set dTimeMillis such that one complete revolution in 0.7 seconds
		final long dTimeMillis = MathUtils.rint(700 / numBars);

		final double dTheta = (2 * Math.PI / numBars);
		final double paddingX = 16;
		final double paddingY = 16;
		final float strokeThickness = 4.5f;

		// create and populate an array of coors from black to light grey
		final List<Color> colors = new ArrayList<Color>();
		final double dColor = .4f / numBars;
		float colorValue = .4f;

		double theta = Math.PI;
		final List<Double> dX = new ArrayList<Double>();
		final List<Double> dY = new ArrayList<Double>();

		for (int i = 0; i < numBars; i++) {
			colors.add(new Color(colorValue, colorValue, colorValue));
			colorValue -= dColor;

			dX.add(Math.cos(theta));
			dY.add(Math.sin(theta));
			theta -= dTheta;
		}

		// start the animation thread
		new Thread(new Runnable() {
			public void run() {
				try {
					final Graphics2D g = splashScreen.createGraphics();
					g.setRenderingHints(GraphicsUtils.getBestRenderingHints());
					g.setStroke(new BasicStroke(strokeThickness, BasicStroke.CAP_ROUND,
							BasicStroke.JOIN_ROUND));

					int colorOffset = 0;
					long timeAnimating = 0;
					while (timeAnimating < maxNumMilliseconds) {

						for (int i = 0; i < numBars; i++) {
							// DebugUtils.println("Angle: " + theta);
							// DebugUtils.println(dX + " " + dY);

							g.setColor(colors.get((int) ((i + colorOffset) % numBars)));
							Double dYVal = dY.get(i);
							Double dXVal = dX.get(i);
							g.draw(new Line2D.Double(center.getX() + dXVal * paddingX, center
									.getY()
									+ dYVal * paddingY, center.getX() + dXVal * radius, center
									.getY()
									+ dYVal * radius));
						}
						splashScreen.update();

						colorOffset++;
						if (colorOffset == numBars) {
							colorOffset = 0;
						}
						// pause for a little bit
						Thread.sleep(dTimeMillis);
						timeAnimating += dTimeMillis;
					}
					splashScreen.close();
				} catch (IllegalStateException ise) {
					// the splash screen is no longer available
					return;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}
}
