package papertoolkit.util.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import papertoolkit.util.files.FileUtils;
import papertoolkit.util.graphics.ImageUtils;

/**
 * <p>
 * Can log images of the frame to disk.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class LoggingJFrame extends JFrame {

	private int nextFileID = 0;
	private String fileName = "";
	private File desktopDirectory;
	
	public LoggingJFrame(String title) {
		super(title);
		desktopDirectory = FileUtils.getDesktopDirectory();
		fileName = title;
	}
	
	public void captureScreenshot() {
		// the default file (keep an internal count to make sure files are unique)
		captureScreenshotToFile(new File(desktopDirectory, fileName+nextFileID+".png"));
		nextFileID++;
	}
	
	public void captureScreenshotToFile(File destFile) {
		Dimension size = getSize();
		BufferedImage buffer = ImageUtils.createWritableBuffer(size.width, size.height);
		Graphics2D graphics = buffer.createGraphics();
		paint(graphics);
		ImageUtils.writeImageToPNG(buffer, destFile);
	}
	
	public static void main(String[] args) {
		final LoggingJFrame loggingJFrame = new LoggingJFrame("NothingJFrame");
		loggingJFrame.setLayout(new BorderLayout());
		loggingJFrame.add(new JLabel("Hello World"), BorderLayout.CENTER);
		
		JButton button = new JButton("Capture");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loggingJFrame.captureScreenshot();
			}
		});
		
		loggingJFrame.add(button, BorderLayout.SOUTH);
		loggingJFrame.setSize(640,480);
		loggingJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		loggingJFrame.setVisible(true);
		
	}
}
