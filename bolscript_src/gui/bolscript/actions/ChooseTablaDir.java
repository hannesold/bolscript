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
import bolscript.config.GuiConfig;
import bolscript.config.UserConfig;

public class ChooseTablaDir extends AbstractAction {

	Dialog owner;
	
	private String chosenFolder = null;
	
	public ChooseTablaDir(Dialog owner) {
		super("Browse");
		this.owner = owner;
	}

	public void actionPerformed(ActionEvent e) {
		String path = new String(UserConfig.libraryFolder);
		File folder = new File(path);
		if (!folder.exists()) {
			path = System.getProperty("user.dir");
		}

		chosenFolder = new String(path);
		Debug.debug(this, "os.name: " + System.getProperty("os.name").toLowerCase() );
		if (Config.operatingSystem == Config.OperatingSystems.Mac) {
			System.setProperty("apple.awt.fileDialogForDirectories", "true");
			FileDialog fileDialog = new FileDialog(owner, "Set Library Folder", FileDialog.LOAD);
			fileDialog.setDirectory(path);
			GuiConfig.setVisibleAndAdaptFrameLocation(fileDialog);
			
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
			fileDialog.setCurrentDirectory(new File(UserConfig.libraryFolder));
			fileDialog.setDialogTitle("Choose Library Folder");
			FileFilter filter = new FolderFilter();
			fileDialog.setDialogType(JFileChooser.OPEN_DIALOG);
			fileDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileDialog.setAcceptAllFileFilterUsed(false);

			int answer = fileDialog.showDialog(null, "Use this folder");
			if (answer != JFileChooser.CANCEL_OPTION) {
				File d = fileDialog.getCurrentDirectory();
				Debug.debug(this, "directory chosen: " + d.getAbsolutePath());
				
				File f = fileDialog.getSelectedFile();
				Debug.debug(this, "file chosen: " + f.getAbsolutePath());
				chosenFolder = f.getAbsolutePath();
			} else Debug.debug(this, "directory chosing aborted"); 

		}
		
		Debug.debug(this, "chosen folder is " + chosenFolder);

		
	}
	
	public String getChosenFolder() {
		return chosenFolder;
		
	}

}
