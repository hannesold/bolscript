package gui.composers.kaida;

import gui.bols.VariationPanel;

import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;

public class VariationSelectorPanel extends JPanel {

	protected VariationSelector variationSelector = null;
	protected JButton restart = null;
	protected JPanel selection = null;
	protected VariationPanel variationPanel = null;
	/**
	 * This is the default constructor
	 */
	public VariationSelectorPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setSize(365, 172);
		this.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Theme", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
		this.setMinimumSize(new java.awt.Dimension(365,160));
		this.setMaximumSize(new java.awt.Dimension(365,1500));
		this.setPreferredSize(new java.awt.Dimension(365,172));
		this.add(getSelection(), null);
		this.add(getVariationPanel(),null);
	}

	/**
	 * This method initializes variationSelector	
	 * 	
	 * @return graphs.algorithm.VariationSelector	
	 */
	public VariationSelector getVariationSelector() {
		if (variationSelector == null) {
			variationSelector = new VariationSelector();
			variationSelector.setLocation(new java.awt.Point(6,5));
			variationSelector.setSize(new java.awt.Dimension(279,22));
		}
		return variationSelector;
	}

	/**
	 * This method initializes restart	
	 * 	
	 * @return javax.swing.JButton	
	 */
	public JButton getRestart() {
		if (restart == null) {
			restart = new JButton();
			restart.setText("set");
			restart.setPreferredSize(new java.awt.Dimension(63,23));
			restart.setMaximumSize(new java.awt.Dimension(63,23));
			restart.setMinimumSize(new java.awt.Dimension(63,23));
			restart.setBounds(new java.awt.Rectangle(290,5,63,23));
			restart.setToolTipText("Confirm selected theme");
		}
		return restart;
	}

	/**
	 * This method initializes selection	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getSelection() {
		if (selection == null) {
			selection = new JPanel();
			selection.setPreferredSize(new java.awt.Dimension(362,35));
			selection.setMinimumSize(new java.awt.Dimension(362,35));
			selection.setMaximumSize(new java.awt.Dimension(362,35));
			selection.setLayout(null);
			selection.add(getVariationSelector(), null);
			selection.add(getRestart(), null);
		}
		return selection;
	}

	/**
	 * This method initializes variationPanel	
	 * 	
	 * @return graphs.bols.VariationPanel	
	 */
	public VariationPanel getVariationPanel() {
		if (variationPanel == null) {
			variationPanel = new VariationPanel();
			//variationPanel.setMaximumSize(new java.awt.Dimension(352,400));
		}
		return variationPanel;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
