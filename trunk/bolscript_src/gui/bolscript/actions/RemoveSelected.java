package gui.bolscript.actions;

import gui.bolscript.BrowserFrame;
import gui.bolscript.EditorFrame;
import gui.bolscript.dialogs.RemoveAndDeleteDialog;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;

import basics.Debug;
import bolscript.Master;
import bolscript.compositions.Composition;
import bolscript.config.GuiConfig;

public class RemoveSelected extends AbstractAction implements EnablingUpdatable{
	EditorFrame editor;
	boolean deleteAfterwards;
	BrowserFrame browser;

	public RemoveSelected(BrowserFrame browser) {
		super("Remove");		
		this.browser = browser;
		updateEnabling();
	}

	public void actionPerformed(ActionEvent e) {

		//Master.master.
		ArrayList<Composition> compsToRemove = getCompositionsToRemove();

		RemoveAndDeleteDialog dialog = new RemoveAndDeleteDialog(browser);
		GuiConfig.setVisibleAndAdaptFrameLocation(dialog);

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
	private ArrayList<Composition> getCompositionsToRemove() {
		//TODO Check why synchronization is necassary at this point!!!

		ArrayList<Composition> compsToRemove = 
			new ArrayList<Composition>(browser
					.getCompositionListPanel()
					.getSelectedCompositions());
		//Debug.temporary(this, "gathering compositions to remove: " + compsToRemove);
		int nrOfCompsToRemove = compsToRemove.size();
		if (nrOfCompsToRemove>0) {

			for (int i=0; i < nrOfCompsToRemove; i++) {
				Composition comp = compsToRemove.get(i);
				boolean remove = false;

				if (comp != null) {

					if (comp.getDataState() != null) {
						switch (comp.getDataState()) {
						case CONNECTED:
							break;
						case EDITING:
							break;
						default:
							remove = true;
						}
					} else remove = true;
				} else remove = true;

				if ( remove && comp != null) {
					//Debug.temporary(this, "not removable: " + comp);
					compsToRemove.remove(comp);
				} else {
					//Debug.temporary(this, "keeping removable: " + comp);
				}
			}
		}
		return compsToRemove;
	}

	@Override
	public boolean  updateEnabling() {
		if (browser == null) {
			this.setEnabled(false);
			return false;
		} else {
			boolean newState = (getCompositionsToRemove().size() >0); 
			enabled = newState;
			return newState;
		}
	}


}
