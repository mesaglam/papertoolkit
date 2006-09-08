package edu.stanford.hci.r3.designer.acrobat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.units.Points;
import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.util.SystemUtils;

/**
 * <p>
 * The AcrobatCommunicationServer will write out XML files whenever it gets some input. It will use
 * this class to parse in the XML information, and write out the corresponding file.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class RegionConfigurationWriter extends DefaultHandler {

	/**
	 * <p>
	 * This class is platform specific to Windows at the moment. Sorry. =]
	 * </p>
	 */
	private class FileNameHandler extends TagHandler {

		public void handleStart(String qName, Attributes attributes) {
			if (outputXMLFile == null && qName.equals("f") && attributes.getLength() == 1) {
				String thePath = null;
				if (!SystemUtils.operatingSystemIsWindowsVariant()) {
					System.err
							.println("RegionConfigurationWriter: Sorry, we may not support "
									+ "your operating system ("
									+ System.getProperty("os.name")
									+ ") at the moment. The file name handler has only been tested on Windows.");
					thePath = attributes.getValue(0);
				} else {
					// the path should look like /C/Documents and Settings/Ron
					// Yeh/Desktop/BlankJavaScriptTest.pdf
					// turn it into C:/Documents and Settings/Ron
					// Yeh/Desktop/BlankJavaScriptTest.pdf
					final String notPath = attributes.getValue(0);

					// kill the first character
					String almostPath = notPath.substring(1);

					// insert a colon AFTER the first character
					// WARNING: This only works for Windows... =(
					thePath = almostPath.substring(0, 1) + ":" + almostPath.substring(1);
				}

				// kill the extension
				thePath = thePath.substring(0, thePath.lastIndexOf("."));
				outputXMLFile = new File(thePath + ".regions.xml");
				System.out.println("RegionConfigurationWriter :: Writing the Regions out to "
						+ outputXMLFile.getAbsolutePath());
			}
		}
	}

	/**
	 * Adds regions to the RegionConfiguration.
	 * 
	 * A rectangular PDF region looks like: <br>
	 * <br>
	 * <square rect="116.572525,458.878479,417.572845,659.878662"
	 * creationdate="D:20060906213756-07'00'" opacity="0.300003" interior-color="#808099"
	 * color="#333333" flags="print" date="D:20060906213757-07'00'" title="Region_1"
	 * fringe="0.500153,0.500153,0.500153,0.500153" page="0">
	 */
	private class RegionHandler extends TagHandler {

		public void handleStart(String qName, Attributes attributes) {
			if (qName.equals("square")) {
				String nameOfRegion = attributes.getValue("title");
				String rectString = attributes.getValue("rect");
				String[] rectValStrings = rectString.split(",");

				final double x = Double.parseDouble(rectValStrings[0]);
				final double y = Double.parseDouble(rectValStrings[1]); // correct this later
				final double w = Double.parseDouble(rectValStrings[2]) - x;
				final double h = Double.parseDouble(rectValStrings[3]) - y;
				final Region r = new Region(new Points(x), new Points(y), new Points(w),
						new Points(h));
				r.setName(nameOfRegion);
				temporaryRegionsList.add(r);
			}
		}
	}

	/**
	 * <p>
	 * </p>
	 */
	private class TagHandler {
		public void handleEnd(String qName) {

		}

		public void handleStart(String qName, Attributes attributes) {

		}

		public void handleValue(String value) {

		}
	}

	/**
	 * <p>
	 * </p>
	 */
	private class WidthAndHeightHandler extends TagHandler {
		private boolean captureNextValue;

		private boolean lookingForHeight;

		private boolean lookingForWidth;

		public void handleStart(String qName, Attributes attributes) {
			if (qName.equals("field") && (attributes.getLength() == 1)) {
				if (attributes.getValue(0).equals("Height")) {
					lookingForHeight = true;
				} else if (attributes.getValue(0).equals("Width")) {
					lookingForWidth = true;
				}
			} else if (qName.equals("value")) {
				captureNextValue = true;
			}
		}

		public void handleValue(String value) {
			if (captureNextValue) {
				if (lookingForHeight) {
					heightInPoints = Double.parseDouble(value);
					// System.out.println("Setting Height to " + value);
					regionConfiguration.setDocumentHeight(heightInPoints);
					resetFlags();
				} else if (lookingForWidth) {
					widthInPoints = Double.parseDouble(value);
					// System.out.println("Setting Width to " + value);
					regionConfiguration.setDocumentWidth(widthInPoints);
					resetFlags();
				}
			}
		}

		private void resetFlags() {
			lookingForHeight = false;
			lookingForWidth = false;
			captureNextValue = false;
		}
	}

	/**
	 * Handles the writing of the XML file once we finish parsing the XFDF information.
	 */
	private class XFDFHandler extends TagHandler {
		public void handleEnd(String qName) {
			if (!qName.equals("xfdf")) {
				return;
			}

			// correct all the y values stored in the region Configuration
			for (Region r : temporaryRegionsList) {

				final Units rh = r.getUnscaledBoundsHeight();
				final Units rw = r.getUnscaledBoundsWidth();
				final Region correctedRegion = new Region(r.getOriginX(), //
						new Points(heightInPoints - r.getOriginY().getValue() - rh.getValue()), //
						rw, rh);
				correctedRegion.setName(r.getName());
				// this region will be overlaid with pattern!
				correctedRegion.setActive(true);
				regionConfiguration.addRegion(correctedRegion);
			}

			// write it out to disk...
			try {
				final FileOutputStream fileOutputStream = new FileOutputStream(outputXMLFile);
				PaperToolkit.toXML(regionConfiguration, fileOutputStream);
				fileOutputStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Individual handlers for the different <tags></tags>
	 */
	private LinkedList<TagHandler> handlers = new LinkedList<TagHandler>();

	/**
	 * Height of the PDF in Points.
	 */
	private double heightInPoints;

	/**
	 * The XML file is generated by the R3 Acrobat plugin and the AcrobatCommunicationServer.
	 */
	private File inputXMLFile;

	/**
	 * Where should we write the XML file out to?
	 */
	private File outputXMLFile;

	/**
	 * Take the simple event-based SAX approach.
	 */
	private SAXParser parser;

	/**
	 * Stores all the information we need until we write it out to an XML file.
	 */
	private RegionConfiguration regionConfiguration = new RegionConfiguration();

	/**
	 * For each tag, we will create an appropriate tag handler (only if we are interested in
	 * processing the tag).
	 */
	private Map<String, TagHandler> tagToHandler = new HashMap<String, TagHandler>();

	/**
	 * Store the regions here for now. We need to correct the Y locations to make it more
	 * GUI-toolkit friendly, i.e., (0,0) == top left.
	 */
	private List<Region> temporaryRegionsList = new ArrayList<Region>();

	/**
	 * Width of the whole PDF, in 1/72 of an inch (Points).
	 */
	private double widthInPoints;

	/**
	 * @param inputXMLFile
	 */
	public RegionConfigurationWriter(File theInputXmlFile) {
		inputXMLFile = theInputXmlFile;
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			parser = factory.newSAXParser();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}

		// set up handlers
		tagToHandler.put("field", new WidthAndHeightHandler());
		tagToHandler.put("f", new FileNameHandler());
		tagToHandler.put("square", new RegionHandler());
		tagToHandler.put("xfdf", new XFDFHandler());
	}

	/**
	 * @param theInputXMLFile
	 * @param theOutputXMLFile
	 */
	public RegionConfigurationWriter(File theInputXMLFile, File theOutputXMLFile) {
		this(theInputXMLFile);
		outputXMLFile = theOutputXMLFile;
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] chars, int start, int n) throws SAXException {
		super.characters(chars, start, n);
		final String trimmedValue = new String(chars, start, n).trim();
		// System.out.println(trimmedValue);
		// dispatch this information to all handlers
		for (int i = handlers.size() - 1; i >= 0; i--) {
			handlers.get(i).handleValue(trimmedValue);
		}
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#endDocument()
	 */
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		// System.out.println("</" + qName + ">");

		// dispatch this information to all handlers
		for (int i = handlers.size() - 1; i >= 0; i--) {
			handlers.get(i).handleEnd(qName);
		}

		// if a handler was added for this tag, remove it now...
		TagHandler handler = tagToHandler.get(qName);
		if (handler != null) {
			handlers.removeLast();
		}
	}

	/**
	 * @return
	 */
	public File getOutputFile() {
		return outputXMLFile;
	}

	/**
	 * 
	 */
	public void processXML() {
		try {
			parser.parse(inputXMLFile, this);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#startDocument()
	 */
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String,
	 *      java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
			throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		// System.out.print("<" + qName);
		// int numAttrs = attributes.getLength();
		// for (int i = 0; i < numAttrs; i++) {
		// System.out.print(" " + attributes.getQName(i) + "=" + attributes.getValue(i));
		// if (i != numAttrs - 1) {
		// System.out.print(",");
		// }
		// }
		// System.out.println(">");

		// get a handler if it exists
		TagHandler handler = tagToHandler.get(qName);
		if (handler != null) {
			handlers.addLast(handler);
		}

		// dispatch this information to all handlers
		for (int i = handlers.size(); i > 0; i--) {
			handlers.get(i - 1).handleStart(qName, attributes);
		}
	}
}
