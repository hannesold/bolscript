package algorithm.pilots;

import static algorithm.pilots.WayPoint.CurvePointTypes.*;

import java.util.ArrayList;
import java.util.HashMap;


import algorithm.composers.kaida.Goal;
import algorithm.composers.kaida.GoalSet;
import algorithm.pilots.WayPoint.CurvePointTypes;

public class Pilot {
	private ArrayList<GoalCurve> goalCurves;
	private HashMap<Class,GoalCurve> goalCurveMap;

	private String label;
	private String description;
	private boolean active;
	
	private int position;
	
	private int duration;
	
	//private 

	public Pilot() {
		this("Pilot", "This pilot has no description.");
		active = true;
	}
	
	public Pilot(String label, String description) {
		goalCurves = new ArrayList<GoalCurve>();
		goalCurveMap = new HashMap<Class,GoalCurve>();
		this.label = label;
		this.description = description;
		active = true;
		position = 0;
	}
	
	public Pilot(GoalSet gs) {
		goalCurves = new ArrayList<GoalCurve>();
		goalCurveMap = new HashMap<Class,GoalCurve>();
		
		for (Goal goal : gs.getGoals()) {
			GoalCurve gc = new GoalCurve(goal.getGenerator().getClass());
			gc.addPoint(0, goal.value, goal.getImportance(), LINEAR);
			addGoalCurve(gc);
		}
		active = true;
		position = 0;
	}
	
	public void addGoalCurve(Class raterClass) {
		GoalCurve gc = new GoalCurve(raterClass);
		goalCurves.add(gc);
		goalCurveMap.put(raterClass,gc);
		
	}
	
	public void addGoalCurve(GoalCurve gc) {
		goalCurves.add(gc);
		goalCurveMap.put(gc.getRaterClass(),gc);
		duration = Math.max(duration, gc.duration);
	}
	
	public GoalCurve getGoalCurve(Class raterClass) {
		return goalCurveMap.get(raterClass);
	}
	
	public void addPoint(Class raterClass, int time, double val, double imp, CurvePointTypes type) {
		GoalCurve gc = goalCurveMap.get(raterClass);
		if (gc == null) {
			gc = new GoalCurve(raterClass);
			goalCurves.add(gc);
			goalCurveMap.put(raterClass,gc);
		} 
		gc.addPoint(time, val, imp, type);
		duration = Math.max(duration, time);
		
	}
	public void updateGoalSet(GoalSet goalSet){
		updateGoalSet(goalSet, position);
	}
	public void updateGoalSet(GoalSet goalSet, int time) {
		if (active) {
			//System.out.println("\nupdateGoalSet("+goalSet+","+time+")");
			ArrayList<Goal> goals = goalSet.getGoals();
			
			//go through goals in goalset
			for (Goal goal : goals) {
	
				if (goal.hasGenerator()) {
					Class raterClass = goal.getGenerator().getClass();
					if (raterClass != null) {
						// find fitting curve
						GoalCurve gc = goalCurveMap.get(raterClass);
						if (gc != null) {
			//				System.out.println("updating goal: " + goal);
							if (goal.wantsToBePiloted()) {
								gc.updateGoal(goal, time);
							}
						}
					}
				}
				
			}
			goalSet.updateVersion();
			
			//System.out.println(time + ". Updated Goalset:\n" + goalSet.toString() );
		}
	}
	
	public boolean isFinishedAt(int time) {
		return (time >= duration);
	}
	
	public boolean isFinishedYet() {
		return (position >= duration);
	}
	
	public String toString() {
		String s = "";
		for (GoalCurve gc : goalCurves) {
			s+=gc.toString();
		}
		return s;
	}

	public String getDescription() {
		return description;
	}

	public String getLabel() {
		return label;
	}

	public String toStringCompact() {
		// TODO Auto-generated method stub
		return this.label; //length
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	

	public synchronized boolean isActive() {
		return active;
	}

	public synchronized void setActive(boolean active) {
		this.active = active;
	}
	
	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	
	public void moveOn(){
		if (position < duration) {
			position++;
		}
	}
	
	

}
