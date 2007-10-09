package papertoolkit.pen.statistics;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import papertoolkit.pen.ink.Ink;
import papertoolkit.pen.ink.InkStroke;
import papertoolkit.pen.synch.PenSynch;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.files.FileUtils;

public class TimeStatistics {
	public static void main(String[] args) {

		// Build a histogram
		// For each day.. keep a count of the ink strokes

		String path = "C:/Documents and Settings/Ron Yeh/Desktop/InkStrokeStatistics/2006_LosTuxtlas";

		// read in a single file
		// for each directory...
		List<File> files = FileUtils.listVisibleFiles(new File(path), "xml");

		// assume each file is a different person right now
		for (File xmlFile : files) {
			HashMap<String, Integer> strokesPerDay = new HashMap<String, Integer>();

			// regular pen synch
			PenSynch regularSynch = new PenSynch(xmlFile);
			List<Ink> importedInk = regularSynch.getImportedInk();

			for (Ink ink : importedInk) {
				// DebugUtils.println("New Ink: " + ink.getSourcePageAddress());
				List<InkStroke> strokes = ink.getStrokes();

				// assuem the strokes are ALREADY ordered in time... as that's how the pen does it...

				// assume any xml file is within a single year

				// keep the day of the year... if it changes, we tally a day

				for (InkStroke s : strokes) {
					Calendar cal = s.getLastTimestampAsCalendar();
					final int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
					final int year = cal.get(Calendar.YEAR);
					final String key = year + "_" + dayOfYear;
					if (strokesPerDay.containsKey(key)) {
						strokesPerDay.put(key, 1 + strokesPerDay.get(key));
					} else {
						strokesPerDay.put(key, 1);
					}
				}
			}

			// report dates from the ink... if it's a new day, tally a day
			DebugUtils.println("This file has ink from " + strokesPerDay.keySet().size() + " days.... "
					+ xmlFile.getName());
			Set<String> days = strokesPerDay.keySet();
			final ArrayList<String> daysList = new ArrayList<String>(days);
			Collections.sort(daysList);
			for (String day : daysList) {

				String[] year_day = day.split("_");
				int year = Integer.parseInt(year_day[0]);
				int dayOfYear = Integer.parseInt(year_day[1]);
				GregorianCalendar gregorian = new GregorianCalendar();
				gregorian.set(Calendar.YEAR, year);
				gregorian.set(Calendar.DAY_OF_YEAR, dayOfYear);
				DebugUtils.println("Day " + dayOfYear + ": " + gregorian.getTime() + " "
						+ strokesPerDay.get(day) + " strokes");
			}
		}
	}
}
