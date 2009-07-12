package algorithm.tools;

public class RouletteKey {
	double key;
	
	public RouletteKey(double d) {
		key = (double) d;
	}	
	
	public boolean equals(Object o) {
		return o.equals(key);
	}
	
}
