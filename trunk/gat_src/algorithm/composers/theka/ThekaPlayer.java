package algorithm.composers.theka;

import javax.sound.midi.Track;

import managing.Command;
import algorithm.composers.Composer;
import algorithm.composers.ComposerStandard;
import algorithm.composers.kaida.Individual;
import algorithm.composers.tihai.TihaiComposer;
import algorithm.interpreters.ThekaInterpreter;
import algorithm.interpreters.VariationInterpreter;
import bols.BolBase;
import bols.SubSequence;
import bols.Variation;
import bols.tals.Tal;

public class ThekaPlayer extends ComposerStandard implements Composer {
	private BolBase bolBase;
	private Tal tal;
	private Individual currentCandidate, initialCandidate;
	private VariationInterpreter talInterpreter;
	private SubSequence tihaiTail;
	
	public ThekaPlayer(BolBase bolBase, Tal tal) throws Exception {
		this(bolBase,tal,null);
	}
	
	public ThekaPlayer(BolBase bolBase, Tal tal, ThreadGroup threadGroup) throws Exception {
		super(threadGroup, "Theka Looper");
		this.bolBase = bolBase;
		this.tal = tal;
		
		this.initialCandidate = new Individual(Variation.fromTal(tal));
		this.currentCandidate = initialCandidate;
		
		this.pauseDuration = 2000;
		this.talInterpreter = new ThekaInterpreter(bolBase);
		this.tihaiTail = null;
	}

	public void run() {
		while(!isInterrupted()) {
			if ((pleaseRestart)||(pleaseTakeOver)) {
				restartOrTakeOver();
			}
			while (isPleasePause()) {
				try {
				    out("ThekaPlayer: starting to wait()");
					synchronized (this) {
						wait();
					}
				} catch (InterruptedException e1) {
					// wait for signal to continue (notify())
					out("Algorithm: waiting ended by interruption");
					//e1.printStackTrace();
				}
			}	
			try {
				
				if (getPleaseFillTrack()) {
					fillTrack(trackToFill,trackFillPosition);
				}
				if (pauseDuration!=0) {
					sleep(pauseDuration);
				}
			}
			catch (NullPointerException e) {
				System.out.println("caught NullPointerException");
				e.printStackTrace();
				System.exit(1);
				setPleasePause(true);
			}
			catch (InterruptedException e) {
				//let go on...
			}
			catch (Exception e) {
				out("theka player threw some kind of exception: " + e);
				e.printStackTrace();
				setPleasePause(true);
			}
		}	
	}
	
	public synchronized void fillTrack(Track track, long startingAt) {

		if (tihaiTail!= null) {
			currentCandidate = new Individual(TihaiComposer.mergeTailWithTheka(tihaiTail, Variation.fromTal(tal)));
			talInterpreter.renderToMidiTrack(currentCandidate.getVariation(), track, startingAt);
			
			//update display
			if (mediator != null) {
				mediator.addCommand(new Command(Command.AddCurrentIndividualToPlaylist, currentCandidate, tal));
			}
			
			currentCandidate = initialCandidate;
			tihaiTail = null;
			
		} else {
		//	render variation to track
			talInterpreter.renderToMidiTrack(currentCandidate.getVariation(), track, startingAt);	
			//update display
			if (mediator != null) {
				mediator.addCommand(new Command(Command.AddCurrentIndividualToPlaylist, currentCandidate, tal));
			}
		}		
		if (mediator != null) {
			if (!mediator.isProcessing()) {
				mediator.interrupt();
			}
		}
		
		out("to be played next:\n" + currentCandidate.toString());
		//add currentCandidate to history of all played
		
		synchronized (this) {
			pleaseFillTrack = false;
		}
		
	}

	public void restartOrTakeOver() {
		if (restartParams != null) {
			try {
				Tal newTal = (Tal) restartParams[1];
				this.tal = newTal;
			} catch (Exception e) {
				//no nothing
			}
			restartParams = null;
		}
		if (takeOverParams != null) {
			try {
				if (takeOverParams.length > 1) {
					tihaiTail = (SubSequence) takeOverParams[2];
					
				}
			} catch (Exception e) {
				//do nothing
			}
			takeOverParams = null;
		}
		pleaseRestart = false;
		pleaseTakeOver = false;
	}
	
	public int getPauseDuration() {
		return pauseDuration;
	}

	public void setPauseDuration(int pauseDuration) {
		this.pauseDuration = pauseDuration;
	}

	public Tal getTal() {
		return tal;
	}

}
