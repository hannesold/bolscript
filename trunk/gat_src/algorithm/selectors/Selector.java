package algorithm.selectors;

import java.util.ArrayList;

import algorithm.composers.kaida.Generation;
import algorithm.composers.kaida.Individual;

public interface Selector {
	
	public boolean wantsFollowingCrossOver();
	public boolean wantsFollowingMutation();	
	public ArrayList<Individual> selectChildren(Generation generation) throws Exception;
	
	public void setEnabled(boolean yesno);
	public boolean isEnabled();
}
