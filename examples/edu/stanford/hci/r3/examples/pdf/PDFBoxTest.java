package edu.stanford.hci.r3.examples.pdf;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pdfbox.exceptions.COSVisitorException;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentCatalog;
import org.pdfbox.pdmodel.PDPage;
import org.pdfbox.pdmodel.PDResources;
import org.pdfbox.pdmodel.common.PDMetadata;
import org.pdfbox.pdmodel.edit.PDPageContentStream;
import org.pdfbox.pdmodel.font.PDFont;
import org.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.pdfbox.util.PDFTextStripper;

import edu.stanford.hci.r3.util.graphics.ImageUtils;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PDFBoxTest {

	private static int imageCounter = 0;

	private static void addPhotoAndText(PDDocument doc) {
		PDPage page = new PDPage();
		doc.addPage(page);

		// WARNING: does not work with pngs!
		PDXObjectImage ximage;
		try {
			ximage = new PDJpeg(doc, new FileInputStream("testData/dragon.jpg"));
			int h = ximage.getHeight();
			int w = ximage.getWidth();
			double scaleFactor = 0.33;
			System.out.println("Image Size " + w + " " + h);

			// WARNING: This is BAD, as your image data will just wrap and looked stupid
			// ximage.setWidth(100);
			// ximage.setHeight(300);

			// WARNING: This does not scale the image data; it just sets the max size of the
			// rendering box
			// setting width alone messes everything up, as the data will wrap also!
			// ximage.setWidth(400);

			// WARNING: Setting the Height alone will crop the picture after a certain point.
			// In Summary, don't use ximage.setWidth OR setHeight. =(
			// ximage.setHeight(300);

			// WARNING: This must come before the contentstream is opened! Otherwise the font will
			// not be embedded correctly.
			PDFont font = PDTrueTypeFont.loadTTF(doc, new File("data/tahoma.ttf"));

			// WARNING: Ordering matters, as you MUST open the content stream AFTER you create the
			// new PDJPeg...
			PDPageContentStream contentStream = new PDPageContentStream(doc, page);

			contentStream.beginText();
			contentStream.setFont(font, 12);
			contentStream.moveTextPositionByAmount(100, 700);
			contentStream.drawString("Hola Shijie!");
			contentStream.endText();

			// =): It is OK to use drawImage(ximage, x, y, w, h)
			// it will scale the image's width and height to fit the box
			// the units here are in points (72 points per inch)
			contentStream.drawImage(ximage, 72, 72, (int) Math.round(w * scaleFactor), (int) Math
					.round(h * scaleFactor));

			contentStream.close();
			doc.save("testData/Test.pdf");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (COSVisitorException e) {
			e.printStackTrace();
		}
	}

	private static void extractImages(PDDocument doc) {
		PDDocumentCatalog documentCatalog = doc.getDocumentCatalog();
		List<PDPage> allPages = documentCatalog.getAllPages();
		try {
			for (PDPage page : allPages) {
				PDResources resources = page.getResources();
				Map<String, PDXObjectImage> images;
				images = resources.getImages();
				if (images != null) {
					Set<String> names = images.keySet();
					for (String name : names) {
						PDXObjectImage image = images.get(name);
						System.out.println(name + ": " + image.getWidth() + ", "
								+ image.getHeight());

						BufferedImage bImage = image.getRGBImage();
						ImageUtils.writeImageToJPEG(bImage, 100, getUniqueFile("testData/BNet",
								"jpg"));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static File getUniqueFile(String prefix, String suffix) {
		String uniqueName = null;
		File f = null;
		while (f == null || f.exists()) {
			uniqueName = prefix + "_" + imageCounter;
			f = new File(uniqueName + "." + suffix);
			imageCounter++;
		}
		return f;
	}

	public static void main(String[] args) throws IOException {
		// the document
		PDDocument doc = null;
		try {
			doc = PDDocument.load(new File("testData/ButterflyNetCHI2006.pdf"));
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (doc != null) {
				doc.close();
			}
		}
	}

	private static void printMetadata(PDMetadata metadata) {
		InputStream stream;
		try {
			stream = metadata.createInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void printNumPages() {
		PDDocument doc;
		try {
			doc = PDDocument.load(new File("testData/ButterflyNetCHI2006.pdf"));
			final PDDocumentCatalog cat = doc.getDocumentCatalog();
			final PDMetadata metadata = cat.getMetadata();

			// how many pages?
			System.out.println(doc.getNumberOfPages());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void printText(PDDocument doc) {
		PDFTextStripper stripper;
		try {
			stripper = new PDFTextStripper();
			System.out.println(stripper.getText(doc));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void saveDocument(PDDocument doc) {
		// save out the document
		try {
			doc.save(new FileOutputStream(new File("testData/Test.pdf")));
		} catch (COSVisitorException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
