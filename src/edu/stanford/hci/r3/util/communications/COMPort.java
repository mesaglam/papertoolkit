package edu.stanford.hci.r3.util.communications;

/**
 * <p>
 * When translated to a string, JavaCOMM can map this to a physical COM port.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public enum COMPort {

	COM1, COM2, COM3, COM4, COM5, COM6, COM7, COM8, COM9;

	public static final Object[] PORTS = new Object[] { COM1, COM2, COM3, COM4, COM5, COM6, COM7, COM8, COM9 };
}
