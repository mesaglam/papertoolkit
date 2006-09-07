package edu.stanford.hci.r3.actions.types;

import java.net.MalformedURLException;
import java.net.URL;

public class OpenURLActionTest {
	public static void main(String[] args) {
		try {
			OpenURLAction o = new OpenURLAction(new URL("http://www.yahoo.com/"));
			System.out.println("Invoking now...");
			o.invoke();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
