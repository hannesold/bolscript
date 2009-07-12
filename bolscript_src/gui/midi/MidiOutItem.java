package gui.midi;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;

public class MidiOutItem {
	
	private MidiDevice.Info midiDeviceInfo;

	public MidiOutItem(Info info) {
		super();
		midiDeviceInfo = info;
	}
	
	public String toString() {
		String s = "" + 
		midiDeviceInfo.getName() /*+ ", " +
		midiDeviceInfo.getVendor() + ", " +
		midiDeviceInfo.getVersion() + ", " +
		midiDeviceInfo.getDescription()*/;
		return s;
	}

	public MidiDevice.Info getMidiDeviceInfo() {
		return midiDeviceInfo;
	}
	
	
}
