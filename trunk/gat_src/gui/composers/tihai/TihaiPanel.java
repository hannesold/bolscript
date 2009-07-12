package gui.composers.tihai;

import gui.bols.VariationPanel;
import gui.composers.ComposerPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import managing.Mediator;
import algorithm.composers.tihai.TihaiComposer;

public class TihaiPanel extends ComposerPanel {

	private TihaiComposer tihaiComposer;
	
	public TihaiPanel(TihaiComposer composer, Mediator mediator) {
		super(composer,mediator);
		tihaiComposer = composer;
		
		BoxLayout boxLayout = new BoxLayout(contentPanel,BoxLayout.PAGE_AXIS);
		this.contentPanel.setLayout(boxLayout);
	}

}
