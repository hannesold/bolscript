package gui.bolscript.actions;

import gui.bolscript.CompositionPanel;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import bols.BolName;
import bolscript.config.Config;

public class SetLanguage extends AbstractAction {
	CompositionPanel compPanel;
	int language;
	
	public SetLanguage(CompositionPanel comp, int language) {
		this.compPanel = comp;
		this.language = language;
		
		this.putValue(NAME, BolName.languageNames[language]);
	}
	
	public void actionPerformed(ActionEvent e) {
		compPanel.setLanguage(language);
		Config.standardLanguage = language;
	}
	
}
