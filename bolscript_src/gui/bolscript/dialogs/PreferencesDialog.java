package gui.bolscript.dialogs;

import gui.bolscript.actions.ChooseTablaDir;
import gui.midi.MidiOutSelectorPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
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
import bolscript.config.PreferenceKeys;
import bolscript.config.UserConfig;

public class PreferencesDialog extends JDialog implements WindowListener, ConfigChangeListener{

	JPanel userNameContainerPanel;
	JTextField txtLibraryFolder;
	JTextField txtUserName;
	JLabel userNameWarning;
	JButton btnLibraryFolderChooser;
	JButton ok;
	ChooseTablaDir chooseAction;
	String previousTxtUserNameText;
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
		JLabel libraryFolderExplanation = new JLabel(
				"<html>" +
				"<div style=\"width:480px; padding: 20px 80px 20px 40px; background: white; font-family:Arial; font-size:14pt; \">" +
				"<h2 style=\"padding-bottom: 10px;\">Choose a <b>Library Folder</b>!</h2>" +
				"When working with bolscript:"+
				"<ul>"+
				"<li style=\"padding-bottom: 10px;\">store <i>all your compositions</i> in the <b>compositions</b> subfolder of your library." +
				"<li style=\"padding-bottom: 10px;\">to customize the available bols edit your <b>bolbase.bolbase.txt</b> in the <b>settings</b> subfolder of your library with wordpad (windows) or textedit (mac).</li>" +
				"</ul>" +
				"<p style=\"font-size: 12pt;\">The subfolders will be constructed and filled with demo compositions " +
				"automatically if you choose an empty/new library folder.</p>" +
				"</div>" +
		"</html>");

		JLabel txtLibraryFolderLabel = new JLabel("Library Folder");

		txtLibraryFolder = new JTextField();
		txtLibraryFolder.setEditable(false);
		txtLibraryFolder.setEnabled(false);

		Debug.debug(this, "setting text");
		txtLibraryFolder.setText(UserConfig.libraryFolder);
		txtLibraryFolder.setPreferredSize(new Dimension(340, txtLibraryFolder.getPreferredSize().height));
		chooseAction = new ChooseTablaDir(this);

		btnLibraryFolderChooser = new JButton(new AbstractAction("Browse...") {	
			@Override
			public void actionPerformed(ActionEvent e) { chooseDir(e); }
		});

		txtLibraryFolderLabel.setLabelFor(txtLibraryFolder);
		JPanel tablaDirPanel = new JPanel();
		tablaDirPanel.add(txtLibraryFolderLabel);
		tablaDirPanel.add(txtLibraryFolder);
		tablaDirPanel.add(btnLibraryFolderChooser);



		JLabel userNameExplanation = new JLabel("<html>" +
				"<div style=\"width:480px; padding: 20px 80px 20px 40px; background: white; font-family:Arial; font-size:14pt; \">" +
				"<h2 style=\"padding-bottom: 10px;\">Choose a <b>User name</b>!</h2>" +
				"The User name is used for:"+
				"<ul>"+
				"<li style=\"padding-bottom: 10px;\">Tracking the edit-history (creation, modification) of a bolscript file.</li>" +
				"<li style=\"padding-bottom: 10px;\">It is used as a default entry for the 'Editor:' tag</li>" +
				"</ul>" +
				"<p style=\"font-size: 12pt;\">If you leave the user name field empty, it will default to 'Unknown'</p>" +
				"</div>" +
		"</html>");

		txtUserName = new JTextField(UserConfig.getUserId());
		previousTxtUserNameText = txtUserName.getText();
		txtUserName.setPreferredSize(new Dimension(340, txtUserName.getPreferredSize().height));
		userNameWarning = new JLabel("<html>" +
				"<div style=\"width:480px; padding: 20px 80px 20px 40px; color: red;  font-family:Arial; font-size:14pt; \">" +
				"This is not a valid username. It will only be stored when this warning is gone." +
				"</div>" +
		"</html>");
		//userNameWarning.setPreferredSize(new Dimension(480,70));


		JLabel userNameLabel = new JLabel("User name");
		JPanel userNameEditPanel = new JPanel();
		userNameEditPanel.add(userNameLabel);
		userNameEditPanel.add(txtUserName);

		txtUserName.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {
				attemptChangeUserName(null);	
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});


		JPanel buttonPanel = new JPanel();
		ok = new JButton(new AbstractAction("OK") {

			@Override
			public void actionPerformed(ActionEvent e) { 
				ok(e);	
			}
		});

		if (UserConfig.firstRun &! UserConfig.hasChosenLibraryFolderThisRun) {
			ok.setEnabled(false);
			//this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		}

		

		buttonPanel.add(ok);
		JPanel libraryFolderPanel = new JPanel(new BorderLayout());
		libraryFolderPanel.add(libraryFolderExplanation, BorderLayout.NORTH);
		libraryFolderPanel.add(tablaDirPanel, BorderLayout.CENTER);

		userNameContainerPanel = new JPanel(new BorderLayout());
		userNameContainerPanel.add(userNameExplanation, BorderLayout.NORTH);
		userNameContainerPanel.add(userNameEditPanel, BorderLayout.CENTER);


		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(libraryFolderPanel);
		panel.add(userNameContainerPanel);

		if (MidiStationSimple.getStandard() !=null) {
			MidiOutSelectorPanel midiOutSelectorPanel = new MidiOutSelectorPanel(MidiStationSimple.getStandard());
			midiOutSelectorPanel.setAlignmentX(0);
			panel.add(midiOutSelectorPanel);
		}

		panel.add(buttonPanel);

		this.setContentPane(panel);

		Config.addChangeListener(this);

		this.addWindowListener(this);
		this.setResizable(false);

	}

	protected void refreshFromConfig() {
		txtUserName.setText(UserConfig.getUserId());
		setUserNameWarnings(false);
		//library folder changes are tracked well enough by other mechanisms
	}

	/**
	 * Is called when the choose button was pressed.
	 * @param e
	 */
	public void chooseDir(ActionEvent e) {
		chooseAction.actionPerformed(e);
		String chosenFolder = chooseAction.getChosenFolder();
		if ((chosenFolder != null)
				&! chosenFolder.equals(UserConfig.libraryFolder)) {
			UserConfig.setLibraryFolder(chooseAction.getChosenFolder());
			Config.fireConfigChangedEvent(PreferenceKeys.LIBRARY_FOLDER);
			changed = true;
			ok.setEnabled(true);
			this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		}
	}

	private void attemptChangeUserName(ActionEvent e) {
		if (!txtUserName.getText().equals(previousTxtUserNameText)) {
			previousTxtUserNameText = txtUserName.getText();
			boolean worked = UserConfig.setUserIdAfterValidation(txtUserName.getText());
			setUserNameWarnings(!worked);
		}
	}
	
	private void setUserNameWarnings(boolean visible) {
		if (visible) {
			txtUserName.setForeground(Color.RED);
			userNameContainerPanel.add(userNameWarning, BorderLayout.SOUTH);
			this.pack();			
		} else {
			txtUserName.setForeground(Color.BLACK);
			Config.fireConfigChangedEvent(PreferenceKeys.USER_ID);
			userNameContainerPanel.remove(userNameWarning);
			this.pack();			
		}
	}

	/**
	 * Gets called by Config.fireConfigChangedEvent()
	 */
	public void configChanged(ConfigChangeEvent e) {
		if (e.hasChanged(PreferenceKeys.LIBRARY_FOLDER)) {
			txtLibraryFolder.setText(UserConfig.libraryFolder);
		}
		if (e.hasChanged(PreferenceKeys.USER_ID)) {
			txtUserName.setText(UserConfig.getUserId());
		}
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
		if (UserConfig.firstRun &! UserConfig.hasChosenLibraryFolderThisRun) {
			System.exit(0); 
		}
		this.setVisible(false);
	}
	
	
	public void setVisible(boolean visible) {
		if (visible) {
			refreshFromConfig();
		}
		super.setVisible(visible);

	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {

		close();
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
