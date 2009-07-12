package algorithm.raters;

import algorithm.composers.kaida.Feature;
import algorithm.composers.kaida.Individual;
import algorithm.tools.Calc;
import bols.BolBase;
import bols.BolName;
import bols.BolSequence;
import bols.Variation;

public class RaterAverageSpeed extends RaterStandard {
	
	protected int type = Rater.SIMPLE;
	private BolBase bolBase;
	private BolName emptyBol;

	
	public RaterAverageSpeed(BolBase bolBase) {
		super();
		// TODO Auto-generated constructor stub
		description = "Measures the average number of bols per beat";
		label = "Average Speed";
		this.bolBase = bolBase;
		emptyBol = bolBase.getEmptyBol();
	}

	public Feature rate(Individual individual) {
		//Measure average number of bols per beat
		//Pauses after a bol are merged with the bol
		
		Variation variation = individual.getVariation();
		BolSequence bolSeq = variation.getAsSequence();
		
		double duration = bolSeq.getDuration();
		int realBolCount = 0;
		double pauseDurationAtBeginning = 0;
		
		for (int i=0; i < bolSeq.getLength(); i++) {
			
			if (bolSeq.getBol(i).getBolName() == emptyBol) {
				//this is a pause "-",
				if (realBolCount==0) {
					pauseDurationAtBeginning += 1 / bolSeq.getBol(i).getSpeed();
				}
			} else {
				//this is not a pause, but a real bol
				realBolCount++;
			}
			
		}
		//full duration is the duration minus any possible pause at the beginning
		duration = bolSeq.getDuration() - pauseDurationAtBeginning; 
		
		double averageSpeed = ((double) realBolCount) / duration; 	
		
		Feature avSpeedFeature = new Feature (this, averageSpeed);
		
		individual.addFeature(avSpeedFeature);
		
		return avSpeedFeature;
	}

	@Override
	public double normalisedDistanceToGoal(Individual in, double val, double goal) {
		if (goal > 0) {
			return normalize((double)(0.25*Calc.log2(val/goal)));
		} else {
			//(goal <=0)
			System.out.println("Warning, goal should not be <= 0!!");
			return 1;
		}
	}
}

