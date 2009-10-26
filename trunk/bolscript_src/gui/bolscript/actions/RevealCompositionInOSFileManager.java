package gui.bolscript.actions;

import gui.bolscript.BrowserFrame;
import gui.bolscript.EditorFrame;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;

import bolscript.Master;
import bolscript.compositions.Composition;
import bolscript.config.Config;

public class RevealCompositionInOSFileManager extends AbstractAction {

	private BrowserFrame browser = null;
	private EditorFrame editor = null;
	
	public RevealCompositionInOSFileManager(BrowserFrame browser, EditorFrame editor) {
		super();
		//if (composition == null) this.setEnabled(false);
		
		this.editor = editor;
		this.browser = browser;
		
		switch (Config.OS) {
		case Config.WINDOWS:
			this.putValue(NAME, "Show in Windows Explorer");
			break;
		case Config.MAC:
			this.putValue(NAME, "Reveal in Finder");
			break;
		default:
			this.putValue(NAME, "Reveal in File Manager");	
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (browser != null) {
			ArrayList<Composition> selectedCompositions = Master.master.getSelectedCompositions();
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
		}
	}

}
