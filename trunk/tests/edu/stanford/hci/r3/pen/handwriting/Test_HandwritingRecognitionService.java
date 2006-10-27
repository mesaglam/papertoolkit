package edu.stanford.hci.r3.pen.handwriting;

import java.io.File;
import java.util.List;

import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Test_HandwritingRecognitionService {
	public static void main(String[] args) {
		HandwritingRecognitionService hwrec = HandwritingRecognitionService.getInstance();
		hwrec.connect();
		String result = hwrec
				.recognizeHandwriting(new File("C:\\Documents and Settings\\Ron Yeh\\My Documents"
						+ "\\Projects\\PaperToolkit\\penSynch\\data\\XML\\2006_10_25__10_49_17.xml"));
		DebugUtils.println(result);
		List<String> alternatives = hwrec.getAlternatives();
		DebugUtils.println("Alternatives...");
		DebugUtils.println(alternatives);
		hwrec.exitServer();
	}
}
