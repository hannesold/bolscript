package algorithm.selectors;

import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;

import algorithm.composers.kaida.Generation;
import algorithm.composers.kaida.Individual;

public class EliteSelector extends SelectorStandard implements Selector {
	private Comparator<Individual> comparator;

	public EliteSelector(int size, Comparator<Individual> comparator) {
		super(size);
		
		label = "Elite Selector";
		description = "Selects the best individuals.";
		
		this.comparator = comparator;
		wantsFollowingCrossOver = false;
		wantsFollowingMutation = false;
	}

	public ArrayList<Individual> selectChildren(Generation population) {
		ArrayList<Individual> parents = new ArrayList<Individual>(population); 
		ArrayList<Individual> children = new ArrayList<Individual>();
		
		Collections.sort(parents, comparator);
		Collections.reverse(parents);
		
		for (int i=0; i < selectionSize; i++) {
			children.add(parents.get(i).getCopyKeepBolSequenceStripFeatures());
			//System.out.println("adding child\n"+parents.get(i));
		}
		
		parents.clear();
		return children;
	}
	
	
}
