package algorithm.composers.kaida;

import java.util.ArrayList;
import java.util.HashMap;

import algorithm.raters.Rater;

/*
 * A set of goals.
 * @see Goal
 */
public class GoalSet extends basics.Basic{ 
	ArrayList<Goal> goals;
	private int version;
	private HashMap<Class, Goal> classMap;
	private HashMap<Rater, Goal> raterMap;	
	
	public int getVersion() {
		return version;
	}

	public GoalSet() {
		goals = new ArrayList<Goal>();
		classMap = new HashMap<Class, Goal>();
		raterMap = new HashMap<Rater, Goal>();
		version = 0;
		DEBUG = false;
	}
	
	public void addGoal(Goal goal) {
		if (getGoal(goal.getGenerator()) == null) {
			goals.add(goal);
			updateVersion();
			
		} else {
			out("goal " + goal.toString() + " already exists ");
		}
	}
	
	public void addGoal(Rater generator, double value, double importance, ValueRange valueRange) {
		Feature f = new Feature(generator, value);
		if (getGoal(generator) == null) {
			Goal g = new Goal(f, importance, valueRange);
			goals.add(g);
			classMap.put(g.getGenerator().getClass(),g);
			raterMap.put(g.getGenerator(),g);
			updateVersion();
		} else {
			out("goal " + f.toString() + " already exists ");
		}	
	}
	public void addGoal(Rater generator, double value, double importance) {
		addGoal(generator,value,importance, new ValueRange(0f,10f,0.25f));
	}
	
	public Goal getGoal(Rater r) {
		return raterMap.get(r);
	}
	
	public Goal getGoal(Class c) {
		return classMap.get(c);
	}
	
	
	public void setGoalImportance(Class raterClass, double imp) {
		out("setGoalImportance " + raterClass + " to " + imp );
		Goal g = getGoal(raterClass);
		out("updating goal: " + g);
		if (g!=null) {
			g.setImportance(imp);
			updateVersion();
		}
		
	}
	
	public void setGoalValue(Class raterClass, double val) {
		out("setGoalValue " + raterClass + " to " + val );
		Goal g = getGoal(raterClass);
		out("updating goal: " + g);
		if (g!=null) {
			g.value = val;
			updateVersion();
		}		
	}
	
	public double getGoalValue(Class raterClass) {
		return getGoal(raterClass).value;
	}
	
	
	public double getGoalImportance(Class raterClass) {
		return getGoal(raterClass).value;
	}
	
	public ArrayList<Goal> getGoals() {
		return goals;
	}

	public String toString() {
		String s = "";
		for (Goal goal : goals) {
			s += goal;			
		}
		s += ", version:"+version;
		return s;
	}

	public void updateVersion() {
		// TODO Auto-generated method stub
		version++;
		out("GoalSet version " + version + "\n" + toString());
	}

}
