package papertoolkit.pen.streaming.data;

import java.io.IOException;
import java.net.Socket;

import papertoolkit.PaperToolkit;
import papertoolkit.pen.PenSample;
import papertoolkit.util.DebugUtils;

public class PenServerFlashXMLSender extends PenServerJavaObjectXMLSender {

	public PenServerFlashXMLSender(Socket s) throws IOException {
		super(s);
		DebugUtils.println("Flash Pen Server Sender Created");
	}

	/* (non-Javadoc)
	 * @see papertoolkit.pen.streaming.data.PenServerJavaObjectXMLSender#sendSample(papertoolkit.pen.PenSample)
	 */
	public void sendSample(PenSample aSample) throws IOException {
		String xmlString = PaperToolkit.toXML(aSample);

		// remove line endings
		if (xmlString.contains("\n")) {
			xmlString = xmlString.replace("\n", "");
		}

		// remove spaces
		if (xmlString.contains(" ")) {
			xmlString = xmlString.replace(" ", "");
		}

		// this is the ONLY LINE that changes from the parent...
		bos.write((xmlString + "\0\r\n").getBytes());
		if (aSample.isPenUp()) {
			DebugUtils.println(xmlString);
		}
		bos.flush();
	}

}
