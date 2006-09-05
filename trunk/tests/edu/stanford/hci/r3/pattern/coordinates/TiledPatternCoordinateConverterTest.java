package edu.stanford.hci.r3.pattern.coordinates;

import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.pen.streaming.PenListener;
import edu.stanford.hci.r3.pen.streaming.PenSample;
import edu.stanford.hci.r3.units.coordinates.StreamedPatternCoordinates;

/**
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class TiledPatternCoordinateConverterTest {

	private static TiledPatternCoordinateConverter converter;

	public static void main(String[] args) {
		// test it with the JRBP map we created for GIGAprints
		converter = new TiledPatternCoordinateConverter( // 
				0, // start page
				4 /* across */, 2 /* down */, // tile configuration
				721, 932, // per tile info
				1330, 0, // tile padding information
				184022418, 7187, // physical coordinates information
				2734.625, 1792.25); // region configuration

		Pen pen = new Pen();
		pen.startLiveMode();
		pen.addLivePenListener(getPenListener());

	}

	private static PenListener getPenListener() {
		return new PenListener() {

			public void penDown(PenSample sample) {

			}

			public void penUp(PenSample sample) {

			}

			public void sample(PenSample sample) {
				StreamedPatternCoordinates coordinates = new StreamedPatternCoordinates(sample);
				if (converter.contains(coordinates)) {
					System.out.println("Location: " + converter.getRelativeLocation(coordinates));
				} else {
					System.out.println("Outside! =( " + sample);
				}
			}
		};
	}
}
