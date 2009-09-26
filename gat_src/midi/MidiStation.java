package midi;

import gui.bolscript.sequences.BolPanel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;
import javax.sound.midi.MidiDevice.Info;

import managing.Command;
import managing.Mediator;
import algorithm.composers.Composer;
import algorithm.composers.kaida.KaidaComposer;
import algorithm.composers.theka.ThekaPlayer;
import algorithm.interpreters.VariationInterpreterStandard;
import basics.Debug;
import basics.GUI;
import basics.Tools;
import bols.BolBase;
import bols.BolMap;
import bols.BolName;
import bols.MidiMap;
import bols.PlayingStyle;
import bols.tals.Teental;
import config.Themes;

public class MidiStation extends MidiStationSimple implements MetaEventListener {
	
	protected Sequence sequence; 
	protected Track track;

	protected int ticksPerBeat;
	
	protected Composer initialComposer;
	protected Mediator mediator;
	
	protected Sequencer sequencer;
	protected boolean sequencerEstablished;	
	
	protected Command lastHighlightCommand;

	
	public static final String CYCLECOMPLETED = "cycl";
	
	public static final String COLLECT = "fill";
	public static final String PREPARE = "prep";
	
	public static final String STOP = "btnStop";
	public static final String SHUTDOWN = "shut";
	public static final String CELLACTIVE = ".";
	public static final String GARBAGECOLLECT = "gcol";
	
	public static final int TICKSPERBEAT = 120;
	
	private static MidiStation standard = null;
	protected static boolean standardInitialized = false;
	
	static {
		if (!standardInitialized) {
			init();
		}
	}
	
	public static void init() {
		try {
			standard = new MidiStation(new ThekaPlayer(BolBase.getStandard(), Teental.getDefaultTeental()));
			standard.initMidi();
			MidiStationSimple.setStandard(standard);
			standardInitialized = true;
			Debug.debug(MidiStation.class, "MidiStation.standard initialised.");
		} catch (Exception e){
			standard = null;
			standardInitialized = false;
			Debug.critical(MidiStation.class, "MidiStation.standard initialisation failed.");
			e.printStackTrace();
		}
		
	}
	
	public static void setStandard(MidiStation station) {
		standard = station;
		MidiStationSimple.setStandard(station);
	}
	public static MidiStation getStandard() {
		if (standard == null) init();
		return standard;
	}
	
	/**
	 * Constructor of the midiStation. Caution: The Mediator has to be set seperately!
	 * @param composer The composer who shall initially be called to fill the tracks first cycle.
	 */
	public MidiStation (Composer composer) {
		super();
		ticksPerBeat = TICKSPERBEAT;
		this.initialComposer = composer;
		this.mediator = null;
		lastHighlightCommand = new Command(Command.HighLightCell,0);
		lastHighlightCommand.setValid(false);
	}


	/**
	 * Initiates Sequencer and Midi Output.
	 * @throws Exception
	 */
	public void initMidi() throws Exception {
		try {
			super.initMidiOut();
			
			//get the standard java realtime sequencer
			sequencer = MidiSystem.getSequencer(false);
			sequencer.open();
			transmitter = sequencer.getTransmitter();
			
			sequence = new Sequence(Sequence.PPQ, ticksPerBeat);
			initSequence();
			
			sequencer.setSequence(sequence);			
			debug.debug("tick length: " + sequencer.getTickLength());
			
			//add listener
			sequencer.addMetaEventListener(this);

			sequencer.setTempoInBPM(180);
			sequencer.setTempoFactor(1.0f);
			debug.debug("sequencer initialised: BPM: " + sequencer.getTempoInBPM() + ", MPQ: " + sequencer.getTempoInMPQ() + ", ticksPerBeat: "+ticksPerBeat);
			
			sequencerEstablished = true;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//System.exit(1);
			sequencerEstablished = false;
		}		

	}
	
	/**
	 * Initialy creates a track in the sequence and lets it be filled by the initial composer.
	 * @throws Exception
	 */
	private void initSequence() throws Exception {
		int nrOfTracks = sequence.getTracks().length;
		if (nrOfTracks != 0) {
			throw new Exception ("Wrong number of tracks available, expected 0, but now there are: " + nrOfTracks);
		}
		track = sequence.createTrack();
		initialComposer.fillTrack(track, 0);
	}
	
	/**
	 * The moment has come, where the composer should start preparing the next cycle...
	 * @see Mediator
	 */
	protected void prepareNextCycle() {
		debug.debug("prepareNextCycle()");
		mediator.addCommand(new Command(Command.PrepareNextCycle, track));
		if (!mediator.isProcessing()) mediator.interrupt();
	}
	
	
	/**
	 * A cycle has been half completed. 
	 * A command to collect new midi data for the next cycle is passed to the mediator
	 * @see Mediator
	 */
	protected void collectForNextCycle() {
		debug.debug("collectForNextCycle()");
		mediator.addCommand(new Command(Command.FillNextCycle, track));
		if (!mediator.isProcessing()) mediator.interrupt();
	}

	/**
	 * Passes on a command that the cycle is completed to mediator.
	 * He will then probably highlight the next cycle. 
	 * @see Mediator
	 */
	protected void cycleCompleted() {
		debug.debug("cycleCompleted()");
		mediator.addCommand(new Command(Command.HighLightLastIndividual));
		if (!mediator.isProcessing()) mediator.interrupt();
	}
	
	/**
	 * Passes on a command to highlight the current Beat "cell" in the gui to mediator. 
	 * @param cell
	 * @see Mediator
	 */
	protected void highlightCell(int cell) {
		debug.debug("highlightCell(cell)");
		
		//don't leave more than one valid highlightCommand in the command queue (overhead)
		if (lastHighlightCommand.isValid()) lastHighlightCommand.setValid(false);
		
		lastHighlightCommand = new Command(Command.HighLightCell, cell);
		mediator.addCommand(lastHighlightCommand);
		if (!mediator.isProcessing()) mediator.interrupt();		
	}
	
	/**
	 * Callback function for when the sequencer fires a MetaMessageEvent and delegates to handling functions.
	 */
	public void meta(MetaMessage arg0) {
		debug.debug("sequencer metamessage called: msg: " + arg0.getMessage() + ", type: " + arg0.getType() +", data: " + new String(arg0.getData()));
		
		try {
			String command = new String(arg0.getData());
			if (arg0.getType() == 81) {
				debug.debug("got type 81 == speed change");
			}
			if (arg0.getType() == 47) {  // 47 is end of track)
				debug.debug("got type 47 == end of track");
				debug.debug("at tick: " + sequencer.getTickPosition());
				shutDown();
			} else if (command.length() > 0) {
				if (command.subSequence(0,1).equals(CELLACTIVE)) {
					int cell = Integer.parseInt(command.substring(1,4));
					highlightCell(cell);
				} else if (command.equals(PREPARE)) {
					prepareNextCycle();
				} else if (command.equals(COLLECT)) {
					collectForNextCycle();
				} else if (command.equals(CYCLECOMPLETED)) {
					cycleCompleted();
				} else if (command.equals(SHUTDOWN)) {
					shutDown();
				}  else if (command.equals(STOP)) {
					stop();
				}  else {
					debug.debug("unhandled command: " + command);
				}
			}  else {
				debug.debug("unhandled command: " + command);
			}
		} catch (Exception e) {
			debug.debug("Error after MetaMessage: " + e.getMessage());
			e.printStackTrace();
		}
	}
	



	/**
	 * Starts playback of the sequencer, if everything is initialised.
	 * @see MidiStation.isReady()
	 */
	public void play() {
		//start playing
		debug.debug("midiStation.play()");
		if (isReady()) {
			sequencer.start();			//start playing
		} else {
			debug.debug("sorry, not midi is not initialised properly");
		}
	}
	
	/**
	 * Stops the sequencer, if it has been started.
	 *
	 */
	public void stop() {
		if (sequencerEstablished) {
			if (sequencer.isOpen()) {
				if (sequencer.isRunning()) {
					sequencer.stop();
				} else {
					System.out.println("sequencer was already stopped");
				}
				
			} else {
				System.out.println("sequencer was already closed");
			}
		} else {
			debug.debug("sequencer was never initialised");
		}
		
	}
	/**
	 * Stops the sequencer after an intervall.
	 * @param ticksFromNow The interall in ticks.
	 */
	public void stop(long ticksFromNow) {
		try {
			long timeNow = sequencer.getTickPosition();
			long timeThen = timeNow + ticksFromNow;
			debug.debug("stopping at tickpos: " + timeThen);
			sequence.getTracks()[0].add(getMetaEvent(STOP,timeThen));
		} catch (Exception e) {
			debug.debug("some error when stopping, STOPPING NOW");
			debug.debug(e.fillInStackTrace());
			shutDown();
		}
	}
	
	
	/**
	 * Shuts down MidiStation after an intervall.
	 * @param ticksFromNow The interall in ticks.
	 * @see MidiStation.shutDown()
	 */
	public void shutDown(long ticksFromNow) {
		try {
			long timeNow = sequencer.getTickPosition();
			long timeThen = timeNow + ticksFromNow;
			debug.debug("shutdown shall happen at tickpos: " + timeThen);
			sequence.getTracks()[0].add(getMetaEvent(SHUTDOWN,timeThen));
		} catch (Exception e) {
			debug.debug("some error when stopping, SHUTDOWN NOW");
			debug.debug(e.fillInStackTrace());
			shutDown();
		}
	}
	
	
	/**
	 * Shuts down MidiStation. Closes MidiOut and Receiver and 
	 * stops and closes sequencer.
	 */
	@Override
	public void shutDown() {
		super.shutDown();

		if (sequencerEstablished) {
			if (sequencer.isRunning()) sequencer.stop();
			if (sequencer.isOpen()) sequencer.close();
		}
	}

	/**
	 * Builds a MidiEvent containing MetaMessage with type=0, containing specified data, at specified time.
	 * @param data The data as a string, maximum length should be 4 bytes.
	 * @param positionInTicks The start time in ticks.
	 * @return The MetaEvent 
	 * @throws Exception
	 */	
	public static MidiEvent getMetaEvent(String data, long positionInTicks) throws Exception{
		MetaMessage mess = new MetaMessage();
		mess.setMessage(0,data.getBytes(),4);
		return new MidiEvent(mess,positionInTicks);
	}

	/**
	 * from Java Sound Resources: 
	 * http://www.jsresources.org/faq_midi.html#tempo_methods
	 * Tempo change is a meta event 
	 * (type 0x51, decimal 81). 
	 * The new tempo value has to be given in 
	 * microseconds per quarter (MPQ).
	 * You can calculate MPQ from BPM: mpq = 60000000 / bpm.	
	 * @param bpm
	 * @param tick
	 * @return A MidiEvent containing a MetaEvent that encodes a Tempochange.
	 * @throws Exception
	 */
	public static MidiEvent getTempChangeEvent(double bpm, long tick) throws Exception {
		final int tempoType = 0x51;
		int tempoInMPQ = BPMToMPQ(bpm);
		byte[] data = new byte[3];
		data[0] = (byte)((tempoInMPQ >> 16) & 0xFF);
		data[1] = (byte)((tempoInMPQ >> 8) & 0xFF);
		data[2] = (byte)(tempoInMPQ & 0xFF);
		
		MetaMessage message = new MetaMessage();
	    message.setMessage(tempoType, data, data.length);
	    
		return new MidiEvent( message, tick );
	}
	
	public static int BPMToMPQ(double bpm) {
		return (int)(60000000f / bpm); 
	}

	/**
	 * Returns true if midiOut and Sequencer are established, else false.
	 */
	@Override
	public boolean isReady() {
		return (midiOutEstablished && sequencerEstablished);
	}
	
	/**
	 * Sets the beats per minute (BPM) of the sequencer to factor * 60 Bpm.
	 * @param factor Factor to be multiplied with 60 BPM (= 1 Beat per second) 
	 */
	public void setBpmByFactor(double factor) {
		/*if (factor != getTempoFactor()) {
			sequencer.setTempoFactor(factor);
		}*/
		if (sequencerEstablished) {
			double bpm = factorToBPM(factor);
			sequencer.setTempoInBPM((float) bpm);
			try {
				track.add(getTempChangeEvent(bpm, sequencer.getTickPosition()));
			} catch (Exception e) {
				debug.debug("sorry, tempo change could not be added to sequence");
			}
		}
	}
	
	
	@Override
	public void setMidiOut(Info info) {
		super.setMidiOut(info);
		if (midiOutEstablished && sequencerEstablished) {
			//connect transmitter-receiver
			transmitter.setReceiver(receiver);
		}
	}

	/**
	 * @return´The tempo as a factor relative to 1 beat per second (60BPM). For example
	 * 		   if the tempo of the sequencer is 120BPM it will return 2. 
	 */
	public double getTempoFactor() {
		if (sequencerEstablished) {
			return BPMToFactor(sequencer.getTempoInBPM());
		} else {
			return 1f;
		}
	}
	
	/**
	 * Calculates the factor needed to get from 60bpm to the requested bpm
	 * @param bpm Requested beats per minute
	 */
	public double BPMToFactor (double bpm) {
		return bpm / 60f;
	}
	
	/**
	 * Calculates the beats per minute, according to factor * 60bpm. 
	 * @param factor The factor
	 */
	public double factorToBPM (double factor) {
		return 60f * factor;
	}
	
	public synchronized Mediator getMediator() {
		return mediator;
	}


	public synchronized void setMediator(Mediator mediator) {
		this.mediator = mediator;
	}


	public static void main(String[] args) throws Exception {
		BolBase bb = new BolBase();
		KaidaComposer al = new KaidaComposer(bb, new Teental(), Themes.getTheme01(bb));
		MidiStation ms = new MidiStation(al);
		ms.initMidi();
		ms.play();
		ms.shutDown(310);
	}



	
	
	public void exportAllToMidiFile(String filename) {
		exportAllToMidiFile(new File(filename));
	}

	/**
	 * Saves the established midi Sequence into the specified file.
	 * @param outputFile The file which shall contain the sequence.
	 */
	public void exportAllToMidiFile(File outputFile) {
		try
		{
			MidiSystem.write(sequence, 0, outputFile);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			debug.debug("file could not be saved");
			//System.exit(1);
		}
	}

	public double getMillisecondsPerBeat() {
		return (60000f / sequencer.getTempoInBPM());
		
	}
	public double getMillisecondsPerTick() {
		return (60000f / (sequencer.getTempoInBPM()*(float)ticksPerBeat));
	}
	
	
	public void printAll() {
		
		debug.debug("\nSequencer");
		debug.debug("  - Duration ticks: " + sequencer.getTickLength());
		debug.debug("  - Duration secs: " + (double)sequencer.getMicrosecondLength() / 1000000);
		debug.debug("  - is opened: " + sequencer.isOpen());		
		debug.debug("  - is running: " + sequencer.isRunning());
		
		debug.debug("  - Position secs: " + (double)sequencer.getMicrosecondPosition() / 1000000);
		debug.debug("  - LoopStart: " + sequencer.getLoopStartPoint());
		debug.debug("  - LoopEnd: " + sequencer.getLoopEndPoint());
		debug.debug("  - LoopCount: " + sequencer.getLoopCount());
		debug.debug("  - BPM: " + sequencer.getTempoInBPM());

		
		debug.debug("\nSequence");
		debug.debug("  - Duration ticks: " + sequence.getTickLength());
		debug.debug("  - Duration micros: " + sequence.getMicrosecondLength());
		
		Track[] tracks = sequence.getTracks();
		for (int i = 0; i < tracks.length; i++) {
			debug.debug("\nTrack " + i);
			Track track = tracks[i];
			debug.debug("  - is muted: " + sequencer.getTrackMute(i));
			debug.debug("  - Durationin ticks: " + track.ticks());
			debug.debug("  - Nr of events: " + track.size());
			
			for (int j = 0; j < track.size(); j++) {
				MidiEvent me = track.get(j);
				PrintEventList.printEvent(me);				
			}
		}
		
	}
	
	public Composer getComposer() {
		return initialComposer;
	}


	public Sequence getSequence() {
		return sequence;
	}


	public Sequencer getSequencer() {
		return sequencer;
	}
	



	public double getRecommendedMaximumRelativeSpeed() {
		return 4f;
	}

	public double getRecommendedMinimumRelativeSpeed() {
		return 0.5f;
	}

	/**
	 * Returns the number of ticks at the current speed, that occupy ms milliseconds.
	 * @param ms The number of milliseconds.
	 * @return
	 */
	public long milliSecondsToTicks(long ms) {
		long msInTicks = (long) (ms / getMillisecondsPerTick());
		//debug.debug(ms + " milliseconds in ticks at MPQ="+sequencer.getTempoInMPQ() +",ticksPerBeat:"+ticksPerBeat + " => "+msInTicks);
		return msInTicks;
	}

}
