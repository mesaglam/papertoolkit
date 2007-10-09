package papertoolkit.pen.synch;

import java.io.File;
import java.util.List;

import javax.swing.JOptionPane;

import papertoolkit.devices.displays.HTMLDisplay;
import papertoolkit.pen.ink.Ink;

/**
 * <p>
 * Reads in a Synch... and presents each page as an image to the user.... The user gets to click: keep or discard.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class SynchSplitter {

	
	public static void main(String[] args) {
		
		String path = "C:/Documents and Settings/Ron Yeh/Desktop/InkStrokeStatistics/2006_LosTuxtlas/";
		String file = path + "2006_06_19__11_13_11_LauraMcDonald_and_CS247.xml";
		File f = new File(file);
		PenSynch synch = new PenSynch(f);
		
		List<Ink> importedInk = synch.getImportedInk();
		for (Ink i : importedInk) {
			File jpegFile = i.renderToJPEGFile();
			HTMLDisplay display = new HTMLDisplay();
			display.addImage(jpegFile);
			display.showNow();
			JOptionPane.showInputDialog("Keep?");
		}
	}
}
