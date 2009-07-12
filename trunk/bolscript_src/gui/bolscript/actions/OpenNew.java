package gui.bolscript.actions;

import gui.bolscript.EditorFrame;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import bolscript.Master;
import bolscript.compositions.Composition;

public class OpenNew extends AbstractAction {
	EditorFrame editor;
	
	public OpenNew() {
		//this.editor = editor;
		this.putValue(NAME, "New Composition");
		this.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		        KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		//closer.setAccelerator();

	}
	
	public void actionPerformed(ActionEvent e) {
		Master.master.openNewComposition();
	}
	
}
