package edu.stanford.hci.r3.designer.acrobat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.thoughtworks.xstream.XStream;

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
			if (qName.equals("f") && attributes.getLength() == 1) {
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
				thePath = thePath + ".regions.xml";
				System.out.println("Write the Regions out to "
						+ new File(thePath).getAbsolutePath());
			}
		}
	}

	private class RegionHandler extends TagHandler {
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
	private class TagHandler {
		public void handleEnd(String qName) {

		}

		public void handleStart(String qName, Attributes attributes) {

		}

		public void handleValue(String value) {

		}
	}

	private double height;

	private double width;

	/**
	 * <p>
	 * </p>
	 */
	private class WidthAndHeightHandler extends TagHandler {
		private boolean captureNextValue;

		private boolean lookingForHeight;

		private boolean lookingForWidth;

		public void handleEnd(String qName) {

		}

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
					height = Double.parseDouble(value);
					System.out.println("Setting Height to " + value);
					resetFlags();
				} else if (lookingForWidth) {
					width = Double.parseDouble(value);
					System.out.println("Setting Width to " + value);
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

	private class XFDFHandler extends TagHandler {
		public void handleEnd(String qName) {
			if (!qName.equals("xfdf")) {
				return;
			}
			// write it out to disk...
			XStream x = new XStream();
			try {
				final FileOutputStream stream = new FileOutputStream(new File(thePath));
				x.toXML(new Object(), stream);
				stream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private LinkedList<TagHandler> handlers = new LinkedList<TagHandler>();

	/**
	 * Take the simple event-based SAX approach.
	 */
	private SAXParser parser;

	/**
	 * For each tag, we will create an appropriate tag handler (only if we are interested in
	 * processing the tag).
	 */
	private Map<String, TagHandler> tagToHandler = new HashMap<String, TagHandler>();

	private String thePath = null;

	private StringBuilder xml = new StringBuilder();

	/**
	 * The XML file is generated by the R3 Acrobat plugin and the AcrobatCommunicationServer.
	 */
	private File xmlFile;

	/**
	 * @param xmlFile
	 */
	public RegionConfigurationWriter(File theXmlFile) {
		xmlFile = theXmlFile;
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

	@Override
	public void characters(char[] chars, int start, int n) throws SAXException {
		super.characters(chars, start, n);
		final String trimmedValue = new String(chars, start, n).trim();
		System.out.print(trimmedValue);
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
		System.out.println("</" + qName + ">");

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
	 * 
	 */
	public void processXML() {
		try {
			parser.parse(xmlFile, this);
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
		System.out.print("<" + qName);
		int numAttrs = attributes.getLength();
		for (int i = 0; i < numAttrs; i++) {
			System.out.print(" " + attributes.getQName(i) + "=" + attributes.getValue(i));
			if (i != numAttrs - 1) {
				System.out.print(",");
			}
		}
		System.out.println(">");

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
