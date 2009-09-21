package algorithm;

import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import junit.framework.TestCase;
import midi.MidiStation;
import algorithm.composers.tihai.TihaiComposer;
import bols.BolBase;
import bols.BolSequence;
import bols.Variation;
import bols.tals.Teental;

public class TihaiTest extends TestCase {
	BolBase bb = BolBase.getStandard();
	
	public void testTihai() throws Exception {
		
		
		Variation var1 = new Variation("Dha Ti Ge Na, Dha Ti Ge Na, Dha Ti Ge Na, Dha Ti Ge Na", bb);

		TihaiComposer composer = new TihaiComposer(BolBase.getStandard(), new Teental(), var1);

		Variation tihai = composer.composeTihai(var1);
		System.out.println("tihai: " + tihai);

		var1 = new Variation(new BolSequence("Dha Ti Ge Na",bb));
		var1.addSubSequence(0,4,2f);
		var1.addSubSequence(0,4,2f);
		var1.addSubSequence(0,4,2f);
		var1.addSubSequence(0,4,2f);
		var1.addSubSequence(0,4,2f);
		var1.addSubSequence(0,4,2f);
		var1.addSubSequence(0,4,2f);
		var1.addSubSequence(0,4,2f);
		
		tihai = composer.composeTihai(var1);
		System.out.println("interpreted: " + tihai);
			
		var1 = new Variation("Dha Ti Ge Na, Dhin Na Ge Na, Dha Ge Ti Ri Ke Te, Ti Ke", bb);

		
		tihai = composer.composeTihai(var1);
		System.out.println("interpreted: " + tihai);

		//var1 = new Variation("Dha Ti Ge Na", bb);
		var1.addSubSequence(0,4,2f);
		var1.addSubSequence(4,4,2f);
		var1.addSubSequence(8,6,2f);
		var1.addSubSequence(14,2,2f);
	
		tihai = composer.composeTihai(var1);
		System.out.println("interpreted: " + tihai);
		
	}
}
