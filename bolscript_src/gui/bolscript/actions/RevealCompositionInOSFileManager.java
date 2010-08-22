package gui.bolscript.actions;

import gui.bolscript.BrowserFrame;
import gui.bolscript.EditorFrame;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;

import bolscript.Master;
import bolscript.compositions.Composition;
import bolscript.compositions.DataState;
import bolscript.config.Config;

public class RevealCompositionInOSFileManager extends AbstractAction implements EnablingUpdatable {

	private BrowserFrame browser = null;
	private EditorFrame editor = null;
	private Composition composition = null;
	
	public RevealCompositionInOSFileManager(BrowserFrame browser, EditorFrame editor, Composition composition) {
		super();
		//if (composition == null) this.setEnabled(false);
		
		this.editor = editor;
		this.browser = browser;
		this.composition = composition; 
		
		switch (Config.operatingSystem) {
		case Windows:
			this.putValue(NAME, "Show in Windows Explorer");
			break;
		case Mac:
			this.putValue(NAME, "Reveal in Finder");
			break;
		default:
			this.putValue(NAME, "Reveal in File Manager");	
		}
		
		updateEnabling();
	}

	public boolean updateEnabling() {
		Composition comp = getMeantComposition();
		if (comp != null) {
			if (comp.getDataState() != DataState.NEW && 
				comp.getDataState() != DataState.MISSING &&
				comp.getDataState() != DataState.ERROR) {
				this.setEnabled(true);
				return true;
			} else {
				this.setEnabled(false);
				return true;
			}
		} else {
			this.setEnabled(false);
			return false;
		}
	}
	
	private Composition getMeantComposition() {
		if (browser != null) {
			ArrayList<Composition> selectedCompositions = browser.getCompositionListPanel().getSelectedCompositions();
			if (selectedCompositions.size() > 0) {
				return selectedCompositions.get(0);
			} else return null;
		} else if (editor != null) {
			return editor.getComposition();		
		} else if (composition != null) {
			return composition;
			
		} else return null;
	}
	
	public void actionPerformed(ActionEvent e) {
		if (browser != null) {
			ArrayList<Composition> selectedCompositions = browser.getCompositionListPanel().getSelectedCompositions();
			if (selectedCompositions.size() > 0) {
				Composition compToReveal = selectedCompositions.get(0);
				if (compToReveal.getLinkLocal() != null) {
					Master.master.revealFileInOSFileManager(compToReveal.getLinkLocal());
				} 
			}
		} else if (editor != null) {
				if (editor.getComposition() != null) {
					Composition composition = editor.getComposition();		
					String path = composition.getLinkLocal();
					if (path != null) {
						Master.master.revealFileInOSFileManager(path);
					}
				}
		} else if (composition != null) {
			String path = composition.getLinkLocal();
			if (path != null) {
				Master.master.revealFileInOSFileManager(path);
			}
			
		}
	}

}
