package gui.bolscript;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import bolscript.UpdateInfo;


public class UpdateFrame extends JFrame {

	JPanel changelogPanel;
	JButton updateButton;
	JButton cancelButton;
	JButton gotoDownloadPageButton;
	JLabel changeLogLabel;
	
	private String changeLogContent;
	private String downloadLink;
	
	UpdateInfo updateInfo;
	public UpdateFrame(UpdateInfo updateInfo) {
		super("Update Frame");
		this.updateInfo = updateInfo;
		JScrollPane scrollPane = new JScrollPane(getChangeLogPanel());
				
		JPanel all = new JPanel();
		all.setLayout(new BorderLayout());
		all.add(scrollPane, BorderLayout.CENTER);
		all.add(getButtonPanel(), BorderLayout.SOUTH);
		this.setContentPane(all);
		this.pack();
	}
	
	public JPanel getButtonPanel() {
		JPanel buttonPanel = new JPanel();
		updateButton = new JButton();
		updateButton.setText("Update");
		cancelButton = new JButton(); 
		cancelButton.setText("Ignore");
		buttonPanel.add(updateButton);
		buttonPanel.add(cancelButton);
		buttonPanel.setPreferredSize(new Dimension(400, 42));		
		return buttonPanel;
	}
	public JPanel getChangeLogPanel() {
		changelogPanel = new JPanel();
		changelogPanel.setBackground(Color.WHITE);
		changeLogLabel = new JLabel(updateInfo.getChangelog());
		changelogPanel.add(changeLogLabel);
		return changelogPanel;
	}
	
}

