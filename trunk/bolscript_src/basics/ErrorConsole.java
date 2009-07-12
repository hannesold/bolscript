package basics;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ErrorConsole extends JFrame {
	JTextArea textArea;
	StringBuilder storage;
	public ErrorConsole(Dimension size) {
		super("Console");
		storage = new StringBuilder();
		textArea = new JTextArea();

		JPanel panel = new JPanel(new BorderLayout());
		JScrollPane scrollpanel = new JScrollPane(textArea);
		scrollpanel.setPreferredSize(size);
		panel.add(scrollpanel, BorderLayout.CENTER);
		this.setContentPane(panel);
		this.pack();
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
	}
	
	public void addText(String s) {
		storage.append(s);
	}
	public void refreshTextField() {
		textArea.setText(storage.toString());
	}
}
