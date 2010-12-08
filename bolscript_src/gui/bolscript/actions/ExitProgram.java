package gui.bolscript.actions;

import gui.bolscript.BrowserFrame;
import gui.bolscript.EditorFrame;
import gui.bolscript.dialogs.RemoveAndDeleteDialog;
import gui.bolscript.tables.CompositionTableModel;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JTable;

import basics.Debug;
import bolscript.Master;
import bolscript.compositions.Composition;

public class ExitProgram extends AbstractAction {
	
	public ExitProgram() {
		this.putValue(NAME, "Quit");
	}
	
	public void actionPerformed(ActionEvent e) {
		Master.master.attemptExit();
	}
	
	
}
