package gui.bolscript.actions;

import gui.bolscript.CompositionFrame;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import basics.Debug;
import basics.SuffixFilter;
import bolscript.Master;
import bolscript.compositions.Composition;
import bolscript.compositions.CompositionBase;
import bolscript.config.Config;
import bolscript.config.GuiConfig;
import bolscript.config.UserConfig;

public class ExportPdf extends AbstractAction {
	CompositionFrame compFrame;
		
	public ExportPdf(CompositionFrame compFrame) {
		if (compFrame==null) this.setEnabled(false);
		this.compFrame = compFrame;
		
		this.putValue(NAME, "Save PDF...");
	}
	
	public void actionPerformed(ActionEvent e) {
		Composition comp = compFrame.getCompositionPanel().getComposition();
				
		// set up a filedialog
		FileDialog fileDialog = new FileDialog(compFrame, "Export Pdf As", FileDialog.SAVE);
		String recommendedFilename = CompositionBase.generateFilename(comp, Config.pdfSuffix, true);
		File recommendedFile = new File(recommendedFilename);
		if (UserConfig.pdfExportPath != null) {
			fileDialog.setDirectory(UserConfig.pdfExportPath);
		} else {
			fileDialog.setDirectory(recommendedFile.getPath());
		}
		fileDialog.setFilenameFilter(new SuffixFilter(Config.pdfSuffix));
		fileDialog.setFile(recommendedFile.getName());
		
		// show the (modal) filedialog
		GuiConfig.setVisibleAndAdaptFrameLocation(fileDialog);

		//check the result of the filedialog
		if (fileDialog.getFile() != null) {
			String filename = fileDialog.getDirectory() + Config.fileSeperator + fileDialog.getFile();
			
			// ensure the chosen filename ends on .pdf
			if (!filename.toLowerCase().endsWith(".pdf")) {
				filename = filename + ".pdf";
				Debug.debug(this, "Appended .pdf suffix to chosen filename.");
			}
			
			Debug.debug(this, "Export Pdf As : " + filename);
			
			this.compFrame.createPdf(filename, false);			
			
			// remember export path
			UserConfig.setPdfExportPath(fileDialog.getDirectory());
			
			Master.master.revealFileInOSFileManager(filename);
		} else {
			// was cancelled
		}
		
		fileDialog.dispose();

	}
	
	
}
