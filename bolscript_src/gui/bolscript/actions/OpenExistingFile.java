package gui.bolscript.actions;

import gui.bolscript.BrowserFrame;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.AbstractAction;

import basics.SuffixFilter;
import bolscript.Master;
import bolscript.config.Config;
import bolscript.config.GuiConfig;

public class OpenExistingFile extends AbstractAction {
	private BrowserFrame browser;
	
	public OpenExistingFile(BrowserFrame browser) {
		super("Import or Open...");
		this.browser = browser;
		this.setEnabled(browser != null);
	}
	
	public void actionPerformed(ActionEvent e) {
		//Master.master.openEditor();

		if (browser == null) return;
		
		FileDialog fileDialog = new FileDialog(browser, "Open", FileDialog.LOAD);
		fileDialog.setDirectory(Config.homeDir);
		fileDialog.setFilenameFilter(new SuffixFilter(Config.bolscriptSuffix));
		GuiConfig.setVisibleAndAdaptFrameLocation(fileDialog);
		
		//File Dialog is modal. 
		//When a location and filename is chosen the program continues here.
	
		if (fileDialog.getFile() != null) {
			ArrayList<File> files = new ArrayList<File>();
			File file = new File(fileDialog.getDirectory() + Config.fileSeperator + fileDialog.getFile());
			files.add(file);
			Master.master.openSomeExistingFiles(files);
			
			/*try {
				String dirPath = fileDialog.getDirectory();
				String compPath = new File(Config.pathToCompositions).getAbsolutePath();
				if (!dirPath.startsWith(compPath)) {
					OpenFileDialog question = new OpenFileDialog(browser);

					GuiConfig.setVisibleAndAdaptFrameLocation(question); // (modal)
					switch (question.getChoice()) {
						case(OpenFileDialog.CANCEL):
							fileDialog.dispose();
							return;
						case (OpenFileDialog.COPY):
							fileDialog.dispose();
							//actionPerformed(e);
							return;
						case (OpenFileDialog.JUST_OPEN):
							fileDialog.dispose();
							break;
					}
				} else {
					//just open the composition which should already be in the compositionbase
				}
			} catch (Exception ex) {
				//proceed
			}
			*/
			/*
			String filename = fileDialog.getDirectory() + Config.fileSeperator + fileDialog.getFile();

			/*if (!filename.endsWith(Config.bolscriptSuffix)) {
				filename = filename + Config.bolscriptSuffix;
			}*/
			/*
			File file = new File(filename);
			Debug.debug(this, "Save As : " + filename);*/
		}

		fileDialog.dispose();
	}

	
}
