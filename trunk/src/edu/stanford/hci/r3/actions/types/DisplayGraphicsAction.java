package edu.stanford.hci.r3.actions.types;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.stanford.hci.r3.actions.R3Action;
import edu.stanford.hci.r3.actions.types.graphicscommands.FillRectMethod;
import edu.stanford.hci.r3.actions.types.graphicscommands.GraphicsCommand;
import edu.stanford.hci.r3.util.WindowUtils;
import edu.stanford.hci.r3.util.graphics.GraphicsUtils;

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
public class DisplayGraphicsAction implements R3Action {

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
					@Override
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

	private List<GraphicsCommand> commandsToRun = new ArrayList<GraphicsCommand>();

	private boolean exitOnCloseFlag;

	/**
	 * 
	 */
	public DisplayGraphicsAction() {

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
		commandsToRun.add(new FillRectMethod(x, y, w, h));
	}

	/**
	 * Causes the graphics commands to be displayed on our local device's display channel.
	 * 
	 * @see edu.stanford.hci.r3.actions.R3Action#invoke()
	 */
	public void invoke() {
		LocalDisplay display = LocalDisplay.getInstance();
		if (exitOnCloseFlag) {
			display.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		display.addRenderingCommands(commandsToRun);
		display.setVisible(true); // if it's already visible, nothing changes
	}

	public void setExitOnClose(boolean flag) {
		exitOnCloseFlag = flag;
	}
}
