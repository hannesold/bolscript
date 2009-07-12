package algorithm.tools;

public class RouletteSegment {
	private double lower;
	private double upper;
	private Object object;
	
	public RouletteSegment(double lower, double upper, Object object) {
		super();
		// TODO Auto-generated constructor stub
		this.lower = lower;
		this.upper = upper;
		this.object = object;
	}
	
	public boolean equals(Object obj) {
		//hack for making it work with rouletteWheel
		double f = (Double) obj;
		return ((f >= lower) && (f < upper));
	}
	
	public String toString () {
		return ("segment ["+lower+","+upper+")");
	}

	public Object getObject() {
		return object;
	}

}
