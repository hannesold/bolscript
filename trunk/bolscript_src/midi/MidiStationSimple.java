package midi;

import gui.bolscript.sequences.BolPanel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;
import javax.sound.midi.MidiDevice.Info;

import basics.Debug;
import basics.GUI;
import basics.Tools;
import bols.Bol;
import bols.BolBase;
import bols.BolMap;
import bols.BolName;
import bols.MidiMap;
import bols.PlayingStyle;

/**
 * A class that manages a midi output and has the ability to send single midi messages.
 * @author hannes
 *
 */
public class MidiStationSimple {
	protected Transmitter transmitter;
	protected Receiver receiver;
	protected MidiDevice outputDevice;
	protected MouseListener singleBolClickListener;
	protected boolean midiOutEstablished;
	protected Debug debug;
	
	private static MidiStationSimple standard = null;
	
	public static void init() {
		standard = new MidiStationSimple();
		standard.initMidiOut();
	}
	
	/**
	 * Returns a standard representative of the class.
	 * Returns null if standard was not initialized.
	 */
	public static MidiStationSimple getStandard() {
		return standard;
	}
	
	/**
	 * This is a simple constructor,
	 * initMidiOut has to be called seperately.
	 */
	public MidiStationSimple() {
		debug = new Debug(MidiStationSimple.class);
	}
	
	/**
	 * Attempts to initializes a midi out, simply taking the first in the list of availables.
	 */
	public void initMidiOut() {
		//Try to set a midi out device (simply the first in the list)
		MidiDevice.Info[] possibleOuts = this.getPossibleOuts();
		if (possibleOuts.length > 0) {
			setMidiOut(possibleOuts[0]);
		} 
	}
	
	/**
	 * @return The available Midi-Out Devices as an Array of MidiDevice.Info Objects
	 * @see MidiDevice.Info
	 */
	public Info[] getPossibleOuts() {

		MidiDevice.Info[] possibleOuts = new MidiDevice.Info[1];
		ArrayList<MidiDevice.Info> outs = new ArrayList<MidiDevice.Info>(15);
		
		MidiDevice.Info[]	deviceInfos = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < deviceInfos.length; i++)
		{
			if (deviceInfos[0] != null) {
				try
				{
					MidiDevice	device = MidiSystem.getMidiDevice(deviceInfos[i]);
					if (device != null) {
						boolean	allowsOutput = (device.getMaxReceivers() != 0);
						if (allowsOutput) {
							outs.add(deviceInfos[i]);
						}
					}
				}
				catch (MidiUnavailableException e)
				{
					// device is obviously not available...
					// debug.debug(e);
				}
			}
		}
		
		possibleOuts = outs.toArray(possibleOuts);
		
		return possibleOuts;
	}
	
	/**
	 * Sends a single Midi ShortMessage to the receiver,
	 * but only if midiOutEstablished is true.
	 * @param message The midi Message
	 */
	public void sendMidi(ShortMessage message) {
		if (midiOutEstablished) {
			try {
				receiver.send(message,-1);
			} catch (Exception e) {
				Debug.debug(this, "midi could not be sent");
			}
		}
	}
	
	/**
	 * Plays a single Bol
	 * @param bn
	 */
	public void playSingleBol(Bol bol) {
		if (midiOutEstablished) {
			BolName bn = bol.getBolName();
			Debug.temporary(this, "play " + bn.toStringComplete());
			
			BolMap bolMap = BolBase.getStandard().getBolMap(bn);
			if (bolMap != null) {
				MidiMap[] midiMaps = bolMap.getMidiMaps();
				if (midiMaps != null) {
					Debug.temporary(this,"got midimaps: " + Tools.toString(midiMaps));
					PlayingStyle style = new PlayingStyle(1f,1f);

					for (int j=0; j < midiMaps.length; j++) {
						MidiMap mm = midiMaps[j];

						if ((mm.getHand() == MidiMap.NONE) || 
								(mm.getBolName().toString() == "-")) {
						} else {
							int vel;

							if (mm.getHand()== MidiMap.LEFT) {				
								vel = (int) (127.0f * style.velocity * bolMap.getLeftHandVelocityScale());
								vel = Math.min(Math.max(vel, 0),127);

							} else if (mm.getHand()==MidiMap.RIGHT) {
								vel = (int) (127.0f * style.velocity * bolMap.getRightHandVelocityScale());
								vel = Math.min(Math.max(vel, 0),127);

							} else {
								debug.debug("sorry, bad hand info");
								vel = 100;
							}

							int note = BolBase.getStandard().getGeneralNoteOffset() + mm.getMidiNote();
							ShortMessage noteOn = noteOn(0, note, vel);
							ShortMessage noteOff = noteOff(0, note, vel);

							Debug.temporary(this, "sending note: " + note + ", vel:" + vel);
							sendMidi(noteOn);
							sendMidi(noteOff);
						}
					}
					
					
					
				}
			}
		}
	}
	
	/**
	 * Is called when a bolPanel is clicked
	 * @param e The MouseEvent
	 */
	public void bolPanelClicked(MouseEvent e) {
		Debug.temporary(this,"playSingleBol");
		if (midiOutEstablished) {
			if (BolPanel.class.isInstance(e.getSource())) {
				BolPanel sender = (BolPanel) e.getSource();
				playSingleBol(sender.getBol());
			}
		}
	}
	
	/**
	 * Returns a Listener, which calls bolPanelClicked.
	 * @return
	 */
	public MouseListener getSingleBolClickListener() {
		if (singleBolClickListener==null) {
			singleBolClickListener = GUI.proxyClickListener(this,"bolPanelClicked");
		} 
		return singleBolClickListener;
	}

	/**
	 * Sets the Midi Out of the MidiStation. This is where the sequencer sends its midi signals to. 
	 * @param info The MidiDevice.Info Object of the MidiDevice
	 */
	public void setMidiOut(Info info) {

		//get the output device (Midi Yoke 1 mapped to Midi Yoke input 4)
		if (outputDevice != null) {
			//close the previous
			if (outputDevice.isOpen()) {
				outputDevice.close();
			}
		}
		outputDevice = null;
		receiver = null;

		//try to get the Output Device 
		if (info == null) {
			debug.debug("no device info found");
			midiOutEstablished = false;
		} else {
			debug.debug("setting output");
			try {
				outputDevice = MidiSystem.getMidiDevice(info);
				debug.debug("MidiDevice: " + outputDevice);
				debug.debug("opening device");
				outputDevice.open();
				debug.debug("getting receiver");
				receiver = outputDevice.getReceiver();

				midiOutEstablished = true;

			} catch (Exception e) {
				debug.debug("midiOut could not be opened");
				midiOutEstablished = false;
			}
		}
	}
	
	
	/**
	 * Returns the outputDevices MidiInfo, or null if midiOut is not established.
	 */
	public Info getOutputDeviceInfo() {
		if (midiOutEstablished) {
			return outputDevice.getDeviceInfo();
		} else {
			return null;
		}
	}
	
	/**
	 * Closes output device and receiver. 
	 */
	public void shutDown() {
		debug.debug("shutting down midiStationSimple");
		if (midiOutEstablished) {
			receiver.close();
			outputDevice.close();
		}
	}
	
	/**
	 * Returns true if the MidiStationSimples midiOutPut is established
	 */
	public boolean isReady() {
		return midiOutEstablished;
	}

	public static void setStandard(MidiStationSimple standard2) {
		standard = standard2;
		
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
