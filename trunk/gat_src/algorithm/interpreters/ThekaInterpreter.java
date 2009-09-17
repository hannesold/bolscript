package algorithm.interpreters;

import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import midi.MidiStation;
import basics.Basic;
import bols.Bol;
import bols.BolBase;
import bols.BolMap;
import bols.BolSequence;
import bols.MidiMap;
import bols.PlayingStyle;
import bols.SubSequence;
import bols.Variation;

/**
 * Renders a variation to midi.
 * @author Hannes
 *
 */
public class ThekaInterpreter extends VariationInterpreterStandard implements VariationInterpreter {
  
	private BolBase bolBase;
	private PlayingStyle lateBolsInSubSeqStyle;
	private PlayingStyle lateBolsInSeqStyle;
	private int ticksPerBeat;
	
	/**
	 * Constructs a new standard interpreter.
	 * @param base The BolBase used.
	 */
	public ThekaInterpreter(BolBase base) {
		super();
		lateBolsInSubSeqStyle = new PlayingStyle(1, (double) 0.85); //0.35
		lateBolsInSeqStyle = new PlayingStyle(1, (double) 0.9); //0.7
		ticksPerBeat = MidiStation.TICKSPERBEAT;
		bolBase = base;
		DEBUG = false;
	}
	 
	public BolBase getBolBase() {
		return bolBase;
	}

	public void setBolBase(BolBase bolBase) {
		this.bolBase = bolBase;
	}

	public void renderToMidiTrack(Variation variation, Track track, long startingAt) {
		renderToBolSequenceAndMidiTrack(variation, track, startingAt);
	}
	
	public BolSequence renderToBolSequenceAndMidiTrack(Variation variation, Track track, long startingAt) {
		
		try {		
			
			//----------------------------------------
			//Step 1: make Copy of Variation, and change Volumes of Sequence starts
			//Variation interpreted = variation.getCopyFull();
			BolSequence interpreted = new BolSequence();
			ArrayList<SubSequence> subSeqs = variation.getSubSequences();
			
			for (int i=0; i < subSeqs.size(); i++) {
				
				BolSequence subSeq = ((SubSequence)subSeqs.get(i)).getAsSequence(); 
				
				//if (subSeq.getLength() > 0 ){
					for (int j=0; j < subSeq.getLength(); j++) {
						Bol currentBol = subSeq.getBol(j).getCopy();
						PlayingStyle currentStyle = currentBol.getPlayingStyle();
						out("Bol "+j+": " + currentBol.toString() + " with "+ currentStyle);
						
						//first beat of sequence
						if ((i==0)&&(j==0)) {
							//dont change first beat
						} else {
							//velocity down for all beats in sequence except first
							currentBol.setPlayingStyle(currentStyle.getProduct(lateBolsInSeqStyle));
							//out("style now: " + currentBol.getPlayingStyle().toString());
							if (j > 0) {
								currentStyle = currentBol.getPlayingStyle();
								currentBol.setPlayingStyle(currentStyle.getProduct(lateBolsInSubSeqStyle));
							}
						} 
						interpreted.addBol(currentBol);
						out("added as " + currentBol.toString() + " with "+ currentStyle);
					} //bols in subseq
				//} //subseq length question
			}//subseqs
			
			//			----------------------------------------
			//Step 2: Transform BolSequence to MidiTrack
			
			BolSequence bolSeq = interpreted.getCopyWithMergedPauses(bolBase);
			out ("BolSequence just before MidiTransform: " + bolSeq.toStringFull());
								
			int generalNoteOffset = bolBase.getGeneralNoteOffset();
			
			long time = startingAt;
			
			boolean lowBayan=true;
			
			for (int i=0; i < bolSeq.getLength(); i++) {
				//(Track track, int channel, int note, int velocity, long time, long length) {
				Bol bol = bolSeq.getBol(i);
				BolMap bolMap = bolBase.getBolMap(bol.getBolName());
				PlayingStyle style = bol.getPlayingStyle();
				out("style = " + bol.getPlayingStyle());
				MidiMap[] midiMaps = bolMap.getMidiMaps();
				
				long durationInTicks = 0;
				
				for (int j=0; j < midiMaps.length;j++) {
					MidiMap mm = midiMaps[j];
					if ((mm.getHand() == MidiMap.NONE) || 
							(mm.getBolName().toString() == "-")) {
						//pause
						durationInTicks = Math.round(((double)ticksPerBeat) / style.getSpeedValue());
						//do nothing
						
					} else {
						
						// interpreting bayan
						// one low one high one low one high ...
						if (mm.getBolName().toString().equals("Ge")) {
							if (!lowBayan) {
								mm = bolBase.getMidiMap("Ge2");
							} 
							lowBayan = !lowBayan;
						}
						
						
						int vel;
						if (mm.getHand()==MidiMap.LEFT) {				
							vel = (int) (127.0f * style.velocity * bolMap.getLeftHandVelocityScale());
							
							vel = Math.min(Math.max(vel, 0),127);
						} else if (mm.getHand()==MidiMap.RIGHT) {
							vel = (int) (127.0f * style.velocity * bolMap.getRightHandVelocityScale());
							
							vel = Math.min(Math.max(vel, 0),127);
						} else {
							out("sorry, bad hand info");
							vel = 100;
						}
						durationInTicks = Math.round(((double)ticksPerBeat) / style.getSpeedValue());
						
					
						int note = generalNoteOffset + mm.getMidiNote();
						//(Track track, int channel, int note, int velocity, long time, long length) {
						addNote(track, 0, note, vel, time, durationInTicks);
						out(mm.getBolName().toString() + ", note:"+note+",vel:"+ vel);
						
					}
				}
				//out("\n");				
				//count on time
				time += durationInTicks;
			}
			
			VariationInterpreterStandard.renderCellHighlighting(track, startingAt, time, ticksPerBeat);
			VariationInterpreterStandard.renderMetaCommands(track,startingAt,time,ticksPerBeat);
			
			return interpreted;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}



	
}
