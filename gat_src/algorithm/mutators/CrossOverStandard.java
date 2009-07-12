package algorithm.mutators;

import algorithm.composers.kaida.Individual;

public class CrossOverStandard implements CrossOver {

	protected boolean enabled = true;
	protected String label = "Cross-Over";
	protected String description = "Standard Cross-Over";
	
	public void crossOver(Individual individual, Individual individual2)
			throws Exception {
	}

	public double getProbability() {
		return 0;
	}

	public void setProbability(double probability) {
	}

	public boolean usesAtomicProb() {
		return false;
	}

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

}
