package papertoolkit.demos.gesture;

import java.util.List;

import javax.print.attribute.standard.JobMessageFromOperator;
import javax.swing.JOptionPane;

import papertoolkit.PaperToolkit;
import papertoolkit.pen.Pen;
import papertoolkit.pen.ink.InkStroke;
import papertoolkit.pen.ink.InkUtils;
import papertoolkit.pen.ink.InkUtils.StationaryPoint;
import papertoolkit.pen.ink.InkUtils.StationaryPointType;
import papertoolkit.pen.streaming.listeners.PenStrokeListener;
import papertoolkit.util.DebugUtils;

/**
 * <p>
 * Draw a motion path for a ball, and calculate the COR automatically. C=SQRT(bounceHeight/dropHeight)
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class CoefficientOfRestitution {

	public CoefficientOfRestitution() {
		Pen pen = new Pen();
		pen.addLivePenListener(new PenStrokeListener() {

			public void strokeArrived(InkStroke stroke) {
				// DebugUtils.println(stroke);
				List<StationaryPoint> verticalLocalMaximaAndMinima = InkUtils
						.getVerticalLocalMaximaAndMinima(stroke);
				DebugUtils.println(verticalLocalMaximaAndMinima);

				StationaryPoint lastMin = null;
				StationaryPoint lastMax;
				StationaryPoint lastLastMax = null;

				// iterate through max, min, max and calculate the COR
				for (StationaryPoint pt : verticalLocalMaximaAndMinima) {
					if (pt.type == StationaryPointType.MAX) {
						lastMax = pt;

						if ((lastMax != null) && (lastLastMax != null) && (lastMin != null)) {
							double COR = Math.sqrt((lastMax.point.getY() - lastMin.point.getY())
									/ (lastLastMax.point.getY() - lastMin.point.getY()));
							DebugUtils.println("COR = " + COR);
							
							String CORString = ""+COR;
							if (CORString.length() > 5) {
								CORString = CORString.substring(0, 5);
							}
								
							PaperToolkit.initializeLookAndFeel();
							JOptionPane.showMessageDialog(null, "The COR is: " + CORString);
							return;
						}

						lastLastMax = lastMax;
					} else {
						lastMin = pt;
					}
				}

			}
		});
		pen.startLiveMode();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new CoefficientOfRestitution();
	}
}
