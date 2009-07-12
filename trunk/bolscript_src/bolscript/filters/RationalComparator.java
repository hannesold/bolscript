package bolscript.filters;

import java.util.Comparator;

import basics.Rational;

public class RationalComparator implements Comparator<String> {

	public int compare(String o1, String o2) {
		Rational r1,r2;
		try {
		r1 = Rational.parseNonNegRational(o1.toString());
		r2 = Rational.parseNonNegRational(o2.toString());
		} catch (Exception e) {
			return 0;
		}
		return r1.compareTo(r2);
	}

}
