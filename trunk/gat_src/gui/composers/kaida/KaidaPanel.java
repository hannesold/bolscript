package gui.composers.kaida;

import gui.bols.VariationItem;
import gui.bols.VariationPanel;
import gui.composers.ComposerPanel;
import gui.composers.kaida.pilots.PilotPanel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import managing.Command;
import managing.Mediator;
import algorithm.composers.kaida.KaidaComposer;
import algorithm.composers.kaida.Goal;
import basics.GUI;

public class KaidaPanel extends ComposerPanel {

	private static final long serialVersionUID = -7781568555500585190L;
	private KaidaComposer composerAl;
	private VariationSelectorPanel variationSelectorPanel;
	private VariationSelector variationSelector;
	private VariationPanel variationPanel;
	private JButton restart;
	private GoalSetPanel goalsPanel;
	private PilotPanel pilotPanel;
	private EvolutionPanel evolutionPanel;
	
	public KaidaPanel(KaidaComposer composer, Mediator mediator) {
		super(composer,mediator);
		this.composerAl = composer; 
		this.mediator = mediator;
		
		BoxLayout boxLayout = new BoxLayout(contentPanel,BoxLayout.Y_AXIS);
		this.contentPanel.setLayout(boxLayout);
		
		variationSelectorPanel = new VariationSelectorPanel();
		pilotPanel = new PilotPanel(composerAl.getPilot(), composerAl);
		
		this.variationSelector = variationSelectorPanel.getVariationSelector();
		this.restart = variationSelectorPanel.getRestart();
		this.variationPanel = variationSelectorPanel.getVariationPanel();
		
		
		variationPanel.setContents(composer.getInitialVariation().getAsRepresentableSequence(), composer.getTal());
		variationSelector.addActionListener(GUI.proxyActionListener(this, "selectVariation"));
		
		restart.addActionListener(GUI.proxyActionListener(this, "restart"));

		goalsPanel = new GoalSetPanel(composerAl.getGoalSet(),pilotPanel);		
		
		evolutionPanel = new EvolutionPanel(composerAl);
		
		this.contentPanel.add(variationSelectorPanel);
		this.contentPanel.add(pilotPanel);
		this.contentPanel.add(goalsPanel);
		this.contentPanel.add(evolutionPanel);
		
		this.contentPanel.setPreferredSize(GUI.addPreferredHeights(
				variationSelectorPanel, pilotPanel, goalsPanel, evolutionPanel));
		
		restart.setVisible(false);
		
	}
	
	public void updateVisuals(Object[] args) {
//		if ((boolean)args[0] == true) {
//			
//		} else {
			pilotPanel.updateVisuals(composerAl.getPilot());
			goalsPanel.updateVisuals(composerAl.getGoalSet());
//		}
	}
	
	public void updateProgress() {
		this.evolutionPanel.updateVisuals();
	}
	
	public void selectVariation(ActionEvent e) {
		if (e.getSource() == variationSelector) {
			//composerAl.reStart(variationSelector.getSelectedVariation());
			VariationItem vi = (VariationItem) variationSelector.getSelectedItem();
			
			Dimension oldSize = variationPanel.getPreferredSize();
			variationPanel.setContents(vi.variation.getAsRepresentableSequence(), vi.tal);
			Dimension newSize = variationPanel.getPreferredSize();
			if (!newSize.equals(oldSize)) {
				this.contentPanel.setPreferredSize(GUI.addPreferredHeights(
						variationSelectorPanel, pilotPanel, goalsPanel, evolutionPanel));
				
			}
			
			restart.setVisible(true);
		} 	
	}
	
	public void restart(ActionEvent e) {
		VariationItem vi = (VariationItem) variationSelector.getSelectedItem();
		mediator.addCommand(new Command(Command.RestartComposer, composer, vi.variation, vi.tal));
		if (!mediator.isProcessing()) {
			mediator.interrupt();
		}
		restart.setVisible(false);
	}
	
	public void passOn(ActionEvent e) {
		mediator.addCommand(new Command(Command.PassOnToComposer, composer.getNextComposer(), composer, composerAl.getLastPlayed().getVariation(), composerAl.getTal()));
		if (!mediator.isProcessing()) {
			mediator.interrupt();
		}		
	}

}
