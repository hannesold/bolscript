package gui.bolscript;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import basics.GUI;

public class SearchPanel extends JPanel {
	private JTextField searchField;
	private JButton clearSearch;
	
	private ArrayList<KeyListener> listeners;
	
	public SearchPanel(String name) {
		super();
		init(name);
	}
	public void init (String name) {
		listeners = new ArrayList<KeyListener>();
		JLabel searchLabel = new JLabel(name);
		searchField = new JTextField("");
		searchField.setPreferredSize(new Dimension(200,20));
		//searchField.set
		//searchField.add
		clearSearch = new JButton("clear");
		this.add(searchLabel);
		this.add(searchField);
		this.add(clearSearch);
		clearSearch.addActionListener(GUI.proxyActionListener(this, "clearSearch"));
	}
	
	public void clearSearch(ActionEvent e) {
		searchField.setText("");
		for (KeyListener listener:listeners) {
			listener.keyReleased(null);
		}
	}
	public JTextField getSearchField() {
		return searchField;
		
	}
	public void addKeyListener(KeyListener listener) {
		searchField.addKeyListener(listener);
		listeners.add(listener);
	}
	
	public void removeKeyListener(KeyListener listener) {
		searchField.removeKeyListener(listener);
		listeners.remove(listener);
	}
}

