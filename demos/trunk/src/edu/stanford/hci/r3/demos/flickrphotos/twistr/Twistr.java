package edu.stanford.hci.r3.demos.flickrphotos.twistr;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.sun.corba.se.internal.Interceptors.PIORB;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.demos.flickrphotos.FlickrPhoto;
import edu.stanford.hci.r3.demos.flickrphotos.PhotoDownloadr;
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

	private PhotoDisplay frame;

	private int lastTurnScore = 100;

	private List<FlickrPhoto> listOfPhotos;

	private int numPhotos;

	private int numTurns = 30;

	/**
	 * Keep track of the scores here.
	 */
	private int p1Score = 0;

	/**
	 * 
	 */
	private int p2Score = 0;

	private int[] possibleScores = new int[] { 5, 10, 20, 40, 50, 30 };

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
			new WinnerFrame(p2Score, p1Score);
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

		DebugUtils.println(photo1);
		DebugUtils.println(photo2);
		DebugUtils.println(photo3);
		DebugUtils.println(photo4);

		frame.placeFourPhotos(photo1.getFileLarge(), photo2.getFileLarge(), photo3.getFileLarge(),
				photo4.getFileLarge());
	}

	/**
	 * 
	 */
	public int getPointsForThisTurn() {
		// random, but bigger near the end...
		if (numTurns == 1) {
			// last turn!
			return lastTurnScore;
		} else {
			int index = (int) (Math.random() * possibleScores.length);
			if (numTurns < 10) {
				index += 2;
			} else if (numTurns < 20) {
				index++;
			}
			if (index >= possibleScores.length) {
				index = possibleScores.length - 1;
			}
			return possibleScores[index];
		}
	}

	/**
	 * @return
	 */
	public int nextTurn() {
		numTurns--;
		if (numTurns < 0) {
			numTurns = 0;
		}
		return numTurns;
	}

	/**
	 * @param numPointsThisTurn
	 * 
	 */
	public void p1WonThisTurn(int numPointsThisTurn) {
		p1Score += numPointsThisTurn;
	}

	public void p2WonThisTurn(int numPointsThisTurn) {
		p2Score += numPointsThisTurn;
	}

	private void setupApp() {
		application = new Application("Twistr");
		application.addSheet(new TwistrPrint(), new File("data/Flickr/Twistr.patternInfo.xml"));

		Pen pen2L = new Pen("P2Left", "localhost");
		Pen pen1L = new Pen("P1Left", "192.168.1.2");
		Pen pen1R = new Pen("P1Right", "192.168.1.3");
		Pen pen2R = new Pen("P2Right", "192.168.1.4");

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
		frame = new PhotoDisplay(this);
	}

}
