package gui.bolscript.actions;

import gui.bolscript.EditorFrame;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import basics.Debug;
import basics.SuffixFilter;
import bolscript.Master;
import bolscript.compositions.CompositionBase;
import bolscript.compositions.State;
import config.Config;

public class SaveAs extends AbstractAction {

	private EditorFrame editor;
	
	
	public SaveAs(EditorFrame editor) {
		super();
		this.editor = editor;
		this.putValue(NAME, "Save as...");
		
	}

	public void actionPerformed(ActionEvent e) {
		String path;
		if (editor.getComposition().getDataState() == State.NEW) {
			path = CompositionBase.generateFilename(editor.getComposition(), Config.bolscriptSuffix);
		} else path = editor.getComposition().getLinkLocal();
		
		File currentFile = new File(path);
		FileDialog fileDialog = new FileDialog(editor, "Save As", FileDialog.SAVE);
		fileDialog.setDirectory(currentFile.getPath());
		fileDialog.setFilenameFilter(new SuffixFilter(Config.bolscriptSuffix));
		fileDialog.setFile(currentFile.getName());
		
		fileDialog.setVisible(true);

		if (fileDialog.getFile() != null) {
			
		String filename = fileDialog.getDirectory() + Config.fileSeperator + fileDialog.getFile();
		
			/*if (!filename.endsWith(Config.bolscriptSuffix)) {
				filename = filename + Config.bolscriptSuffix;
			}*/
			File file = new File(filename);
			Debug.debug(this, "Save As : " + filename);
			Master.master.saveEditorAs(editor, filename);
		}
		
		fileDialog.dispose();
		//if (fileDialog.get)
		
		
		//fileDialog.add
	}

}
