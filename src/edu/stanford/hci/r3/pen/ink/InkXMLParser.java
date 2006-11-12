package edu.stanford.hci.r3.pen.ink;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.xml.TagType;

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
		BEGIN, END, F, T, X, Y
	}

	/**
	 * <p>
	 * </p>
	 */
	public enum Nodes {
		INK, P, STROKE
	}

	/**
	 * What was seen recently during parsing of XML files.
	 */
	private String recentXMLText = "";

	/**
	 * @param xmlr
	 * @param index
	 */
	private void processAttribute(XMLStreamReader xmlr, int index) {
		// String prefix = xmlr.getAttributePrefix(index);
		// String namespace = xmlr.getAttributeNamespace(index);
		final String localName = xmlr.getAttributeName(index).toString();
		DebugUtils.println(localName);

		try {
			final Attributes type = Attributes.valueOf(localName.toUpperCase());
			final String value = xmlr.getAttributeValue(index);
			switch (type) {

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
				DebugUtils.println("Stroke");
				break;
			case P:
				break;
			}
		} else { // END
			switch (type) {
			case STROKE:
				break;
			case P:
				break;
			}
		}
	}

}
