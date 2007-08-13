package papertoolkit.demos.externallibs.java.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class Simple {

	public static void main(String[] args) {
		// UI Thread! =) OR, just use the main thread as the UI thread.
		// new Thread(new Runnable() {
		// public void run() {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Simple Example!");

		// add a label
		Label label = new Label(shell, SWT.NONE);
		label.setText("Ni hao, SWT!");
		label.setBounds(10, 10, 100, 20);

		// add a mouse listener
		label.addMouseListener(getLabelListener());

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
		// }
		// }).start();
	}

	private static MouseListener getLabelListener() {
		return new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				System.out.println("Clicked " + e.count + " times.");
			}
		};
	}
}
