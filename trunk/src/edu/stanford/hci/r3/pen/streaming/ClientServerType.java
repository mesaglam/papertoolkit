package edu.stanford.hci.r3.pen.streaming;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * When we connect to a pen server, we can either stream Java objects (serialized to XML) or just
 * show plain text.
 */
public enum ClientServerType {
	JAVA, 
	PLAINTEXT
}
