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

public class RemoveSelected extends AbstractAction {
	EditorFrame editor;
	boolean deleteAfterwards;
	JTable compositionTable;
	BrowserFrame browser;
	
	public RemoveSelected(BrowserFrame browser, JTable compositionTable) {
		this.compositionTable = compositionTable;
		this.browser = browser;
		this.putValue(NAME, "Remove");
	}
	
	public void actionPerformed(ActionEvent e) {
		int [] rows = compositionTable.getSelectedRows();
		ArrayList<Composition> compsToRemove = new ArrayList<Composition>();
		for (int i=0; i < rows.length; i++) {
			compsToRemove.add(((CompositionTableModel)compositionTable.getModel()).getComposition(
					compositionTable.getRowSorter().convertRowIndexToModel(rows[i])
			));
		}
		
		RemoveAndDeleteDialog dialog = new RemoveAndDeleteDialog(browser);
		dialog.setVisible(true);
		
		switch (dialog.getChoice()) {
		
		case RemoveAndDeleteDialog.ONLY_REMOVE:
			Master.master.removeCompositions(compsToRemove, false);
			break;
		case RemoveAndDeleteDialog.DELETE:
			Master.master.removeCompositions(compsToRemove, true);
			break;
		case RemoveAndDeleteDialog.CANCEL:
			Debug.temporary(this, "cancelled removing");
			break;
		}

	}
	
	
}