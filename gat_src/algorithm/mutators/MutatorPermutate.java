package algorithm.mutators;

import java.util.ArrayList;

import algorithm.composers.kaida.Individual;
import bols.SubSequence;

public class MutatorPermutate extends MutatorStandard implements Mutator {
	
	private double probability;
	private double atomicProbability;
	
	public MutatorPermutate(double probability, double atomic) {
		super();
		
		label = "Permutation";
		description = "Permutates subsequences.";
		
		this.probability = probability;
		this.atomicProbability = atomic;
	}

	public void mutate(Individual individual) {
		ArrayList<SubSequence> subSeqs = individual.getVariation().getSubSequences();
		int i=0;
		int changes = 0;
		int nrOfSubSeqs = subSeqs.size();
		
		double subAtomicProbability = atomicProbability/nrOfSubSeqs;
		
		if (nrOfSubSeqs > 1) {
			while (i < nrOfSubSeqs) {
				SubSequence subSeq = subSeqs.get(i);
				
				if (Math.random() < subAtomicProbability) {
					int j = i;
					
					do {
						j = (int) Math.min(Math.floor(Math.random() * (double)nrOfSubSeqs),nrOfSubSeqs);	
					} while (i == j);		
					
					SubSequence subSeq2 = subSeqs.get(j);
					subSeqs.remove(j);
					subSeqs.add(j, subSeq);
					subSeqs.remove(i);
					subSeqs.add(i, subSeq2);
					changes++;
				}
				
				i++;
			}
		}
		if (changes>0) {
			individual.getVariation().setSubSequences(subSeqs);
			//System.out.println("variation after mutation: " + individual.getVariation());
			
		}
	}
	
	public double getAtomicProbability() {
		return atomicProbability;
	}

	public void setAtomicProbability(double atomicProbability) {
	//	this.atomicProbability = atomicProbability;
	}
	
	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	public boolean usesAtomicProb() {
		return false;
	}

}
