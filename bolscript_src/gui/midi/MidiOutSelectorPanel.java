package gui.midi;

import java.awt.event.ActionEvent;

import javax.sound.midi.MidiDevice;

import midi.MidiStationSimple;
import basics.GUI;

public class MidiOutSelectorPanel extends MidiOutputSelectorPanelGraphics {
	private MidiStationSimple midiStation;

	public MidiOutSelectorPanel(MidiStationSimple station) {
		super();
		midiStation = station;
		
		fillComboBox();
		
		this.midiOutputSelector.addActionListener(GUI.proxyActionListener(this, "selectedInfo"));
		this.btnSet.addActionListener(GUI.proxyActionListener(this, "setInfo"));
	}
	
	private void fillComboBox() {
		MidiDevice.Info[] possibleOuts = midiStation.getPossibleOuts();
		
		for (int i=0; i < possibleOuts.length; i++) {
			
			this.midiOutputSelector.addItem(new MidiOutItem(possibleOuts[i]));
			
			if (midiStation.isReady()) {
				if (midiStation.getOutputDeviceInfo().equals(possibleOuts[i])) {
					midiOutputSelector.setSelectedIndex(i);
				}
			}
		
		}
		
		
	}
	
	public void selectedInfo(ActionEvent e) {
	}

	public void setInfo(ActionEvent e) {
		midiStation.setMidiOut(((MidiOutItem) midiOutputSelector.getSelectedItem()).getMidiDeviceInfo());
	}
	
}
