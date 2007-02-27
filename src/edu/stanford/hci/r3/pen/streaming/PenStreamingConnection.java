package edu.stanford.hci.r3.pen.streaming;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TooManyListenersException;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.comm.UnsupportedCommOperationException;

import edu.stanford.hci.r3.pen.PenSample;
import edu.stanford.hci.r3.pen.streaming.listeners.PenListener;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * This class reads from a COM port (connected to a Bluetooth transceiver). It streams data from the
 * Nokia SU-1B pen and converts it according to the Nokia Document.
 * 
 * The idea for this class is that it reports low-level pen events. It does not do any bit of
 * gesture recognition.
 * </p>
 * <p>
 * Example code is taken from: http://java.sun.com/products/javacomm/javadocs/API_users_guide.html
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PenStreamingConnection implements SerialPortEventListener {

	private static enum StreamingField {
		FORCE, HEADER, X, X_FRACTION, Y, Y_FRACTION
	}

	private static final boolean DEBUG = false;

	public static final String DEFAULT_PORT = "COM5";

	// PenUP Identifier
	private static final byte ID_PEN_UP = 0x01;

	// SimpleCoord Identifier
	private static final byte ID_SIMPLE_COORD = 0x00;

	private static PenStreamingConnection instance = null;

	// length of the PenUP Packet
	private static final byte LENGTH_PEN_UP = 0x00;

	// length of the Simple Coordinate Packet
	private static final byte LENGTH_SIMPLE_COORD = 0x0B;

	private static CommPortIdentifier portID;

	private static Enumeration portList;

	/**
	 * @return use COM5
	 */
	public static PenStreamingConnection getInstance() {
		return getInstance(null);
	}

	/**
	 * @param port
	 *            if port is null, use the default port (COM5)
	 */
	public static PenStreamingConnection getInstance(String port) {
		if (instance != null) {
			return instance;
		}

		// set up a connection to the COM port
		// read from it, and display to console
		// boolean portFound = false;
		if (port == null || port.length() == 0) {
			port = DEFAULT_PORT;
		}

		DebugUtils.print("PenStreamingConnection: Looking for " + port + ". Found {");

		portList = CommPortIdentifier.getPortIdentifiers();
		while (portList.hasMoreElements()) {
			portID = (CommPortIdentifier) portList.nextElement();
			if (portID.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portID.getName().equals(port)) {
					System.out.println(port + "}");
					instance = new PenStreamingConnection();
					System.out.flush();
					return instance;
				}
			}
		}
		System.out.println("}");
		System.out.println("Port " + port + " not found.");
		System.out.flush();
		System.err.println("Is JavaCOMM not installed?");
		return null;
	}

	private byte bCurrent;

	private byte bLast;

	private byte bLastLast;

	private int force = 0;

	private InputStream inputStream;

	private PenSample lastSample;

	// list of listeners; add a PenListener to this list to listen to pen events
	private List<PenListener> listeners = new ArrayList<PenListener>();

	private StreamingField nextUp = StreamingField.HEADER;

	private int numBytesCoord;

	private boolean penIsUp = true;

	private SerialPort serialPort;

	private long timestamp;

	private int x = 0;

	private int xFraction = 0;

	private int y = 0;

	private int yFraction = 0;

	/**
	 * @see
	 */
	private PenStreamingConnection() {
		try {
			serialPort = (SerialPort) portID.open("StreamingPen", 2000);
		} catch (PortInUseException e) {
			e.printStackTrace();
		}

		try {
			// TODO: Possible Null Pointer Exception Bug
			inputStream = serialPort.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			serialPort.addEventListener(this);
		} catch (TooManyListenersException e) {
			e.printStackTrace();
		}

		serialPort.notifyOnDataAvailable(true);

		try {
			serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
		} catch (UnsupportedCommOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add a Pen Listener to the internal list. Pen Listeners' callbacks will be called when pen
	 * events are detected.
	 * 
	 * @param pl
	 */
	public void addPenListener(PenListener pl) {
		if (DEBUG) {
			System.out.println("Adding a listener...");
		}
		listeners.add(pl);
	}

	/**
	 * We process one byte at a time.
	 * 
	 * @param streamedByte
	 */
	private void handleByte(byte streamedByte) {

		// we got a new byte, so we push the others back
		bLastLast = bLast;
		bLast = bCurrent;
		bCurrent = streamedByte;

		// System.out.println(nextUp);

		// looking for the header portion of the data
		if (nextUp == StreamingField.HEADER) {
			if (bCurrent == LENGTH_SIMPLE_COORD && bLast == 0x00 && bLastLast == ID_SIMPLE_COORD) {
				// System.out.print("SAMPLE: ");
				// we are now in the sample mode
				// we should read the next 0x0B bytes as coordinates and force
				nextUp = StreamingField.X;
				numBytesCoord = 0;
			} else if (bCurrent == LENGTH_PEN_UP && bLast == 0x00 && bLastLast == ID_PEN_UP) {
				// System.out.println("PEN UP");

				double lastX = 0;
				double lastY = 0;
				if (lastSample != null) {
					lastX = lastSample.x;
					lastY = lastSample.y;
				}

				penIsUp = true;
				for (PenListener pl : listeners) {
					// on October 27, 2006, I changed behavior so that the pen up sample
					// now passes X & Y info
					// before, it passed x=0, y=0
					final PenSample penSample = new PenSample(lastX, lastY, 0, timestamp, true);
					// on June 12, 2006, I changed the behavior so that a .sample event is NOT
					// generated on pen up. It simply passes the pen up event with the timestamp
					// along...
					// thus, .sample is NEVER called with isPenUp() == true
					// pl.sample(penSample);
					pl.penUp(penSample);
				}
			}
		} else if (nextUp == StreamingField.X) { // 4 bytes long, X Coordinate
			numBytesCoord++;
			x = x << 8; // shift left by one byte
			x = x | (bCurrent & 0xFF); // attach the byte

			if (numBytesCoord == 4) {
				// after four loops, the x value is set to what we want
				nextUp = StreamingField.Y;
				numBytesCoord = 0;
			}
		} else if (nextUp == StreamingField.Y) { // 4 bytes long, Y Coordinate
			numBytesCoord++;
			y = y << 8;
			y = y | (bCurrent & 0xFF);

			if (numBytesCoord == 4) {
				// after four loops, the Y value is set to what we want
				nextUp = StreamingField.X_FRACTION;
				numBytesCoord = 0;
			}
		} else if (nextUp == StreamingField.X_FRACTION) {
			// save the value
			xFraction = (bCurrent >> 5) & 0x7; // last three bits
			nextUp = StreamingField.Y_FRACTION;
		} else if (nextUp == StreamingField.Y_FRACTION) {
			// save the value
			yFraction = (bCurrent >> 5) & 0x7; // last three bits
			nextUp = StreamingField.FORCE;
		} else if (nextUp == StreamingField.FORCE) {
			// save the value
			// mask it to make it unsigned
			// force = 128 - (((int) bCurrent) & 0xFF);
			force = 126 - (bCurrent & 0xFF) * 2;
			if (force < 0) {
				force = 0;
			}

			// IMPLEMENTATION NOTE:
			// type 'float' is NOT long enough to hold the orignial X/Y and their
			// fraction part simutaneously, since the original X/Y is too big
			// so we have to append the fraction part after conversion.

			timestamp = System.currentTimeMillis();

			// done with the whole streaming sample, so output it!
			if (DEBUG) {
				System.out.println("(" + (x + xFraction * 0.125) + ", " + (y + yFraction * 0.125)
						+ ")" + " f: " + force + " t: " + timestamp);
				System.out.flush();
			}

			final PenSample penSample = new PenSample(x + (xFraction * 0.125), y
					+ (yFraction * 0.125), force, timestamp, false);

			if (penIsUp) {
				penIsUp = false;
				for (PenListener pl : listeners) {
					// Nov 12, 2006, I changed the behavior of .penDown to NOT send a .sample
					// event... because It seems rather redundant.
					// so now, neither penUp nor penDown sends an extra sample event
					// penDown contains a true sample
					// penUp just contains the values of the most recent sample
					// It is designed this way to facilitate calibration.
					pl.penDown(penSample);
				}
			} else {
				// pen is already down, so we just generate .sample events...

				for (PenListener pl : listeners) {
					// June 12, 2006
					// (ronyeh) I changed the behavior of pen listeners a bit here...
					// now, we only pass ONE pen sample to all listeners
					// if there are multiple listeners, then they must make their own copies if
					// they're gonna keep them around
					// (OR beware that others may have your samples too)
					pl.sample(penSample);
				}
			}

			// keep it around so that we can pass this information to the pen up event!
			lastSample = penSample;

			// reset our values
			x = 0;
			y = 0;
			xFraction = 0;
			yFraction = 0;
			force = 0;

			// look for the header of the next sample
			nextUp = StreamingField.HEADER;
		}
	}

	/**
	 * Whenever data is available, send bytes one in a row to the processor.
	 * 
	 * @see javax.comm.SerialPortEventListener#serialEvent(javax.comm.SerialPortEvent)
	 */
	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {

		case SerialPortEvent.BI:
			// fall through
		case SerialPortEvent.OE:
			// fall through
		case SerialPortEvent.FE:
			// fall through
		case SerialPortEvent.PE:
			// fall through
		case SerialPortEvent.CD:
			// fall through
		case SerialPortEvent.CTS:
			// fall through
		case SerialPortEvent.DSR:
			// fall through
		case SerialPortEvent.RI:
			// fall through
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;

		case SerialPortEvent.DATA_AVAILABLE: // there is data to process!
			byte[] readBuffer = new byte[20];
			try {
				while (inputStream.available() > 0) {
					int numBytes = inputStream.read(readBuffer);

					// process the byte of data
					for (int i = 0; i < numBytes; i++) {
						handleByte(readBuffer[i]);
					}
				}
			} catch (IOException e) {
			}

			break;
		}
	}
}
