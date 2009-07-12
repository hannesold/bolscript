package algorithm.composers.kaida;

import java.util.ArrayList;
import java.util.Collection;


public class Generations extends ArrayList<Generation> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1282145595982027095L;

	public Generations() {
		super(1000);
	}
	
	public Generations(Collection<? extends Generation> c) {
		super(c);
		// TODO Auto-generated constructor stub
	}

	
	public Generations getRange (int from, int to) {
		return new Generations(this.subList(from,to));
	}
	
	public ArrayList<Individual> getFittest(int from, int to) {
		int elementCount = size();
		ArrayList<Individual> fittest = new ArrayList<Individual>();
		for (int i=from;i<to;i++) {
			if (i < elementCount) {
				if (get(i).hasFittest()) {
					try {
						fittest.add(get(i).getTheFittest());
					} catch (Exception e) {
						//dont add if no fittest is there
					}
				}
			}
		}
		return fittest;
		
	}
	//add .add(..), where hasConverged is set.	

	public Generation lastElement() {
		// TODO Auto-generated method stub
		if (this.size()>0) { 
			return this.get(this.size()-1);
		} else return null;
	}
}
