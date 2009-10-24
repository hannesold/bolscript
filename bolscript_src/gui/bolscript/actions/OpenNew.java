package gui.bolscript.actions;

import gui.bolscript.EditorFrame;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import bolscript.Master;
import bolscript.config.GuiConfig;

public class OpenNew extends AbstractAction {
	EditorFrame editor;
	
	public OpenNew() {
		//this.editor = editor;
		this.putValue(NAME, "New Composition");
		this.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		        KeyEvent.VK_N, GuiConfig.MENU_SHORTKEY_MASK));
		//closer.setAccelerator();

	}
	
	public void actionPerformed(ActionEvent e) {
		Master.master.openNewComposition();
	}
	
}
