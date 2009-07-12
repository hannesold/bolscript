package gui.composers.kaida;

import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;

import basics.GUI;

import algorithm.composers.kaida.KaidaComposer;

public class EvolutionPanel extends EvolutionPanelGraphics {
	
	private KaidaComposer kaidaComposer;

	public EvolutionPanel(KaidaComposer composer) {
		super();
		kaidaComposer = composer;
		generationsSpinner.setModel(new SpinnerNumberModel(kaidaComposer.getMaxNrOfGenerationsPerCycle(),1,200,1));
		generationsSpinner.addChangeListener(GUI.proxyChangeListener(this,"onGenerationsChange"));
	}
	
	public void onGenerationsChange(ChangeEvent e) {
		kaidaComposer.setMaxNrOfGenerationsPerCycle(
				((Number)generationsSpinner.getValue()).intValue()
				);
	}
	
	public void updateVisuals() {
		kaidaComposer.setMaxNrOfGenerationsPerCycle(
				((Number)generationsSpinner.getValue()).intValue()
				);
		generationsProgress.setValue(kaidaComposer.getEvolutionProgress());
		//System.out.println("trying to set progress to " + kaidaComposer.getEvolutionProgress());
	}
}
