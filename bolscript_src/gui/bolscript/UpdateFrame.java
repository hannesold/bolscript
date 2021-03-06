package gui.bolscript;

import gui.bolscript.actions.OpenURL;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import bolscript.UpdateInfo;
import bolscript.UpdateInfo.VersionState;
import bolscript.UpdateManager.UpdateManagerListener;
import bolscript.config.Config;
import bolscript.config.Config.OperatingSystems;


public class UpdateFrame extends JFrame implements UpdateManagerListener{

	JPanel infoPanel;
	JPanel changelogPanel;
	JButton updateButton;
	JButton okButton;
	JButton cancelButton;
	JButton gotoDownloadPageButton;
	JLabel changeLogLabel;
	boolean autoUpdatePossible;
	boolean openLinkPossible;
	JButton gotoDownloadPage;
	ActionListener closeListener;

	private String changeLogContent;
	private String downloadLink;

	UpdateInfo updateInfo = null;
	
	public UpdateFrame() {
		super("Bolscript Updates");
		closeListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				close();				
			}
		};

		autoUpdatePossible = false;  
		/*try {
			autoUpdatePossible = Config.jarIsEditable(Config.getJarPath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */

		openLinkPossible = false;
		if(java.awt.Desktop.isDesktopSupported() ) {
			java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
			if(desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ) {
				openLinkPossible = true;
			}
		}	
		
		initStuff();
	}

	private void initStuff() {		
		
		JScrollPane scrollPane = new JScrollPane(getChangeLogPanel());

		JPanel all = new JPanel();
		
		all.setLayout(new BorderLayout());
		all.add(getInfoPanel(), BorderLayout.NORTH);
		
		int height = 150; 
		if (updateInfo != null) {
			if (updateInfo.getResult() != VersionState.OK) {
				all.add(scrollPane, BorderLayout.CENTER);
				if (updateInfo.getResult() != VersionState.OK) {
					height = 400;
				}			
			}
		}
		
		if (Config.operatingSystem == OperatingSystems.Windows) {
			height +=15;
		}
		
		all.add(getButtonPanel(), BorderLayout.SOUTH);
		this.setContentPane(all);

		this.setContentPane(all);
		this.setPreferredSize(new Dimension(460,height));
		this.setMinimumSize(this.getPreferredSize());
		this.pack();
	}

	private JPanel getInfoPanel() {
		infoPanel = new JPanel();
		infoPanel.setBackground(Color.WHITE);
		JLabel infoText = new JLabel();
		infoPanel.add(infoText);
		if (updateInfo == null) {
			infoText.setText("Checking for updates...");
		} else {			
			String currentVersionOutput = "";
			String currentVersion = "";
			if (updateInfo.getCurrentVersion() != null) {
				if (updateInfo.getCurrentVersion().getGeneratedVersionString() != null) {
					currentVersionOutput= updateInfo.getCurrentVersion().getGeneratedVersionString();
					currentVersion = "<span style=\"color:#444444;\">Your current Version is: "+currentVersionOutput+"</span>";
				}
			}
			switch (updateInfo.getResult()) {
			case HasUpdates:
				String newVersion = "";

				if (updateInfo.getDownload() != null) {
					newVersion = "<h3>"+updateInfo.getDownload().getTitle()+"</h3>";
				}

				infoText.setText("<html><body style=\"width:300px;\">" 
						+((!currentVersionOutput.isEmpty())?currentVersion:"")+
						"<div><b>A newer Version is available</b></div>" 
						+ newVersion  +
				"</body></html>");
				break;
			case CouldNotCheck:
				infoText.setText("<html><body style=\"width:300px;\">" +
						"<div>Automatic update checking did not work.</div>"
						+"Please check on the download page yourself."
						+((!currentVersionOutput.isEmpty())?"<br/>"+currentVersion:"")
						+"</body></html>");
				break;
			case OK:
				infoText.setText("<html><body style=\"width:300px;\"><div>You already have the latest version.</div>"
						+((!currentVersionOutput.isEmpty())?currentVersion+"<br/>":"")
						+"No need for updating...</body></html>");
			}
			
		}
		infoPanel.setPreferredSize(new Dimension(400,85));
		return infoPanel;
	}

	public JPanel getButtonPanel() {
		JPanel buttonPanel = new JPanel();

		if (updateInfo == null) {
			cancelButton = new JButton(); 
			cancelButton.setText("Cancel");
			cancelButton.addActionListener(closeListener);
			buttonPanel.add(cancelButton);			
		} else {
			if (updateInfo.getResult() == VersionState.OK) {
				okButton = new JButton("OK");
				buttonPanel.add(okButton);
				okButton.setAction(new AbstractAction("OK") {
					@Override
					public void actionPerformed(ActionEvent e) {
						close();					
					}				
				});		

			} else {

				if (autoUpdatePossible) {
					updateButton = new JButton();		
					updateButton.setText("Update");
					buttonPanel.add(updateButton);
				} 

				if (updateInfo.getDownloadUrl() != null) {
					gotoDownloadPage = new JButton();			
					gotoDownloadPage.setAction(new OpenURL(updateInfo.getDownloadUrl(), "Visit downloads webpage"));
					gotoDownloadPage.addActionListener(closeListener);				
					if (openLinkPossible) {
						buttonPanel.add(gotoDownloadPage);
					}
				}

				cancelButton = new JButton(); 
				cancelButton.setText("Ignore");
				cancelButton.addActionListener(closeListener);
				buttonPanel.add(cancelButton);
			}
		}
		buttonPanel.setPreferredSize(new Dimension(400, 42));		
		return buttonPanel;
	}
	
	public JPanel getChangeLogPanel() {
		changelogPanel = new JPanel();
		if (updateInfo == null) {
		
		} else {
		changelogPanel.setBackground(Color.WHITE);
			if (updateInfo.hasChangeLog()) {
				changeLogLabel = new JLabel(updateInfo.getChangelog());
			} else {
				changeLogLabel = new JLabel("Could not retrieve Changelog from bolscript server...");
			}
			changelogPanel.add(changeLogLabel);
		}
		return changelogPanel;
	}

	public void close() {
		this.setVisible(false);
	}

	@Override
	public void displayInfo(UpdateInfo updateInfo) {
		this.updateInfo = updateInfo;
		initStuff();

	}

}

