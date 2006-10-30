package edu.stanford.hci.r3.actions.types;

public class Test_RunJavaAppAction {
	public static void main(String[] args) {
		RunJavaAppAction action = new RunJavaAppAction(DummyWithMain.class);
		action.invoke();
	}
}
