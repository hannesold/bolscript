package algorithm.mutators;

import java.util.ArrayList;

import algorithm.composers.kaida.Individual;
import bols.PlayingStyle;
import bols.SubSequence;

public class MutatorDoublifier extends MutatorStandard implements Mutator {
	
	private double probability;
	private double atomicProbability;
	private int maxSpeed;
	
	public MutatorDoublifier(double probability, double atomicProbability) {
		super();
		// TODO Auto-generated constructor stub
		this.probability = probability;
		this.atomicProbability = atomicProbability;
		maxSpeed = 8;
	}

	public void mutate(Individual individual) {
		
		ArrayList<SubSequence> subSeqs = individual.getVariation().getSubSequences();
		int i=0;
		int changes = 0;
		while (i < subSeqs.size()) {
			SubSequence subSeq = subSeqs.get(i);
			
			if (Math.random() < atomicProbability) {
				//get a new style, doubling the speed
				PlayingStyle oldStyle = subSeq.getPlayingStyle();
				PlayingStyle newStyle = oldStyle.getProduct(new PlayingStyle(2,1));
				//correct outofbound speeds
				if (newStyle.getSpeedValue()<=maxSpeed) {
				
					int timesToPlayForCompensation = (int) (newStyle.getSpeedValue() / oldStyle.getSpeedValue());
					
					//modify subSequence
					subSeqs.remove(i);
					//System.out.println("removed " + subSeq);
					//System.out.println("without:  " + individual.getVariation());
					SubSequence newSubSeq = subSeq.getCopy();
					newSubSeq.setPlayingStyle(newStyle);
					//System.out.println("insert " + timesToPlayForCompensation + " times: " + newSubSeq);
					
					for (int j=0; j < timesToPlayForCompensation; j++) {
						//add copies to compensate for duration shrinkage due to speed increase
						subSeqs.add(i, newSubSeq);	
					}
					i += timesToPlayForCompensation-1;
				}
				//System.out.println("Variaion now: " + individual.getVariation());
				changes++;
			}
			i++;
		}
		if (changes>0) {
			individual.getVariation().setSubSequences(subSeqs);
		}
	}
	
	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}
	
	public boolean usesAtomicProb() {
		return true;
	}

}
