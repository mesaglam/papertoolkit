package edu.stanford.hci.r3.demos.flickrphotos.twistr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.demos.flickrphotos.FlickrPhoto;
import edu.stanford.hci.r3.demos.flickrphotos.PhotoDownloadr;
import edu.stanford.hci.r3.devices.Device;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * A Twistr game with Flickr photos. This is used as the first task in the GIGAprints study. We
 * should log actions to a file, so we can calculate the acquisition times...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Twistr {

	public static final String P1_LEFT = "P1Left";

	public static final String P1_RIGHT = "P1Right";

	public static final String P2_LEFT = "P2Left";

	public static final String P2_RIGHT = "P2Right";

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static void downloadPhotos() {
		Calendar startDay = Calendar.getInstance();
		startDay.set(Calendar.MONTH, 3); // 8==september
		startDay.set(Calendar.DATE, 1);
		startDay.set(Calendar.YEAR, 2006);

		// download photos
		PhotoDownloadr p = new PhotoDownloadr();
		p.downloadInterestingPhotos(3, startDay, 30, new File("data/Flickr/Twistr/"), new File(
				"data/Flickr/TwistrTemp.xml"));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// some setup code.... to be run once
		// downloadPhotos();

		// set up and run the application
		new Twistr();
	}

	private Application application;

	private PhotoDisplay display;

	private int lastTurnScore = 100;

	private List<FlickrPhoto> listOfPhotos;

	private int numPhotos;

	public static final int MAX_NUM_TURNS = 15;

	private int numTurns = MAX_NUM_TURNS;

	private String p1leftIPAddr;

	private String p1LeftPhotoName = new String();

	private String p1rightIPAddr;

	private String p1RightPhotoName = new String();

	/**
	 * Keep track of the scores here.
	 */
	private int p1Score = 0;

	private String p2leftIPAddr;

	private String p2LeftPhotoName = new String();

	private String p2rightIPAddr;

	private String p2RightPhotoName = new String();

	/**
	 * 
	 */
	private int p2Score = 0;

	private int[] possibleScores = new int[] { 5, 10, 20, 40, 50, 30 };

	private boolean p1RightOK;

	private boolean p2LeftOK;

	private boolean p1LeftOK;

	private boolean p2RightOK;

	private int numPointsThisTurn = 0;

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Twistr() {
		PaperToolkit.initializeLookAndFeel();

		// unserialize our TwistrFinal.xml file
		listOfPhotos = (ArrayList<FlickrPhoto>) PaperToolkit.fromXML(new File(
				"data/Flickr/TwistrFinal.xml"));
		numPhotos = listOfPhotos.size();
		DebugUtils.println(numPhotos + " photos in this game");
		setupFrame();
		setupApp();
	}

	/**
	 * 
	 */
	public void declareWinner() {
		if (p1Score > p2Score) {
			new WinnerFrame(p1Score, p2Score, "Player 1");
			DebugUtils.println("Player 1 is is the better twistrr!");
		} else if (p1Score < p2Score) {
			new WinnerFrame(p2Score, p1Score, "Player 2");
			DebugUtils.println("Player 2 is awesome!");
		} else {
			new WinnerFrame(p1Score);
			DebugUtils.println("There was a TIE!!!!");
		}
	}

	public int getP1Score() {
		return p1Score;
	}

	/**
	 * @return
	 */
	public int getP2Score() {
		return p2Score;
	}

	/**
	 * Returns four RANDOM photos every single time.
	 */
	public void getPhotos() {
		double r1 = Math.random();
		double r2 = Math.random();
		double r3 = Math.random();
		double r4 = Math.random();
		DebugUtils.println(r1 + " " + r2 + " " + r3 + " " + r4);

		int p1 = (int) (numPhotos * r1);
		int p2 = (int) (numPhotos * r2);
		int p3 = (int) (numPhotos * r3);
		int p4 = (int) (numPhotos * r4);

		FlickrPhoto photo1 = listOfPhotos.get(p1);
		FlickrPhoto photo2 = listOfPhotos.get(p2);
		FlickrPhoto photo3 = listOfPhotos.get(p3);
		FlickrPhoto photo4 = listOfPhotos.get(p4);

		p1LeftPhotoName = photo1.getFile().getName().replace(".jpg", "");
		p1RightPhotoName = photo2.getFile().getName().replace(".jpg", "");
		p2LeftPhotoName = photo3.getFile().getName().replace(".jpg", "");
		p2RightPhotoName = photo4.getFile().getName().replace(".jpg", "");

		DebugUtils.println(photo1);
		DebugUtils.println(photo2);
		DebugUtils.println(photo3);
		DebugUtils.println(photo4);

		display.placeFourPhotos(photo1.getFileLarge(), photo2.getFileLarge(),
				photo3.getFileLarge(), photo4.getFileLarge());
	}

	/**
	 * 
	 */
	public int getPointsForThisTurn() {

		// random, but bigger near the end...
		if (numTurns == 1) {
			// last turn!
			numPointsThisTurn = lastTurnScore;
			return lastTurnScore;
		} else {
			int index = (int) (Math.random() * possibleScores.length);
			if (numTurns < MAX_NUM_TURNS / 3.0) {
				index += 2;
			} else if (numTurns < 2 * MAX_NUM_TURNS / 3.0) {
				index++;
			}
			if (index >= possibleScores.length) {
				index = possibleScores.length - 1;
			}
			numPointsThisTurn = possibleScores[index];
			return possibleScores[index];
		}
	}

	/**
	 * 
	 */
	private void loadIPAddressesFromFile() {
		Properties p = new Properties();
		try {
			p.load(new FileInputStream(new File("data/Flickr/PenServers.ini")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		p1leftIPAddr = p.getProperty("P1Left");
		p2leftIPAddr = p.getProperty("P2Left");
		p1rightIPAddr = p.getProperty("P1Right");
		p2rightIPAddr = p.getProperty("P2Right");
	}

	/**
	 * @return
	 */
	public int nextTurn() {
		numTurns--;
		if (numTurns < 0) {
			numTurns = 0;
		}
		if (numTurns == 1) {
			Device.doPlaySound(new File("data/Flickr/Twistr/audio/For100points.wav"));
		} else if (numTurns == MAX_NUM_TURNS - 1) {
			Device.doPlaySound(new File("data/Flickr/Twistr/audio/LetsGo.wav"));
		}
		return numTurns;
	}

	private String[] p1Sounds = new String[] { "sacrebleu.wav", "en-garde.wav", "gitmemore.wav",
			"sweet.wav", "sorrylove.wav", "vamos.wav" };

	private String[] p2Sounds = new String[] { "touche.wav", "bon.wav", "bravo.wav", "cool.wav",
			"gorgeous.wav", "shock.wav", "wabamm.wav" };

	/**
	 * @param numPointsThisTurn
	 * 
	 */
	public void p1WonThisTurn(int numPointsThisTurn) {
		p1Score += numPointsThisTurn;

		String name = p1Sounds[(int) (Math.random() * p1Sounds.length)];
		File soundFile = new File("data/Flickr/Twistr/audio/" + name);
		Device.doPlaySound(soundFile);

		display.nextTurn();
	}

	public void p2WonThisTurn(int numPointsThisTurn) {
		p2Score += numPointsThisTurn;

		String name = p2Sounds[(int) (Math.random() * p2Sounds.length)];
		File soundFile = new File("data/Flickr/Twistr/audio/" + name);
		Device.doPlaySound(soundFile);

		display.nextTurn();
	}

	/**
	 * @param numPointsThisTurn
	 */
	public void p1And2TiedThisTurn(int numPointsThisTurn) {
		p1Score += numPointsThisTurn;
		p2Score += numPointsThisTurn;

		String name = p1Sounds[(int) (Math.random() * p1Sounds.length)];
		File soundFile = new File("data/Flickr/Twistr/audio/" + name);
		Device.doPlaySound(soundFile);

		display.nextTurn();
	}

	/**
	 * @param penName
	 * @param fileName
	 */
	public void penPressed(String penName, String fileName) {
		if (penName.equals(P1_LEFT)) {
			if (fileName.equals(p1LeftPhotoName)) {
				p1LeftOK = true;
				display.highlightP1LImage(true);
			} else {
				p1LeftOK = false;
				display.highlightP1LImage(false);
			}
		} else if (penName.equals(P2_RIGHT)) {
			if (fileName.equals(p2RightPhotoName)) {
				p2RightOK = true;
				display.highlightP2RImage(true);
			} else {
				p2RightOK = false;
				display.highlightP2RImage(false);
			}

		} else if (penName.equals(P2_LEFT)) {
			if (fileName.equals(p2LeftPhotoName)) {
				p2LeftOK = true;
				display.highlightP2LImage(true);
			} else {
				p2LeftOK = false;
				display.highlightP2LImage(false);
			}

		} else if (penName.equals(P1_RIGHT)) {
			if (fileName.equals(p1RightPhotoName)) {
				p1RightOK = true;
				display.highlightP1RImage(true);
			} else {
				p1RightOK = false;
				display.highlightP1RImage(false);
			}
			// highlight that picture and play a sound!
		}

		if (p1LeftOK && p1RightOK && p2LeftOK && p2RightOK) {
			// both scored at the same time!
			// this should never happen...
			p1And2TiedThisTurn(numPointsThisTurn);
			resetOKFlags();
		} else if (p1LeftOK && p1RightOK) {
			// p1 scores
			p1WonThisTurn(2 * numPointsThisTurn);
			resetOKFlags();
		} else if (p2LeftOK && p2RightOK) {
			// p2 scores
			p2WonThisTurn(2 * numPointsThisTurn);
			resetOKFlags();
		}
	}

	private void resetOKFlags() {
		p2LeftOK = false;
		p2RightOK = false;
		p1LeftOK = false;
		p1RightOK = false;
	}

	/**
	 * @param penName
	 * @param fileName
	 */
	public void penReleased(String penName, String fileName) {
		if (penName.equals(P2_LEFT)) {
			display.clearHighlightP2LImage();
			if (p2LeftOK) {
				p2WonThisTurn(numPointsThisTurn);
			}
			p2LeftOK = false;
		} else if (penName.equals(P1_RIGHT)) {
			display.clearHighlightP1RImage();
			if (p1RightOK) {
				p1WonThisTurn(numPointsThisTurn);
			}
			p1RightOK = false;
		} else if (penName.equals(P1_LEFT)) {
			display.clearHighlightP1LImage();
			if (p1LeftOK) {
				p1WonThisTurn(numPointsThisTurn);
			}
			p1LeftOK = false;
		} else if (penName.equals(P2_RIGHT)) {
			display.clearHighlightP2RImage();
			if (p2RightOK) {
				p2WonThisTurn(numPointsThisTurn);
			}
			p2RightOK = false;
		}
	}

	/**
	 * 
	 */
	private void setupApp() {
		application = new Application("Twistr");
		TwistrPrint print = new TwistrPrint();
		application.addSheet(print, new File("data/Flickr/Twistr.patternInfo.xml"));

		print.setParent(this);

		loadIPAddressesFromFile();

		Pen pen2L = new Pen(P2_LEFT, p2leftIPAddr);
		Pen pen1L = new Pen(P1_LEFT, p1leftIPAddr);
		Pen pen1R = new Pen(P1_RIGHT, p1rightIPAddr);
		Pen pen2R = new Pen(P2_RIGHT, p2rightIPAddr);

		application.addPen(pen2R);
		application.addPen(pen1R);
		application.addPen(pen1L);
		application.addPen(pen2L);

		// load it
		PaperToolkit p = new PaperToolkit(true);
		p.startApplication(application);
	}

	/**
	 * 
	 */
	private void setupFrame() {
		display = new PhotoDisplay(this);
	}
}
