package algorithm.composers;

import gui.composers.ComposerPanel;

import javax.sound.midi.Track;

import basics.Debug;

import managing.Command;
import managing.Mediator;

public abstract class ComposerStandard extends Thread implements Composer {

	protected boolean pleasePause;
	protected int pauseDuration;
	
	protected boolean pleaseFillTrack;
	
	protected boolean pleasePrepareFilling;
	
	protected boolean pleaseTerminate;
	
	protected Track trackToFill;
	protected long trackFillPosition;
	protected boolean DEBUG;	
	
	protected String label;
	
	protected Mediator mediator;
	
	/**
	 * Parameters that can be set by setPleaseRestart(Object[])
	 */
	protected Object[] restartParams;
	
	protected boolean pleaseRestart;
	
	protected Object[] takeOverParams;
	protected boolean pleaseTakeOver;
	
	protected ComposerPanel composerPanel;
	protected Composer nextComposer;
	
	public ComposerStandard(ThreadGroup threadGroup, String string) {
		super((threadGroup != null)?threadGroup:Thread.currentThread().getThreadGroup(),string);
		label = string;
		DEBUG = true;
		pleasePause = false;
		pleaseFillTrack = false;
		pleasePrepareFilling = false;
		pleaseTerminate = false;
		pleaseRestart = false;
		mediator = null;
	}

	public void start() {
		super.start();
	}
	
	public void restartOrTakeOver() {
		pleaseRestart = false;
		pleaseTakeOver = false;
	}
	
	public void takeOver() {
		pleaseTakeOver = false;
	}

	public abstract void fillTrack(Track track, long startingAt);
	
	//terminate
	public boolean isTerminated() {
		return false;
	}

	public void setPleaseTerminate(boolean yesno) {	
		pleaseTerminate = yesno;
	}
	
	//pause
	public synchronized boolean isPleasePause() {
		return pleasePause;
	}

	public synchronized void setPleasePause(boolean pleasePause) {
		this.pleasePause = pleasePause;
		out(this + ".setPleasePause " + pleasePause);
	}
	
	/**
	 * fill	
	 */
	public synchronized void setPleaseFillTrack(Track track, long startingAt) {
		pleaseFillTrack = true;
		trackToFill = track;
		trackFillPosition = startingAt;
		out(this + ".setPleaseFillTrack " + pleaseFillTrack);
	}
	
	public synchronized boolean getPleaseFillTrack() {
		return pleaseFillTrack;
	}
	
	protected void out(Object message)
	{
		Debug.debug(this, message);
	}


	public Mediator getMediator() {
		return mediator;
	}

	public void setMediator(Mediator mediator) {
		this.mediator = mediator;
	}

	public void setPleaseRestart(Object[] params) {
		this.restartParams = params;
		pleaseRestart = true;
	}
	
	
	public ComposerPanel getComposerPanel() {
		return composerPanel;
	}

	public void setComposerPanel(ComposerPanel composerPanel) {
		this.composerPanel = composerPanel;
	}
	
	public Composer getNextComposer() {
		return nextComposer;
	}

	public void setNextComposer(Composer nextComposer) {
		this.nextComposer = nextComposer;
	}
	
	public void passOnToNextComposer() {
		mediator.addCommand(new Command(Command.PassOnToComposer, nextComposer, this));
	}

	public void setPleaseTakeOver(Object[] params) {
		this.takeOverParams = params;
		pleaseTakeOver = true;		
	}

	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isPleasePrepareFilling() {
		return pleasePrepareFilling;
	}

	/**
	 * A notification, that filling of the track shall be prepared.
	 * @param pleasePrepareFilling
	 */
	public void setPleasePrepareFilling(boolean pleasePrepareFilling) {
		this.pleasePrepareFilling = pleasePrepareFilling;
	}
	
}
