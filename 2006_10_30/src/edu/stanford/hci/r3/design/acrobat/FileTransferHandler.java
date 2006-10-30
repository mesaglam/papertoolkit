package edu.stanford.hci.r3.design.acrobat;

import java.awt.Desktop;
import java.awt.datatransfer.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

/**
 * <p>
 * Helps the drag and drop interaction for AcrobatDesignerLauncher.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class FileTransferHandler extends TransferHandler {

	private static final DataFlavor FILE_FLAVOR = DataFlavor.javaFileListFlavor;

	/**
	 * Check if one of the possible transfer flavors is file transfer.
	 * 
	 * @param transferFlavors
	 * @return
	 */
	public static boolean hasFileFlavor(DataFlavor[] transferFlavors) {
		for (DataFlavor f : transferFlavors) {
			if (FILE_FLAVOR.equals(f)) {
				return true;
			}
		}
		return false;
	}

	public FileTransferHandler() {
	}

	/**
	 * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent,
	 *      java.awt.datatransfer.DataFlavor[])
	 */
	@Override
	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
		if (hasFileFlavor(transferFlavors)) {
			return true;
		}
		return false;
	}

	/**
	 * Filters a list of files for *.PDF.
	 * 
	 * @param comp
	 * @param t
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<File> getOnlyPDFs(JComponent comp, Transferable t) {
		final List<File> okFiles = new ArrayList<File>();
		if (!canImport(comp, t.getTransferDataFlavors())) {
			return okFiles;
		}
		if (hasFileFlavor(t.getTransferDataFlavors())) {
			try {
				List<File> files = (List<File>) t.getTransferData(FILE_FLAVOR);
				for (File f : files) {
					// TODO: Is this OS Specific? OS X doesn't require PDF extensions...
					// But, all cool people use extensions anyways. :)
					if (f.getName().toLowerCase().endsWith(".pdf")) {
						okFiles.add(f);
					}
				}
				return okFiles;
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return okFiles;
	}

	/**
	 * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
	 */
	@Override
	public int getSourceActions(JComponent c) {
		System.out.println("Getting Source Actions");
		return COPY_OR_MOVE;
	}

	/**
	 * @see javax.swing.TransferHandler#getVisualRepresentation(java.awt.datatransfer.Transferable)
	 */
	@Override
	public Icon getVisualRepresentation(Transferable t) {
		System.out.println("Get Visual Representation");
		return super.getVisualRepresentation(t);
	}

	/**
	 * When files are dropped onto the launcher, we will get the list of PDFs and start the server
	 * to listen for R3 data coming from Acrobat.
	 * 
	 * @see javax.swing.TransferHandler#importData(javax.swing.JComponent,
	 *      java.awt.datatransfer.Transferable)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean importData(JComponent comp, Transferable t) {
		if (!canImport(comp, t.getTransferDataFlavors())) {
			return false;
		}
		if (hasFileFlavor(t.getTransferDataFlavors())) {
			final List<File> onlyPDFs = getOnlyPDFs(comp, t);
			if (onlyPDFs.size() == 0) {
				return false;
			}

			// start the acrobat communication server if it hasn't already started...
			AcrobatCommunicationServer.startServer();

			System.out.println("Opening... " + onlyPDFs);
			for (File f : onlyPDFs) {
				try {
					Desktop.getDesktop().open(f);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return true;
		}
		return false;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "PDF File Transfer Handler";
	}

}
