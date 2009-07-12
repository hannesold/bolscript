package midi.sometests;

import java.awt.event.ActionEvent;

import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Track;

import config.Themes;

import managing.Command;
import managing.Mediator;
import midi.MidiCommon;
import midi.MidiStation;
import midi.PrintEventList;
import algorithm.composers.Composer;
import algorithm.composers.kaida.KaidaComposer;
import algorithm.interpreters.VariationInterpreterStandard;
import bols.BolBase;
import bols.tals.Teental;

public class MidiTestBlaster2 extends MidiStation implements MetaEventListener{
	private boolean DEBUG;
	private Sequencer sequencer;
	private Sequence sequence; 
	private Receiver receiver;
	private MidiDevice outputDevice;
	private int ticksPerBeat;
	private Composer composer;
	private Mediator mediator;
	private Track track;
	private Command lastHighlightCommand;
	
	public static final String CYCLECOMPLETED = "cycl";
	public static final String HALFWAY = "half";
	public static final String STOP = "btnStop";
	public static final String SHUTDOWN = "shut";
	public static final String CELLACTIVE = ".";
	public static final int TICKSPERBEAT = 120;
	
	public MidiTestBlaster2 (Composer composer)  throws Exception{
		super(composer);
		DEBUG = true;
		ticksPerBeat = TICKSPERBEAT;
		this.composer = composer;
		this.mediator = null;
		lastHighlightCommand = new Command(Command.HighLightCell,0);
		lastHighlightCommand.setValid(false);
	}
	
	
    public void actionPerformed( ActionEvent e ) {
    
    }
	
    
	public void initSequence() throws Exception {
		int nrOfTracks = sequence.getTracks().length;
		if (nrOfTracks != 0) {
			throw new Exception ("wrong number of tracks available, expected 0, but now there are: " + nrOfTracks);
		}
		
		Sequence otherSequence = new Sequence(Sequence.PPQ,TICKSPERBEAT);
		track = otherSequence.createTrack();
		preFillTrack();
		//composer.fillTrack(track, 0);

		// add HALFWAY Message to end of track
		//track.add(getMetaEvent(HALFWAY, (long)((double)track.ticks()/2f)));
		
		
	}
	
	private void preFillTrack() {
		//composer.fillTrack(track, 0);
		Track realTrack = sequence.createTrack();
		long t = 0;
		long duration = ticksPerBeat / 4;
		long ticksAtEndTime = ticksPerBeat * 2000;
		for (t=0; t < ticksAtEndTime; t+=duration) {
			VariationInterpreterStandard.addNote(realTrack,1,BolBase.getStandard().getMidiMap("Na").getMidiNote()+BolBase.getStandard().getGeneralNoteOffset(),100, t, duration);
			if (t%(ticksPerBeat*16)==0) {
				try {
					realTrack.add(getMetaEvent(HALFWAY,t));
				} catch ( Exception e) {
					//do nothing
				}
			}
		}
	}
	
	protected void collectForNextCycle() {
		out("collectForNextCycle()");
		mediator.addCommand(new Command(Command.FillNextCycle, track));
		
		if (!mediator.isProcessing()){
			mediator.interrupt();
		}
	}

	protected void cycleCompleted() {
		//there have to be 2 Tracks at least, one old and one new (muted)
		out("cycleCompleted()");
		mediator.addCommand(new Command(Command.HighLightLastIndividual));
		
		if (!mediator.isProcessing()) {
			mediator.interrupt();
		}
	}
	

	protected void highlightCell(int cell) {
		// TODO Auto-generated method stub
		out("highlightCell(cell)");
		
		if (lastHighlightCommand.isValid()) {
			lastHighlightCommand.setValid(false);
		}
		lastHighlightCommand = new Command(Command.HighLightCell, cell);
		mediator.addCommand(lastHighlightCommand);
		//if (mediator.getState() == Thread.State.WAITING) 
		
		if (!mediator.isProcessing()) {
			mediator.interrupt();
		}		
	}
	
	public void meta(MetaMessage arg0) {
		// TODO Auto-generated method stub
		out("sequencer metamessage called: msg: " + arg0.getMessage() + ", type: " + arg0.getType() +", data: " + new String(arg0.getData()));
		try {
			String command = new String(arg0.getData());
		if (command.equals(STOP)) {
			out("sequencer stopped");
			sequencer.stop();
		} else if (arg0.getType() == 47) {  // 47 is end of track)
			out ("got type 47 == end of track");
			out ("at tick: " + sequencer.getTickPosition());
			shutDown();
		} else if (command.equals(HALFWAY)) {
			out("halfway through, should get track for next loop");
			collectForNextCycle();
		} else if (command.equals(CYCLECOMPLETED)) {
			out("loop completed, should activate new track");
			cycleCompleted();
		} else if (command.equals(SHUTDOWN)) {
			out("command to shut down");
			shutDown();
		} else if (command.subSequence(0,1).equals(CELLACTIVE)) {
			out("command to highlight cell" + command.substring(1,4));
			int cell = Integer.parseInt(command.substring(1,4));
			highlightCell(cell);
		} else {
			out("unhandled command: " + command);
		}

		} catch (Exception e) {
			out("Error after MetaMessage: " + e.getMessage());
			e.printStackTrace();
		}
	}
	


	public void initMidi() throws Exception {
		try {
			//get the standard java realtime sequencer
			sequencer = MidiSystem.getSequencer(false);
			sequencer.open();
			
			//get the output device (Midi Yoke 1 mapped to Midi Yoke input 4)
			String outputName ="MIDI Yoke NT:  1";
			outputDevice = null;
			MidiDevice.Info	info = MidiCommon.getMidiDeviceInfo(outputName, true);
			receiver = null;
			
			//try to get the Output Device 
			if (info == null) {
				out("no device info found for name " + outputName);
				System.exit(1);
			}
			out("setting output");
			outputDevice = MidiSystem.getMidiDevice(info);
			if (DEBUG) out("MidiDevice: " + outputDevice);
			
			outputDevice.open();
			
			if (outputDevice == null) {
				out("wasn't able to retrieve MidiDevice");
				System.exit(1);
			}
			
			out("getting receiver");
				receiver = outputDevice.getReceiver();
			TerrorTestSequencer terrorSequencer = new TerrorTestSequencer();
			terrorSequencer.setPriority(Thread.MAX_PRIORITY);
			//MidiDevice synthi = MidiSystem.getSynthesizer();
			//synthi.open();
			//receiver = synthi.getReceiver();
			terrorSequencer.setReceiver(receiver);
			terrorSequencer.start();
			
			//connect transmitter-receiver
				//sequencer.getTransmitter().setReceiver(receiver);
			
			// create a seqence, add a track (track1)
			//VariationInterpreter interpreter = new Interpreter1(bolBase);
			
			sequence = new Sequence(Sequence.PPQ, ticksPerBeat);
			//Track track = sequence.createTrack();
			//interpreter.renderToMidiTrack(variation, track);
			initSequence();
			
			sequencer.setSequence(sequence);			
			out("tick length: " + sequencer.getTickLength());
			//add listener
			sequencer.addMetaEventListener(this);

			//initiate sequencer
			//sequencer.setSequence(sequence);
		

			sequencer.setTempoFactor(1.5f);
			
			//sequencer.setTempoInBPM(400);

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}		

	}
	
	public void play() {
		//start playing
		out("midiStation.play()");
		sequencer.start();			//start playing
	}
	
	public void stop() {
		if (sequencer.isOpen()) {
			if (sequencer.isRunning()) {
				sequencer.stop();
			} else {
				System.out.println("sequencer was already stopped");
			}
			
		} else {
			System.out.println("sequencer was already closed");
		}
		
	}
	public void stop(long ticksFromNow) {
		try {
			long timeNow = sequencer.getTickPosition();
			long timeThen = timeNow + ticksFromNow;
			out("stopping at tickpos: " + timeThen);
			sequence.getTracks()[0].add(getMetaEvent(STOP,timeThen));
		} catch (Exception e) {
			out("some error when stopping, STOPPING NOW");
			out(e.fillInStackTrace());
			shutDown();
		}
	}
	
	public void shutDown() {
		out("shutting down midiStation");
		if (sequencer.isRunning()) sequencer.stop();
		if (sequencer.isOpen()) sequencer.close();
		receiver.close();
		outputDevice.close();
	}
	
	public void shutDown(long ticksFromNow) {
		try {
			long timeNow = sequencer.getTickPosition();
			long timeThen = timeNow + ticksFromNow;
			out("shutdown shall happen at tickpos: " + timeThen);
			sequence.getTracks()[0].add(getMetaEvent(SHUTDOWN,timeThen));
		} catch (Exception e) {
			out("some error when stopping, SHUTDOWN NOW");
			out(e.fillInStackTrace());
			shutDown();
		}
	}
	
	
	private void out(Object message)
	{
		if (DEBUG) System.out.println(message);
	}
	
	public static MidiEvent getMetaEvent(String s, long pos) throws Exception{
		MetaMessage mess = new MetaMessage();
		mess.setMessage(0,s.getBytes(),4);
		return new MidiEvent(mess,pos);
	}


	public Composer getComposer() {
		return composer;
	}


	public Sequence getSequence() {
		return sequence;
	}


	public Sequencer getSequencer() {
		return sequencer;
	}

	public void printAll() {
		
		out("\nSequencer");
		out("  - Duration ticks: " + sequencer.getTickLength());
		out("  - Duration secs: " + (double)sequencer.getMicrosecondLength() / 1000000);
		out("  - is opened: " + sequencer.isOpen());		
		out("  - is running: " + sequencer.isRunning());
		
		out("  - Position secs: " + (double)sequencer.getMicrosecondPosition() / 1000000);
		out("  - LoopStart: " + sequencer.getLoopStartPoint());
		out("  - LoopEnd: " + sequencer.getLoopEndPoint());
		out("  - LoopCount: " + sequencer.getLoopCount());
		out("  - BPM: " + sequencer.getTempoInBPM());

		
		out("\nSequence");
		out("  - Duration ticks: " + sequence.getTickLength());
		out("  - Duration micros: " + sequence.getMicrosecondLength());
		
		Track[] tracks = sequence.getTracks();
		for (int i = 0; i < tracks.length; i++) {
			out("\nTrack " + i);
			Track track = tracks[i];
			out ("  - is muted: " + sequencer.getTrackMute(i));
			out ("  - Durationin ticks: " + track.ticks());
			out ("  - Nr of events: " + track.size());
			
			for (int j = 0; j < track.size(); j++) {
				MidiEvent me = track.get(j);
				PrintEventList.printEvent(me);				
			}
		}
		
	}
	
	public void setTempo(double fBPM) {
	    double fCurrent = sequencer.getTempoInBPM();
	    double fFactor = fBPM / fCurrent;
	    sequencer.setTempoFactor((float) fFactor);
	}
	
	public static void main(String[] args) throws Exception {
		BolBase bb = new BolBase();
		KaidaComposer al = new KaidaComposer(bb, new Teental(bb), Themes.getTheme01(bb));
		MidiStation ms = new MidiStation(al);
		ms.initMidi();
		ms.play();
		ms.shutDown(310);
	}


	public synchronized Mediator getMediator() {
		return mediator;
	}


	public synchronized void setMediator(Mediator mediator) {
		this.mediator = mediator;
	}


	protected synchronized boolean isDEBUG() {
		return DEBUG;
	}


	public synchronized void setDEBUG(boolean debug) {
		DEBUG = debug;
	}

}
