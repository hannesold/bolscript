package gui.bolscript.actions;

import gui.bolscript.CompositionFrame;
import gui.bolscript.CompositionPanel;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import bolscript.config.Config;


public class IncreaseFontSize extends AbstractAction {
	CompositionPanel compPanel;

	public IncreaseFontSize(CompositionPanel compPanel) {
		this.compPanel = compPanel;
		this.putValue(NAME, "Increase fontsize");
	}
	
	public void actionPerformed(ActionEvent e) {
		compPanel.increaseFontSize();
	}
	
}
