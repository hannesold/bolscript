package gui.managing;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JButton;
import gui.composers.ComposersPanel;

import gui.playlist.PlaylistScrollPane;
import gui.midi.SpeedSetterPanel;
import javax.swing.JCheckBoxMenuItem;

public class ManagerGraphics extends JFrame {

	protected JPanel jContentPane = null;
	protected JMenuBar topMenu = null;
	protected JMenu menuFile = null;
	protected JMenuItem menuExportMidi = null;
	protected JMenuItem menuExit = null;
	protected ComposersPanel composersPanel = null;
	protected JPanel controlsPanel = null;
	protected JButton btnStart = null;
	protected JButton btnStop = null;
	protected PlaylistScrollPane playlistScrollPane = null;
	protected JMenu settingsMenu = null;
	protected JMenuItem menuSetMidiOut = null;
	protected SpeedSetterPanel speedSetterPanel = null;
	protected JCheckBoxMenuItem menuSetViewFollowing = null;
	protected JMenu menuView = null;
	protected JMenuItem menuExportPlaylist = null;
	/**
	 * This is the default constructor
	 */
	public ManagerGraphics() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	protected void initialize() {
		this.setSize(1000,800);
		this.setJMenuBar(getTopMenu());
		this.setContentPane(getJContentPane());
		this.setTitle("GAT - Genetic Algorithm Tabla");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	protected JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getComposersPanel(), java.awt.BorderLayout.NORTH);
			jContentPane.add(getControlsPanel(), java.awt.BorderLayout.SOUTH);
			jContentPane.add(getPlaylistScrollPane(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes topMenu	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	protected JMenuBar getTopMenu() {
		if (topMenu == null) {
			topMenu = new JMenuBar();
			topMenu.add(getMenuFile());
			topMenu.add(getSettingsMenu());
			topMenu.add(getMenuView());
		}
		return topMenu;
	}

	/**
	 * This method initializes menuFile	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	protected JMenu getMenuFile() {
		if (menuFile == null) {
			menuFile = new JMenu();
			menuFile.setText("File");
			menuFile.add(getMenuExportMidi());
			menuFile.add(getMenuExportPlaylist());
			menuFile.add(getMenuExit());
		}
		return menuFile;
	}

	/**
	 * This method initializes menuExportMidi	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	protected JMenuItem getMenuExportMidi() {
		if (menuExportMidi == null) {
			menuExportMidi = new JMenuItem("Export Midi");
			menuExportMidi.setText("Export As Midi File");
		}
		return menuExportMidi;
	}

	/**
	 * This method initializes menuExit	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	protected JMenuItem getMenuExit() {
		if (menuExit == null) {
			menuExit = new JMenuItem("Exit");
		}
		return menuExit;
	}

	/**
	 * This method initializes composersPanel	
	 * 	
	 * @return gui.composers.ComposersPanel	
	 */
	protected ComposersPanel getComposersPanel() {
		if (composersPanel == null) {
			composersPanel = new ComposersPanel();
		}
		return composersPanel;
	}

	/**
	 * This method initializes controlsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	protected JPanel getControlsPanel() {
		if (controlsPanel == null) {
			controlsPanel = new JPanel();
			controlsPanel.add(getSpeedSetterPanel(), null);
			controlsPanel.add(getBtnStart(), null);
			controlsPanel.add(getBtnStop(), null);
		}
		return controlsPanel;
	}

	/**
	 * This method initializes btnStart	
	 * 	
	 * @return javax.swing.JButton	
	 */
	protected JButton getBtnStart() {
		if (btnStart == null) {
			btnStart = new JButton();
			btnStart.setText("start");
		}
		return btnStart;
	}

	/**
	 * This method initializes btnStop	
	 * 	
	 * @return javax.swing.JButton	
	 */
	protected JButton getBtnStop() {
		if (btnStop == null) {
			btnStop = new JButton();
			btnStop.setText("stop");
		}
		return btnStop;
	}

	/**
	 * This method initializes playlistScrollPane	
	 * 	
	 * @return gui.playlist.PlaylistScrollPane	
	 */
	protected PlaylistScrollPane getPlaylistScrollPane() {
		if (playlistScrollPane == null) {
			playlistScrollPane = new PlaylistScrollPane();
		}
		return playlistScrollPane;
	}

	/**
	 * This method initializes settingsMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	protected JMenu getSettingsMenu() {
		if (settingsMenu == null) {
			settingsMenu = new JMenu();
			settingsMenu.setText("Settings");
			settingsMenu.add(getMenuSetMidiOut());
		}
		return settingsMenu;
	}

	/**
	 * This method initializes menuSetMidiOut	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	protected JMenuItem getMenuSetMidiOut() {
		if (menuSetMidiOut == null) {
			menuSetMidiOut = new JMenuItem("Midi Out");
		}
		return menuSetMidiOut;
	}

	/**
	 * This method initializes speedSetterPanel	
	 * 	
	 * @return gui.midi.SpeedSetterPanel	
	 */
	protected SpeedSetterPanel getSpeedSetterPanel() {
		if (speedSetterPanel == null) {
			speedSetterPanel = new SpeedSetterPanel();
		}
		return speedSetterPanel;
	}

	/**
	 * This method initializes menuSetViewFollowing	
	 * 	
	 * @return javax.swing.JCheckBoxMenuItem	
	 */
	protected JCheckBoxMenuItem getMenuSetViewFollowing() {
		if (menuSetViewFollowing == null) {
			menuSetViewFollowing = new JCheckBoxMenuItem("Follow Playback");
			
		}
		return menuSetViewFollowing;
	}

	/**
	 * This method initializes menuView	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getMenuView() {
		if (menuView == null) {
			menuView = new JMenu();
			menuView.setText("View");
			menuView.add(getMenuSetViewFollowing());
		}
		return menuView;
	}

	/**
	 * This method initializes menuExportPlaylist	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	protected JMenuItem getMenuExportPlaylist() {
		if (menuExportPlaylist == null) {
			menuExportPlaylist = new JMenuItem();
			menuExportPlaylist.setText("Export Notation As Image");
		}
		return menuExportPlaylist;
	}

}
