package edu.stanford.hci.r3.pen.streaming;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PenStreamingTest {

	public static void main(String[] args) {
	}

	private static void localhost() {
		System.out.println("Connecting to Text and Java Ports on Localhost");
		
		PenClient clientText = new PenClient("localhost", PenServer.DEFAULT_PLAINTEXT_PORT,
				ClientServerType.PLAINTEXT);
		clientText.connect();

		
		PenClient clientJava = new PenClient("localhost", PenServer.DEFAULT_JAVA_PORT,
				ClientServerType.JAVA);
		clientJava.connect();
	}

}
