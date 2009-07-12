package algorithm.pilots;


/**
 * A waypoint in a curve.
 * @author Hannes
 * @see Curve
 */
public class WayPoint {
	
	public static enum CurvePointTypes {LINEAR, LEVEL};
	
	public double value;
	public CurvePointTypes type;
	public long start;
	
	public WayPoint(long start, double value, CurvePointTypes type) {
		super();
		// TODO Auto-generated constructor stub
		this.start = start;
		this.value = value;
		this.type = type;
	}
	
	public String toString() {
		return "start: " + start + ", val: " + value + ", type: " + type;
	}
	
}
