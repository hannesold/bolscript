package gui.midi;

import java.awt.event.ActionEvent;

import javax.swing.JFileChooser;

import midi.MidiStation;
import basics.GUI;

public class MidiExportFrame extends MidiExportFrameGraphics {
	private MidiStation midiStation;

	private static String[] validEndings = new String[]{".mid",".midi",".MID",".MIDI"};
	
	public MidiExportFrame(MidiStation midiStation) {
		super();
		this.midiStation = midiStation;
		jFileChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
		jFileChooser.addActionListener(GUI.proxyActionListener(this,"fileChooseAction"));
	}
	
	public void fileChooseAction(ActionEvent e) {
		if (e.getActionCommand() == JFileChooser.CANCEL_SELECTION) {
			this.setVisible(false);
		} else if (e.getActionCommand() == JFileChooser.APPROVE_SELECTION) {
			
			//validate suffix
			String path = jFileChooser.getSelectedFile().getPath();
			boolean hasCorrectEnding = false;
			int i=0;
			while ((i< validEndings.length) && (!hasCorrectEnding)) {
				if (path.endsWith(validEndings[i])) hasCorrectEnding = true;
				i++;
			}
			if (hasCorrectEnding) {
				midiStation.exportAllToMidiFile(jFileChooser.getSelectedFile());
			} else {
				midiStation.exportAllToMidiFile(path.concat(".midi"));
			}
			
			this.setVisible(false);
		}
	}
}
	