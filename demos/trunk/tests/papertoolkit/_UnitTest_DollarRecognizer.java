package papertoolkit;

import static org.junit.Assert.fail;

import org.junit.Test;

import papertoolkit.pen.Pen;
import papertoolkit.pen.gesture.dollar.DollarRecognizer;
import papertoolkit.pen.gesture.dollar.DollarRecognizer.RecognitionResult;
import papertoolkit.pen.ink.InkStroke;
import papertoolkit.pen.streaming.listeners.PenStrokeListener;
import papertoolkit.util.DebugUtils;

public class _UnitTest_DollarRecognizer {

	public static void main(String[] args) {
		final DollarRecognizer dollarRecognizer = new DollarRecognizer();
		
		// capture ink from a pen
		Pen pen = new Pen();
		pen.addLivePenListener(new PenStrokeListener() {
			@Override
			public void penStroke(InkStroke stroke) {
				RecognitionResult result = dollarRecognizer.recognize(stroke);
				DebugUtils.println(result);
			}
		});
		pen.startLiveMode();
		
	}
	
	@Test
	public void testDollarRecognizer() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddTemplate() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteUserTemplates() {
		fail("Not yet implemented");
	}

	@Test
	public void testRecognize() {
		fail("Not yet implemented");
	}

}
