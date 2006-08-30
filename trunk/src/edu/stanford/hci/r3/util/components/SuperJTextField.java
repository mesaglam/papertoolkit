/**
 * 
 */
package edu.stanford.hci.r3.util.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.text.*;

/**
 * This JTextField is great for inputting text. It offers amazing features, so much so that you will
 * want to use it everywhere... After reading this, you will never use a JTextField again!
 * 
 * As you type, the 10 nearest above, possible Entries are quickly trimmed down, due to prefix
 * search. A typing listener needs to be installed needs to check for enter or backspace...
 * 
 * Features: Autocomplete, Input Hints
 * 
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> ( ronyeh(AT)cs.stanford.edu )
 * @changelog August 19, 2005 -- Merged AutoCompleteTextField with SuperJTextField (ronyeh)
 * @changelog March 10, 2005 -- Created AutoCompleteTextField
 * 
 * TODO: Implement ComboBoxEditor to allow this to be an awesome combobox editor field.
 */
public class SuperJTextField extends JTextField {

	/**
	 * This private class provides autocomplete functionality.
	 * 
	 * @author ronyeh
	 */
	private class SuperDocument extends PlainDocument {

		private SuperJTextField parent;

		/**
		 * @param p
		 */
		public SuperDocument(SuperJTextField p) {
			parent = p;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.text.Document#insertString(int, java.lang.String,
		 *      javax.swing.text.AttributeSet)
		 */
		public void insertString(int offs, String str, AttributeSet attributeSet)
				throws BadLocationException {
			super.insertString(offs, str, attributeSet);
			// check the current text, to see if it matches any prefix
			// and that the current text is not a parsable number
			// if there is only one match, then enter the rest of that text into
			// this document!

			final String myText = parent.getText().toLowerCase();
			String match = null;

			// if the current text is a parsable number, then we're done!
			// don't autocomplete numbers
			try {
				// try to cause an exception here; if an exception, that means that it is NOT a number
				// The exception will skip the return; line, and we will be able to continue
				NumberFormat.getInstance().parse(myText);
				// System.out.println("Number is: " + n);
				return;
			} catch (ParseException e) {
				// do nothing
				// e.printStackTrace();
			}

			// look at all the strings that are possible completions
			String restOfText = null;

			// we should pull the first one (MRU), because we always populate head first....
			for (final String testString : possibleCompletions) {
				// match! The toLowerCase() thing replicates Excel's behavior
				if (testString.toLowerCase().startsWith(myText)) {

					// instead of shortest (this was a bug in LosTuxtlas)
					// choose the most recent
					match = testString;
					restOfText = match.substring(myText.length());

					// a match, autocomplete!
					// Logger.log("AutoComplete::One Match Found: " + match);
					// Logger.log("The rest of the text is: " + restOfText);
					super.insertString(offs + 1, restOfText, attributeSet);
					final Caret c = parent.getCaret();
					c.setDot(match.length());
					c.moveDot(offs + 1);
					autocompleteJustHappened = true;
					break;
				}
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final JFrame f = new JFrame();
		SuperJTextField stf = new SuperJTextField("Hello");
		{
			stf.setFont(new Font("Verdana", Font.PLAIN, 11));
			stf.setInputHintFont(new Font("Verdana", Font.ITALIC, 11));
			stf.setInputHint("<Search>");
		}
		{
			stf.addPossibleCompletion("Bumpkin");
			stf.addPossibleCompletion("Pumpkin");
			stf.addPossibleCompletion("Word Up");
			stf.addPossibleCompletion("821.56");
		}

		f.add(stf, BorderLayout.CENTER);
		f.add(new JButton("X"), BorderLayout.EAST);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setVisible(true);
	}

	private boolean autocompleteJustHappened = false;

	private boolean focusOnEdit;

	private Color hintColor = Color.LIGHT_GRAY;

	private String inputHint;

	private Font inputHintFont;

	protected boolean inputHintShowing;

	// last key user pressed
	protected int lastKeyCode = -1;

	private boolean lastKeyShiftDown;

	// for each completion, we track how many times it has been entered into this box
	// it should either match to the most popular, or the latest...
	// I think either one will work, but in Los Tuxtlas, basically the latest was good
	// because people corrected mistakes, and our UI sucked rocks.
	// Excel basically does the one match... If there are other matches, it does nothing (as opposed
	// to putting in wrong data; tradeoff between efficiency and errors)
	// the real solution here would be to drop down a text box with popular completions, like most
	// cool text boxes nowadays.
	// for now, we will just do the most recent.
	// protected HashMap<String, Integer> possibleCompletions = new HashMap<String, Integer>();
	private LinkedList<String> possibleCompletions = new LinkedList<String>();

	//
	protected Color textColor = Color.BLACK;

	//
	protected Font textFont;

	/**
	 * 
	 */
	public SuperJTextField() {
		setup();
	}

	/**
	 * @param i
	 */
	public SuperJTextField(int numCols) {
		super(numCols);
		setup();
	}

	/**
	 * @param string
	 */
	public SuperJTextField(String string) {
		super();
		setup();
		setText(string);
	}

	/**
	 * @param defaultText
	 * @param numCols
	 */
	public SuperJTextField(String defaultText, int numCols) {
		super(numCols);
		setup();
		setText(defaultText);
	}

	/**
	 * OLD: Add an autocomplete entry, and keep track of the number of times it has been "added" to
	 * the completions list.
	 * 
	 * NEW: Just keep track of completions, and match with the most recent (earliest) in the list.
	 * 
	 * @param completion
	 */
	public void addPossibleCompletion(String completion) {
		possibleCompletions.addFirst(completion);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JTextField#createDefaultModel()
	 */
	protected Document createDefaultModel() {
		return new SuperDocument(this);
	}

	/**
	 * Asks for the last key pressed. It can be used for fun things, like... How did the user
	 * set/confirm the value, and exit this box?
	 * 
	 * @return
	 */
	public int getExitKey() {
		return lastKeyCode;
	}

	/**
	 * @return
	 */
	public String getInputHint() {
		return inputHint;
	}

	/**
	 * @return
	 */
	public boolean getLastKeyShiftDown() {
		return lastKeyShiftDown;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.text.JTextComponent#getText()
	 */
	public String getText() {
		if (inputHintShowing) {
			return "";
		} else {
			return super.getText();
		}
	}

	/**
	 * @return if the JTextField has no text showing...
	 */
	protected boolean noText() {
		return super.getText() == null || super.getText().equals("");
	}

	/**
	 * @param completion
	 */
	public void removePossibleCompletion(String completion) {
		possibleCompletions.remove(completion);
	}

	public void setFocusGainedOnEdit(boolean foe) {
		focusOnEdit = foe;
	}

	/**
	 * Set it to null to turn this feature off.
	 * 
	 * @param hint
	 */
	public void setInputHint(String hint) {
		inputHint = hint;
		if (noText()) {
			setInputHintVisible(true);
		}
	}

	/**
	 * @param font
	 */
	private void setInputHintFont(Font font) {
		inputHintFont = font;
	}

	/**
	 * @param toBeVisible
	 */
	private void setInputHintVisible(boolean toBeVisible) {
		if (toBeVisible) { // show
			if (inputHint != null && !inputHint.equals("")) {
				setForeground(hintColor);
				super.setText(inputHint);
				setFont(inputHintFont);
				inputHintShowing = true;
			}
		} else { // hide
			if (inputHintShowing) {
				super.setText("");
				setForeground(textColor);
				setFont(textFont);
				inputHintShowing = false;
			}
		}
	}

	/**
	 * @param possibleCompletions
	 */
	public void setPossibleCompletions(final List<String> possible) {
		possibleCompletions.clear(); // clear the list
		for (final String entry : possible) {
			possibleCompletions.addFirst(entry);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.text.JTextComponent#setText(java.lang.String)
	 */
	@Override
	public void setText(String t) {
		super.setText(t);

		if (noText()) {
			setInputHintVisible(true);
		} else {
			setForeground(textColor);
			inputHintShowing = false;
		}
	}

	/**
	 * 
	 */
	private void setup() {
		textFont = getFont();
		inputHintFont = getFont();

		addFocusListener(new FocusListener() {

			// if someone clicks on me
			// if there was no text to begin with, clear the textfield
			// if there was text to begin with, set it
			public void focusGained(FocusEvent arg0) {
				setForeground(textColor);
				setFont(textFont);

				setInputHintVisible(false);

				final int length = SuperJTextField.super.getText().length();

				// if autocomplete did not just happen, we select everything
				// if autocomplete JUST happened, we do nothing
				// if we focused when we tried to edit a cell, we do NOT select everything
				if (!autocompleteJustHappened && !focusOnEdit) {
					select(0, length);
				}
				// for fixing the JTable bug
				autocompleteJustHappened = false;
				focusOnEdit = false;
			}

			// if someone clicks or tabs away
			// if there was text to begin with, just gray it out
			public void focusLost(FocusEvent arg0) {
				// System.out.println("Focus Lost");

				if (noText()) {
					setInputHintVisible(true);
				}
				autocompleteJustHappened = false;
			}
		});

		addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent ke) {

				// what was the last key pressed?
				lastKeyCode = ke.getKeyCode();
				// System.out.println(lastKeyCode);

				lastKeyShiftDown = ke.isShiftDown();
			}
		});
	}

}
