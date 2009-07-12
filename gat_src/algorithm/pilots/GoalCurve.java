package algorithm.pilots;

import algorithm.composers.kaida.Goal;
import algorithm.pilots.WayPoint.CurvePointTypes;

/*
 * A Curve that describes a change of a Goal in value and importance
 * @see Curve, Goal
 */
public class GoalCurve {
	private Curve values;
	private Curve importances;
	private Class raterClass;
	public int duration;

	public static enum CurveTypes {VALUE, IMPORTANCE};
	
	public GoalCurve(Class rater) {
		super();
		// TODO Auto-generated constructor stub
		this.raterClass = rater;
		values = new Curve();
		importances = new Curve();
		duration = 0;
	}
	
	public void addPoint(CurveTypes curve, int start, double val, CurvePointTypes type) {
		try {
			switch (curve) {
			case VALUE:
				values.addWayPoint(start,val,type);
				break;
			case IMPORTANCE:
				importances.addWayPoint(start,val,type);
				break;
			}
			duration = Math.max(duration, start);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addValuePoint(int start, double val, CurvePointTypes type) {
		addPoint(CurveTypes.VALUE,start,val,type);
	}
	public void addImportancePoint(int start, double val, CurvePointTypes type) {
		addPoint(CurveTypes.IMPORTANCE,start,val,type);
	}
	
	public void addPoint(int start, double val, double importance, CurvePointTypes type) {
		addPoint(CurveTypes.VALUE,start,val,type);
		addPoint(CurveTypes.IMPORTANCE,start,importance,type);
	}
	
	public void updateGoal(Goal goal, int time) {
		goal.value = values.valueAt(time);
		goal.setImportance(importances.valueAt(time));
	}
	
	public String toString() {
		String s = "GoalCurve for: " + raterClass.getSimpleName() + "\n";
		for (int i=0; i <= duration; i++) {
			s += "("+i+"-> val: "+values.valueAt(i)+",imp: "+importances.valueAt(i)+")\n";
		}
		return s;
	}
	public String pointsToString() {
		String s = "GoalCurve for: " + raterClass.getSimpleName() +"\n";
		s += "values:\n" + values.pointsToString() + "importances: \n" + importances.pointsToString();
		return s;
	}

	public Class getRaterClass() {
		// TODO Auto-generated method stub
		return raterClass;
	}
	
	
}
