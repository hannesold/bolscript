package algorithm.selectors;

import java.util.ArrayList;

import algorithm.composers.kaida.Individual;

public abstract class SelectorStandard implements Selector {
	protected boolean wantsFollowingMutation = false;
	protected boolean wantsFollowingCrossOver = false;
	protected int selectionSize;
	protected boolean enabled = true;
	
	protected String label = "Selector";
	protected String description = "Standard Selector";
	
	public String getLabel() {
		return label;
	}
	
	public String getDescription() {
		return description;
	}
	public String toString() {
		return "(" + label + ", " + description + ")";
	}
		
	public SelectorStandard(int size) {
		super();
		this.selectionSize = size;
		enabled = true;
	}
	
	public int getSelectionSize() {
		return selectionSize;
	}

	public void setSelectionSize(int selectionSize) {
		this.selectionSize = selectionSize;
	}

	public boolean wantsFollowingCrossOver() {
		return wantsFollowingCrossOver;
	}

	public boolean wantsFollowingMutation() {
		return wantsFollowingMutation;
	}

	public ArrayList<Individual> selectChildren() throws Exception{
		return null;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean yesno) {
		enabled = yesno;
	}

}
