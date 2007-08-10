package papertoolkit.tools.browse;

import java.awt.Color;

import edu.umd.cs.piccolo.PCanvas;

public class ContentCanvas extends PCanvas {

	private Color defaultLineColor;
	private Color defaultFillColor;

	public ContentCanvas() {
		useDefaultTheme();
	}

	public void useDefaultTheme() {
		setBackground(new Color(50, 50, 50));
		defaultLineColor = new Color(240, 240, 240);
		defaultFillColor = new Color(140, 140, 140);
	}

}
