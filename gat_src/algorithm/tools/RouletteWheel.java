package algorithm.tools;

import java.util.ArrayList;

public class RouletteWheel {

	private ArrayList<RouletteSegment> segments;
	private double lower;
	
	public RouletteWheel() {
		segments = new ArrayList<RouletteSegment>();
		lower = 0;
	}
		
	public void put(double prob, Object object) throws Exception {
		if (prob <= 0.0f) {
			throw new Exception ("Invalid probability: " + prob);
		}
		if (object == null) {
			throw new Exception ("Invalid object: " + object);
		}
		segments.add(new RouletteSegment(lower,lower+prob,object));
		lower+=prob;
	}	
	
	public Object get(RouletteKey key) {
		int i = segments.indexOf(key);
		if (i == -1) {
			return null;
		} else return ((RouletteSegment)segments.get(i)).getObject();
	}
	
	public Object getRandom() throws Exception {
		if (segments.size()==0) throw new Exception ("You have to add Segments to the Roulette Wheel first!");
		Object obj = null;
		int i=0;
		while ((obj==null)&&(i<10)) {
			double rnd = Math.random() * (double)lower;
			RouletteKey key = new RouletteKey(rnd);
			obj = get(key);
			if (obj==null) {
				System.out.println("warning: Roulettewheel missed a random hit for time " + i +", rnd: " + rnd);
			}
			i++;
		}
		return obj;
	}
	
	public void clear() {
		segments.clear();
	}
}
