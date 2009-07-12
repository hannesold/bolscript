package gui.bolscript.actions;

import gui.bolscript.CompositionFrame;
import gui.bolscript.CompositionPanel;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import config.Config;

public class IncreaseBundling extends AbstractAction {
	CompositionPanel compPanel;

	public IncreaseBundling(CompositionPanel compPanel) {
		this.compPanel = compPanel;
		this.putValue(NAME, "Increase bundling");
	}
	
	public void actionPerformed(ActionEvent e) {
		compPanel.increaseBundling();
	}
	
}
