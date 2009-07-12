package algorithm.raters;

import java.util.ArrayList;

import algorithm.composers.kaida.Feature;
import algorithm.composers.kaida.Individual;
import algorithm.tools.Calc;
import bols.Bol;
import bols.BolBase;
import bols.BolName;
import bols.BolSequence;
import bols.Variation;

public class RaterSpeedStdDeviation extends RaterStandard implements Rater {

	protected int type = Rater.SIMPLE;
	private BolBase bolBase;
	private BolName emptyBol;
	private RaterAverageSpeed avSpeedRater;
	
	public RaterSpeedStdDeviation(BolBase bolBase) {
		super();
		// TODO Auto-generated constructor stub
		description = "Measures the StdDeviation of the speed";
		label = "Speed Standard Deviation";
		this.bolBase = bolBase;
		emptyBol = bolBase.getEmptyBol();
		avSpeedRater = new RaterAverageSpeed(bolBase);
	}

	public Feature rate(Individual individual) {
		//Get average number of bols per beat
		//Pauses after a bol are merged with the bol
		
		Variation variation = individual.getVariation();
		BolSequence bolSeq = variation.getAsSequence().getCopyWithMergedPauses(bolBase);
		
		//needs feature average speed to exist
		Feature avSpeedFeature = individual.getFeature(RaterAverageSpeed.class);
		if (avSpeedFeature == null) {
			//rate it
			avSpeedFeature = avSpeedRater.rate(individual);
		}
		double avSpeed = avSpeedFeature.value;
		
		int realBolCount = 0;
		double diffSum = 0;
		double lastBolSpeed = 0;
		double lastDiffSqr = 0;
		
		for (int i=0; i < bolSeq.getLength(); i++) {
			Bol bol = bolSeq.getBol(i);
			if (bol.getBolName() == emptyBol) {
				//pause in the beginning...
			} else {
				if (bol.getSpeed() == lastBolSpeed) {
					diffSum += lastDiffSqr;
					
				} else {
					lastBolSpeed = bol.getSpeed();
					double diff = Calc.log2(lastBolSpeed/avSpeed);
					lastDiffSqr = diff*diff;
					diffSum += lastDiffSqr;
					
					realBolCount++;					
				}
				
			}
		}
		double dev = (double) Math.sqrt(diffSum / (double)realBolCount);
		Feature standardDeviation = new Feature(this, dev);
		individual.addFeature(standardDeviation);
		return standardDeviation;
	}

	public Feature rate(Individual individual, ArrayList<Individual> individuals) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public double normalisedDistanceToGoal(Individual in, double val, double goal) {
		double diff = Math.abs(goal - val);
		if (diff <= 1) {
			return 0.5f * diff;
		} else if ( diff < 3){
			return 0.5f + (0.25f*(diff-1.0f));
		} else {
			return 1f;
		}
	}

}
