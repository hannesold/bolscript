package algorithm.selectors;

import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;

import algorithm.composers.kaida.Generation;
import algorithm.composers.kaida.Individual;

public class WorstSelector extends SelectorStandard implements Selector {
	private Comparator<Individual> comparator;

	public WorstSelector(int size, Comparator<Individual> comparator) {
		super(size);
		
		label = "Worst";
		description = "Selects the worst individuals only.";
		
		this.comparator = comparator;
		wantsFollowingCrossOver = false;
		wantsFollowingMutation = false;
	}

	public ArrayList<Individual> selectChildren(Generation population) {
		ArrayList<Individual> parents = new ArrayList<Individual>(population); 
		ArrayList<Individual> children = new ArrayList<Individual>();
		
		Collections.sort(parents, comparator);
		
		for (int i=0; i < selectionSize; i++) {
			children.add(parents.get(i).getCopyKeepBolSequenceStripFeatures());
		}
		parents.clear();
		return children;
	}
	
	
}
