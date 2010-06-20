package gui.bolscript.actions;

import gui.bolscript.CompositionFrame;
import gui.bolscript.composition.CompositionPanel;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;

import bolscript.config.Config;


public class PrintPreview extends AbstractAction {
	CompositionPanel compPanel;

	public PrintPreview(CompositionPanel compPanel) {
		this.compPanel = compPanel;
		this.putValue(NAME, "Show pdf page borders");
	}
	
	public void actionPerformed(ActionEvent e) {
		
		boolean newSetting = compPanel.isInPdfPreviewMode();
		
		if (JCheckBoxMenuItem.class.isInstance(e.getSource())) {
			newSetting = ((JCheckBoxMenuItem) e.getSource()).isSelected();
		} 
		compPanel.setPdfPreviewMode(newSetting);
	}
	
	
}
