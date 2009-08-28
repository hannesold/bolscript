package gui.bolscript.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class Redo extends AbstractAction {

	private UndoManager undoManager;
	
	
	public Redo(UndoManager undoManager) {
		super();
		if (undoManager == null) this.setEnabled(false);
		this.undoManager = undoManager;
		this.putValue(NAME, "Redo");
		
	}

	public void actionPerformed(ActionEvent e) {
	       try {
               if (undoManager.canRedo()) {
                   undoManager.redo();
               }
           } catch (CannotUndoException ex) {
           }

	}
	

}
