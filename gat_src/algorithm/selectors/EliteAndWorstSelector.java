package algorithm.selectors;

import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;

import algorithm.composers.kaida.Generation;
import algorithm.composers.kaida.Individual;

public class EliteAndWorstSelector extends SelectorStandard implements Selector {
	private Comparator<Individual> comparator;
	private int eliteSize, worstSize;
	private ArrayList<Individual> parents;
	
	public EliteAndWorstSelector(int eliteSize, int worstSize, Comparator<Individual> comparator) {
		super(eliteSize+worstSize);
		
		label = "Elite And Worst";
		description = "Selects the best and worst.";
		
		this.eliteSize = eliteSize;
		this.worstSize = worstSize;
		this.comparator = comparator;
		
		wantsFollowingCrossOver = false;
		wantsFollowingMutation = false;
	}

	public ArrayList<Individual> selectChildren(Generation population) {
		parents = new ArrayList<Individual>(population); 
		ArrayList<Individual> children = new ArrayList<Individual>();
		
		Collections.sort(parents, comparator);

		//add worst
		for (int i=0; i < worstSize; i++) {
			children.add(parents.get(i).getCopyKeepBolSequenceStripFeatures());
		}
		//add elite
		for (int i=parents.size()-1-eliteSize; i < parents.size(); i++) {
			children.add(parents.get(i).getCopyKeepBolSequenceStripFeatures());
		}
		
		//parents.clear();
		parents.clear();
		
		return children;
	}
	
	
}
