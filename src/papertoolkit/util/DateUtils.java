package papertoolkit.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class DateUtils {
	public static Date parseDateTime(String dateTimeString) {
		Date d = null;
		try {
			// assume it's the US time format...
			d = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a").parse(dateTimeString);
		} catch (ParseException e) {
			// assume it's a universal time format (anywhere but US)
			try {
				d = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(dateTimeString);
			} catch (ParseException parseException) {
				parseException.printStackTrace();
			}
		}
		return (d == null) ? new Date() : d;
	}
}
