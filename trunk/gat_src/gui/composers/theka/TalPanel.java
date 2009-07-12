package gui.composers.theka;

import gui.bols.VariationPanel;
import gui.composers.ComposerPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import bols.Variation;

import managing.Command;
import managing.Mediator;
import algorithm.composers.theka.ThekaPlayer;

public class TalPanel extends ComposerPanel {

	private static final long serialVersionUID = -7781568555500585190L;
	private ThekaPlayer composerTP;
	
	public TalPanel(ThekaPlayer composer, Mediator mediator) {
		super(composer,mediator);
		composerTP = composer; 
		
		BoxLayout boxLayout = new BoxLayout(contentPanel,BoxLayout.PAGE_AXIS);
		this.contentPanel.setLayout(boxLayout);
		
		VariationPanel vp = new VariationPanel(Variation.fromTal(composerTP.getTal()),composerTP.getTal(), new Dimension(400,200), 0, "", 0);
		
		JPanel variationDisplays = new JPanel(new BorderLayout());
		variationDisplays.add(vp, BorderLayout.CENTER);
		
		this.contentPanel.add(variationDisplays);
	}
	
	public void passOn(ActionEvent e) {
		mediator.addCommand(new Command(Command.PassOnToComposer, composer.getNextComposer(), composer));
		if (!mediator.isProcessing()) {
			mediator.interrupt();
		}		
	}

}
