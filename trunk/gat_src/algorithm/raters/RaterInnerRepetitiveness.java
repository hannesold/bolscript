package algorithm.raters;

import java.util.ArrayList;

import algorithm.composers.kaida.Feature;
import algorithm.composers.kaida.Individual;
import bols.BolBase;
import bols.SubSequence;
import bols.Variation;

public class RaterInnerRepetitiveness extends RaterStandard {
	
	protected int type = Rater.SIMPLE;
	private BolBase bolBase;
	private RaterAverageSpeed avSpeedRater;
	
	public RaterInnerRepetitiveness(BolBase bolBase) {
		super();
		// TODO Auto-generated constructor stub
		description = "Measures how many repetitions of subsequences exist";
		label = "Inner Repetitions";
		this.bolBase = bolBase;
		avSpeedRater = new RaterAverageSpeed(bolBase);
	}

	public Feature rate(Individual individual) {
		
		Variation variation = individual.getVariation();		
		ArrayList<SubSequence> subSeqs = new ArrayList<SubSequence>(variation.getSubSequences());
		
		int i = 0;
		int foundRepetitions = 0;
		while (i < subSeqs.size()) {
			SubSequence sub = subSeqs.get(i);
			
			int j = i+1;
			while (j < subSeqs.size()) {
				if (subSeqs.get(j).equals(sub)) {
					subSeqs.remove(j);
					foundRepetitions +=1;
				} else {
					j++;
				}
			}
			i++;
		}
		
		//needs feature average speed to exist
		Feature avSpeedFeature = individual.getFeature(RaterAverageSpeed.class);
		if (avSpeedFeature == null) {
			//rate it
			avSpeedFeature = avSpeedRater.rate(individual);
		}
		
		double avSpeed = avSpeedFeature.value;
		
		Feature innerRepetitiveness = new Feature (this, (double)foundRepetitions / avSpeed);
		
		individual.addFeature(innerRepetitiveness);
		
		return innerRepetitiveness;
	}
	
	public double normalisedDistanceToGoal(Individual in, double val, double goal) {
		
		double absDist = Math.abs(goal - val);
		
		//assert val >= 0
		if (goal >= 0) {
			if (absDist <= 4) {
				return (double) (absDist * 0.20);
			} else if (absDist <=9){
				return (double) (0.8 + (absDist-4) * 0.04);
			} else {
				return 1f;
			}		
		} else { 
			// => goal<0)
			System.out.println("Warning, goal should not be < 0!!");
			return 1;
		}
	}
}
