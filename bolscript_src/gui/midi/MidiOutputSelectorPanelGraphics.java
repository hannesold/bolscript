package gui.midi;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JButton;

public class MidiOutputSelectorPanelGraphics extends JPanel {

	protected JLabel comment = null;
	protected JComboBox midiOutputSelector = null;
	protected JButton btnSet = null;

	/**
	 * This is the default constructor
	 */
	public MidiOutputSelectorPanelGraphics() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	protected void initialize() {
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.insets = new java.awt.Insets(0,15,0,0);
		gridBagConstraints2.gridy = 1;
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.ipadx = 0;
		gridBagConstraints1.gridx = 0;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(0,0,15,0);
		gridBagConstraints.gridx = 0;
		comment = new JLabel();
		comment.setText("Please select a midi output");
		this.setLayout(new GridBagLayout());
		this.setSize(435, 71);
		this.setBorder(javax.swing.BorderFactory.createEmptyBorder(15,15,15,15));
		this.add(comment, gridBagConstraints);
		this.add(getMidiOutputSelector(), gridBagConstraints1);
		this.add(getBtnSet(), gridBagConstraints2);
	}

	/**
	 * This method initializes midiOutputSelector	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	protected JComboBox getMidiOutputSelector() {
		if (midiOutputSelector == null) {
			midiOutputSelector = new JComboBox();
		}
		return midiOutputSelector;
	}

	/**
	 * This method initializes btnSet	
	 * 	
	 * @return javax.swing.JButton	
	 */
	protected JButton getBtnSet() {
		if (btnSet == null) {
			btnSet = new JButton();
			btnSet.setText("Apply");
		}
		return btnSet;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
