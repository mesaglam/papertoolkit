package papertoolkit.demos.externallibs.java.swt;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class AppWindow extends ApplicationWindow {

	private class ExitAction extends Action {
		AppWindow window;

		public ExitAction(AppWindow win) {
			window = win;
			setText("E&xit@Ctrl+X");
			setToolTipText("Exit Application");
			setImageDescriptor(star);
		}

		public void run() {
			window.close();
		}
	}

	private MenuManager menuMgr;

	/**
	 * @see org.eclipse.jface.window.Window#initializeBounds()
	 */
	protected void initializeBounds() {
		getShell().setSize(640, 480);
		getShell().setLocation(10, 10);
	}

	/**
	 * It MUST be named createMenuManager. =)
	 * 
	 * @see org.eclipse.jface.window.ApplicationWindow#createMenuManager()
	 */
	protected MenuManager createMenuManager() {
		if (menuMgr == null) {
			menuMgr = new MenuManager();

			// file menu
			MenuManager fileMenu = new MenuManager("&File");
			fileMenu.add(new ExitAction(this));
			menuMgr.add(fileMenu);
		}
		return menuMgr;
	}

	/**
	 * @see org.eclipse.jface.window.Window#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		System.out.println("Created Contents.");
		composite = new Composite(parent, SWT.NONE);
		return composite;
	}

	/**
	 * Oct 3, 2006
	 */
	public static void main(String[] args) {
		AppWindow test = new AppWindow();
		test.setBlockOnOpen(true);
		test.open();
		Display.getCurrent().dispose();
	}

	private Composite composite;

	private ImageDescriptor moon = ImageDescriptor.createFromFile(AppWindow.class, "/icons/moon.png");

	private ImageDescriptor star = ImageDescriptor.createFromFile(AppWindow.class, "/icons/flystar.png");

	/**
	 * @param arg0
	 */
	public AppWindow() {
		super(null);
		addMenuBar();
		addStatusLine();
	}

	/**
	 * @see org.eclipse.jface.window.ApplicationWindow#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("JFace Example");
	}

}
