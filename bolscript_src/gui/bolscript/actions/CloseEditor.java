package gui.bolscript.actions;

import gui.bolscript.EditorFrame;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import bolscript.Master;
import bolscript.compositions.Composition;
import bolscript.compositions.DataState;

public class CloseEditor extends AbstractAction {
	EditorFrame editor;
	
	public CloseEditor(EditorFrame editor) {
		this.editor = editor;
		if (editor == null) this.setEnabled(false);
		this.putValue(NAME, "Close");  
	}
	
	public void actionPerformed(ActionEvent e) {
		closeEditor();
	}
	
	public void closeEditor() {
		Composition comp = editor.getComposition();
		//if (comp.getDataState() == Composition.CHANGED) {
		
		//check if a comps file was deleted outside or is missing somehow
		if (comp.getDataState() == DataState.EDITING) {
			if (comp.getLinkLocal() != null) {
				File f = new File(comp.getLinkLocal());
				if (!f.exists()) {
					comp.getDataState().connectfailed(comp);
					JOptionPane.showMessageDialog(editor, "<html>It seems that the file has been deleted outside of the program.<br>You will be asked if you want to save it again.</html>", "File missing", JOptionPane.WARNING_MESSAGE);
				}
			} else comp.getDataState().connectfailed(comp);
		}
		
		//the actual procedure
		if (comp.hasChangedSinceBackup() || (comp.getDataState() == DataState.NEW)) {
			Master.master.showSaveChangesDialog(editor);
		} else Master.master.closeEditor(editor);
	}
	
}
