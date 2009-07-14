package gui.bolscript.dialogs;

import gui.bolscript.actions.ChooseTablaDir;
import gui.midi.MidiOutSelectorPanel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import midi.MidiStationSimple;
import basics.Debug;
import basics.GUI;
import bolscript.config.Config;
import bolscript.config.ConfigChangeEvent;
import bolscript.config.ConfigChangeListener;

public class PreferencesDialog extends JDialog implements WindowListener, ConfigChangeListener{

	JTextField txtTablaDir;
	JButton btnTablaDir;
	JButton ok;
	ChooseTablaDir chooseAction;
	boolean changed = true;
	
	public PreferencesDialog() {
		super();
		this.setTitle("Preferences");
		init();
		this.pack();
	}
	
	public void init() {
		JPanel panel = new JPanel();
		JLabel explanation = new JLabel("<html><p style=\"width:400px; padding: 10px;\">The <b>Tabla directory</b> has to contain two subfolders:<br><b>compositions</b> for your compositions and <br><b>settings</b> for your settings.<br><br><i>These subfolders will be constructed automatically if they don't exist.</i></p></html>");
		JLabel txtTablaDirLabel = new JLabel("Tabla Folder");
		txtTablaDir = new JTextField();
		txtTablaDir.setEditable(false);
		Debug.debug(this, "setting text");
		txtTablaDir.setText(Config.tablaFolder);
		txtTablaDir.setPreferredSize(new Dimension(340, txtTablaDir.getPreferredSize().height));
		chooseAction = new ChooseTablaDir(this);
		
		btnTablaDir = new JButton("Choose");
		btnTablaDir.addActionListener(GUI.proxyActionListener(this, "chooseDir"));
		txtTablaDirLabel.setLabelFor(txtTablaDir);
		JPanel tablaDirPanel = new JPanel();
		tablaDirPanel.add(txtTablaDirLabel);
		tablaDirPanel.add(txtTablaDir);
		tablaDirPanel.add(btnTablaDir);
		
		BoxLayout bx = new BoxLayout(panel, BoxLayout.Y_AXIS);
		explanation.setAlignmentX(0);
		tablaDirPanel.setAlignmentX(0);
		
		JPanel buttonPanel = new JPanel();
		ok = new JButton("OK");
		buttonPanel.add(ok);
		ok.addActionListener(GUI.proxyActionListener(this,"ok"));
		
		panel.setLayout(bx);
		panel.add(explanation);
		panel.add(tablaDirPanel);
		
		if (MidiStationSimple.getStandard() !=null) {
			MidiOutSelectorPanel midiOutSelectorPanel = new MidiOutSelectorPanel(MidiStationSimple.getStandard());
			midiOutSelectorPanel.setAlignmentX(0);
			panel.add(midiOutSelectorPanel);
		}
		
		panel.add(buttonPanel);
		
		this.setContentPane(panel);
		
		Config.addChangeListener(this);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}
	
	/**
	 * Is called when the choose button was pressed.
	 * @param e
	 */
	public void chooseDir(ActionEvent e) {
		chooseAction.actionPerformed(e);
		String chosenFolder = chooseAction.getChosenFolder();
		if ((chosenFolder != null)
				&! chosenFolder.equals(Config.tablaFolder)) {
			Config.setTablaFolder(chooseAction.getChosenFolder());
			Config.fireConfigChangedEvent();
			changed = true;
		}
	}
	
	/**
	 * Gets called by Config.fireConfigChangedEvent()
	 */
	public void configChanged(ConfigChangeEvent e) {
		txtTablaDir.setText(Config.tablaFolder);
	}
	
	/**
	 * Gets called when the OK button was pressed
	 */
	public void ok(ActionEvent e) {
		close();
	}
	
	/**
	 * closes the preferences dialog
	 */
	public void close() {
		this.setVisible(false);
	}
	
	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {

	}

	public void windowClosing(WindowEvent e) {
		close();
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	

}
