package gui.playlist;

import java.awt.event.ActionEvent;

import javax.swing.JFileChooser;

import basics.GUI;

public class PlaylistExportFrame extends PlaylistExportFrameGraphics {
	
	private Playlist playlist;
	private static String[] validEndings = new String[]{".png",".PNG",".Png"};

	public PlaylistExportFrame(Playlist playlist) {
		super();
		this.playlist = playlist;
		jFileChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
		jFileChooser.addActionListener(GUI.proxyActionListener(this,"fileChooseAction"));
	}
	
	public void fileChooseAction(ActionEvent e) {
		if (e.getActionCommand() == JFileChooser.CANCEL_SELECTION) {
			this.setVisible(false);
		} else if (e.getActionCommand() == JFileChooser.APPROVE_SELECTION) {
			
//			validate suffix
			String path = jFileChooser.getSelectedFile().getPath();
			boolean hasCorrectEnding = false;
			int i=0;
			while ((i< validEndings.length) && (!hasCorrectEnding)) {
				if (path.endsWith(validEndings[i])) hasCorrectEnding = true;
				i++;
			}
			if (hasCorrectEnding) {
				playlist.exportToImageFile(jFileChooser.getSelectedFile());
			} else {
				playlist.exportToImageFile(path.concat(".png"));
			}
			
			
			
			this.setVisible(false);
		}
	}
	
}
	