package papertoolkit.util.classpath;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import papertoolkit.util.xml.TagType;

/**
 * <p>
 * Allows us to parse an Eclipse project's classpath.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class EclipseProjectClassPath {

	public enum Attributes {
		EXCLUDING, KIND, PATH
	}

	private class ClassPathEntry {

		public boolean excluding = false;

		public String kind;

		public String path;

		public String toString() {
			return kind + "\texclude?:" + excluding + "\t" + path;
		}
	}

	public enum Nodes {
		ATTRIBUTE, ATTRIBUTES, CLASSPATH, CLASSPATHENTRY
	}

	private File classpathFile;

	private ClassPathEntry currentNode;

	private List<String> listOfClassPaths = new ArrayList<String>();

	private String prefix;

	/**
	 * what was seen recently during parsing
	 */
	private String recentText = "";

	private Set<String> referencePaths;

	/**
	 * @param cpFile
	 */
	public EclipseProjectClassPath(File cpFile) {
		classpathFile = cpFile;
		// DebugUtils.println(classpathFile.getAbsolutePath());
	}

	/**
	 * @param cpFile
	 * @param pathPrefix
	 * @param srcPaths
	 * @throws XMLStreamException
	 * @throws FileNotFoundException
	 * @throws FactoryConfigurationError
	 */
	private List<String> parseFile(String pathPrefix, Set<String> srcPaths) throws FileNotFoundException,
			XMLStreamException {
		referencePaths = srcPaths;
		prefix = pathPrefix;

		// Create an input factory
		final XMLInputFactory xmlif = XMLInputFactory.newInstance();
		// Create an XML stream reader
		final XMLStreamReader xmlr = xmlif.createXMLStreamReader(new FileReader(classpathFile));
		// Loop over XML input stream and process events
		while (xmlr.hasNext()) {
			processEvent(xmlr);
			xmlr.next();
		}

		return listOfClassPaths;
	}

	/**
	 * @return
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 */
	public List<String> parseFile() {
		try {
			return parseFile("", new HashSet<String>());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		return new ArrayList<String>();
	}

	/**
	 * @param xmlr
	 * @param index
	 */
	private void processAttribute(XMLStreamReader xmlr, int index) {

		// String prefix = xmlr.getAttributePrefix(index);
		// String namespace = xmlr.getAttributeNamespace(index);
		final String localName = xmlr.getAttributeName(index).toString();
		try {
			final Attributes type = Attributes.valueOf(localName.toUpperCase());
			final String value = xmlr.getAttributeValue(index);
			switch (type) {
			case PATH:
				currentNode.path = value;
				break;
			case EXCLUDING:
				currentNode.excluding = true;
				break;
			case KIND:
				currentNode.kind = value;
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
	 * @param xmlr
	 */
	private void processEvent(XMLStreamReader xmlr) {
		int start = 0;
		int length = 0;
		String text = "";

		switch (xmlr.getEventType()) {
		case XMLStreamConstants.START_ELEMENT:
			recentText = "";
			processTag(xmlr, TagType.BEGIN_TAG);
			processAttributes(xmlr);
			break;
		case XMLStreamConstants.END_ELEMENT:
			processTag(xmlr, TagType.END_TAG);
			recentText = "";
			break;
		case XMLStreamConstants.CHARACTERS:
			start = xmlr.getTextStart();
			length = xmlr.getTextLength();
			text = new String(xmlr.getTextCharacters(), start, length);
			recentText += text;
			break;
		case XMLStreamConstants.SPACE:
			start = xmlr.getTextStart();
			length = xmlr.getTextLength();
			text = new String(xmlr.getTextCharacters(), start, length);
			break;

		// case XMLStreamConstants.COMMENT:
		// case XMLStreamConstants.PROCESSING_INSTRUCTION:
		// if (xmlr.hasText()) {
		// String piOrComment = xmlr.getText();
		// }
		// break;

		}
	}

	/**
	 * @param xmlr
	 * @param startTag
	 */
	private void processTag(XMLStreamReader xmlr, TagType beginOrEnd) {
		if (xmlr.hasName()) {
			final String localName = xmlr.getLocalName();
			// DebugUtils.println(localName);
			final Nodes type = Nodes.valueOf(localName.toUpperCase());
			if (beginOrEnd == TagType.BEGIN_TAG) {
				switch (type) {
				case CLASSPATH:
					// nothing
					break;
				case CLASSPATHENTRY:
					// create a new classpathentry object to hold the information
					currentNode = new ClassPathEntry();
					break;
				}
			} else { // END
				final String currentKind = currentNode.kind;
				final String currentPath = currentNode.path;
				switch (type) {
				case CLASSPATH:
					// nothing
					break;
				case CLASSPATHENTRY:
					// only if eclipse considers this path "included"
					if (!currentNode.excluding && !currentKind.equals("con") && !currentKind.equals("var")) {
						// if it's a src/ type, we gotta recursively compute the classpath
						if (currentKind.equals("src")) {
							if (currentPath.equals("src")) {
								// don't do anything
								// e.g., path="src"
							} else if (currentPath.startsWith("/")) {
								// references other projects...

								// check if this src path has been used before
								// if so, skip it!
								if (referencePaths.contains(currentPath)) {
									// System.out.println("Has it already!");
								} else {
									// System.out.println(currentNode);

									referencePaths.add(currentPath);
									File cpFile = null;
									try {
										cpFile = new File(new File(new File(".."), currentPath), ".classpath")
												.getCanonicalFile();
									} catch (IOException e1) {
										e1.printStackTrace();
									}
									// System.out.println(cpFile.getAbsolutePath());

									try {
										// get classpaths recursively
										List<String> referencedClasspaths = new EclipseProjectClassPath(
												cpFile).parseFile(".." + currentPath + "/", referencePaths);
										listOfClassPaths.addAll(referencedClasspaths);
									} catch (FileNotFoundException e) {
										e.printStackTrace();
									} catch (XMLStreamException e) {
										e.printStackTrace();
									}
								}
							} else {
								// DebugUtils.println("Unknown: " + currentPath);
							}
						} else if (currentKind.equals("lib") && currentPath.startsWith("/")) {
							// e.g., /HCILib/lib/mlibwrapper_jai.jar
							// make sure it's not in the already included paths...

							if (referencePaths.contains(currentPath)) {
								// has it already!
							} else {
								listOfClassPaths.add(prefix + ".." + currentPath);

								// System.out.println(currentNode);
								referencePaths.add(currentPath);
							}
						} else {
							// System.out.println(currentNode);

							// otherwise, add it to the current classpath string
							listOfClassPaths.add(prefix + currentPath);
						}
					}
					break;
				}
			}
		}
	}
}
