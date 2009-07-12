package algorithm.raters;

import algorithm.composers.kaida.GoalSet;

public interface FitnessRater extends Rater{
	public void setGoalSet(GoalSet goalSet);
}
