package basics;

import gui.bolscript.actions.ToggleConsole;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import bolscript.config.Config;

public class ErrorConsole extends JFrame implements WindowListener {
	TextArea textArea;
	StringBuilder storage;
	
	/*
	 * instanciates a (hidden) debug console
	 */
	public ErrorConsole(Dimension size) {
		super("Console");
		storage = new StringBuilder();
		textArea = new TextArea();

		JPanel panel = new JPanel(new BorderLayout());
		//JScrollPane scrollpanel = new JScrollPane(textArea);
		//scrollpanel.setPreferredSize(size);
		//panel.add(scrollpanel, BorderLayout.CENTER);
		panel.add(textArea);
		this.setContentPane(panel);
		this.pack();
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setVisible(false);
		
		this.addWindowListener(this);
		
	}
	
	public void addText(String s) {
		synchronized (storage) {
			storage.append(s);
		}
		
	}
	
	public void refreshTextField() {
		synchronized(storage) {
			textArea.setText(storage.toString());
			textArea.setCaretPosition(textArea.getText().length()-1);
			
			/*textArea.setBackground(Color.black);
			textArea.setForeground(Color.white);*/
			
		}
	}
	
	public void refreshLater() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				refreshTextField();}
			});
	}


	@Override
	public void windowClosing(WindowEvent e) {
		ToggleConsole.getStandard().actionPerformed(null);
	}

	@Override
	public void windowActivated(WindowEvent e) {}
	@Override
	public void windowClosed(WindowEvent e) {}
	@Override
	public void windowDeactivated(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e) {}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowOpened(WindowEvent e) {}
	
}
