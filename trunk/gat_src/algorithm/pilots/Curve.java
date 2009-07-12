package algorithm.pilots;
import java.util.ArrayList;

import algorithm.pilots.WayPoint.CurvePointTypes;

/**
 * A curve built by waypoints
 * @author Hannes
 * @see WayPoint, GoalCurve
 *
 */
public class Curve extends ArrayList<WayPoint> {
	private long duration;
	
	
	public Curve() {
		super();
		duration = 0;
		// TODO Auto-generated constructor stub
	}

	public void addWayPoint(long start, double val, CurvePointTypes type) throws Exception {
		boolean exists = false;
		for (WayPoint wp: this) {
			if (wp.start == start) {
				exists = true;
				break;
			}
		}
		if (exists) {
			System.out.println("Waypoint already exists at " +start);
			throw(new Exception("Waypoint already exists at " +start));
		} else  {
			add(new WayPoint(start,val,type));	
			duration = Math.max(duration, start);
		}
	}

	public double valueAt(long time) {
		// TODO Auto-generated method stub
		//get correct waypoint
		int foundAt=0;
		boolean found = false;
		int len = this.size();
		if (len != 0) {
			for (int i=0; i < (len-1); i++) {
				WayPoint wp = this.get(i);
				if ((wp.start <= time) && (time < get(i+1).start)) {
					foundAt = i;
					found=true;
					break;
				}
			}
			if (!found) {
				if (this.get(len-1).start <= time) {
					return this.get(len-1).value;
				} else {
					System.out.println("no value found in curve for time " + time);
					return 0;
				}
			} else {
				WayPoint p1 = get(foundAt);
				WayPoint p2 = get(foundAt+1);
				switch (p1.type) {
				case LINEAR:
					//linear interpolation
					return p1.value + ((time-p1.start) * ((p2.value - p1.value)/((double)(p2.start - p1.start))));
				case LEVEL:
					return p1.value;
				default:
					System.out.println("unknown curvepoint type, returning like when type is LEVEL");
					return p1.value;
				}				
			}
		} else {
			System.out.println("Curve is empty! returning 0");
			return 0;	
		}
		
		
	}

	public String toString() {
		String s = "";
		for (long i=0; i <= duration; i++) {
			s += "("+i+"-> "+valueAt(i)+")\n";
		}
		return s;
	}
	public String pointsToString() {
		String s = "";
		int len = size();
		for (int i=0; i < len; i++) {
			s += "("+i+"-> "+get(i).toString()+")\n";
		}
		return s;
	}
	
}
