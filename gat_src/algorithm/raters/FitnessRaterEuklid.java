package algorithm.raters;

import gui.playlist.FeaturesGoalTableModel;
import algorithm.composers.kaida.Feature;
import algorithm.composers.kaida.Goal;
import algorithm.composers.kaida.GoalSet;
import algorithm.composers.kaida.Individual;

public class FitnessRaterEuklid extends RaterStandard implements FitnessRater{
	
	private GoalSet goalSet;
	private double maxDistance;
	private int goalSetVersion;
	
	public FitnessRaterEuklid (GoalSet goalSet) {
		setGoalSet(goalSet);
		description = "Measures the inverse of euklydian distance to goal.";
		label = "Euklid Fitness";
	}
	
	public GoalSet getGoalSet() {
		return goalSet;
	}
	
	public void setGoalSet(GoalSet goalSet) {
		this.goalSet = goalSet;
		goalSetVersion = goalSet.getVersion();
		calcParams();
	}
	
	public void calcParams() {
		double maxDSum = 0.0f;
		for (Goal goal : goalSet.getGoals()) {
			maxDSum += Math.pow(goal.getImportance(),2.0);			
		}
		maxDistance = Math.sqrt(maxDSum);		
	}
	
	
	/* (non-Javadoc)
	 * @see algorithm.raters.Rater#rate(algorithm.Individual)
	 */
	public Feature rate(Individual individual) {
		
		//features have to be normalized before this!
		
		double fitness = 0;
		double distanceSum = 0.0;
		if (goalSet.getVersion() != goalSetVersion) {
			goalSetVersion = goalSet.getVersion();
			calcParams();
		}
		
		for (Goal goal: goalSet.getGoals()) {
			Rater rater = goal.getGenerator();
			Feature feature = individual.getFeature(rater.getClass());
			if (feature==null) {
				feature = rater.rate(individual);
			}
			
			double importance = goal.getImportance();
			double distFromGoal = rater.normalisedDistanceToGoal(individual, 
					feature.value,
					goal.value
					);
			
			
			individual.addFeature(new Feature(FeaturesGoalTableModel.DISTANCE_REAL + FeaturesGoalTableModel.DEMARKER + 			goal.getLabel(), goal.value - feature.value));
			individual.addFeature(new Feature(FeaturesGoalTableModel.DISTANCE_NORMALISED + FeaturesGoalTableModel.DEMARKER + 	goal.getLabel(),(double)distFromGoal));
			individual.addFeature(new Feature(FeaturesGoalTableModel.GOAL_VALUE + FeaturesGoalTableModel.DEMARKER + 			goal.getLabel(), goal.value));
			individual.addFeature(new Feature(FeaturesGoalTableModel.GOAL_IMPORTANCE + FeaturesGoalTableModel.DEMARKER + 		goal.getLabel(), goal.getImportance()));
			
			
			distanceSum += 
				Math.pow(distFromGoal*importance, 2.0);
		}
		if (maxDistance != 0) {
			fitness = (double) (1.0 - (Math.sqrt(distanceSum)/maxDistance));
		} else {
			fitness = 1;
		}
		
		Feature f = new Feature("Overall Fitness", this, fitness);
		individual.addFeature(f);
		return f;
	}


}
