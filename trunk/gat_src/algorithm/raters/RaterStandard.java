package algorithm.raters;

import java.util.ArrayList;

import algorithm.composers.kaida.Feature;
import algorithm.composers.kaida.Individual;

public abstract class RaterStandard extends basics.Basic implements Rater {

	protected String label;
	protected String description;
	protected int type;
	
	public String getLabel() {
		return label;
	}
	
	public String getDescription() {
		return description;
	}
	public String toString() {
		return "(" + label + ", " + description + ")";
	}
	
	public Feature rate(Individual individual) {
		//overwrite!
		return null;
	}
	public Feature rate(Individual individual, ArrayList<Individual> individuals) {
		//overwrite!
		return null;
	}
	
	public int getType() {
		return type;
	}

	public double normalisedDistanceToGoal(Individual in, double val, double goal) {
		// overwrite!
		return 0;
	}
	
	public static double normalize(double val) {
		//projects val in [-1...1]
		//else
		return ((val>=0)&&(val<=1))?val:(Math.max(0, Math.min(1,Math.abs(val))));
		//if negatives are alowed:
		//return ((val>=-1)&&(val<=1))?val:(Math.max(-1, Math.min(1,val)));
	}

}
