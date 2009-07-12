package algorithm.selectors;

import java.util.Comparator;

import algorithm.composers.kaida.Feature;
import algorithm.composers.kaida.Individual;
import algorithm.raters.Rater;

public class ComparerByFitness implements Comparator<Individual> {
	private Rater rater;
	
	public ComparerByFitness(Rater rater) {
		super();
		// TODO Auto-generated constructor stub
		this.rater = rater;
	}

	public int compare(Individual in1, Individual in2) {
		//compares by fitness
		//positive if in1 is better, negative if in2 is better
		
		Feature f1 = in1.getFeature(rater);
		Feature f2 = in2.getFeature(rater);
		
		if ((f1 == null) || (f2 == null)) {
			System.out.println("features to compare not found!");
			return 0;
		}
		
		int compValue = (f1.value < f2.value) ? -1 : (f1.value > f2.value) ? 1 : 0; 
		return compValue;
	}

}
