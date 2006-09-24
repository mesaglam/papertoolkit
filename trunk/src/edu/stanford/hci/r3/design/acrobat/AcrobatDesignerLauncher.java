package edu.stanford.hci.r3.design.acrobat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.TooManyListenersException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.util.WindowUtils;
import edu.stanford.hci.r3.util.components.BufferedImagePanel;
import edu.stanford.hci.r3.util.graphics.GraphicsUtils;
import edu.stanford.hci.r3.util.graphics.ImageCache;

/**
 * <p>
 * Part of our design suite. This launches your system's Acrobat Pro installation (if it is assigned
 * to edit pdf files). It enables the designer to add regions to an existing PDF of the paper UI.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class AcrobatDesignerLauncher {

	private static BufferedImagePanel activeArea;

	private static DropTarget dropTarget;

	private static DropTargetAdapter dropTargetAdapter;

	private static FileTransferHandler fileTransferHandler;

	private static final Font FONT = new Font("Trebuchet MS", Font.BOLD, 28);

	private static JFrame frame;

	private static JPanel mainPanel;

	/**
	 * A List of PDFs, filtered from the bigger list of files by our FileTransferHandler.
	 */
	private static List<File> onlyPDFs;

	/**
	 * Icon that pops up when you drag PDF files on to the active area.
	 */
	private static BufferedImage pdfLogo;

	/**
	 * For the text overlay of file names.
	 */
	private static final Color TRANSLUCENT_GRAY = new Color(50, 50, 50, 88);

	/**
	 * @return
	 */
	private static Component getActiveArea() {
		if (activeArea == null) {
			activeArea = new BufferedImagePanel() {
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					if (onlyPDFs == null) {
						return;
					}

					// draw list of file names
					final Graphics2D g2d = (Graphics2D) g;
					g2d.setRenderingHints(GraphicsUtils.getBestRenderingHints());
					g2d.setColor(TRANSLUCENT_GRAY);
					g2d.setFont(FONT);
					int stringY = (int) ((activeArea.getHeight() - onlyPDFs.size() * 40) / 2.0);
					for (File f : onlyPDFs) {
						g2d.drawString(f.getName(), 50, stringY);
						stringY += 50;
					}
				}
			};
			activeArea.setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10, Color.LIGHT_GRAY));
			activeArea.setBackground(Color.DARK_GRAY);
			fileTransferHandler = new FileTransferHandler();
			activeArea.setTransferHandler(fileTransferHandler);

			try {
				dropTarget = new DropTarget();
				dropTarget.setComponent(activeArea);
				dropTarget.addDropTargetListener(getDropTargetAdapter());
			} catch (TooManyListenersException e) {
				e.printStackTrace();
			}

			pdfLogo = ImageCache.loadBufferedImage(AcrobatDesignerLauncher.class
					.getResource("/icons/pdfIcon.png"));
		}
		return activeArea;
	}

	/**
	 * @return
	 */
	private static DropTargetAdapter getDropTargetAdapter() {
		if (dropTargetAdapter == null) {
			dropTargetAdapter = new DropTargetAdapter() {
				public void dragEnter(DropTargetDragEvent dtde) {
					frame.setAlwaysOnTop(true);
					frame.setAlwaysOnTop(false);

					// System.out.println(FileTransferHandler.hasFileFlavor(dtde.getCurrentDataFlavors()));
					// System.out.println(activeArea.getTransferHandler());
					// if there are PDFs in the list...
					onlyPDFs = fileTransferHandler.getOnlyPDFs(activeArea, dtde.getTransferable());
					// show the icon
					if (onlyPDFs.size() > 0) {
						activeArea.setBackground(Color.WHITE);
						activeArea.setImageCentered(pdfLogo);
					}
				}

				/**
				 * @see java.awt.dnd.DropTargetAdapter#dragExit(java.awt.dnd.DropTargetEvent)
				 */
				public void dragExit(DropTargetEvent dte) {
					hideBackground();
				}

				/**
				 * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
				 */
				public void drop(DropTargetDropEvent dtde) {
					dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
					fileTransferHandler.importData(activeArea, dtde.getTransferable());

					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// hide background
					hideBackground();
				}

				private void hideBackground() {
					activeArea.setBackground(Color.DARK_GRAY);
					activeArea.setImage(null);
					onlyPDFs = null;
				}
			};
		}
		return dropTargetAdapter;
	}

	/**
	 * @return the directions for operating this app.
	 */
	private static Component getLabel() {
		JLabel label = new JLabel("<html>Drag a PDF on to the active area below to start the "
				+ "R3 Acrobat Designer. You must have Acrobat Pro <br/>on your system, "
				+ "with the R3 plugin installed.</html>");
		label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		return label;
	}

	/**
	 * @return panel with a big active area where you can drop PDF files (yes, >=1).
	 */
	private static Container getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(getLabel(), BorderLayout.NORTH);
			mainPanel.add(getActiveArea(), BorderLayout.CENTER);
		}
		return mainPanel;
	}

	/**
	 * Sep 12, 2006
	 */
	public static void main(String[] args) {
		start();
	}

	/**
	 * @return
	 */
	public static JFrame start() {
		PaperToolkit.initializeLookAndFeel();
		if (frame == null) {
			frame = new JFrame("Acrobat Designer Launcher");
			frame.setContentPane(getMainPanel());
			frame.setSize(640, 480);
			frame.setLocation(WindowUtils.getWindowOrigin(frame, WindowUtils.DESKTOP_CENTER));
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		}
		frame.setVisible(true);
		return frame;
	}
}
