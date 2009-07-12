package algorithm.mutators;

import java.util.ArrayList;

import midi.MidiStation;

import algorithm.composers.kaida.Individual;
import algorithm.tools.RouletteWheel;
import bols.PlayingStyle;
import bols.SubSequence;
import bols.SubSequenceAdvanced;
import bols.Variation;

public class MutatorSpeedChange extends MutatorStandard implements Mutator {
	
	private double probability;
	private double atomicProbability;
	private double maxSpeed;
	RouletteWheel wheel;
	private double minSpeed;
	
	public MutatorSpeedChange(double probability, double atomicProbability) {
		super();

		label = "Partial Speed Change";
		description = "Changes single subsequences in speed and quantity";
		
		this.probability = probability;
		this.atomicProbability = atomicProbability;
		maxSpeed = MidiStation.getStandard().getRecommendedMaximumRelativeSpeed();
		minSpeed = MidiStation.getStandard().getRecommendedMinimumRelativeSpeed();
		
		try {
			wheel = new RouletteWheel();
			//wheel.put(0.001f, new Double(0.75f));			
			wheel.put(0.7f, new Double(0.5f));
			wheel.put(0.4f, new Double(2f));
			//wheel.put(0.001f, new Double(3f));
			wheel.put(0.1f, new Double(4f));
			
			
			//wheel.put(0.0005f, new Double(6f));
			//wheel.put(0.0005f, new Double(8f));
		} catch (Exception e) {
			//not a critical error, and should not happen
			//just leave factor at 1f for safety
			e.printStackTrace();
		}
	}

	public void mutate(Individual individual) {
		
		Variation var = individual.getVariation();
		ArrayList<SubSequence> subSeqs = var.getSubSequences();
		int i=0;
		int changes = 0;
		while (i < subSeqs.size()) {
			SubSequence subSeq = subSeqs.get(i);
			
			if (Math.random() < atomicProbability) {
				//get a new style
				PlayingStyle oldStyle = subSeq.getPlayingStyle();

				double speedFactor = 1f;
				try {
					speedFactor = (Double) wheel.getRandom();
				} catch (Exception e) {
					//not a critical error, and should not happen
					//just leave factor at 1f for safety
					e.printStackTrace();
				}
				
				
				PlayingStyle newStyle = oldStyle.getProduct(new PlayingStyle(speedFactor,1));
				//correct outofbound speeds
				if ((newStyle.getSpeedValue()<=maxSpeed)&&(newStyle.getSpeedValue() >= minSpeed)) {
				
					double timesToPlayForCompensation = (newStyle.getSpeedValue() / oldStyle.getSpeedValue());
					if (timesToPlayForCompensation>1) {
						//modify subSequence
						subSeqs.remove(i);
						//System.out.println("removed " + subSeq);
						//System.out.println("without:  " + individual.getVariation());
						SubSequence newSubSeq = subSeq.getCopy();
						newSubSeq.setPlayingStyle(newStyle);
						//System.out.println("insert " + timesToPlayForCompensation + " times: " + newSubSeq);
						
						for (int j=0; j < timesToPlayForCompensation; j++) {
							//add copies to compensate for duration shrinkage due to speed increase
							subSeqs.add(i,newSubSeq);	
						}
						i += timesToPlayForCompensation-1;
						changes++;
					} else {
						if (timesToPlayForCompensation == 0.5f) {
							//half speed
							//System.out.println("doing 1/2 stuff");
							
							if (changes>0) {
								var.assignSubSeqPositions();
							}
							double splitPoint = var.getPositions().get(i) + subSeq.getAsSequence().getDuration()*2f;
							int splitIndex = var.getIndexOfSplitPoint(splitPoint);
							if (splitIndex != -1) {	
								
								///new subseq
								SubSequence newSubSeq = subSeq.getCopy();
								newSubSeq.setPlayingStyle(newStyle);
								
								//remove parts to replace
								for(int j=splitIndex-1; j >= i; j--) {
									subSeqs.remove(j);
								}
								
								//add new 1 time
								for(int j=0; j<1; j++) {
									subSeqs.add(i, newSubSeq);
									//System.out.println("inserting " + newSubSeq);
								}
								//System.out.println("inserting " + newSubSeq);
								
								changes++;
							}
							
						} else if (speedFactor == 0.75f) {
							//System.out.println("doing 3/4 stuff");
//							// 0.75x speed (3/4)
							if (changes>0) {
								var.assignSubSeqPositions();
							}
							double splitPoint = var.getPositions().get(i) + subSeq.getAsSequence().getDuration()*4f;
							int splitIndex = var.getIndexOfSplitPoint(splitPoint);
							if (splitIndex != -1) {	
								
								///new subseq
								SubSequence newSubSeq = subSeq.getCopy();
								newSubSeq.setPlayingStyle(newStyle);
								
								//remove parts to replace
								for(int j=splitIndex-1; j >= i; j--) {
									subSeqs.remove(j);
								}
								
								//add new 3 times
								for(int j=0; j<3; j++) {
									subSeqs.add(i, newSubSeq);
									//System.out.println("inserting " + newSubSeq);
								}
								//System.out.println("inserting " + newSubSeq);
								
								changes++;
							}

						}
					}
				}
				//System.out.println("Variaion now: " + individual.getVariation());
				
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
