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
import bolscript.compositions.DataState;
import bolscript.config.Config;
import bolscript.config.UserConfig;

public class ExportPdf extends AbstractAction {
	CompositionFrame compFrame;
		
	public ExportPdf(CompositionFrame compFrame) {
		if (compFrame==null) this.setEnabled(false);
		this.compFrame = compFrame;
		
		this.putValue(NAME, "Save PDF");
	}
	
	public void actionPerformed(ActionEvent e) {
		Composition comp = compFrame.getCompositionPanel().getComposition();
		
		String path;
		path = CompositionBase.generateFilename(comp, Config.pdfSuffix);
		
		File currentFile = new File(path);
		FileDialog fileDialog = new FileDialog(compFrame, "Export Pdf As", FileDialog.SAVE);
		if (UserConfig.pdfExportPath!=null) {
			fileDialog.setDirectory(UserConfig.pdfExportPath);
		} else fileDialog.setDirectory(currentFile.getPath());
		
		fileDialog.setFilenameFilter(new SuffixFilter(Config.pdfSuffix));
		fileDialog.setFile(currentFile.getName());
		
		fileDialog.setVisible(true);

		if (fileDialog.getFile() != null) {
			String filename = fileDialog.getDirectory() + Config.fileSeperator + fileDialog.getFile();
			Debug.debug(this, "Export Pdf As : " + filename);
			this.compFrame.createPdf(filename, false);
			UserConfig.setPdfExportPath(fileDialog.getDirectory());
		}
		
		fileDialog.dispose();

	}
	
}
