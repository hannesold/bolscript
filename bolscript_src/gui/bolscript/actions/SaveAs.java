package gui.bolscript.actions;

import gui.bolscript.EditorFrame;
import gui.bolscript.dialogs.SaveOutsideTablaFolder;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import basics.Debug;
import basics.SuffixFilter;
import bolscript.Master;
import bolscript.compositions.CompositionBase;
import bolscript.compositions.DataState;
import bolscript.config.Config;

public class SaveAs extends AbstractAction {

	private EditorFrame editor;


	public SaveAs(EditorFrame editor) {
		super();
		if (editor == null) this.setEnabled(false);
		this.editor = editor;
		this.putValue(NAME, "Save as...");

	}

	public void actionPerformed(ActionEvent e) {
		String path;
		
		
		if (editor.getComposition().getDataState() == DataState.NEW) {
			path = CompositionBase.generateFilename(editor.getComposition(), Config.bolscriptSuffix);
		} else path = editor.getComposition().getLinkLocal();

		File currentFile = new File(path);
		FileDialog fileDialog = new FileDialog(editor, "Save As", FileDialog.SAVE);
		Debug.temporary(this, "setting path to " + currentFile.getParent());
		fileDialog.setDirectory(currentFile.getParent());
		fileDialog.setFilenameFilter(new SuffixFilter(Config.bolscriptSuffix));
		fileDialog.setFile(currentFile.getName());

		fileDialog.setVisible(true);
		//File Dialog is modal. 
		//When a location and filename is chosen the program continues here.
		
		
		if (fileDialog.getFile() != null) {
			
			//int choice =;
			
			try {
				String dirPath = fileDialog.getDirectory();
				String compPath = new File(Config.pathToCompositions).getAbsolutePath();
				if (!dirPath.startsWith(compPath)) {
					SaveOutsideTablaFolder question = new SaveOutsideTablaFolder(editor);
					question.setVisible(true); // (modal)
					switch (question.getChoice()) {
						case(SaveOutsideTablaFolder.CANCEL):
							fileDialog.dispose();
							return;
						case (SaveOutsideTablaFolder.CHANGE_FOLDER):
							fileDialog.dispose();
							actionPerformed(e);
							return;
						case (SaveOutsideTablaFolder.PROCEED):
							break;
					}
				}
			} catch (Exception ex) {
				//proceed
			}
			
			
			String filename = fileDialog.getDirectory() + Config.fileSeperator + fileDialog.getFile();

			/*if (!filename.endsWith(Config.bolscriptSuffix)) {
				filename = filename + Config.bolscriptSuffix;
			}*/
			File file = new File(filename);
			Debug.debug(this, "Save As : " + filename);
			Master.master.saveEditorAs(editor, filename);
		}

		fileDialog.dispose();
	}

}
