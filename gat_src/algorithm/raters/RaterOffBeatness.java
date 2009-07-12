package algorithm.raters;

import java.util.ArrayList;

import algorithm.composers.kaida.Feature;
import algorithm.composers.kaida.Individual;
import algorithm.tools.Calc;
import bols.BolBase;
import bols.BolPositionedAndWeighted;
import bols.BolSequenceAdvanced;
import bols.Variation;

public class RaterOffBeatness extends RaterStandard implements Rater {
	
	private BolBase bb;// = BolBase.standard;
	
	public RaterOffBeatness(BolBase bb) {
		super();
		this.bb = bb;
		description = "Measures the level of 'Off-Beatness', a measure for syncopation";
		label = "Offbeatness";
		DEBUG = false;
	}
	
	/**
	 * Transforms a variation into a BolSequenceAdvanced where each Bol is positioned and weighted,
	 * according to its type and subSequence position.
	 * @param variation
	 * @return
	 */
	private BolSequenceAdvanced getWeighted (Variation variation) {
		BolSequenceAdvanced seq = new BolSequenceAdvanced(variation.getAsSequence());
		
		/*
		 * weigh bols by type {Empty:0, Dha:2, or not Dha:1}
		 */
		for (int i = 0; i < seq.getLength(); i++) {
			seq.getBol(i).weight = seq.getBol(i).getBolName().equals(bb.getEmptyBol())?0:
					(seq.getBol(i).getBolName().getNameShort().equals("Dha")? 2f : 1f);
		}
		
		/*
		 * weigh Subsequence starts {Empty:0, else:+1}
		 */
		ArrayList<Double> subSeqPositions = variation.getPositions();	
		for (double pos : subSeqPositions) {
			if (seq.getBol(pos) != null) {
				if (!seq.getBol(pos).getBolName().equals(bb.getEmptyBol())) {
					seq.getBol(pos).weight += 1f;
				}
			}
		}
		
		return seq;
	}
	
	/**
	 * Determines the offBeatness for a variation
	 * @param variation
	 * @return
	 */
	private double getOffBeatness(Variation variation) {
		BolSequenceAdvanced seq = getWeighted(variation);
		out(seq);
		
		double duration = variation.getAsSequence().getDuration();
		
		/*
		 * determine maximum (binary) speed
		 */ 
		double maxSpeed = 1f;
			
		for (int i = 0; i < seq.getLength(); i++) {
			double currentSpeed = seq.getBol(i).getSpeed();
			
			if ((currentSpeed > maxSpeed) && // is a maximum speed
				(Calc.log2(currentSpeed) % 1.0) == 0.0) { // is a binary speed

				maxSpeed = currentSpeed;
			}
		}
		
		if (maxSpeed == 1f) {
			return 0;
		}
		
		/*
		 * calculate number of different speeds to examine
		 */
		int nrOfSpeedLevels = (int) Calc.log2(maxSpeed/2f)+1;
		double[] speeds = new double[nrOfSpeedLevels];
		
		/*
		 * store the different speeds
		 */
		double speed = 2;
		for (int i = 0; i < nrOfSpeedLevels; i++) {
			speeds[i] = speed;
			speed = speed * 2;
		
		}
		
		/*
		 * This will be the result in the end
		 */
		double offBeatnessSummed = 0f;
		
		/*
		 * rate at different speeds
		 */
		for (int i = nrOfSpeedLevels-1; i >= 0; i--) {
			speed = speeds[i];
			
			double halfStep = 1.0 / speed;
			double step = 2.0 * halfStep;
			double tripletStep = step * (1.0/3.0); 
					
			/*
			 * determine offBeatness at this speed
			 */
			double offBeatnessSummedAtThisSpeed = 0f;
			double pos = 0f;
			while ( pos < duration) {
				
				BolPositionedAndWeighted[] offBeatBols = seq.getBolsBetween(pos, (double)(pos+step));
			
				/*
				 * go through all bols between (position) and (next position)
				 */
				for (int j = 0; j < offBeatBols.length; j++) {

					// init offBeatness as the weight
					double offBeatness = offBeatBols[j].weight; 
					
					/*
					 * subtract benefits from surrounding OnBeat or Triplet bols
					 */ 
					
					if (Calc.equalsTolerantly((offBeatBols[j].position-pos) % tripletStep, 0.0)) {
						// is a triplet subposition
						double lastPos = (double) Calc.roundIfClose(offBeatBols[j].position - tripletStep);
						double nextPos = (double) Calc.roundIfClose(offBeatBols[j].position + tripletStep) % duration;						
						
						if (seq.getBol(lastPos) != null) {
							offBeatness -= seq.getBol(lastPos).weight/2f;
						}
						if (seq.getBol(nextPos) != null) {
							offBeatness -= seq.getBol(nextPos).weight/2f;
						}
						
					} else if (Calc.equalsTolerantly(offBeatBols[j].position, pos+halfStep)) {
						// is at a binary subposition 
						double lastPos = (double) Calc.roundIfClose(pos);
						double nextPos = (double) Calc.roundIfClose(pos+step)% duration;
						
						if (seq.getBol(lastPos) != null) {
							offBeatness -= seq.getBol(lastPos).weight/2f;
						}
						if (seq.getBol(nextPos) != null) {
							offBeatness -= seq.getBol(nextPos).weight/2f;
						}
						
					} 
					
					/*
					 * add to local sum
					 */
					if (offBeatness > 0) offBeatnessSummedAtThisSpeed += offBeatness;
				}
				
				/*
				 * remove examined bols from sequence
				 */
				for (int j = 0; j < offBeatBols.length; j++) {
					seq.removeBol(offBeatBols[j]);
				}
			
				// proceed to next position
				pos = (double) Calc.roundIfClose(pos+step);
			}
			
			/*
			 * add to total sum, multiplied by speed -> examples:
			 * 1 offbeat at speed 2 -> offBeatness 2,
			 * 1 offbeat at speed 8 -> offBeatness 8 
			 */
			offBeatnessSummed += offBeatnessSummedAtThisSpeed * (speed);
		}
		
		return offBeatnessSummed;
	}
	
	/**
	 * Rates the individual
	 */
	public Feature rate(Individual individual) {
		Feature offBeatness = new Feature(this, (double) getOffBeatness(individual.getVariation()));
		individual.addFeature(offBeatness);
		
		return offBeatness;
	}

	
	/**
	 * Up to distance = 32 : linear progression from 0 to 0.64,
	 * then scaled and strechted 1/x that gives 0.90 for distance = 64 ->
	 */
	public double normalisedDistanceToGoal(Individual in, double val, double goal) {
		double dist = Math.abs(goal-val);
		if (dist <= 32) {
			return (double) ((0.64/32) * dist);
		} else {
			return (double) (0.64+(0.36/((dist/32.0)+1)));
		}
	}
	
	
}
