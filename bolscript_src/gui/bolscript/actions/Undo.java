package gui.bolscript.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class Undo extends AbstractAction {

	private UndoManager undoManager;
	
	
	public Undo(UndoManager undoManager) {
		super();
		if (undoManager == null) this.setEnabled(false);
		this.undoManager = undoManager;
		this.putValue(NAME, "Undo");
		
	}

	public void actionPerformed(ActionEvent e) {
	       try {
               if (undoManager.canUndo()) {
                   undoManager.undo();
               }
           } catch (CannotUndoException ex) {
           }

	}


}
