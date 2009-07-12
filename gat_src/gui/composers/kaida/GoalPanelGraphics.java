package gui.composers.kaida;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import algorithm.composers.kaida.ImportanceRange;
import algorithm.composers.kaida.ValueRange;

public class GoalPanelGraphics extends JPanel {

	protected JPanel controls = null;
	protected JCheckBox auto = null;
	protected JSpinner valueSpinner = null;
	protected JSpinner importanceSpinner = null;
	private JLabel valueLabel = null;
	protected JLabel Importance = null;
	protected JPanel contents = null;
	private JLabel autolbl = null;
	protected JLabel name = null;
	private ValueRange valueRange = null;  //  @jve:decl-index=0:visual-constraint=""
	private ImportanceRange importanceRange = null;  //  @jve:decl-index=0:visual-constraint=""
	private JPanel onOffPanel = null;  //  @jve:decl-index=0:visual-constraint="395,35"
	protected JCheckBox onOff = null;  //  @jve:decl-index=0:visual-constraint="504,71"
	
	/**
	 * This is the default constructor
	 */
	public GoalPanelGraphics() {
		super();
		initialize();
	}
	

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setSize(365, 50);
		this.setAlignmentX(0.5F);
		this.setMinimumSize(new java.awt.Dimension(365,50));
		this.setMaximumSize(new java.awt.Dimension(365,50));
		this.setBorder(javax.swing.BorderFactory.createEmptyBorder(1,0,1,0));
		this.setPreferredSize(new java.awt.Dimension(365,50));
		this.add(getOnOffPanel(),null);
		this.add(getContents(), null);
		this.add(getControls(), null);
	}

	/**
	 * This method initializes controls	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getControls() {
		if (controls == null) {
			autolbl = new JLabel();
			autolbl.setText("auto");
			autolbl.setLocation(new java.awt.Point(137,5));
			autolbl.setSize(new java.awt.Dimension(30,14));
			Importance = new JLabel();
			Importance.setBounds(new java.awt.Rectangle(17,25,30,20));
			Importance.setText("imp");
			valueLabel = new JLabel();
			valueLabel.setBounds(new java.awt.Rectangle(17,3,30,20));
			valueLabel.setText("val");
			controls = new JPanel();
			controls.setPreferredSize(new java.awt.Dimension(180,50));
			controls.setBackground(new java.awt.Color(237,250,217));
			controls.setLayout(null);
			controls.setMaximumSize(new java.awt.Dimension(180,50));
			controls.setMinimumSize(new java.awt.Dimension(150,50));
			controls.add(getImportanceSpinner(), null);
			controls.add(getValueSpinner(), null);
			controls.add(valueLabel, null);
			controls.add(Importance, null);
			controls.add(getAuto(), null);
			controls.add(autolbl, null);
		}
		return controls;
	}

	/**
	 * This method initializes auto	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getAuto() {
		if (auto == null) {
			auto = new JCheckBox();
			auto.setBounds(new java.awt.Rectangle(137,20,21,21));
			auto.setOpaque(false);
			auto.setToolTipText("autopilot control");
			
		}
		return auto;
	}

	/**
	 * This method initializes valueSpinner	
	 * 	
	 * @return javax.swing.JSpinner	
	 */
	private JSpinner getValueSpinner() {
		if (valueSpinner == null) {
			valueSpinner = new JSpinner();
			valueSpinner.setBounds(new java.awt.Rectangle(52,3,70,20));
			valueSpinner.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
			valueSpinner.setAlignmentX(1.0F);
			valueSpinner.setModel(getValueRange());
			valueSpinner.setToolTipText("goal value");
			//valueSpinner.setEditor(new JSpinner.NumberEditor(valueSpinner,"00.00"));
			//JTextField jtf = 
		}
		return valueSpinner;
	}

	/**
	 * This method initializes jSpinner	
	 * 	
	 * @return javax.swing.JSpinner	
	 */
	private JSpinner getImportanceSpinner() {
		if (importanceSpinner == null) {
			importanceSpinner = new JSpinner();
			importanceSpinner.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
			importanceSpinner.setBounds(new java.awt.Rectangle(52,25,70,20));
			//importanceSpinner.setModel(getValueRange());
			importanceSpinner.setModel(getImportanceRange());
			importanceSpinner.setToolTipText("importance");
			//importanceSpinner.setEditor(new JSpinner.NumberEditor(importanceSpinner,"00.00"));			
		}
		return importanceSpinner;
	}

	/**
	 * This method initializes contents	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getContents() {
		if (contents == null) {
			name = new JLabel();
			name.setText("Goal name");
			name.setMinimumSize(new java.awt.Dimension(50,24));
			name.setMaximumSize(new java.awt.Dimension(50,24));
			name.setPreferredSize(new java.awt.Dimension(50,24));
			name.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,15,5,15));
			contents = new JPanel();
			contents.setLayout(new BorderLayout());
			contents.setPreferredSize(new java.awt.Dimension(150,50));
			contents.setBackground(java.awt.Color.white);
			contents.setMinimumSize(new java.awt.Dimension(100,50));
			contents.add(name, java.awt.BorderLayout.CENTER);
		}
		return contents;
	}


	/**
	 * This method initializes valueRange	
	 * 	
	 * @return algorithm.ValueRange	
	 */
	private ValueRange getValueRange() {
		if (valueRange == null) {
			valueRange = new ValueRange();
		}
		return valueRange;
	}


	/**
	 * This method initializes importanceRange	
	 * 	
	 * @return algorithm.ImportanceRange	
	 */
	private ImportanceRange getImportanceRange() {
		if (importanceRange == null) {
			importanceRange = new ImportanceRange();
		}
		return importanceRange;
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getOnOffPanel() {
		if (onOffPanel == null) {
			onOffPanel = new JPanel();
			onOffPanel.setLayout(null);
			onOffPanel.setSize(new java.awt.Dimension(25,50));
			onOffPanel.setMinimumSize(new java.awt.Dimension(25,50));
			onOffPanel.setPreferredSize(new java.awt.Dimension(25,50));
			onOffPanel.setBackground(new java.awt.Color(237,250,217));
			onOffPanel.setMaximumSize(new java.awt.Dimension(25,50));
			onOffPanel.add(getOnOff(), null);
		}
		return onOffPanel;
	}


	/**
	 * This method initializes onOff	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getOnOff() {
		if (onOff == null) {
			onOff = new JCheckBox();
			onOff.setAlignmentY(0.5F);
			onOff.setOpaque(false);
			onOff.setBounds(new java.awt.Rectangle(2,13,21,21));
			onOff.setSelected(true);
			onOff.setAlignmentX(0.5F);
		}
		return onOff;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
