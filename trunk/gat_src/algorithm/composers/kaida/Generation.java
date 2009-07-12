package algorithm.composers.kaida;

import java.util.ArrayList;

import algorithm.raters.FitnessRater;
import algorithm.raters.FitnessRaterEuklid;
import algorithm.statistics.FitnessStats;

public class Generation extends ArrayList<Individual> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7627307034197288479L;
	
	private boolean rated;
	private String label;
	private boolean hasFittestAssigned;
	private Individual theFittest;
	private FitnessStats fitnessStats;
	private boolean statsCalculated;
	
	public Generation() {
		super();
		rated = false;
		label = "Unnamed Generation";
		hasFittestAssigned = false;
		fitnessStats = new FitnessStats();
		statsCalculated = false;
	}
	
	public Generation(String name) {
		super();
		rated = false;
		label = name;
		hasFittestAssigned = false;
		
		fitnessStats = new FitnessStats();
		statsCalculated = false; 
	}

	public boolean isRated() {
		return rated;
	}
	public void setRated(boolean rated) {
		this.rated = rated;	
	}
	
	public void calculateStats(FitnessRater fRater) {
		int elementCount = size();
		if (rated) {
			double max = 0f;
			double min = this.get(0).getFeature(fRater).value;
			double sum = 0f;
			double av = 0f;
			double std = 0f;
			
			double[] fitnesses = new double[elementCount];
			int i = 0;
			for (Individual ind : this ) {
				//alternatively: 1.=>max, nth=>min
				double fitness = ind.getFeature(FitnessRaterEuklid.class).value;
				fitnesses[i] = fitness;
				if (fitness > max) {
					max = fitness;
				}
				if (fitness < min) {
					min = fitness;
				}
				sum += fitness;
				i++;
			}
			av = sum / (double) elementCount;
			
			double dev = 0f; //summed deviation
			for (int j = 0; j < fitnesses.length; j++) {
				double f = fitnesses[j];
				double delta = f - av;
				dev += (double) Math.pow(delta,2.0);
			}
			std = (double) Math.sqrt(dev / (double) elementCount);
			
			fitnessStats.set(FitnessStats.MINIMUMFITNESS, min);
			fitnessStats.set(FitnessStats.MAXIMUMFITNESS, max);
			fitnessStats.set(FitnessStats.AVERAGEFITNESS, av);
			fitnessStats.set(FitnessStats.SUMMEDFITNESS, sum);
			fitnessStats.set(FitnessStats.STDDEVIATION, std);
			statsCalculated = true;
		}
	}
	
	public double getStat(int i) throws Exception{
		if (!statsCalculated ) {
			throw new Exception ("Statistics not calculated yet!");
		} 
		return fitnessStats.get(i);
	}
	
	public FitnessStats getStats() {
		return fitnessStats;
	}
	
	public void clearExceptFittestAndStats() {
		this.clear();
	}

	public String toString() {
		String s;
		s = "(" + label + ", Size: " + size() + ", " + ((rated) ? "Fitness: " + fitnessStats.get(FitnessStats.AVERAGEFITNESS) : "Unrated") + ")";
		return s;
	}

	public String getLabel() {
		return label;
	}

	public Individual getTheFittest() throws Exception {
		if (!hasFittestAssigned) throw new Exception ("" + this + " has no fittest to return!");
		return theFittest;
	}

	public void setTheFittest(Individual theFittest) {
		this.theFittest = theFittest;
		hasFittestAssigned = true;
	}

	public boolean hasFittest() {
		return hasFittestAssigned;
	}

	public boolean hasStats() {
		// TODO Auto-generated method stub
		return statsCalculated;
	}

}
