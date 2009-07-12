package algorithm.selectors;

import java.util.ArrayList;


import algorithm.composers.kaida.Feature;
import algorithm.composers.kaida.Generation;
import algorithm.composers.kaida.Individual;
import algorithm.raters.FitnessRater;
import algorithm.tools.RouletteWheel;

public class RouletteWheelSelector extends SelectorStandard implements Selector {

	private FitnessRater fitnessRater;
	
	public RouletteWheelSelector (int size, FitnessRater fitnessRater) {
		super(size);
		label = "Roulette Wheel";
		description = "An implementation of Roulette Wheel selection.";
		
		wantsFollowingCrossOver = true;
		wantsFollowingMutation = true;
		this.fitnessRater = fitnessRater;
	}
	
	
	public ArrayList<Individual> selectChildren(Generation population) throws Exception {
		
		ArrayList<Individual> children = new ArrayList<Individual>();
		
		//determine probabilities for survival
		RouletteWheel wheel = new RouletteWheel();
		for (Individual individual : population) {
			Feature fitness = individual.getFeature(fitnessRater);		
			//assign according rouletteSegment to wheel
			wheel.put(fitness.value, individual);
		}
		
		for (int i = 0; i < (selectionSize); i++) {
			children.add(((Individual) wheel.getRandom()).getCopyKeepBolSequenceStripFeatures());			
		}
		wheel.clear();
		return children;
	}
	
	
	public FitnessRater getFitnessRater() {
		return fitnessRater;
	}

	public void setFitnessRater(FitnessRater fitnessRater) {
		this.fitnessRater = fitnessRater;
	}
}
