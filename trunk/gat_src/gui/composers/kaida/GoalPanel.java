package gui.composers.kaida;


import java.awt.event.ActionEvent;

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;

import algorithm.composers.kaida.Goal;
import algorithm.composers.kaida.GoalSet;
import basics.GUI;

public class GoalPanel extends GoalPanelGraphics {
	private static final long serialVersionUID = 2848332656623517328L;
	private Goal goal = null;
	private GoalSet goalSet;
	private boolean wantsToBePiloted;
	private boolean wasAutoBefore = true;
	private double importanceBefore;
	
	/**
	 * This is the default constructor
	 */
	public GoalPanel(Goal goal, GoalSet goalSet) {
		super();
		this.goal = goal;
		this.goalSet = goalSet;
		
		name.setText(goal.getLabel());
		name.setToolTipText(goal.getGenerator().getDescription());
		this.valueSpinner.setModel(goal.getValueRange());
		this.valueSpinner.setEditor(new JSpinner.NumberEditor(valueSpinner,"00.00"));
		this.valueSpinner.setValue(goal.value);
		System.out.println("setting value to ..." + goal.value);
		this.valueSpinner.addChangeListener(GUI.proxyChangeListener(this,"changeValue"));
		//this.importanceSpinner.setModel(new ValueRange(0.0,1.0,0.01));
		
		this.importanceSpinner.setValue(goal.getImportance());
		this.importanceSpinner.addChangeListener(GUI.proxyChangeListener(this,"changeImportance"));
		this.wantsToBePiloted = true;
		this.auto.setSelected(wantsToBePiloted);
		setAuto(null);
		this.auto.addActionListener(GUI.proxyActionListener(this,"setAuto"));
		
		this.onOff.addActionListener(GUI.proxyActionListener(this,"setOnOff"));
		//importanceSpinner.setEditor(new JSpinner.NumberEditor(importanceSpinner));
		
	}

	public void updateGoalVisuals(Goal goal) {
		if (((Number)goal.value).doubleValue() != ((Number)valueSpinner.getValue()).doubleValue()) {
			this.valueSpinner.setValue(goal.value);
		}
		if (((Number)goal.getImportance()).doubleValue() != ((Number)importanceSpinner.getValue()).doubleValue()) {
			this.importanceSpinner.setValue(goal.getImportance());		
		}
	}
	
	public void setAuto(ActionEvent e) {
		setAuto(auto.isSelected());
	}
	
	public void setAuto(boolean selected) {
		System.out.println("switching auto");
		if (selected != auto.isSelected()) {
			auto.setSelected(selected);
		}
		wantsToBePiloted = selected;
		goal.setWantsToBePiloted(wantsToBePiloted);
		this.valueSpinner.setEnabled(!wantsToBePiloted);
		this.importanceSpinner.setEnabled(!wantsToBePiloted);
	}
	
	public void setOnOff(ActionEvent e) {
		setOnOff(onOff.isSelected());
	}
	
	public void setOnOff(boolean yesno) {
		
		System.out.println("switching on/off");
		if (!yesno) {
			//turn it off
			//save state:
			wasAutoBefore = auto.isSelected();
			importanceBefore = ((Number)importanceSpinner.getValue()).doubleValue();
			goalSet.setGoalImportance(goal.getGenerator().getClass(),0f);
			
			setAuto(false);
			goalSet.setGoalImportance(goal.getGenerator().getClass(),0f);
			valueSpinner.setEnabled(false);
			importanceSpinner.setEnabled(false);
			name.setEnabled(false);
		} else {
			//turn it on
			//revert state:
			goalSet.setGoalImportance(goal.getGenerator().getClass(),importanceBefore);
			setAuto(wasAutoBefore);
			name.setEnabled(true);
		}
		
		auto.setEnabled(yesno);
	}
	
	
	public void changeValue(ChangeEvent e) {
		goalSet.setGoalValue(goal.getGenerator().getClass(), ((Number)valueSpinner.getValue()).doubleValue());
	}
	
	public void changeImportance(ChangeEvent e) {
		goalSet.setGoalImportance(goal.getGenerator().getClass(), ((Number)importanceSpinner.getValue()).doubleValue());
	}
	
	
	
	

}  
