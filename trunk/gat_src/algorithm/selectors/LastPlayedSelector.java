package algorithm.selectors;

import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;

import algorithm.composers.kaida.Generation;
import algorithm.composers.kaida.Individual;

public class LastPlayedSelector extends SelectorStandard implements Selector {
	private Comparator<Individual> comparator;

	public LastPlayedSelector(int size, Comparator<Individual> comparator) {
		super(size);
		
		label = "Last Played Selector";
		description = "Selects the last played individual";
		
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
			children.add(parents.get(0).getCopyKeepBolSequenceStripFeatures());
			//System.out.println("adding child\n"+parents.get(i));
		}
		
		parents.clear();
		enabled = false;

		return children;
	}
	
	
}
