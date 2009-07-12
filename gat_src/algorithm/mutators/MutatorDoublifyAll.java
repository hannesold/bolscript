package algorithm.mutators;

import java.util.Iterator;
import java.util.ArrayList;

import midi.MidiStation;

import algorithm.composers.kaida.Individual;
import bols.PlayingStyle;
import bols.SubSequence;
import bols.Variation;

public class MutatorDoublifyAll extends MutatorStandard implements Mutator {
	
	private double probability;
	private int maxSpeed;
	
	public MutatorDoublifyAll(double probability) {
		super();

		label = "Total Duplicator";
		description = "Transforms A,B into A,B,A,B at double speed.";
		this.probability = probability;
		maxSpeed = (int) MidiStation.getStandard().getRecommendedMaximumRelativeSpeed();

	}

	public void mutate(Individual individual) {
		
		ArrayList<SubSequence> subSeqs = individual.getVariation().getSubSequences();
		ArrayList<SubSequence> newSubs = new ArrayList<SubSequence>(subSeqs.size()*2);
		
		int changes = 1;
		boolean doIt = true;
		for (SubSequence sequence : subSeqs) {
			if ((sequence.getPlayingStyle().getSpeedValue()*2f) > maxSpeed) {
				doIt = false;
				break;
			}
		}
		
		if (doIt) {
			for (int i=0;i<2;i++) {
				for (SubSequence subSeq: subSeqs) {
					SubSequence s = subSeq.getCopy();
					s.setPlayingStyle(s.getPlayingStyle().getProduct(new PlayingStyle(2f,1f)));
					newSubs.add(s);
				}
			}
			individual.getVariation().setSubSequences(newSubs);
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
