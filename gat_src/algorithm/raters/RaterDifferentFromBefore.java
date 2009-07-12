package algorithm.raters;

import java.util.ArrayList;

import algorithm.composers.kaida.Feature;
import algorithm.composers.kaida.Individual;
import bols.BolBase;
import bols.BolSequence;
import bols.Variation;

public class RaterDifferentFromBefore extends RaterStandard implements Rater{
	
	private BolBase bolBase;
	private int nrToCompareWith;
	
	public RaterDifferentFromBefore(BolBase bolBase, int nrToCompareWith) {
		super();
		// TODO Auto-generated constructor stub
		description = "Measures difference to previous (5) Variations as either 1 or 0";
		label = "Difference from previous";
		this.bolBase = bolBase;
		type = Rater.COMPARATIVE;
		this.nrToCompareWith = nrToCompareWith;
	}
	
	public RaterDifferentFromBefore(BolBase bolBase) {
		this(bolBase,2);
	}


	public Feature rate(Individual individual, ArrayList<Individual> oldIndividuals) {		
		Variation variation = individual.getVariation();
		BolSequence bolSeq = variation.getAsSequence();
						
		boolean isDifferentFromAll = true;
		
		int nrOfOldIns = oldIndividuals.size();
		if (nrOfOldIns>0) {
			int highestIndex = Math.max(0,nrOfOldIns);
			int smallestIndex = Math.max(0,highestIndex - 1 - nrToCompareWith);		
			
			//System.out.println("running from " + smallestIndex + " to " + highestIndex);
			
			for (int i=smallestIndex; i<highestIndex; i++) {
			    Individual oldIn = oldIndividuals.get(i);
			    
			    //check if the current sequence resembles the examined individual
				if (oldIn.getVariation().getAsSequence().equals(bolSeq)) {			
					//if it matches one of them, then difference will be 0
					isDifferentFromAll = false;
					break;
				}

			}
		}
		Feature f;
		if (isDifferentFromAll) {
			f = new Feature(this, 1f);
		} else {
			f = new Feature(this, 0f);
		}
		
		
		individual.addFeature(f);
		//System.out.println(individual);
		return f;
	}
	
	public double normalisedDistanceToGoal(Individual in, double val, double goal) {
		return Math.abs(Math.round(goal - val));
	}
}
