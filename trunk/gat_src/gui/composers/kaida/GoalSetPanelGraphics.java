package gui.composers.kaida;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JButton;

public class GoalSetPanelGraphics extends JPanel {

	protected JPanel goalSetControls = null;
	protected JButton btnSelectAll = null;
	protected JButton btnDeselectAll = null;

	/**
	 * This is the default constructor
	 */
	public GoalSetPanelGraphics() {
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
		this.setSize(350, 70);
		this.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Goals", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
		this.setPreferredSize(new java.awt.Dimension(350,84));
		this.add(getGoalSetControls(), null);
	}

	/**
	 * This method initializes goalSetControls	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	protected JPanel getGoalSetControls() {
		if (goalSetControls == null) {
			goalSetControls = new JPanel();
			goalSetControls.setLayout(null);
			goalSetControls.setMinimumSize(new java.awt.Dimension(350,36));
			goalSetControls.setMaximumSize(new java.awt.Dimension(350,36));
			goalSetControls.setPreferredSize(new java.awt.Dimension(350,36));
			goalSetControls.add(getSelectAll(), null);
			goalSetControls.add(getDeselectAll(), null);
		}
		return goalSetControls;
	}

	/**
	 * This method initializes selectAll	
	 * 	
	 * @return javax.swing.JButton	
	 */
	protected JButton getSelectAll() {
		if (btnSelectAll == null) {
			btnSelectAll = new JButton();
			btnSelectAll.setText("all auto");
			btnSelectAll.setLocation(new java.awt.Point(76,3));
			btnSelectAll.setToolTipText("All goals to autopilot control");
			btnSelectAll.setSize(new java.awt.Dimension(95,23));
		}
		return btnSelectAll;
	}

	/**
	 * This method initializes deselectAll	
	 * 	
	 * @return javax.swing.JButton	
	 */
	protected JButton getDeselectAll() {
		if (btnDeselectAll == null) {
			btnDeselectAll = new JButton();
			btnDeselectAll.setText("none auto");
			btnDeselectAll.setLocation(new java.awt.Point(162,3));
			btnDeselectAll.setToolTipText("All goals to manual control");
			btnDeselectAll.setSize(new java.awt.Dimension(110,23));
		}
		return btnDeselectAll;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
