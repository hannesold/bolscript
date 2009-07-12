package algorithm.statistics;

import java.util.HashMap;
import java.util.ArrayList;

import algorithm.composers.kaida.Generation;
import algorithm.composers.kaida.Generations;

public class GenerationsSummed extends ArrayList<Generations>{
	
	private static final long serialVersionUID = -8571723993617513115L;
	private HashMap<Integer, FitnessStats> stats;
	private int nrOfGenerationsWithStats;
	private String label;
	
	
	
	public GenerationsSummed() {
		super();
		stats = new HashMap<Integer, FitnessStats>();
		nrOfGenerationsWithStats = 0;
		label = "Generations Summed";
	}
	
	public GenerationsSummed(int size, String label) {
		super(size);
		stats = new HashMap<Integer, FitnessStats>();
		nrOfGenerationsWithStats = 0;
		this.label = label;		
	}
	
	public GenerationsSummed(String label) {
		super();		
		stats = new HashMap<Integer, FitnessStats>();
		nrOfGenerationsWithStats = 0;
		this.label = label;
	}
	
	public void addGenerations(Generations gens) throws Exception{
		add(gens);
	}
	
	public void calculateStats() throws Exception{
		stats.clear();
		nrOfGenerationsWithStats = 0;
		double elementCount = size();
		//go through each generation
		for (int i=0; i < this.get(0).size(); i++) {
			
			double minSum = 0f;
			double maxSum = 0f;
			double avSum = 0f;
			double sumSum = 0f;
			double stdSum = 0f;
			
			nrOfGenerationsWithStats++;
			boolean thisGenHasStats = true;
			//go through each dataset ("generations")
			for (Generations gens: this) {
				
				Generation g = gens.get(i);
				if (g.hasStats()) {
					minSum += g.getStat(FitnessStats.MINIMUMFITNESS);
					maxSum += g.getStat(FitnessStats.MAXIMUMFITNESS);
					avSum += g.getStat(FitnessStats.AVERAGEFITNESS);
					sumSum += g.getStat(FitnessStats.SUMMEDFITNESS);
					stdSum += g.getStat(FitnessStats.STDDEVIATION);
				} else {
					thisGenHasStats = false;
					break;
				}
			}
			if (thisGenHasStats) {
				FitnessStats stat = new FitnessStats();
				stat.set(FitnessStats.MINIMUMFITNESS, minSum / elementCount);
				stat.set(FitnessStats.MAXIMUMFITNESS, maxSum / elementCount);
				stat.set(FitnessStats.AVERAGEFITNESS, avSum / elementCount);
				stat.set(FitnessStats.SUMMEDFITNESS, sumSum / elementCount);
				stat.set(FitnessStats.STDDEVIATION, stdSum / elementCount);
				stats.put(i, stat);				
			}
			
		}
	}
	public int nrOfGenerations() {
		return stats.size();
	}
	
	public double getStat(int i, int j) {
		return stats.get(i).get(j);
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}	
	
}
