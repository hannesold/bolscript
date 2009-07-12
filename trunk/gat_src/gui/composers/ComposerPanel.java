package gui.composers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.SoftBevelBorder;

import managing.Command;
import managing.Mediator;
import algorithm.composers.ComposerStandard;
import basics.GUI;
import basics.GenericListener;

public class ComposerPanel extends ComposerPanelGraphics {
	
	protected boolean solo;
	
	protected Mediator mediator;
	protected ComposerStandard composer;
	
	public ComposerPanel(ComposerStandard composer, Mediator mediator) {
		super();
				
		this.composer = composer;
		
		if (composer!=null) {
			composer.setComposerPanel(this);
			this.nameLabel.setText(composer.getLabel());
		}
		
		this.mediator = mediator;
		
		
		solo = false;
		updateBtnSolo();
		btnSolo.addActionListener(proxyActionListener(this,"switchSolo"));	
		passOn.addActionListener(GUI.proxyActionListener(this,"passOn"));
				
	}

	private void updateBtnSolo() {
		// TODO Auto-generated method stub
		btnSolo.getModel().setSelected(solo);
		passOn.setEnabled(solo);
	}

	public void switchSolo(ActionEvent e) {
		if (!solo) {
			mediator.addCommand(new Command(Command.SetComposer, composer, this));
			if (!mediator.isProcessing()) {
				mediator.interrupt();
			}
			solo = true;
			
		} else {
			mediator.addCommand(new Command(Command.MuteComposer, composer, this));
			if (!mediator.isProcessing()) {
				mediator.interrupt();
			}
			solo = false;
			
		}
	}
	
	public void passOn(ActionEvent e) {
		//overwrite!!
		//tell composer to pass on...
	}
	
	static ActionListener proxyActionListener(Object implementor, String methodname) {
		//vorher: static ActionListener proxyActionListener(JComponent target, Object implementor, String methodname) { 
		return (ActionListener) (GenericListener.create(ActionListener.class,
				"actionPerformed", implementor, methodname));
	}

	public void informToBeSolo() {
		// TODO Auto-generated method stub
		solo = true;
		updateBtnSolo();
		
	}
	
	public void informToBeMuted() {
		// TODO Auto-generated method stub
		solo = false;
		updateBtnSolo();
	}

	public ComposerStandard getComposer() {
		// TODO Auto-generated method stub
		return composer;
	}

	public void updateVisuals(Object[] args) {
		// overwrite		
	}
}
