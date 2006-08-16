/**
 * 
 */
package edu.stanford.hci.r3.examples.pdf;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.net.MalformedURLException;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class ITextToPDF {

	
	public static final Font TAHOMA_BOLD = new Font("Tahoma", Font.BOLD, 21);

	/**
	 * Use iText to draw some dots into a PDF document.
	 */
	private static void drawDots() {
		Rectangle customPageSize8x8 = new Rectangle(0, 0, 576, 576);
		Document doc = new Document(customPageSize8x8, 50, 50, 50, 50);
		try {
			PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream("testData/dots.pdf"));
			doc.open();
			String dot = "•";
			// top layer for pattern
			PdfContentByte cb = writer.getDirectContent();
			Graphics2D g2d = cb.createGraphicsShapes(customPageSize8x8.width(), customPageSize8x8.height());

			AffineTransform transform = g2d.getTransform();
			transform.scale(600/2540.0/72, 600/2540.0/72);
			g2d.setTransform(transform);
			
			g2d.setFont(TAHOMA_BOLD);
			for (int i=0; i<6000; i+=30) {
				for (int j = 0; j<6000; j+=30) {
					g2d.drawString(dot, 600+i, 600+j);
				}
			}
			g2d.dispose();
			doc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Aug 14, 2006
	 */
	public static void main(String[] args) {
		Document doc = new Document(PageSize.LETTER, 50, 50, 50, 50);
		System.out.println("EPS");
		try {
			PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(
					"testData/8x8.pdf"));
			doc.open();
			Image img = Image.getInstance("testData/8x8.ps");
			img.setAbsolutePosition(0, 0);
			doc.add(img);
			doc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a PDF file by inserting a PNG image.
	 */
	private static void pngToPdf() {
		Document doc = new Document(PageSize.LETTER, 50, 50, 50, 50);
		System.out.println("Png to Pdf");
		try {
			PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(
					"testData/dragonSmall.pdf"));
			doc.open();
			Image img = Image.getInstance("testData/dragonSmall.png");
			img.setAbsolutePosition(0, 0);
			doc.add(img);
			doc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
