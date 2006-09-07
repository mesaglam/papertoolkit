package edu.stanford.hci.r3.actions.types;

import java.net.MalformedURLException;
import java.net.URL;

public class OpenURL2ActionTest {
	public static void main(String[] args) {
		try {
			OpenURL2Action action = new OpenURL2Action(new URL("http://www.yahoo.com/"), OpenURL2Action.IE);
			action.invoke();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
