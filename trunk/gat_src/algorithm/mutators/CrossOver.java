package algorithm.mutators;

import algorithm.composers.kaida.Individual;

public interface CrossOver {
	
	public final double USESATOMICPROB = -1.0f;

	public void crossOver(Individual individual, Individual individual2) throws Exception;
	
	public double getProbability();

	public void setProbability(double probability);
	
	public boolean usesAtomicProb();
	
	public String getLabel();
	
	public String getDescription();
	
	public String toString();
	
	public boolean isEnabled();
	
	public void setEnabled(boolean enabled);
	
}
