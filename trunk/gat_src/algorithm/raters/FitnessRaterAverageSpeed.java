package algorithm.raters;

import algorithm.composers.kaida.Feature;
import algorithm.composers.kaida.Goal;
import algorithm.composers.kaida.GoalSet;
import algorithm.composers.kaida.Individual;

public class FitnessRaterAverageSpeed extends RaterStandard implements FitnessRater{
	private GoalSet goalSet;
	
	public FitnessRaterAverageSpeed (GoalSet goalSet) {
		this.goalSet = goalSet;	
	}
	
	public GoalSet getGoalSet() {
		return goalSet;
	}
	
	public void setGoalSet(GoalSet goalSet) {
		this.goalSet = goalSet;
	}
	
	/* (non-Javadoc)
	 * @see algorithm.raters.Rater#rate(algorithm.Individual)
	 */
	public Feature rate(Individual individual) {
		
		double fitness = 0;
		
		Goal wanted = goalSet.getGoal(RaterAverageSpeed.class);
		Feature real = individual.getFeature(RaterAverageSpeed.class);
		
		fitness = real.value / wanted.value;
		
		Feature f = new Feature ("Overall Fitness", this, fitness);
		individual.addFeature(f);
		return f;
	}


}
