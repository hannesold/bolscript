package gui.bolscript.actions;

import gui.bolscript.EditorFrame;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import bolscript.Master;
import bolscript.compositions.Composition;
import bolscript.compositions.DataState;

public class OpenComposition extends AbstractAction {

	Composition composition;
	
	public OpenComposition(Composition composition) {
		super("Open");
		this.composition = composition;
		if (composition == null) this.setEnabled(false);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (composition != null) {
			Master.master.openEditor(composition);
		}
		
	}

	
}
