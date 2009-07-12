package gui.midi;

import javax.swing.event.ChangeEvent;

import basics.GUI;
import algorithm.composers.kaida.ValueRange;
import midi.MidiStation;

public class SpeedSetterPanel extends SpeedSetterPanelGraphics {
	private MidiStation midiStation = null;
	private ValueRange speedRangeModel = null;
	
	public SpeedSetterPanel() {
		this(null);
	}
	
	public SpeedSetterPanel(MidiStation station) {
		super();
		
		speedRangeModel = new ValueRange(0.25, 8.0, 0.05);
		speedSelector.setModel(speedRangeModel);
		speedSelector.addChangeListener(GUI.proxyChangeListener(this,"setSpeed"));
		setMidiStation(station);
	}

	public void setSpeed(ChangeEvent e) {
		if (midiStation != null) {
			midiStation.setBpmByFactor(((Number) speedSelector.getValue()).doubleValue());		
		}
	}
	
	public MidiStation getMidiStation() {
		return midiStation;
	}

	public void setMidiStation(MidiStation midiStation) {
		this.midiStation = midiStation;
		if (midiStation != null) {
			speedSelector.setValue(midiStation.getTempoFactor());
		} else {
			speedSelector.setValue(1f);
		}
	}
}
