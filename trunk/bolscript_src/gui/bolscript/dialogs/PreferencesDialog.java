package gui.bolscript.dialogs;

import gui.bolscript.actions.ChooseTablaDir;
import gui.midi.MidiOutSelectorPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import midi.MidiStationSimple;
import basics.Debug;
import bolscript.config.Config;
import bolscript.config.ConfigChangeEvent;
import bolscript.config.ConfigChangeListener;
import bolscript.config.UserConfig;

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
	
	@SuppressWarnings("serial")
	public void init() {
		JPanel panel = new JPanel();
		JLabel explanation = new JLabel(
				"<html>" +
				"<div style=\"width:480px; padding: 20px 80px 20px 40px; background: white; font-family:Arial; font-size:14pt; \">" +
				"<h2 style=\"padding-bottom: 10px;\">Choose a <b>Tabla Folder</b>!</h2>" +
				"When working with bolscript:"+
				"<ul>"+
						"<li style=\"padding-bottom: 10px;\">store <i>all your compositions</i> in the <b>compositions</b> subfolder of your tablafolder." +
						"<li style=\"padding-bottom: 10px;\">to customize the available bols edit your <b>bolbase.bolbase.txt</b> in the <b>settings</b> subfolder of your tablafolder with wordpad (windows) or textedit (mac).</li>" +
					"</ul>" +
				"<p style=\"font-size: 12pt;\">The subfolders will be constructed and filled with demo compositions " +
				"automatically if you choose an empty/new tabla folder.</p>" +
				"</div>" +
				"</html>");
		
		JLabel txtTablaDirLabel = new JLabel("Tabla Folder");
		
		txtTablaDir = new JTextField();
		txtTablaDir.setEditable(false);
		Debug.debug(this, "setting text");
		txtTablaDir.setText(UserConfig.tablaFolder);
		txtTablaDir.setPreferredSize(new Dimension(340, txtTablaDir.getPreferredSize().height));
		chooseAction = new ChooseTablaDir(this);
		
		btnTablaDir = new JButton(new AbstractAction("Choose") {	
			@Override
			public void actionPerformed(ActionEvent e) { chooseDir(e); }
		});
		
		txtTablaDirLabel.setLabelFor(txtTablaDir);
		JPanel tablaDirPanel = new JPanel();
		tablaDirPanel.add(txtTablaDirLabel);
		tablaDirPanel.add(txtTablaDir);
		tablaDirPanel.add(btnTablaDir);
		
		/*BoxLayout bx = new BoxLayout(panel, BoxLayout.Y_AXIS);
		explanation.setAlignmentX(0);
		tablaDirPanel.setAlignmentX(0);*/
		
		JPanel buttonPanel = new JPanel();
		ok = new JButton(new AbstractAction("OK") {
			
			@Override
			public void actionPerformed(ActionEvent e) { 
				ok(e);	
			}
		});
		if (UserConfig.firstRun &! UserConfig.hasChosenTablaFolderThisRun) {
			ok.setEnabled(false);
			
		}
		
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		buttonPanel.add(ok);

		panel.setLayout(new BorderLayout());
		panel.add(explanation, BorderLayout.NORTH);
		panel.add(tablaDirPanel, BorderLayout.CENTER);
		
		if (MidiStationSimple.getStandard() !=null) {
			MidiOutSelectorPanel midiOutSelectorPanel = new MidiOutSelectorPanel(MidiStationSimple.getStandard());
			midiOutSelectorPanel.setAlignmentX(0);
			panel.add(midiOutSelectorPanel);
		}
		
		panel.add(buttonPanel, BorderLayout.SOUTH);
		
		this.setContentPane(panel);
		
		Config.addChangeListener(this);
		
		this.setResizable(false);
	}
	
	/**
	 * Is called when the choose button was pressed.
	 * @param e
	 */
	public void chooseDir(ActionEvent e) {
		chooseAction.actionPerformed(e);
		String chosenFolder = chooseAction.getChosenFolder();
		if ((chosenFolder != null)
				&! chosenFolder.equals(UserConfig.tablaFolder)) {
			UserConfig.setTablaFolder(chooseAction.getChosenFolder());
			Config.fireConfigChangedEvent();
			changed = true;
			ok.setEnabled(true);
		}
	}
	
	/**
	 * Gets called by Config.fireConfigChangedEvent()
	 */
	public void configChanged(ConfigChangeEvent e) {
		txtTablaDir.setText(UserConfig.tablaFolder);
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
