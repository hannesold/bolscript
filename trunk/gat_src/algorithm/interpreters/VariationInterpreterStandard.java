package algorithm.interpreters;

import java.text.DecimalFormat;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import midi.MidiStation;
import basics.Basic;

public class VariationInterpreterStandard extends Basic {
	
	/**
	 * Add the cell highlighting meta events to the midi track.
	 * @param track
	 * @param startingAt
	 * @param time
	 * @param ticksPerBeat
	 * @throws Exception
	 */
	public static void renderCellHighlighting(Track track, long startingAt, long time, int ticksPerBeat) throws Exception {
		DecimalFormat df = new DecimalFormat ( "000" );
		int cell = 0;
		for (long t = startingAt; t<time; t+=ticksPerBeat) {
			//add next Cell Message
			String msg = MidiStation.CELLACTIVE + df.format(cell);
			//System.out.println("Metamessage: " + msg);
			track.add(MidiStation.getMetaEvent(msg, (cell==0)?t:t-10));
			cell++;
		}
	}
	
	/**
	 * Adds the MetaEvents PREPARE, COLLECT and CYCLECOMPLETED to the midi track 
	 * at proper positions.
	 * @param track
	 * @param startingAt
	 * @param time
	 * @param ticksPerBeat
	 * @throws Exception
	 */
	public static void renderMetaCommands(Track track, long startingAt, long time, int ticksPerBeat) throws Exception {
		//add signals
		
		MidiStation midiStation = MidiStation.getStandard();		
		
		long cycleDuration = time - startingAt;						
		long nextCycle = time;
		
		long nextPreparation = Math.max(startingAt, nextCycle - midiStation.milliSecondsToTicks(6000));
		long nextCollection  = Math.max(nextPreparation+1, nextCycle - midiStation.milliSecondsToTicks(500));
		
		track.add(MidiStation.getMetaEvent(MidiStation.PREPARE, nextPreparation));
		track.add(MidiStation.getMetaEvent(MidiStation.COLLECT, nextCollection));
		track.add(MidiStation.getMetaEvent(MidiStation.CYCLECOMPLETED, nextCycle-10));
		
	}
	
//	public static void renderGCCommand(Track track, long time, int ticksPerBeat) throws Exception {
//		//add Garbage Collect Meta Event
//		track.add(MidiStation.getMetaEvent(MidiStation.GARBAGECOLLECT, time+10));
//		//track.add(MidiStation.getMetaEvent(MidiStation.CYCLECOMPLETED, nextCycle+5));
//	}
	
	/**
	 * Add a single NoteON and NotOFF to a midi track
	 */
	public static void addNote (Track track, int channel, int note, int velocity, long time, long length) {
		
		ShortMessage s = new ShortMessage();
		ShortMessage s2 = new ShortMessage();	
		//type, channel, note, velocity
		try {
			s.setMessage(ShortMessage.NOTE_ON, channel, note, velocity);
			s2.setMessage(ShortMessage.NOTE_OFF, channel, note, velocity);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		track.add(new MidiEvent(s,time));
		track.add(new MidiEvent(s2,time+length));
		
	}
	
	/**
	 * Returns a Midi ShortMessage for NoteOn
	 * @param channel
	 * @param note
	 * @param velocity
	 * @return
	 */
	public static ShortMessage noteOn ( int channel, int note, int velocity) {
		
		ShortMessage s = new ShortMessage();
		//MidiEvent event = null;
		//type, channel, note, velocity
		try {
			s.setMessage(ShortMessage.NOTE_ON, channel, note, velocity);
			//event = new MidiEvent(s, time);
		} catch (InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}
	
	/**
	 * Returns a Midi ShortMessage for NoteOff
	 * @param channel
	 * @param note
	 * @param velocity
	 * @return
	 */
	public static ShortMessage noteOff (int channel, int note, int velocity) {
		
		ShortMessage s = new ShortMessage();
		//MidiEvent event = null;
		//type, channel, note, velocity
		try {
			s.setMessage(ShortMessage.NOTE_OFF, channel, note, velocity);
			//event = new MidiEvent(s, time);
		} catch (InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}
	
}
