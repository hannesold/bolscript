package gui.bolscript.actions;


import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import bolscript.Master;

public class OpenPreferences extends AbstractAction {
	
	public OpenPreferences() {
		this.putValue(NAME, "Preferences");
	}
	
	public void actionPerformed(ActionEvent e) {
		showSettings();
	}
	
	public void showSettings() {
		Master.master.showPreferences();
	}
	
}
