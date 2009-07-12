package gui.bolscript.actions;

import gui.bolscript.EditorFrame;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;

import bolscript.Master;
import bolscript.compositions.Composition;

public class CancelClosingEditor extends AbstractAction {
	EditorFrame editor;
	boolean closeAfterwards;
	
	public CancelClosingEditor(EditorFrame editor) {
		this.editor = editor;
		this.putValue(NAME, "Cancel");
	}
	
	public void actionPerformed(ActionEvent e) {
	}
	
}
