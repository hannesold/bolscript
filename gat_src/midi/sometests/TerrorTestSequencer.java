package midi.sometests;

import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;

import algorithm.interpreters.VariationInterpreterStandard;
import bols.BolBase;

public class TerrorTestSequencer extends Thread {
	Receiver receiver;
	boolean open;
	boolean started;
	Sequence sequence;
	long t = 0;
	long duration = 90;
	long durationTotal;
	
	public TerrorTestSequencer() {
		super("TerrorSequencer");
	}
	public Receiver getReceiver() {
		return receiver;
	}

	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

	public void run() {
		t = 0;
		durationTotal = 1000 * 120;
		ShortMessage message = VariationInterpreterStandard.noteOn(
				1,BolBase.getStandard().getMidiMap("Na").getMidiNote()+BolBase.getStandard().getGeneralNoteOffset(), 100)
				;
		while (t < durationTotal) {
			//if (receiver.)
			receiver.send(
					message, -1);
			try {
				sleep(duration);
				t += duration;
			} catch (InterruptedException e) {
				//ignore ?
			}
		}
	}
	
	
	
	
}
