package algorithm.statistics;

import java.util.ArrayList;

import algorithm.composers.kaida.KaidaComposer;
import algorithm.composers.kaida.Generation;
import algorithm.composers.kaida.Generations;

public class TestRunGenerationStatistics {

	private static final long serialVersionUID = -8571723993617513115L;
	private ArrayList<FitnessStats> stats;
	private ArrayList<FitnessStats> statsSum;
	private ArrayList<Boolean> statsCalced;
	
	private int nrOfTestSets, nrOfGenerations, nrOfRunsYet;
	private String label;
	private KaidaComposer al;
	
	public TestRunGenerationStatistics(int nrOfTestRuns, int nrOfGenerations, String label, KaidaComposer al) {
		stats = new ArrayList<FitnessStats>(nrOfGenerations);
		statsSum = new ArrayList<FitnessStats>(nrOfGenerations);
		statsCalced = new ArrayList<Boolean>(nrOfGenerations);
		this.nrOfTestSets = nrOfTestRuns;
		this.nrOfGenerations = nrOfGenerations;
		this.label = label;		
		
		for (int i=0; i < nrOfGenerations; i++) {
			statsSum.add(new FitnessStats());	//also sets all 0		
			statsCalced.add(new Boolean(false));
		}
		
		this.al = al;
		
		nrOfRunsYet = 0;
	}
	
	
	public void addTestRun(Generations gens) throws Exception{
		
		statsCalced.clear();
		
		for (int i=0; i < gens.size(); i++) {
			Generation g = gens.get(i);
			
			if (g.hasStats()) {
				FitnessStats statsTemp = statsSum.get(i);
				for (int k = 0; k < FitnessStats.NrOfKeys; k++) {
					//add all stat details to existing sums
					statsTemp.add(k, g.getStat(k));					
				}
				statsCalced.add(new Boolean(true));
			} else {
				statsCalced.add(new Boolean(false));
				throw new Exception("only generations with statistics should be added");
			}			
		}
		nrOfRunsYet++;
		
	}
	
	public void calculateStats() throws Exception{
		stats.clear();
		for (int i = 0; i < nrOfGenerations; i++) {
			
			FitnessStats statsSumTemp = statsSum.get(i);
			FitnessStats statsTemp = new FitnessStats();
			
			for (int k = 0; k < FitnessStats.NrOfKeys; k++) {
				//add all stat details to existing sums
				statsTemp.set(k, (statsSumTemp.get(k) / (double)nrOfRunsYet));
			}	
			
			stats.add(statsTemp);
		}
		
	}
	
	public int nrOfGenerations() {
		return stats.size();
	}
	
	public double getStat(int i, int k) {
		return stats.get(i).get(k);
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}


	public KaidaComposer getAl() {
		return al;
	}


	public void setAl(KaidaComposer al) {
		this.al = al;
	}	
	
	public String toString() {
		String s = "["+label + " with "+nrOfGenerations+" generations and " + nrOfTestSets + " trials: ";
		for (int i=0; i<nrOfGenerations;i++) {
			s += stats.get(i).toString();
		}
		return s+"]";
	}


	public boolean hasStat(int i) {
		// TODO Auto-generated method stub
		if (i < statsCalced.size()) {
			return statsCalced.get(i);
		} else return false;
	}


}
