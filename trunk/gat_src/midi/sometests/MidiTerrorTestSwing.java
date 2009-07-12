package midi.sometests;

import javax.swing.JFrame;

import managing.Manager;

import algorithm.composers.kaida.KaidaComposer;
import bols.BolBase;
import bols.Variation;
import bols.tals.Teental;

public class MidiTerrorTestSwing extends JFrame{
	
	public MidiTerrorTestSwing() {
		super("tester...");
		this.setSize(400,400);
		this.pack();
		this.setVisible(true);
	}
	
	public static void main(String[] args) {
		try {
			//MidiStation midiStation = new MidiTestBlaster2(new KaidaComposer(BolBase.standard, new Teental(BolBase.standard),Variation.getTestVariation(BolBase.standard)));
			//midiStation.initMidi();
			
			Manager swing = new Manager();
		} catch (Exception e) {
			//do nothing!
		}
		
	}
	
}
