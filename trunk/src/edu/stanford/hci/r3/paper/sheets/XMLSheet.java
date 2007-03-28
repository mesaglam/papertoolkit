package edu.stanford.hci.r3.paper.sheets;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.paper.regions.XMLRegion;
import edu.stanford.hci.r3.units.Points;

/**
 * A Sheet object based on xml regions as defined by SketchToPaperUI
 * @author Marcello
 */
public class XMLSheet extends Sheet {
	
	public static void main(String args[]) {
		try {
			File file = new File("SketchedPaperUI.xml");
			/*XMLSheet sheet = */new XMLSheet(file);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public XMLSheet(File xmlFile) throws IOException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Node sheetNode = builder.parse( xmlFile ).getFirstChild();
			
			NamedNodeMap sheetAttributes = sheetNode.getAttributes();
			
			double sheetWidth = Double.parseDouble(sheetAttributes.getNamedItem("width").getTextContent());
			double sheetHeight = Double.parseDouble(sheetAttributes.getNamedItem("height").getTextContent());
	
			setSize(new Points(sheetWidth), new Points(sheetHeight));
			
			System.out.println("sheet: width="+sheetWidth+" x height="+sheetHeight);
			
			for (Node region : new Iterate(sheetNode)) {
				if (region.getNodeName().equals("region")) {
					NamedNodeMap attributes = region.getAttributes();
					String name = attributes.getNamedItem("name").getTextContent();
					double x = Double.parseDouble(attributes.getNamedItem("x").getTextContent());
					double y = Double.parseDouble(attributes.getNamedItem("y").getTextContent());
					double width = Double.parseDouble(attributes.getNamedItem("width").getTextContent());
					double height = Double.parseDouble(attributes.getNamedItem("height").getTextContent());
					System.out.println("region: "+name+"[x="+x+",y="+y+" width="+width+" x height="+height+"]");
					
					String eventType = null;
					
					for (Node eventHandler : new Iterate(region)) {
						if (eventHandler.getNodeName().equals("eventHandler")) {
							eventType = eventHandler.getAttributes().getNamedItem("type").getTextContent();
							System.out.println("event: "+ eventType);
						}
					}
					addRegion(new XMLRegion(name,x,y,width,height,eventType));
					
				}
			}
		} catch (SAXException ex) {
			throw new IOException(ex);
		} catch (ParserConfigurationException ex) {
			throw new IOException(ex);
		}
	}
	
	private class Iterate implements Iterator<Node>,Iterable<Node> {
		NodeList list;
		int i = 0;
		Iterate(Node n) {
			this.list = n.getChildNodes();
		}
		Iterate(NodeList list) {
			this.list = list;
		}
		public boolean hasNext() {
			return i < list.getLength();
		}
		public Node next() {
			return list.item(i++);
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
		public Iterator<Node> iterator() {
			return new Iterate(list);
		}
		
	}
}
