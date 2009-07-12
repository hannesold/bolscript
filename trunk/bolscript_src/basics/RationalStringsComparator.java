package basics;

import java.util.Comparator;

public class RationalStringsComparator implements Comparator<Object> {

	public int compare(Object o1, Object o2) {
		if (o1 == null) return -1;
		if (o2 == null) return 1;
		
		try {
			Rational r1 = Rational.parseNonNegRational(o1.toString());
			Rational r2 = Rational.parseNonNegRational(o1.toString());
		} catch (IllegalArgumentException e) {
		}
		
		return 0;
	}

}
