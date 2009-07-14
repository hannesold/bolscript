package gui.bolscript.actions;

import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import basics.Debug;
import basics.FolderFilter;
import bolscript.config.Config;

public class ChooseTablaDir extends AbstractAction {

	Dialog owner;
	

	private String chosenFolder = null;
	
	public ChooseTablaDir(Dialog owner) {
		super();
		this.owner = owner;
		this.putValue(NAME, "Browse");
	}

	public void actionPerformed(ActionEvent e) {
		String path = new String(Config.tablaFolder);
		File folder = new File(path);
		if (!folder.exists()) {
			path = System.getProperty("user.dir");
		}

		chosenFolder = new String(path);
		Debug.debug(this, "os.name: " + System.getProperty("os.name").toLowerCase() );
		if (Config.OS == Config.MAC) {
			System.setProperty("apple.awt.fileDialogForDirectories", "true");
			FileDialog fileDialog = new FileDialog(owner, "Set Tabla Folder", FileDialog.LOAD);
			fileDialog.setDirectory(path);
			fileDialog.setVisible(true);
			
			if (fileDialog.getDirectory() != null) {
				Debug.debug(this, "directory chosen: " + fileDialog.getDirectory());
			
				if (fileDialog.getFile() != null) {
					Debug.debug(this, "file chosen: " + fileDialog.getFile());
					
					chosenFolder = fileDialog.getDirectory() + fileDialog.getFile();
					
				}
			}
			fileDialog.dispose();
			System.setProperty("apple.awt.fileDialogForDirectories", "false");
		} else {
			JFileChooser fileDialog = new JFileChooser();
			fileDialog.setCurrentDirectory(new File(Config.tablaFolder));
			//fileDialog.setVisible(false);
			fileDialog.setDialogTitle("Choose Tabla Folder");
			FileFilter filter = new FolderFilter();
			//FileFilter filter = new FolderFilter();
			//fileDialog.addChoosableFileFilter(filter);
			fileDialog.setDialogType(JFileChooser.OPEN_DIALOG);
			fileDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//(new FolderFilter());
			fileDialog.setAcceptAllFileFilterUsed(false);

			//fileDialog.setFileSelectionMode(JFileChooser.)
			
			int answer = fileDialog.showDialog(null, "Use this folder");
			if (answer != JFileChooser.CANCEL_OPTION) {
				File d = fileDialog.getCurrentDirectory();
				Debug.debug(this, "directory chosen: " + d.getAbsolutePath());
				
				File f = fileDialog.getSelectedFile();
				Debug.debug(this, "file chosen: " + f.getAbsolutePath());
				chosenFolder = f.getAbsolutePath();
			} else Debug.debug(this, "directory chosing aborted"); 
			//Debug.debug(this, "directory chosen: " + fileDialog.getF);
			//chosenFolder = fileDialog.getDirectory();

		}
		
		Debug.debug(this, "chosen folder is " + chosenFolder);

		
	}
	
	public String getChosenFolder() {
		return chosenFolder;
		
	}

}
