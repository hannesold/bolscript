package algorithm.mutators;

import algorithm.composers.kaida.Individual;

public class MutatorStandard implements Mutator {
	
	protected boolean enabled = true;
	protected String label = "Mutator";
	protected String description = "Standard Mutator";
	protected int type;
	
	public String getLabel() {
		return label;
	}
	
	public String getDescription() {
		return description;
	}
	public String toString() {
		return "(" + label + ", " + description + ")";
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void mutate(Individual individual) {
//		overwrite
	}

	public double getProbability() {
//		overwrite
		return 0;
	}

	public void setProbability(double probability) {
//		overwrite
	}

	public boolean usesAtomicProb() {
//		overwrite		
		return false;
	}

}
