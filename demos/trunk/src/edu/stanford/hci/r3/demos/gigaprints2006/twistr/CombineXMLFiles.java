package edu.stanford.hci.r3.demos.gigaprints2006.twistr;

import java.io.File;

import papertoolkit.util.SystemUtils;
import papertoolkit.util.files.FileUtils;


/**
 * <p>
 * Because jickr doesn't work as seamlessly as I had wanted it to. We need to combine the separate
 * XML files I generated earlier.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class CombineXMLFiles {

	public static void main(String[] args) {
		// beware, this next call results in possibly inconsistent behavior
		SystemUtils.setWorkingDirectory(new File("data/Flickr/"));

		StringBuilder builder1 = FileUtils.readFileIntoStringBuffer(new File("Twistr1.xml"), true);
		StringBuilder builder2 = FileUtils.readFileIntoStringBuffer(new File("Twistr2.xml"), true);
		StringBuilder builder3 = FileUtils.readFileIntoStringBuffer(new File("Twistr3.xml"), true);
		StringBuilder builder4 = FileUtils.readFileIntoStringBuffer(new File("Twistr4.xml"), true);
		StringBuilder builder5 = FileUtils.readFileIntoStringBuffer(new File("Twistr5.xml"), true);
		StringBuilder builder6 = FileUtils.readFileIntoStringBuffer(new File("Twistr6.xml"), true);
		StringBuilder builder7 = FileUtils.readFileIntoStringBuffer(new File("Twistr7.xml"), true);
		StringBuilder builder8 = FileUtils.readFileIntoStringBuffer(new File("Twistr8.xml"), true);
		StringBuilder builder9 = FileUtils.readFileIntoStringBuffer(new File("Twistr9.xml"), true);

		// truncate the last tag, except for the last builder
		String b1 = builder1.substring(0, builder1.indexOf("</list>"));
		String b2 = builder2.substring(builder2.indexOf("<list>") + 6, builder2.indexOf("</list>"));
		String b3 = builder3.substring(builder3.indexOf("<list>") + 6, builder3.indexOf("</list>"));
		String b4 = builder4.substring(builder4.indexOf("<list>") + 6, builder4.indexOf("</list>"));
		String b5 = builder5.substring(builder5.indexOf("<list>") + 6, builder5.indexOf("</list>"));
		String b6 = builder6.substring(builder6.indexOf("<list>") + 6, builder6.indexOf("</list>"));
		String b7 = builder7.substring(builder7.indexOf("<list>") + 6, builder7.indexOf("</list>"));
		String b8 = builder8.substring(builder8.indexOf("<list>") + 6, builder8.indexOf("</list>"));
		String b9 = builder9.substring(builder9.indexOf("<list>") + 6);
		// builder9 gets to keep its </list>

		FileUtils.writeStringToFile(b1 + b2 + b3 + b4 + b5 + b6 + b7 + b8 + b9, new File(
				"TwistrFinal.xml"));
	}

}
