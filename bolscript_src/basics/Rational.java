package basics;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bolscript.sequences.RepresentableSequence;


/*************************************************************************
 *  From http://www.cs.princeton.edu/introcs/92symbolic/Rational.java.html
 *  Compilation:  javac Rational.java
 *  Execution:    java Rational
 *
 *  Immutable ADT for Rational numbers. 
 * 
 *  Invariants
 *  -----------
 *   - gcd(num, den) = 1, i.e, the rational number is in reduced form
 *   - den >= 1, the denominator is always a positive integer
 *   - 0/1 is the unique representation of 0
 *
 *  We employ some tricks to stave of overflow, but if you
 *  need arbitrary precision rationals, use BigRational.java.
 *
 *************************************************************************/
/**
 * <p>A class for managing Rational Numbers.</p>
 * <p>From http://www.cs.princeton.edu/introcs/92symbolic/Rational.java.html</p>
 * <p>Extended by Hannes</p>
 */
public class Rational implements Comparable<Rational> {
    private static Rational zero = new Rational(0, 1);

    /**
     * Regex: A nonnegative rational number which fills a string.
     */
    private static String NONNEG_RATIONAL = "^(\\d+)(?:/(\\d+))?$";
    /**
     * Regex Pattern: for parsing nonnegative rationals.
     */
    public static Pattern nonNegativeRationalPattern = Pattern.compile(NONNEG_RATIONAL);
	
    public static Rational ONE = new Rational(1,1);
    
    private int num;   // the numerator
    private int den;   // the denominator

    // create and initialize a new Rational object
    public Rational(int numerator, int denominator) {

        // deal with x/0
        //if (denominator == 0) {
        //   throw new RuntimeException("Denominator is zero");
        //}

        // reduce fraction
        int g = gcd(numerator, denominator);
        num = numerator   / g;
        den = denominator / g;

        // only needed for negative numbers
        if (den < 0) { den = -den; num = -num; }
    }
    
    public Rational (double d) {
    	this((int)Math.round(d*100000),100000);
    }
    
    public Rational (int integer) {
    	this(integer, 1);
    }
    
    /**
     * Copy constructor.
     * @param r
     */
    public Rational(Rational r) {
    	this(r.num, r.den);
    }
    
    /**
     * @author Hannes
     * Parses Strings of the form 12/23.
     * Currently only nonnegative rationals!
     */
    public static Rational parseNonNegRational(String asString) throws IllegalArgumentException {
    	Matcher m = nonNegativeRationalPattern.matcher(asString);
    	if (m.matches()) {
    		
    		int num = Integer.parseInt(m.group(1));
    		int den;
    		if (m.group(2) != null) {
    			den = Integer.parseInt(m.group(2));
    		} else {
    			den = 1;
    		}
    		return new Rational(num, den);
    	} else {
    		throw(new IllegalArgumentException("'" + asString + "' is not a in proper format for a Rational"));
    	}
    }

    // return the numerator and denominator of (this)
    public int numerator()   { return num; }
    public int denominator() { return den; }

    /**
     * Return the Integer portion (i.e. 5/4 -> 1)
     * @return
     */
    public int integerPortion () {
    	return num/den;
    }
    
    /**
     * Returns true if a is an integer multiple of b.
     * @author Hannes
     * @return (a mod b == 0)
     */
    public boolean divides(Rational r) {
    	return ((r.num*den)%(num*r.den)) == 0; 
    }
    
    /**
     * return double precision representation of (this)
     * @return
     */
    public double toDouble() {
        return (double) num / den;
    }

    /**
     * return string representation of (this)
     */
    public String toString() { 
        if (den == 1) return num + "";
        else          return num + "/" + den;
    }

    /**
     * return { -1, 0, +1 } if a < b, a = b, or a > b
     */
    public int compareTo(Rational b) {
        Rational a = this;
        int lhs = a.num * b.den;
        int rhs = a.den * b.num;
        if (lhs < rhs) return -1;
        if (lhs > rhs) return +1;
        return 0;
    }

    /**
     * is this Rational object equal to y?
     */
    public boolean equals(Object y) {
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        Rational b = (Rational) y;
        return compareTo(b) == 0;
    }

    /**
     * hashCode consistent with equals() and compareTo()
     */
    public int hashCode() {
        return this.toString().hashCode();
    }


    // create and return a new rational (r.num + s.num) / (r.den + s.den)
    public static Rational mediant(Rational r, Rational s) {
        return new Rational(r.num + s.num, r.den + s.den);
    }

    // return gcd(|m|, |n|)
    private static int gcd(int m, int n) {
        if (m < 0) m = -m;
        if (n < 0) n = -n;
        if (0 == n) return m;
        else return gcd(n, m % n);
    }

    // return lcm(|m|, |n|)
    private static int lcm(int m, int n) {
        if (m < 0) m = -m;
        if (n < 0) n = -n;
        return m * (n / gcd(m, n));    // parentheses important to avoid overflow
    }

    // return a * b, staving off overflow as much as possible by cross-cancellation
    public Rational times(Rational b) {
        Rational a = this;

        // reduce p1/q2 and p2/q1, then multiply, where a = p1/q1 and b = p2/q2
        Rational c = new Rational(a.num, b.den);
        Rational d = new Rational(b.num, a.den);
        return new Rational(c.num * d.num, c.den * d.den);
    }


    // return a + b, staving off overflow
    public Rational plus(Rational b) {
        Rational a = this;

        // special cases
        if (a.compareTo(zero) == 0) return b;
        if (b.compareTo(zero) == 0) return a;

        // Find gcd of numerators and denominators
        int f = gcd(a.num, b.num);
        int g = gcd(a.den, b.den);

        // add cross-product terms for numerator
        Rational s = new Rational((a.num / f) * (b.den / g) + (b.num / f) * (a.den / g),
                                  lcm(a.den, b.den));

        // multiply back in
        s.num *= f;
        return s;
    }

    // return -a
    public Rational negate() {
        return new Rational(-num, den);
    }

    // return a - b
    public Rational minus(Rational b) {
        Rational a = this;
        return a.plus(b.negate());
    }


    public Rational reciprocal() { return new Rational(den, num);  }

    /**
     * Rational division. 
     * @author Hannes
     * @return a/b
     */
    public Rational dividedBy(Rational b) {
        Rational a = this;
        return a.times(b.reciprocal());
    }

    // test client
    public static void main(String[] args) {
        Rational x, y, z;

        // 1/2 + 1/3 = 5/6
        x = new Rational(1, 2);
        y = new Rational(1, 3);
        z = x.plus(y);
        System.out.println(z);

        // 8/9 + 1/9 = 1
        x = new Rational(8, 9);
        y = new Rational(1, 9);
        z = x.plus(y);
        System.out.println(z);

        // 1/200000000 + 1/300000000 = 1/120000000
        x = new Rational(1, 200000000);
        y = new Rational(1, 300000000);
        z = x.plus(y);
        System.out.println(z);

        // 1073741789/20 + 1073741789/30 = 1073741789/12
        x = new Rational(1073741789, 20);
        y = new Rational(1073741789, 30);
        z = x.plus(y);
        System.out.println(z);

        //  4/17 * 17/4 = 1
        x = new Rational(4, 17);
        y = new Rational(17, 4);
        z = x.times(y);
        System.out.println(z);

        // 3037141/3247033 * 3037547/3246599 = 841/961 
        x = new Rational(3037141, 3247033);
        y = new Rational(3037547, 3246599);
        z = x.times(y);
        System.out.println(z);

        // 1/6 - -4/-8 = -1/3
        x = new Rational( 1,  6);
        y = new Rational(-4, -8);
        z = x.minus(y);
        System.out.println(z);
        
        x = new Rational(1);
        y = new Rational(0.33);
        System.out.println(y);
        
        z = new Rational(x);
        // from string "1/6" + "100/200" + "10"
        try {
        	x = parseNonNegRational("1/6");
        	y = parseNonNegRational("100/200");
        	z = parseNonNegRational("10");
        	System.out.println(x.plus(y).plus(z));
        } catch (Exception e) {
        	System.out.println(e);
        }
    }

    /**
     * @author hannes
     * @return An independent copy.
     */
	public Rational getCopy() {
		return new Rational(num, den);
	}

	public static Rational max(Rational a, Rational b) {
		return (a.compareTo(b)>0)?a:b;
	}

}
