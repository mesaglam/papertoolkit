package edu.stanford.hci.r3.examples.java.swing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class Simple {
	private static MouseListener getLabelListener() {
		return new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				System.out.println("Clicked " + me.getClickCount() + " times.");
			}
		};
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Main App");
		JLabel label = new JLabel("Hola, Swing!");
		label.addMouseListener(getLabelListener());
		frame.add(label);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
