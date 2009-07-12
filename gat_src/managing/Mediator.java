package managing;

import gui.composers.ComposerPanel;
import gui.composers.ComposersPanel;
import gui.composers.kaida.KaidaPanel;
import gui.playlist.Playlist;

import java.util.ArrayList;

import javax.sound.midi.Track;

import algorithm.composers.Composer;
import algorithm.composers.ComposerStandard;
import algorithm.composers.kaida.Individual;
import algorithm.composers.kaida.KaidaComposer;
import bols.tals.Tal;


public class Mediator extends Thread {
	private Playlist playlist;
	private ComposersPanel composersPanel;
	
	private ArrayList<Command> commands;
	private Boolean processing;
	private boolean doVisuals;
	private ArrayList<ComposerStandard> composers;
	private ComposerStandard currentComposer;
	private ComposerStandard lastRecentComposer;
	private boolean DEBUG;
	
	public Mediator(Playlist playlist, ComposersPanel composersPanel, ComposerStandard initialComposer) {
		super("Mediator");
		//this.algorithm = algorithm;
		composers = new ArrayList<ComposerStandard>();
		currentComposer = initialComposer;
		lastRecentComposer = null;
		composers.add(currentComposer);
		this.composersPanel = composersPanel;
		this.playlist = playlist;
		this.commands = new ArrayList<Command>();
		this.processing = Boolean.FALSE;
		doVisuals = true;
		DEBUG = true;
	}
	
	public Mediator(Playlist playlist, ComposersPanel composersPanel) {
		super("Mediator");
		//this.algorithm = algorithm;
		composers = new ArrayList<ComposerStandard>();
		currentComposer = null;
		lastRecentComposer = null;
		this.composersPanel = composersPanel;
		this.playlist = playlist;
		this.commands = new ArrayList<Command>();
		this.processing = Boolean.FALSE;
		doVisuals = true;
		DEBUG = true;
	}
	
	
	public void addComposer(ComposerStandard c) {
		out("addComposer " + c);
		composers.add(c);
	}
	
	public void run() {
		
		while (true) {
			//boolean wasInterrupted=false;
			try {
				synchronized (processing) {
					processing = Boolean.FALSE;
				}
				sleep(10000);
				
			} catch (InterruptedException e) {
				
				
//				System.out.println("mediator had interruptException");
			}
//				System.out.println("mediator is interrupted!");
			setProcessing(Boolean.TRUE);
				//interrupted, check for commands
				if (commands.isEmpty()) {
					//do nothing, wait again
//					System.out.println("mediator has no commands!");
				} else {					
					try {
						
					while (commands.size()>0) {
						Command command = commands.get(0);
//						System.out.println("mediator has" + commands.size() + " commands");
						Thread.State state;
						switch (command.getType()) {
						
						case Command.FillNextCycle:
							out("mediator should fill next cycle");
							Track t = (Track) command.getArgs()[0];
							currentComposer.setPleaseFillTrack(t, t.ticks());
							
							wakeUp(currentComposer);
							break;
							
						case Command.PrepareNextCycle:
							out("mediator should prepare filling of next cycle");
							currentComposer.setPleasePrepareFilling(true);
							
							wakeUp(currentComposer);
							break;							
							
						case Command.OneEvolutionStepDone:
							try {
								Composer c = (Composer) command.getArgs()[0];
								
									if (c.getClass() == KaidaComposer.class) {
										KaidaPanel cp = (KaidaPanel) c.getComposerPanel();
										if (cp != null) {
											cp.updateProgress();
										}
									}
								
							} catch (Exception e) {
								//nothing
							}
							break;
							
						case Command.HighLightLastIndividual:
							if (doVisuals) {
//								System.out.println("mediator should highlight inidividual");
								playlist.setPlayingLast();
							}
							break;
							
						case Command.AddCurrentIndividualToPlaylist:
							if (doVisuals) {
//							System.out.println("mediator should add individual to playlist");
								playlist.addIndividual((Individual)command.getArgs()[0],(Tal)command.getArgs()[1]);
							}
							break;
							
						case Command.SetComposer:
							out("Set Composer " + (Composer) command.getArgs()[0]);
							lastRecentComposer = currentComposer;
							currentComposer = (ComposerStandard) command.getArgs()[0];
							
							if (composers.indexOf(currentComposer)==-1) {
								composers.add(currentComposer);
							}
							
							for (Composer composer : composers) {
								if (composer != currentComposer) {
									composer.setPleasePause(true);
								}
							}
							
							wakeUp(currentComposer);
							
							composersPanel.setSolo(currentComposer);
							break;		
							
						case Command.HighLightCell:
//							System.out.println("mediator should highlightcell");
							if (doVisuals) {
								playlist.highlightCell((Integer)command.getArgs()[0]);
							}
							break;
							
						case Command.MuteComposer:
							out("Mute Composer " + currentComposer);
							//activate another one
							
							if (composers.size() > 1) {
								currentComposer.setPleasePause(true);
								if (lastRecentComposer != null) {
									
									ComposerStandard c = currentComposer;
									currentComposer = lastRecentComposer;
									lastRecentComposer = c;
									currentComposer.setPleasePause(false);
									state = currentComposer.getState();
									synchronized (currentComposer) {
										currentComposer.notifyAll();
									}
									composersPanel.setSolo(currentComposer);
									break;
									
								} else {
									for (ComposerStandard composer : composers) {
										if (composer != currentComposer) {
											
											ComposerStandard c = currentComposer;
											currentComposer = composer;
											lastRecentComposer = c;
											
											wakeUp(currentComposer);
											
											composersPanel.setSolo(currentComposer);
											//Manager.showThreads(null);
											break;
										}
									}
								}
							}
							break;
							
							case Command.PausePlayback:
								for (ComposerStandard composer : composers) {
									composer.setPleasePause(true);
								}
								break;
								
							case Command.ContinuePlayback:
								if (currentComposer!= null) {
									wakeUp(currentComposer);
								}
								break;
								
							case Command.PassOnToComposer:
								Composer nextComposer = (Composer)command.getArgs()[0];
								Object[] takeOverParams = command.getArgs();
								nextComposer.setPleaseTakeOver(takeOverParams);
								this.addCommand(new Command(Command.SetComposer, nextComposer));
								break;
								
							case Command.RestartComposer:
								Composer composer = (Composer)command.getArgs()[0];
								Object[] params = command.getArgs();
								composer.setPleaseRestart(params);
								
								break;
								
							
							case Command.CycleCompleted:
								if (currentComposer!=null) {
									ComposerPanel cp = currentComposer.getComposerPanel();
									if (cp != null) {
										cp.updateVisuals(command.getArgs());
									}
								}
								break;
								
						}
						
						command.setValid(false);
						commands.remove(command);
					}
					} catch (Exception e2) {
						e2.printStackTrace();
						System.exit(1);
					}
				}	
		}
			
	}
	
	private void wakeUp(ComposerStandard composer) {
		composer.setPleasePause(false);
		if (composer.getState() == Thread.State.WAITING) {
			synchronized(composer) {
				composer.notify();
			}
		} else if (composer.getState() == Thread.State.TIMED_WAITING) {
			synchronized(composer) {
				composer.interrupt();
			}
		}
		
	}

	public synchronized void addCommand(Command command) {
		commands.add(command);
		//out("added command: " + command);
	}
	
	public synchronized boolean isProcessing() {
		return processing.booleanValue();
	}

	protected synchronized void setProcessing(Boolean processing) {
		this.processing = processing;
	}

	protected synchronized boolean isDoVisuals() {
		return doVisuals;
	}

	protected synchronized void setDoVisuals(boolean doVisuals) {
		this.doVisuals = doVisuals;
	}
	
	public void out(String s) {
		if (DEBUG) {
			System.out.println(s);
		}
	}

	

}
