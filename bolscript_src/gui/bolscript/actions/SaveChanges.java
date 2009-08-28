package gui.bolscript.actions;

import gui.bolscript.EditorFrame;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;

import bolscript.Master;
import bolscript.compositions.Composition;
import bolscript.compositions.State;

public class SaveChanges extends AbstractAction {
	EditorFrame editor;
	boolean closeAfterwards;
	
	public SaveChanges(EditorFrame editor, boolean closeAfterwards) {
		this.editor = editor;
		if (editor == null) this.setEnabled(false);
		this.closeAfterwards = closeAfterwards;
		this.putValue(NAME, "Save");
	}
	
	public void actionPerformed(ActionEvent e) {
		if (editor.getComposition().getDataState() == State.NEW) {
			new SaveAs(editor).actionPerformed(null);
		} else {
			Master.master.saveEditor(editor);
		}
		if (closeAfterwards) Master.master.closeEditor(editor);
		
	}
	
}
