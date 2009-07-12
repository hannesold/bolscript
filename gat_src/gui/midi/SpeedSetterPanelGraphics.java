package gui.midi;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JSpinner;

public class SpeedSetterPanelGraphics extends JPanel {

	protected JLabel label = null;
	protected JSpinner speedSelector = null;
	/**
	 * This is the default constructor
	 */
	public SpeedSetterPanelGraphics() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	protected void initialize() {
		label = new JLabel();
		label.setText("Beats per second");
		this.setSize(194, 33);
		this.add(label, null);
		this.add(getSpeedFactorSelector(), null);
	}

	/**
	 * This method initializes speedFactorSelector	
	 * 	
	 * @return javax.swing.JSpinner	
	 */
	protected JSpinner getSpeedFactorSelector() {
		if (speedSelector == null) {
			speedSelector = new JSpinner();
			speedSelector.setPreferredSize(new java.awt.Dimension(50,18));
			speedSelector.setToolTipText("Speed");
		}
		return speedSelector;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
