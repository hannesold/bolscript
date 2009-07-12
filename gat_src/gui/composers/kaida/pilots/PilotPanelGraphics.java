package gui.composers.kaida.pilots;

import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JTextPane;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import javax.swing.JSlider;
import javax.swing.JLabel;
import javax.swing.BoxLayout;

public class PilotPanelGraphics extends JPanel {

	protected JCheckBox pilotActive = null;
	protected JButton setPilot = null;
	protected JTextPane description = null;
	protected PilotSelector pilotSelector = null;
	protected JSlider pilotPositionSlider = null;
	protected JLabel progressReport = null;
	private JPanel sliderPanel = null;

	/**
	 * This is the default constructor
	 */
	public PilotPanelGraphics() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
        progressReport = new JLabel();
        progressReport.setText("0/4");
        progressReport.setBounds(new java.awt.Rectangle(298,114,54,22));
        progressReport.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        progressReport.setAlignmentX(0.0F);
        this.setLayout(null);
        this.setPreferredSize(new java.awt.Dimension(365,167));
        this.setMinimumSize(new java.awt.Dimension(365,170));
        this.setMaximumSize(new java.awt.Dimension(365,170));
        this.setSize(new java.awt.Dimension(365,167));
        this.setLocation(new java.awt.Point(0,0));
        this.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pilot", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
        this.add(getSliderPanel(), null);
        this.add(getSetPilot(), null);
        this.add(getPilotActive(), null);
        this.add(getPilotSelector(), null);
        this.add(getDescription(), null);
        this.add(getPilotPositionSlider(), null);
        this.add(progressReport, null);
	}

	/**
	 * This method initializes pilotActive	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	protected JCheckBox getPilotActive() {
		if (pilotActive == null) {
			pilotActive = new JCheckBox();
			pilotActive.setAlignmentX(0.5F);
			pilotActive.setSize(new java.awt.Dimension(21,21));
			pilotActive.setLocation(new java.awt.Point(7,27));
			pilotActive.setToolTipText("pilot on/off");
		}
		return pilotActive;
	}

	/**
	 * This method initializes setPilot	
	 * 	
	 * @return javax.swing.JButton	
	 */
	protected JButton getSetPilot() {
		if (setPilot == null) {
			setPilot = new JButton();
			setPilot.setText("set");
			setPilot.setToolTipText("Confirm selected pilot");
			setPilot.setLocation(new java.awt.Point(300,25));
			setPilot.setSize(new java.awt.Dimension(63,23));
			setPilot.setMaximumSize(new java.awt.Dimension(63,23));
			setPilot.setMinimumSize(new java.awt.Dimension(63,23));
			setPilot.setPreferredSize(new java.awt.Dimension(63,23));
		}
		return setPilot;
	}

	/**
	 * This method initializes description	
	 * 	
	 * @return javax.swing.JTextPane	
	 */
	protected JTextPane getDescription() {
		if (description == null) {
			description = new JTextPane();
			description.setText("Pilot description");
			description.setMaximumSize(new java.awt.Dimension(200,40));
			description.setBounds(new java.awt.Rectangle(32,56,257,51));
			description.setEditable(false);
			description.setPreferredSize(new java.awt.Dimension(200,40));
		}
		return description;
	}

	/**
	 * This method initializes pilotSelector	
	 * 	
	 * @return graphs.algorithm.pilots.PilotSelector	
	 */
	protected PilotSelector getPilotSelector() {
		if (pilotSelector == null) {
			pilotSelector = new PilotSelector();
			pilotSelector.setMaximumSize(new java.awt.Dimension(200,22));
			pilotSelector.setPreferredSize(new java.awt.Dimension(200,28));
			pilotSelector.setLocation(new java.awt.Point(31,26));
			pilotSelector.setSize(new java.awt.Dimension(259,22));
			pilotSelector.setMinimumSize(new java.awt.Dimension(200,22));
		}
		return pilotSelector;
	}

	/**
	 * This method initializes pilotPositionSlider	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	protected JSlider getPilotPositionSlider() {
		if (pilotPositionSlider == null) {
			pilotPositionSlider = new JSlider();
			pilotPositionSlider.setMajorTickSpacing(1);
			pilotPositionSlider.setMaximum(10);
			pilotPositionSlider.setValue(0);
			pilotPositionSlider.setSnapToTicks(true);
			pilotPositionSlider.setToolTipText("Position in pilot plan");
			pilotPositionSlider.setPaintLabels(true);
			pilotPositionSlider.setPaintTicks(true);
			pilotPositionSlider.setMaximumSize(new java.awt.Dimension(200,47));
			pilotPositionSlider.setMinimumSize(new java.awt.Dimension(200,47));
			pilotPositionSlider.setAlignmentX(0.0F);
			pilotPositionSlider.setBounds(new java.awt.Rectangle(32,113,261,47));
			pilotPositionSlider.setMinorTickSpacing(1);
		}
		return pilotPositionSlider;
	}

	/**
	 * This method initializes sliderPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getSliderPanel() {
		if (sliderPanel == null) {
			sliderPanel = new JPanel();
			sliderPanel.setLayout(new BoxLayout(getSliderPanel(), BoxLayout.X_AXIS));
			sliderPanel.setBounds(new java.awt.Rectangle(124,22,0,0));
		}
		return sliderPanel;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
