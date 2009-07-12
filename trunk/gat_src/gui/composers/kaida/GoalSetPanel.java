package gui.composers.kaida;

import gui.composers.kaida.pilots.PilotPanel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;

import basics.GUI;

import algorithm.composers.kaida.Goal;
import algorithm.composers.kaida.GoalSet;

public class GoalSetPanel extends GoalSetPanelGraphics {
	private Dimension dimensions;
	private HashMap<Goal, GoalPanel> goalPanelMap;
	private ArrayList<GoalPanel> goalPanels;
	private PilotPanel pilotPanel;
	
	public GoalSetPanel(GoalSet goalSet, PilotPanel pilotPanel) {
		super();
		dimensions = new Dimension(350,70);
		this.setPreferredSize(dimensions);
		
		this.pilotPanel = pilotPanel;
		goalPanelMap = new HashMap<Goal, GoalPanel>();
		goalPanels = new ArrayList<GoalPanel>();
		
		if (goalSet!=null) {
			setGoals(goalSet);
		}
		
		btnSelectAll.addActionListener(GUI.proxyActionListener(this,"selectAutoForAll"));
		btnDeselectAll.addActionListener(GUI.proxyActionListener(this,"deselectAutoForAll"));
	}

	/**
	 * Adds a goalpanel for each goal in the goalset
	 */
	public void setGoals(GoalSet goalSet) {	
	    ArrayList<Goal> goals = goalSet.getGoals();
	
	    for (Goal goal : goals) {
	    	
	    	GoalPanel gp = new GoalPanel(goal, goalSet);
	    	this.add(gp);
	    	goalPanels.add(gp);
	    	goalPanelMap.put(goal,gp);
	    	dimensions.height += gp.getPreferredSize().height;
	    	
	    }
	    
	    this.setPreferredSize(dimensions);	
	}
	
	public void updateVisuals(GoalSet goalSet) {
		ArrayList<Goal> goals = goalSet.getGoals();
		for (Goal goal : goals) {
			GoalPanel gp = goalPanelMap.get(goal);
			if (gp!=null) {
				gp.updateGoalVisuals(goal);
			}
		}
	}
	
	public void selectAutoForAll(ActionEvent e) {
		for (GoalPanel gp : goalPanels) {
			gp.setAuto(true);
		}
	}
	public void deselectAutoForAll(ActionEvent e) {
		for (GoalPanel gp : goalPanels) {
			gp.setAuto(false);
		}		
	}
}
