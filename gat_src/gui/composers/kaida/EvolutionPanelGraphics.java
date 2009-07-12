package gui.composers.kaida;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JSpinner;
import javax.swing.JProgressBar;

public class EvolutionPanelGraphics extends JPanel {

	protected JLabel generationsLabel = null;
	protected JSpinner generationsSpinner = null;
	protected JProgressBar generationsProgress = null;

	/**
	 * This is the default constructor
	 */
	public EvolutionPanelGraphics() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	protected void initialize() {
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.gridwidth = 2;
		gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints3.insets = new java.awt.Insets(10,0,0,0);
		gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints3.gridy = 1;
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.insets = new java.awt.Insets(0,15,0,0);
		gridBagConstraints1.gridy = 0;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		generationsLabel = new JLabel();
		generationsLabel.setText("Generations per Cycle");
		this.setLayout(new GridBagLayout());
		this.setSize(300, 90);
		this.setMinimumSize(new java.awt.Dimension(300,90));
		this.setPreferredSize(new java.awt.Dimension(300,90));
		this.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Evolution", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
		this.add(generationsLabel, gridBagConstraints);
		this.add(getGenerationsSpinner(), gridBagConstraints1);
		this.add(getGenerationsProgress(), gridBagConstraints3);
	}

	/**
	 * This method initializes generationsSpinner	
	 * 	
	 * @return javax.swing.JSpinner	
	 */
	protected JSpinner getGenerationsSpinner() {
		if (generationsSpinner == null) {
			generationsSpinner = new JSpinner();
			generationsSpinner.setPreferredSize(new java.awt.Dimension(60,18));
			generationsSpinner.setMinimumSize(new java.awt.Dimension(50,18));
			generationsSpinner.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
		}
		return generationsSpinner;
	}

	/**
	 * This method initializes generationsProgress	
	 * 	
	 * @return javax.swing.JProgressBar	
	 */
	protected JProgressBar getGenerationsProgress() {
		if (generationsProgress == null) {
			generationsProgress = new JProgressBar();
		}
		return generationsProgress;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
