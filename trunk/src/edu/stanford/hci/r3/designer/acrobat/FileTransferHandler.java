package edu.stanford.hci.r3.designer.acrobat;

import java.awt.Desktop;
import java.awt.datatransfer.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class FileTransferHandler extends TransferHandler {

	protected static final DataFlavor FILE_FLAVOR = DataFlavor.javaFileListFlavor;

	/**
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

	private List<File> okFiles = new ArrayList<File>();

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

	@SuppressWarnings("unchecked")
	public List<File> getOnlyPDFs(JComponent comp, Transferable t) {
		okFiles.clear();
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
			List<File> onlyPDFs = getOnlyPDFs(comp, t);
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

	public String toString() {
		return "My Cool File Transfer Handler";
	}

}
