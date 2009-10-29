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
import bolscript.compositions.DataState;

public class RemoveSelected extends AbstractAction implements EnablingUpdatable{
	EditorFrame editor;
	boolean deleteAfterwards;
	BrowserFrame browser;

	public RemoveSelected(BrowserFrame browser) {
		super("Remove");
		if (browser == null) {
			this.setEnabled(false);
		}
		this.browser = browser;
	}



	public void actionPerformed(ActionEvent e) {

		//Master.master.
		ArrayList<Composition> compsToRemove = getCompositionsToRemove();

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

	/**
	 * Gathers all removable Compositions from the browsers composition list tables selection.
	 * @return
	 */
	private synchronized ArrayList<Composition>  getCompositionsToRemove() {
		//TODO Check why synchronization is necassary at this point!!!
		
		ArrayList<Composition> compsToRemove = new ArrayList<Composition>(browser.getCompositionListPanel().getSelectedCompositions());
		//Debug.temporary(this, "gathering compositions to remove: " + compsToRemove);
		if (compsToRemove.size()>0) {
			for (Composition comp: compsToRemove) {
				if (comp != null) {
					if (comp.getDataState() != null) {
						switch (comp.getDataState()) {
						case CONNECTED:
							break;
						case EDITING:
							break;
						default:
							compsToRemove.remove(comp);
						}
					} else compsToRemove.remove(comp);
				} else compsToRemove.remove(comp);
			}
		}
		return compsToRemove;
	}

	@Override
	public boolean  updateEnabling() {
		boolean newState = (getCompositionsToRemove().size() >0); 
		enabled = newState;
		return newState;
	}


}
