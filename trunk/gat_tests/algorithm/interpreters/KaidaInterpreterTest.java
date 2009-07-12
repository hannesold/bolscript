package algorithm.interpreters;

import java.util.ArrayList;

import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import config.Themes;

import midi.MidiStation;

import junit.framework.TestCase;
import bols.BolBase;
import bols.BolSequence;
import bols.Variation;

public class KaidaInterpreterTest extends TestCase {
	
	public void testInterpreter2Render() throws Exception {
		BolBase bb = new BolBase();
		Variation var1 = Themes.getTheme01(bb);
		Sequence midiSeq = new Sequence(Sequence.PPQ, MidiStation.TICKSPERBEAT);
		
		VariationInterpreter Interpreter2 = new KaidaInterpreter(bb);
		Interpreter2.renderToMidiTrack(var1, midiSeq.createTrack(),0);

		ArrayList<Variation> vars = new ArrayList<Variation>();
		
		vars.add(Themes.getTheme01(bb));
		vars.add(Themes.getTheme02(bb));
		vars.add(Themes.getTheme01DbSpeed(bb));
		BolSequence seq = new BolSequence("Dha - Dhin Dhin Dha - Dha -",bb);
		
		var1 = new Variation(seq);
		var1.addSubSequence(0,2);
		var1.addSubSequence(2,4);
		var1.addSubSequence(4,2);
		vars.add(var1);
		
		var1 = new Variation(seq);
		var1.addSubSequence(0,2);
		var1.addSubSequence(2,4);
		var1.addSubSequence(4,2,2);
		var1.addSubSequence(4,2,2);
		vars.add(var1);
		
		var1 = new Variation(seq);
		var1.addSubSequence(0,2);
		var1.addSubSequence(2,4);
		var1.addSubSequence(4,2,4);
		var1.addSubSequence(4,2,4);
		var1.addSubSequence(4,2,4);
		var1.addSubSequence(4,2,4);
		vars.add(var1);
		
		var1 = new Variation(seq);
		var1.addSubSequence(0,2);
		var1.addSubSequence(2,4);
		var1.addSubSequence(4,2,3);
		var1.addSubSequence(4,2,3);
		var1.addSubSequence(4,2,3);
		vars.add(var1);
		//vars.add(new Variation());
		
		for (Variation var :vars) {
			Track track = midiSeq.createTrack();
			BolSequence bolSeq = Interpreter2.renderToBolSequenceAndMidiTrack(var, track,0);
			System.out.println("original dur: " + var.getDuration() + "interpreted dur: " + bolSeq.getDuration());
			assertEquals("duration of " +var  +" should be a multiple of original duration ", 0.0, (bolSeq.getDuration()%var.getDuration()));
			assertEquals("duration of " +var +" should be a 2xoriginal duration ", 2.0, (bolSeq.getDuration()/var.getDuration()));
			assertEquals("duration of " +var + " in ticks should be ticksperbeat*duration " ,(double)MidiStation.TICKSPERBEAT*bolSeq.getDuration(), (double)track.ticks());			
			midiSeq.deleteTrack(track);
		}
		
		vars.clear();
		var1 = new Variation(seq);
		var1.addSubSequence(0,4,2);
		var1.addSubSequence(0,4,2);
		var1.addSubSequence(4,4,2);
		var1.addSubSequence(4,4,2);	
		var1.addSubSequence(0,4,2);
		var1.addSubSequence(0,4,2);
		var1.addSubSequence(4,4,2);
		var1.addSubSequence(4,4,2);			
		vars.add(var1);
		
		BolSequence seq2 = new BolSequence("Dha Ge Te Re Ke Te Dha - Dha - Dha Ge Te Re Ke Te", bb);
		var1 = new Variation(seq2);
		var1.addSubSequence(0,6,2);
		var1.addSubSequence(0,6,2);
		var1.addSubSequence(0,6,2);
		var1.addSubSequence(0,6,2);
		var1.addSubSequence(6,2,2);
		
		for (Variation var :vars) {
			Track track = midiSeq.createTrack();
			BolSequence bolSeq = Interpreter2.renderToBolSequenceAndMidiTrack(var, track,0);
			System.out.println("original dur: " + var.getDuration() + "interpreted dur: " + bolSeq.getDuration());
			assertEquals("duration of " +var  +" should be a multiple of original duration ", 0.0, (bolSeq.getDuration()%var.getDuration()));
			assertEquals("duration of " +var +" should be a 2xoriginal duration ", 2.0, (bolSeq.getDuration()/var.getDuration()));
			assertEquals("duration of " +var +" should be 32 ", 32.0f, bolSeq.getDuration());
			assertEquals("duration of " +var + " in ticks should be ticksperbeat*duration " ,(double)MidiStation.TICKSPERBEAT*bolSeq.getDuration(), (double)track.ticks());			
			midiSeq.deleteTrack(track);
		}
		
	}
}
