package papertoolkit.actions.types;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import papertoolkit.actions.Action;
import papertoolkit.actions.types.graphicscommands.DrawImageCommand;
import papertoolkit.actions.types.graphicscommands.DrawRectCommand;
import papertoolkit.actions.types.graphicscommands.DrawShape;
import papertoolkit.actions.types.graphicscommands.FillRectCommand;
import papertoolkit.actions.types.graphicscommands.GraphicsCommand;
import papertoolkit.actions.types.graphicscommands.SetColorCommand;
import papertoolkit.util.WindowUtils;
import papertoolkit.util.graphics.GraphicsUtils;


/**
 * <p>
 * Ideally, this is done with reflection, runtime class loading and compilation, etc. But, that's
 * crazy. And we're not gonna do it before the CHI deadline. =) In fact, let's do a little test and
 * try to serialize a JPanel and send it across the wire.
 * 
 * First, test this on a local ActionReceiver. =)
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class DisplayGraphicsAction implements Action {

	/**
	 * <p>
	 * Regardless of where the graphics commands are sent, you need to eventually get a LOCAL
	 * display and a LOCAL graphics object. This static inner class allows us to do just that.
	 * </p>
	 */
	public static class LocalDisplay extends JFrame {

		private static LocalDisplay instance = null;

		/**
		 * @return
		 */
		public static LocalDisplay getInstance() {
			if (instance == null) {
				instance = new LocalDisplay();
			}
			return instance;
		}

		private List<GraphicsCommand> commands = Collections
				.synchronizedList(new ArrayList<GraphicsCommand>());

		private JPanel mainPanel;

		/**
		 * 
		 */
		private LocalDisplay() {
			super("Local Display");
			setLocation(0, 0);
			super.setContentPane(getMainPanel());
			setSize(800, 600);
			WindowUtils.getWindowOrigin(this, WindowUtils.DESKTOP_CENTER);
			setVisible(false);
		}

		/**
		 * @param commandsToAppend
		 */
		public void addRenderingCommands(List<GraphicsCommand> commandsToAppend) {
			commands.addAll(commandsToAppend);
		}

		/**
		 * @return
		 */
		private JPanel getMainPanel() {
			if (mainPanel == null) {
				mainPanel = new JPanel() {
					protected void paintComponent(Graphics g) {
						super.paintComponent(g);
						final Graphics2D g2d = (Graphics2D) g;
						g2d.setRenderingHints(GraphicsUtils.getBestRenderingHints());

						// run my commands....
						for (GraphicsCommand gcmd : commands) {
							gcmd.invoke(g2d);
						}
					}
				};
			}
			return mainPanel;
		}

		/**
		 * @param commandsToRun
		 */
		public void setRenderingCommands(List<GraphicsCommand> commandsToRun) {
			commands.clear();
			addRenderingCommands(commandsToRun);
		}
	}

	/**
	 * 
	 */
	private List<GraphicsCommand> commandsToRun = new ArrayList<GraphicsCommand>();

	/**
	 * 
	 */
	private boolean exitOnCloseFlag;

	private int extendedState = JFrame.NORMAL;

	private int frameHeight = 600;

	private Point frameOrigin;

	private int frameWidth = 800;

	private boolean bringToFront;

	/**
	 * 
	 */
	public DisplayGraphicsAction() {

	}

	/**
	 * @param imgFile
	 * @param x
	 * @param y
	 */
	public void drawImage(File imgFile, int x, int y) {
		commandsToRun.add(new DrawImageCommand(imgFile, x, y));
	}

	/**
	 * @param imgFile
	 * @param x
	 * @param y
	 * @param scaleFactor
	 */
	public void drawImage(File imgFile, int x, int y, double scaleFactor) {
		commandsToRun.add(new DrawImageCommand(imgFile, x, y, AffineTransform.getScaleInstance(
				scaleFactor, scaleFactor)));
	}

	/**
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public void drawRect(int x, int y, int w, int h) {
		commandsToRun.add(new DrawRectCommand(x, y, w, h));
	}

	/**
	 * @param s
	 */
	public void drawShape(Shape s) {
		commandsToRun.add(new DrawShape(s));
	}

	/**
	 * Queues up a fillRect...
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public void fillRect(int x, int y, int w, int h) {
		commandsToRun.add(new FillRectCommand(x, y, w, h));
	}

	/**
	 * Causes the graphics commands to be displayed on our local device's display channel.
	 * 
	 * @see papertoolkit.actions.Action#invoke()
	 */
	public void invoke() {
		LocalDisplay display = LocalDisplay.getInstance();
		display.setSize(frameWidth, frameHeight);
		display.setLocation(frameOrigin);
		display.setExtendedState(extendedState);
		if (exitOnCloseFlag) {
			display.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} else {
			display.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		}

		display.addRenderingCommands(commandsToRun);
		display.setVisible(true); // if it's already visible, nothing changes
		if (bringToFront) {
			// a hack to bring the window to the top...
			display.setAlwaysOnTop(true);
			display.setAlwaysOnTop(false);
		}
	}

	/**
	 * 
	 */
	public void maximizeFrame() {
		extendedState = JFrame.MAXIMIZED_BOTH;
	}

	/**
	 * 
	 */
	public void minimizeFrame() {
		extendedState = JFrame.ICONIFIED;
	}

	/**
	 * 
	 */
	public void restoreFrame() {
		extendedState = JFrame.NORMAL;
	}

	/**
	 * @param col
	 */
	public void setColor(Color col) {
		commandsToRun.add(new SetColorCommand(col));
	}

	/**
	 * @param flag
	 */
	public void setExitOnClose(boolean flag) {
		exitOnCloseFlag = flag;
	}

	public void setFrameLocation(Point windowOrigin) {
		frameOrigin = windowOrigin;
	}

	public void setFrameSize(int w, int h) {
		frameWidth = w;
		frameHeight = h;
	}

	public void setBringToFront(boolean flag) {
		bringToFront = flag;
	}
}
