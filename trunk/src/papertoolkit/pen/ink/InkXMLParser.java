package papertoolkit.pen.ink;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import papertoolkit.pattern.coordinates.PageAddress;
import papertoolkit.pen.PenSample;
import papertoolkit.util.xml.TagType;


/**
 * <p>
 * Responsible for Reading from XML Files that have been saved through Ink.saveToXMLFile(File xmlFileDest)
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class InkXMLParser {

	/**
	 * <p>
	 * </p>
	 */
	public enum Attributes {
		ADDRESS, BEGIN, END, F, T, X, Y
	}

	/**
	 * <p>
	 * PAGE and INK are aliases, as our ButterflyNet format would create PAGE xml files that are more or less
	 * compatible with our new INK xml files.
	 * </p>
	 */
	public enum Nodes {
		INK, P, PAGE, STROKE
	}

	private long beginTS;
	private PenSample currentSample;
	private ArrayList<PenSample> currentStroke;
	private long endTS;
	private int f;
	private Ink ink;

	/**
	 * What was seen recently during parsing of XML files.
	 */
	private String recentXMLText = "";

	private long t;
	private double x;
	private double y;

	public InkXMLParser(Ink theInk) {
		ink = theInk;
	}

	/**
	 * 
	 * @param xmlFileSource
	 */
	public void parse(File xmlFileSource) {
		// Create an input factory
		final XMLInputFactory xmlif = XMLInputFactory.newInstance();
		// Create an XML stream reader
		XMLStreamReader xmlr;
		try {
			xmlr = xmlif.createXMLStreamReader(new FileReader(xmlFileSource));
			// Loop over XML input stream and process events
			while (xmlr.hasNext()) {
				processEvent(xmlr);
				xmlr.next();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param xmlr
	 * @param index
	 */
	private void processAttribute(XMLStreamReader xmlr, int index) {
		// String prefix = xmlr.getAttributePrefix(index);
		// String namespace = xmlr.getAttributeNamespace(index);
		final String localName = xmlr.getAttributeName(index).toString();
		// DebugUtils.println(localName);

		try {
			final Attributes type = Attributes.valueOf(localName.toUpperCase());
			final String value = xmlr.getAttributeValue(index);
			switch (type) {
			case ADDRESS:
				ink.setSourcePageAddress(new PageAddress(value));
				break;
			case BEGIN:
				beginTS = Long.parseLong(value);
				break;
			case END:
				endTS = Long.parseLong(value);
				break;
			case X:
				x = Double.parseDouble(value);
				break;
			case Y:
				y = Double.parseDouble(value);
				break;
			case F:
				f = Integer.parseInt(value);
				break;
			case T:
				t = Long.parseLong(value);
				break;
			}
		} catch (IllegalArgumentException iae) {
			// System.out.println("Not Handling Attribute: " + localName);
		}
	}

	/**
	 * @param xmlr
	 */
	private void processAttributes(XMLStreamReader xmlr) {
		for (int i = 0; i < xmlr.getAttributeCount(); i++) {
			processAttribute(xmlr, i);
		}
	}

	/**
	 * Process the XML data.
	 * 
	 * @param xmlr
	 */
	private void processEvent(XMLStreamReader xmlr) {
		int start = 0;
		int length = 0;
		String text = "";

		switch (xmlr.getEventType()) {
		case XMLStreamConstants.START_ELEMENT:
			recentXMLText = "";
			processTag(xmlr, TagType.BEGIN_TAG);
			processAttributes(xmlr);
			break;
		case XMLStreamConstants.END_ELEMENT:
			processTag(xmlr, TagType.END_TAG);
			recentXMLText = "";
			break;
		case XMLStreamConstants.CHARACTERS:
			start = xmlr.getTextStart();
			length = xmlr.getTextLength();
			text = new String(xmlr.getTextCharacters(), start, length);
			recentXMLText += text;
			break;
		case XMLStreamConstants.SPACE:
			start = xmlr.getTextStart();
			length = xmlr.getTextLength();
			text = new String(xmlr.getTextCharacters(), start, length);
			break;
		}
	}

	/**
	 * @param xmlr
	 * @param beginOrEnd
	 */
	private void processTag(XMLStreamReader xmlr, TagType beginOrEnd) {
		if (!xmlr.hasName()) {
			return;
		}
		final String localName = xmlr.getLocalName();
		// DebugUtils.println(localName);
		final Nodes type = Nodes.valueOf(localName.toUpperCase());
		if (beginOrEnd == TagType.BEGIN_TAG) { // BEGIN
			switch (type) {
			case STROKE:
				// DebugUtils.println("Stroke");
				currentStroke = new ArrayList<PenSample>();
				break;
			case P:
				break;
			case INK:
			case PAGE:
				// 
				break;
			}
		} else { // END
			switch (type) {
			case STROKE:
				// set the up flag for the last sample we parsed
				currentSample.setPenUp(true);
				ink.addStroke(new InkStroke(currentStroke));
				break;
			case P:
				currentSample = new PenSample(x, y, f, t);
				currentStroke.add(currentSample);
				break;
			case INK:
				break;
			}
		}
	}

}
