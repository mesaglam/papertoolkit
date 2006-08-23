package edu.stanford.hci.r3.examples.pdf;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
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
			PdfWriter writer = PdfWriter
					.getInstance(doc, new FileOutputStream("testData/dots.pdf"));
			doc.open();
			String dot = "•";
			// top layer for pattern
			PdfContentByte cb = writer.getDirectContent();
			Graphics2D g2d = cb.createGraphicsShapes(customPageSize8x8.width(), customPageSize8x8
					.height());

			AffineTransform transform = g2d.getTransform();
			transform.scale(600 / 2540.0 / 72, 600 / 2540.0 / 72);
			g2d.setTransform(transform);

			g2d.setFont(TAHOMA_BOLD);
			for (int i = 0; i < 6000; i += 30) {
				for (int j = 0; j < 6000; j += 30) {
					g2d.drawString(dot, 600 + i, 600 + j);
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
	 * 
	 */
	private static void drawOnExistingPDF() {
		try {
			// reader = new PdfReader(new FileInputStream(new
			// File("testData/ButterflyNetCHI2006.pdf")));
			PdfReader reader = new PdfReader(new FileInputStream(new File(
					"testData/BobHorn-AvianFlu.pdf")));
			System.out.println("NumPages: " + reader.getNumberOfPages());

			PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(new File(
					"testData/Test.pdf")));

			// change the content beneath page 1
			// PdfContentByte under = stamp.getUnderContent(1);
			// Graphics2D g2Under = under.createGraphics(1024, 768);
			// g2Under.fillOval(10, 10, 60, 60);
			// g2Under.dispose();

			// change the content on top of page 1
			PdfContentByte over = stamp.getOverContent(1);
			int height = 36 * 72;
			int width = 120 * 72;
			Graphics2D g2Over = over.createGraphics(width, height);
			g2Over.setTransform(AffineTransform.getTranslateInstance(200, 200));
			g2Over.fillOval(20, 20, 300, 300);
			g2Over.setColor(new Color(200, 200, 100, 90));
			g2Over.fillRect(0, 0, width, height);
			g2Over.setColor(Color.BLUE);
			g2Over.drawRect(1, 1, width - 1, height - 1);
			g2Over.dispose();

			// the graphics is placed on the lower left corner of the page, in accordance w/
			// postscript
			Graphics2D g2Over2 = over.createGraphics(1024, 1024);
			g2Over2.setTransform(AffineTransform.getTranslateInstance(10, 100));
			g2Over2.setColor(Color.ORANGE);
			g2Over2.fillOval(0, 0, 1000, 1000);
			g2Over2.setColor(Color.BLUE);
			g2Over2.drawRect(1, 1, width - 1, height - 1);
			g2Over2.dispose();

			stamp.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		PdfReader reader = new PdfReader(new FileInputStream(new File(
				"testData/BobHorn-AvianFlu.pdf")));
		System.out.println("NumPages: " + reader.getNumberOfPages());

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
