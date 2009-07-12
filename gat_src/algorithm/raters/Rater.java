package algorithm.raters;

import java.util.ArrayList;

import algorithm.composers.kaida.Feature;
import algorithm.composers.kaida.Individual;

public interface Rater {
	
	public static int SIMPLE = 0;
	public static int COMPARATIVE = 1;

	public String getLabel();
	public String getDescription();
	public int getType();
	public String toString();
	
	public Feature rate(Individual individual);
	public Feature rate(Individual individual, ArrayList<Individual> individuals);
	
	public double normalisedDistanceToGoal(Individual in, double val, double goal);
}
