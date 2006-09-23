package edu.stanford.hci.r3.pen.gesture;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.thoughtworks.xstream.XStream;

import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.pen.gesture.GestureDatabase;

public class Test_GestureDatabase {
	private static BufferedReader stdin = new BufferedReader(new InputStreamReader( System.in ) );
	/**
	 * @param args
	 * @throws IOException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String databaseName = stdin.readLine();
		GestureDatabase database;
		try {
			File file = new File(databaseName + ".xml");
			FileReader reader = new FileReader(file);
			XStream xstream = new XStream();
			database = (GestureDatabase)xstream.fromXML(reader);
		} catch (FileNotFoundException e1) {
			database = new GestureDatabase(databaseName);
		}
		Pen pen = new Pen();
		pen.startLiveMode();
		pen.addLivePenListener(database.getListener());
		try {
			database.buildDatabase();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

}
