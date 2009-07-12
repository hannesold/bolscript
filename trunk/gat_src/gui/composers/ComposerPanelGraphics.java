package gui.composers;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.BoxLayout;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.FlowLayout;
import javax.swing.JToggleButton.ToggleButtonModel;
import javax.swing.JScrollPane;
import javax.swing.JButton;

public class ComposerPanelGraphics extends JPanel {

	private JPanel headerPanel = null;
	protected JLabel nameLabel = null;
	protected JPanel contentPanel = null;
	protected JToggleButton btnSolo = null;
	protected JPanel controls = null;
	protected JScrollPane contentScrollPane = null;
	protected JButton passOn = null;

	/**
	 * This is the default constructor
	 */
	public ComposerPanelGraphics() {
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
		this.setMinimumSize(new java.awt.Dimension(400,130));
		this.setPreferredSize(new java.awt.Dimension(400,400));
		this.setMaximumSize(new java.awt.Dimension(400,800));
		this.setSize(new java.awt.Dimension(400,130));
		this.setLocation(new java.awt.Point(0,0));
		this.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
		this.add(getHeaderPanel(), null);
		this.add(getContentScrollPane(), null);
		this.add(getControls(), null);
		
	}

	/**
	 * This method initializes headerPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getHeaderPanel() {
		if (headerPanel == null) {
			nameLabel = new JLabel();
			nameLabel.setText("composer name");
			nameLabel.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 12));
			headerPanel = new JPanel();
			headerPanel.setPreferredSize(new java.awt.Dimension(400,25));
			headerPanel.setMaximumSize(new java.awt.Dimension(400,25));
			headerPanel.setMinimumSize(new java.awt.Dimension(400,25));
			headerPanel.setMaximumSize(new java.awt.Dimension(400,25));
			headerPanel.setPreferredSize(new java.awt.Dimension(400,25));
			headerPanel.setMinimumSize(new java.awt.Dimension(400,25));
			headerPanel.add(nameLabel, null);
			headerPanel.add(getControls(), null);
		}
		return headerPanel;
	}

	/**
	 * This method initializes contentPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getContentPanel() {
		if (contentPanel == null) {
			contentPanel = new JPanel();
			contentPanel.setMinimumSize(new java.awt.Dimension(400,140));
			contentPanel.setMaximumSize(new java.awt.Dimension(400,1600));
			contentPanel.setSize(new java.awt.Dimension(400,70));
			contentPanel.setPreferredSize(new java.awt.Dimension(400,140));
		}
		return contentPanel;
	}

	/**
	 * This method initializes solo	
	 * 	
	 * @return javax.swing.JToggleButton	
	 */
	private JToggleButton getSolo() {
		if (btnSolo == null) {
			JToggleButton.ToggleButtonModel toggleButtonModel = new JToggleButton.ToggleButtonModel();
			toggleButtonModel.setPressed(false);
			btnSolo = new JToggleButton();
			btnSolo.setText("solo");
			btnSolo.setEnabled(true);
			btnSolo.setBounds(new java.awt.Rectangle(127,1,73,23));
			btnSolo.setModel(toggleButtonModel);
			btnSolo.getModel().setPressed(false);
		}
		return btnSolo;
	}

	/**
	 * This method initializes controls	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getControls() {
		if (controls == null) {
			controls = new JPanel();
			controls.setLayout(null);
			controls.setMaximumSize(new java.awt.Dimension(430,30));
			controls.setPreferredSize(new java.awt.Dimension(430,30));
			controls.setMinimumSize(new java.awt.Dimension(430,30));
			controls.add(getSolo(), null);
			controls.add(getPassOn(), null);
			controls.setPreferredSize(new java.awt.Dimension(430,25));
			controls.setMinimumSize(new java.awt.Dimension(430,25));
			controls.setMaximumSize(new java.awt.Dimension(430,25));
		}
		return controls;
	}

	/**
	 * This method initializes contentScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getContentScrollPane() {
		if (contentScrollPane == null) {
			contentScrollPane = new JScrollPane();
			contentScrollPane.setMinimumSize(new java.awt.Dimension(430,70));
			//contentScrollPane.setPreferredSize(new java.awt.Dimension(430,140));
			contentScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			contentScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			contentScrollPane.setViewportView(getContentPanel());
			//contentScrollPane.setMaximumSize(new java.awt.Dimension(430,800));
		}
		return contentScrollPane;
	}

	/**
	 * This method initializes passOn	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getPassOn() {
		if (passOn == null) {
			passOn = new JButton();
			passOn.setText("pass on");
			passOn.setBounds(new java.awt.Rectangle(189,1,100,23));
		}
		return passOn;
	}

}
