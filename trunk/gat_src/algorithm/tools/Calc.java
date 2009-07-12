package algorithm.tools;

public class Calc {
	public static double lg2 = Math.log(2);
	
	public static double roundIfClose(double x) {
		double xRounded = Math.round(x);
		if (Math.abs(xRounded-x) < 0.0001){ 
			return xRounded;
		} else {
			return x;
		}
	}
	
	public static boolean equalsTolerantly(double x, double y) {
		double distRounded = Math.abs(Math.round(y-x));
		double distReal = Math.abs(y - x);
	//	double diff = Math.abs(distRounded-distReal);
		if (Math.abs(distRounded-distReal) < 0.0001){ 
			return true;
		} else {
			return false;
		}
	}
	
	public static double round(double x, int decimalsAfterComma) {
		double factor =  Math.pow(10,decimalsAfterComma);
		return Math.round( x * factor)/factor;
	}



	public static double log2( double x ) {
		  return Math.log(x) / lg2;
	}
	
	
	/*public static diffRounded(double x, int decimalsAfterComma) {
		double factor =  Math.pow(10,decimalsAfterComma);
		return Math.round( x * factor)/factor;
	}*/
}
