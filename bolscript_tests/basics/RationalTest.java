package basics;

import static org.junit.Assert.*;

import org.junit.Test;

public class RationalTest {

	@Test
	public void testDivides() {
		Rational a = new Rational(1,8);
		Rational b = new Rational(1,2);
		Rational c = new Rational(1,6);
		
		assertFalse(b.divides(a));
		assertFalse(c.divides(a));
		assertFalse(b.divides(c));
		assertTrue(a.divides(b));
		assertTrue(c.divides(b));
		
	}

}
