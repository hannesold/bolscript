package managing;

import gui.composers.kaida.KaidaPanel;
import gui.composers.theka.TalPanel;
import gui.composers.tihai.TihaiPanel;
import gui.managing.ManagerGraphics;
import gui.midi.MidiExportFrame;
import gui.midi.MidiOutFrame;
import gui.midi.MidiOutSelectorPanel;
import gui.playlist.Playlist;
import gui.playlist.PlaylistExportFrame;
import gui.playlist.PlaylistScrollPane;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JPanel;

import config.Config;
import config.Themes;

import midi.MidiStation;
import algorithm.composers.kaida.Individual;
import algorithm.composers.kaida.KaidaComposer;
import algorithm.composers.theka.ThekaPlayer;
import algorithm.composers.tihai.TihaiComposer;
import algorithm.mutators.MutatorDoublifyAll;
import basics.Debug;
import basics.GUI;
import bols.BolBase;
import bols.tals.Tal;
import bols.tals.Teental;

public class Manager extends ManagerGraphics implements WindowListener, ActionListener {
	
	private MidiStation midiStation = null;
	
	private KaidaComposer kaidaComposer = null;
	private ThekaPlayer thekaPlayer = null;
	private TihaiComposer tihaiComposer = null;

	private Mediator mediator = null;

	private BolBase bolBase = BolBase.getStandard();

	private boolean DEBUG;

	public JPanel displaysPanel = null;
	private Playlist playlistPanel = null;
	
	private MidiOutFrame midiOutFrame = null;
	private MidiExportFrame midiExportFrame = null;
	private PlaylistExportFrame playlistExportFrame = null;
	
	public Manager() throws Exception {
		super();
		// this.setLayout(new BorderLayout());
		DEBUG = true;
		try {
			Debug.init();
			Config.init();
			BolBase.init(this.getClass());
			
			mediator = new Mediator(getPlaylistPanel(), getComposersPanel());
			mediator.setDoVisuals(true);
			mediator.start();
			
			initComposers();
			initMidi();
			initMediator();
			
			// add composer panels
			getComposersPanel().add(new TalPanel(thekaPlayer, mediator));
			getComposersPanel().add(new KaidaPanel(kaidaComposer, mediator));
			getComposersPanel().add(new TihaiPanel(tihaiComposer, mediator));
			
			mediator.addCommand(new Command(Command.SetComposer,thekaPlayer));
			mediator.addCommand(new Command(Command.HighLightLastIndividual));
			if (!mediator.isProcessing()) {
				mediator.interrupt();
			}
			
			this.addWindowListener(this);
			
			
			
			speedSetterPanel.setMidiStation(midiStation);
			menuSetViewFollowing.setSelected(playlistPanel.isViewFollowing());
			
			btnStart.addActionListener(GUI.proxyActionListener(this,"startPlaying"));
			btnStop.addActionListener(GUI.proxyActionListener(this,"stopPlaying"));
			
			menuExit.addActionListener(GUI.proxyActionListener(this,"quit"));
			menuExportMidi.addActionListener(GUI.proxyActionListener(this,"openMidiExport"));
			menuSetMidiOut.addActionListener(GUI.proxyActionListener(this,"openMidiOutSettings"));
			menuSetViewFollowing.addActionListener(GUI.proxyActionListener(this,"setViewFollowing"));
			menuExportPlaylist.addActionListener(GUI.proxyActionListener(this,"openPlaylistExport"));
			
			//set Thread priorities
			allThreadsToMinPriority(null);
			
			this.setVisible(true);
			
		} catch (Exception e) {
			if (midiStation != null) {
				midiStation.shutDown();
			}
			out(e.getMessage());
		}

	}
	
	private void initMidi() throws Exception {
		midiStation = new MidiStation(thekaPlayer);
		MidiStation.setStandard(midiStation);
		midiStation.setMediator(mediator);
		midiStation.initMidi();
		Debug.debug(this, "midi ready: " + midiStation.isReady());
	}
	
	private void initComposers() throws Exception {
		ThreadGroup composerGroup = null;//new ThreadGroup(Thread.currentThread().getThreadGroup(), "Composer ThreadGroup");
		
		Tal tal = new Teental(bolBase);
		kaidaComposer = new KaidaComposer(bolBase, tal, Themes
				.getTheme01(bolBase), true, composerGroup);
		kaidaComposer.setPauseDuration(0);
		kaidaComposer.setMaxNrOfGenerationsPerCycle(50);
		
		kaidaComposer.setMediator(mediator);
		
		thekaPlayer = new ThekaPlayer(bolBase, tal, composerGroup);
		thekaPlayer.setMediator(mediator);
		
		Individual tihaiInit = new Individual(kaidaComposer.getInitialVariation().getCopyFull());
		new MutatorDoublifyAll(1f).mutate(tihaiInit);

		tihaiComposer = new TihaiComposer(bolBase, tal, tihaiInit.getVariation());
		tihaiComposer.setMediator(mediator);
		
		//set following order
		thekaPlayer.setNextComposer(kaidaComposer);
		kaidaComposer.setNextComposer(tihaiComposer);
		tihaiComposer.setNextComposer(thekaPlayer);
		
		kaidaComposer.setPleasePause(true);
		kaidaComposer.start();
		thekaPlayer.setPleasePause(true);
		thekaPlayer.start();
		tihaiComposer.setPleasePause(true);
		tihaiComposer.start();
	}
	
	private void initMediator() {
		mediator.addComposer(kaidaComposer);
		mediator.addComposer(thekaPlayer);

	}
	
	
	public void startPlaying(ActionEvent e) {
		if (midiStation.isReady()) {
			out("starting midiStation...");
			midiStation.play();
			mediator.addCommand(new Command(Command.ContinuePlayback));
		} else {
			out("midiStation is not ready");
		}
	}

	public void stopPlaying(ActionEvent e) {
		out("stopping midiStation and composers...");
		midiStation.stop();
		mediator.addCommand(new Command(Command.PausePlayback));
	}

	public void quit(ActionEvent e) {
		midiStation.shutDown();
		System.exit(0);
	}
	
	
	public void openMidiExport(ActionEvent e) {
		getMidiExportFrame().setVisible(true);
	}
	
	public MidiExportFrame getMidiExportFrame() {
		if (midiExportFrame == null) {
			midiExportFrame = new MidiExportFrame(midiStation);
		}
		return midiExportFrame;
	}
	
	
	public void openPlaylistExport(ActionEvent e) {
		getPlaylistExportFrame().setVisible(true);
	}
	

	
	public PlaylistExportFrame getPlaylistExportFrame() {
		if (playlistExportFrame == null) {
			playlistExportFrame = new PlaylistExportFrame(playlistPanel);
		}
		return playlistExportFrame;
	}


	public void setViewFollowing(ActionEvent e) {
		this.playlistPanel.setViewFollowing(this.menuSetViewFollowing.isSelected());
	}

	public void openMidiOutSettings(ActionEvent e) {
		getMidiOutFrame().setVisible(true);
		
	}
	public MidiOutFrame getMidiOutFrame() {
		if (midiOutFrame==null) {
			midiOutFrame = new MidiOutFrame(midiStation);
		}
		return midiOutFrame;
	}

	public void windowClosing(WindowEvent e) {
		quit(null);
	}
	public void windowDeactivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}

	public static void allThreadsToMinPriority(ActionEvent e) {
		System.out.println("setting all threads to min priority");
		ThreadGroup top = Thread.currentThread().getThreadGroup();

		while (top.getParent() != null)
			top = top.getParent();

		toMinPriority(top);
	}

	public static void toMinPriority(ThreadGroup group) {
		Thread threads[] = new Thread[group.activeCount()];

		group.enumerate(threads, false);
		System.out.println(group);

		for (int i = 0; i < threads.length; i++) {
			if (threads[i] != null) {
				
				if (group.getName() != "system") {
				if ((threads[i].getName().equals("Java Sound Sequencer")) || 
						(threads[i].getName().equals("TerrorSequencer"))){
					threads[i].setPriority(Thread.MAX_PRIORITY);
				} else if (threads[i].getName().equals("Java Sound Event Dispatcher")){
					threads[i].setPriority(1);
					//threads[i].getThreadGroup().
				} else {
					threads[i].setPriority(Thread.MIN_PRIORITY);
				}
				}
				System.out.println(group.getName() + " -> " + threads[i]
						+ " is" + isDeamon(threads[i]) + " Daemon, Priority: "
						+ threads[i].getPriority());
			}
		}

		ThreadGroup activeGroup[] = new ThreadGroup[group.activeGroupCount()];
		group.enumerate(activeGroup, false);

		for (int i = 0; i < activeGroup.length; i++)
			toMinPriority(activeGroup[i]);
	}

	public static void showThreads(ActionEvent e) {
		ThreadGroup top = Thread.currentThread().getThreadGroup();

		while (top.getParent() != null)
			top = top.getParent();

		showGroupInfo(top);
	}

	public static void showGroupInfo(ThreadGroup group) {
		Thread threads[] = new Thread[group.activeCount()];

		group.enumerate(threads, false);
		System.out.println(group);

		for (int i = 0; i < threads.length; i++)
			if (threads[i] != null)

				System.out.println(group.getName() + " -> " + threads[i]
						+ " is" + isDeamon(threads[i]) + " Daemon ");

		ThreadGroup activeGroup[] = new ThreadGroup[group.activeGroupCount()];
		group.enumerate(activeGroup, false);

		for (int i = 0; i < activeGroup.length; i++)
			showGroupInfo(activeGroup[i]);
	}

	private static String isDeamon(Thread t) {
		return t.isDaemon() ? "" : " no";
	}
	
	public void out(String s) {
		if (DEBUG) {
			System.out.println(s);
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		//do nothing...
	}

	private JPanel getDisplays() {
		if (displaysPanel == null) {
			displaysPanel = new JPanel(new BorderLayout());
			displaysPanel.add(getComposersPanel(), BorderLayout.NORTH);
			displaysPanel.add(new PlaylistScrollPane(getPlaylistPanel()), BorderLayout.CENTER);
		}
		return displaysPanel;
	}


	private Playlist getPlaylistPanel() {
		if (playlistPanel == null) {
			playlistPanel = getPlaylistScrollPane().getPlaylist();
		}
		return playlistPanel;
	}

	@Override
	protected JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = super.getJContentPane();
			jContentPane.add(getDisplays(), BorderLayout.CENTER);
			jContentPane.add(getControlsPanel(), BorderLayout.SOUTH);
		} 
		return jContentPane;
	}
	
	public static void main(String args[]) {
		
		//normal:
		
		if (args.length >0) {
			if (args[args.length-1].endsWith("cp")) {
				basics.GUI.setCrossPlattformLookAndFeel();		
			} else {
				basics.GUI.setNativeLookAndFeel();	
			}
		} else {
			basics.GUI.setNativeLookAndFeel();
		}
		
		System.out.println("starting GAT");
		Manager manager;
		try {
			manager = new Manager();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



}
