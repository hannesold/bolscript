package algorithm.raters;

import algorithm.composers.kaida.Feature;
import algorithm.composers.kaida.Individual;
import bols.BolBase;
import bols.BolSequence;
import bols.Variation;

public class RaterSimilarStart extends RaterStandard {
	
	protected int type = Rater.SIMPLE;
	
	public RaterSimilarStart(BolBase bolBase) {
		super();
		// TODO Auto-generated constructor stub
		description = "Measures how many bols from the start are similar to those of the basicBolSeq";
		label = "Similar Start";
	}

	public Feature rate(Individual individual) {
		
		Variation variation = individual.getVariation();
		BolSequence basicBolSeq = variation.getBasicBolSequence();
		BolSequence bolSeq = variation.getAsSequence();
						
		int length = Math.min(bolSeq.getLength()-1, basicBolSeq.getLength()-1);
		double speed = bolSeq.getBol(0).getSpeed();

		boolean mismatch = false;
		
		int matchedBolCount = 0;
		int i = 0;
		
		while ((i<=length)&&(!mismatch)) {
			if ((bolSeq.getBol(i).getSpeed() == speed) && 
				(bolSeq.getBol(i).getBolName() == basicBolSeq.getBol(i).getBolName())) {
				//we have another match
				matchedBolCount++; //alternatively add amount of similarity (1/1+diff) rather than 1 
				i++;
			} else {
				mismatch = true;
			}
		}
				
		Feature simStartFeature = new Feature (this, (double)matchedBolCount);
		
		individual.addFeature(simStartFeature);
		
		return simStartFeature;
	}
	
	public double normalisedDistanceToGoal(Individual in, double val, double goal) {
		
		//assert val >= 0
		if (goal > 0) {
			return normalize((double)Math.max(1.0f - (val / goal),0.0f));
		} else if (goal==0) {
			double maxPossibleLen = in.getVariation().getBasicBolSequence().getLength();
			return normalize(Math.max(0.0f, val/maxPossibleLen));			
		} else { 
			// => goal<0)
			System.out.println("Warning, goal should not be < 0!!");
			return 1;
		}
	}
}
