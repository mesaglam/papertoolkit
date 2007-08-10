package papertoolkit.pen.streaming.data;

import java.io.IOException;

import papertoolkit.pen.PenSample;


/**
 * <p></p>
 * <p><span class="BSDLicense">
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.
 * </span></p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public interface PenServerSender {

	public void sendSample(PenSample as) throws IOException;

	public void destroy();
}
