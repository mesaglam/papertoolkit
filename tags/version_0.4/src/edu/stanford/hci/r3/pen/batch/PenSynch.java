package edu.stanford.hci.r3.pen.batch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.stanford.hci.r3.pen.PenSample;
import edu.stanford.hci.r3.pen.ink.Ink;
import edu.stanford.hci.r3.pen.ink.InkStroke;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * Represents a single pen synch (i.e., one xml file in the penSynch XML directory).
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class PenSynch {

	private static final String PAGE = "page";
	private static final String REQUEST_INFORMATION = "requestInformation";
	private static final String STROKE = "stroke";

	public static void main(String[] args) {
		File xmlFile = new File("penSynch/data/XML/2007_03_10__01_09_38_SketchedPaperUI.xml");
		new PenSynch(xmlFile);
	}

	/**
	 * Ink on a per page basis... corresponds to <page></page> tags.
	 */
	private List<Ink> importedInk = new ArrayList<Ink>();
	private Date localTime;
	private int numPages;
	private String penID;
	private Date universalTime;

	public PenSynch(File penSynchXMLFile) {
		XMLInputFactory xmlInput = XMLInputFactory.newInstance();
		try {
			XMLEventReader eventReader = xmlInput.createXMLEventReader(new FileInputStream(
					penSynchXMLFile));

			while (eventReader.hasNext()) {
				XMLEvent nextEvent = eventReader.nextEvent();
				// DebugUtils.println(nextEvent.getEventType());
				switch (nextEvent.getEventType()) {
				case XMLEvent.START_DOCUMENT:
					break;
				case XMLEvent.START_ELEMENT:
					// DebugUtils.println(nextEvent);
					StartElement startEvent = (StartElement) nextEvent;
					String elementName = startEvent.getName().toString();
					// DebugUtils.println(startEvent.getName());

					if (elementName.equals(REQUEST_INFORMATION)) {
						processRequestInformation(eventReader);
					} else if (elementName.equals(PAGE)) {
						processPage(eventReader, startEvent);
					}
					break;
				default:
					break;
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// printOutDetails();
	}
	
	public List<Ink> getImportedInk() {
		return importedInk;
	}

	public Date getLocalTime() {
		return localTime;
	}

	public int getNumPages() {
		return numPages;
	}

	public String getPenID() {
		return penID;
	}

	public Date getUniversalTime() {
		return universalTime;
	}

	public void printOutDetails() {
		DebugUtils.println(importedInk.size() + " page synchronized.");
		for (Ink ink : importedInk) {
			DebugUtils.println(ink.getNumStrokes() + " strokes on page "
					+ ink.getSourcePageAddress());
			DebugUtils.println("Ink Boundaries: " + ink.getMinX() + " " + ink.getMinY() + " "
					+ ink.getMaxX() + " " + ink.getMaxY());

			List<InkStroke> strokes = ink.getStrokes();
			for (InkStroke s : strokes) {
				DebugUtils.println(s.toString());
				List<PenSample> samples = s.getSamples();
				for (PenSample sample : samples) {
					DebugUtils.println(sample);
				}
			}

		}
	}

	private void processPage(XMLEventReader eventReader, StartElement page)
			throws XMLStreamException {

		Ink pageInk = new Ink();
		pageInk.setSourcePageAddress(page.getAttributeByName(new QName("address")).getValue());

		// DebugUtils.println(pageInk.getSourcePageAddress());

		while (eventReader.hasNext()) {
			XMLEvent nextEvent = eventReader.nextEvent();
			String elementName;
			switch (nextEvent.getEventType()) {
			case XMLEvent.START_ELEMENT:
				StartElement startEvent = (StartElement) nextEvent;
				elementName = startEvent.getName().toString();
				if (elementName.equals(STROKE)) {
					InkStroke stroke = new InkStroke();
					processStroke(eventReader, stroke);
					pageInk.addStroke(stroke);
				}

				break;
			case XMLEvent.END_ELEMENT:
				EndElement endEvent = (EndElement) nextEvent;
				elementName = endEvent.getName().toString();
				if (elementName.equals(PAGE)) {

					// add to the ink we have read in...
					importedInk.add(pageInk);

					// we are done handling the <page/> tag
					return;
				}
				break;
			default:
				break;
			}
		}

	}

	/**
	 * @param eventReader
	 * @throws XMLStreamException
	 * @throws ParseException
	 */
	private void processRequestInformation(XMLEventReader eventReader) throws XMLStreamException,
			ParseException {
		while (eventReader.hasNext()) {
			XMLEvent nextEvent = eventReader.nextEvent();
			String elementName;
			switch (nextEvent.getEventType()) {
			case XMLEvent.START_DOCUMENT:
				break;
			case XMLEvent.START_ELEMENT:
				// DebugUtils.println(nextEvent);
				StartElement startEvent = (StartElement) nextEvent;
				elementName = startEvent.getName().toString();
				// DebugUtils.println(startEvent.getName());
				if (elementName.equals("universalTime")) {
					Attribute timeAttr = startEvent.getAttributeByName(new QName("time"));
					String time = timeAttr.getValue();
					// DebugUtils.println(time);
					universalTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a").parse(time);
				} else if (elementName.equals("localTime")) {
					Attribute timeAttr = startEvent.getAttributeByName(new QName("time"));
					String time = timeAttr.getValue();
					// DebugUtils.println(time);
					localTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a").parse(time);
				} else if (elementName.equals("penID")) {
					Attribute idAttr = startEvent.getAttributeByName(new QName("id"));
					penID = idAttr.getValue();
					// DebugUtils.println(penID);
				} else if (elementName.equals("numPages")) {
					Attribute idAttr = startEvent.getAttributeByName(new QName("num"));
					numPages = Integer.parseInt(idAttr.getValue());
					// DebugUtils.println(numPages);
				}

				break;
			case XMLEvent.END_ELEMENT:
				EndElement endEvent = (EndElement) nextEvent;
				elementName = endEvent.getName().toString();
				if (elementName.equals(REQUEST_INFORMATION)) {
					// we are done handling the <requestInformation/> tag
					return;
				}
				// DebugUtils.println("End: " + nextEvent);
				break;
			default:
				break;
			}
		}
	}

	private void processStroke(XMLEventReader eventReader, InkStroke stroke)
			throws XMLStreamException {
		while (eventReader.hasNext()) {
			XMLEvent nextEvent = eventReader.nextEvent();
			String elementName;
			switch (nextEvent.getEventType()) {
			case XMLEvent.START_ELEMENT:
				StartElement startEvent = (StartElement) nextEvent;
				elementName = startEvent.getName().toString();
				if (elementName.equals("p")) {
					String xAttr = startEvent.getAttributeByName(new QName("x")).getValue();
					String yAttr = startEvent.getAttributeByName(new QName("y")).getValue();
					String fAttr = startEvent.getAttributeByName(new QName("f")).getValue();
					String tAttr = startEvent.getAttributeByName(new QName("t")).getValue();
					stroke.addSample(Double.parseDouble(xAttr), Double.parseDouble(yAttr), Integer
							.parseInt(fAttr), Long.parseLong(tAttr));
				}
				break;
			case XMLEvent.END_ELEMENT:
				EndElement endEvent = (EndElement) nextEvent;
				elementName = endEvent.getName().toString();
				if (elementName.equals(STROKE)) {
					// we are done handling the <stroke/> tag
					return;
				}
				break;
			default:
				break;
			}
		}
	}
}
