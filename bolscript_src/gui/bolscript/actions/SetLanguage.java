package gui.bolscript.actions;

import gui.bolscript.composition.CompositionPanel;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import bols.BolName;
import bolscript.config.UserConfig;

public class SetLanguage extends AbstractAction {
	CompositionPanel compPanel;
	int language;
	
	public SetLanguage(CompositionPanel comp, int language) {
		if (comp == null) this.setEnabled(false);
		this.compPanel = comp;
		this.language = language;
		
		this.putValue(NAME, BolName.languageNames[language]);
	}
	
	public void actionPerformed(ActionEvent e) {
		compPanel.setLanguage(language);
		UserConfig.standardLanguage = language;
	}
	
}
