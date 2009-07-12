package midi.sometests;

import config.Themes;
import midi.MidiStation;
import algorithm.composers.kaida.KaidaComposer;
import bols.BolBase;
import bols.tals.Teental;

public class MidiTerrorTest {
	
	public static void main(String[] args) {
		try {
			MidiStation midiStation = new MidiTestBlaster2(new KaidaComposer(BolBase.getStandard(), new Teental(BolBase.getStandard()),Themes.getTheme01(BolBase.getStandard())));
			midiStation.initMidi();
		} catch (Exception e) {
			//do nothing!
		}
		
	}
	
}
