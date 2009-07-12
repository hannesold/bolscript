package algorithm.composers;

import gui.composers.ComposerPanel;

import javax.sound.midi.Track;

import managing.Mediator;

public interface Composer {
	
	public String getLabel();
	public void setLabel(String label);
	
	public void start();
	public void restartOrTakeOver();
	//public void reStart(Variation var)
	public boolean isTerminated();
	public void fillTrack(Track track, long position);
	
	public void setPleaseFillTrack(Track track, long position);
	public void setPleasePause(boolean yesno);
	public boolean isPleasePause();
	public void setPleaseTerminate(boolean yesno);

	public Mediator getMediator();

	public void setMediator(Mediator mediator);
	public void setPleaseRestart(Object[] params);
	public void setPleaseTakeOver(Object[] params);
	
	public ComposerPanel getComposerPanel();
	public void setComposerPanel(ComposerPanel panel);
	
	public Composer getNextComposer();
	public void setNextComposer(Composer nextComposer);
	public void passOnToNextComposer();
	
	
}
