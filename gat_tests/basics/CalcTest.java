package basics;

import algorithm.tools.Calc;
import junit.framework.TestCase;

public class CalcTest extends TestCase {

	public void testRoundDoubleInt() {
		assertEquals("10.1 rounded with 0 post-comma decimals should be 10", 10.0,Calc.round(10.1,0));
		assertEquals("10.9 rounded with 0 post-comma decimals should be 11", 11.0,Calc.round(10.9,0));
		assertEquals("10.01 rounded with 0 post-comma decimals should be 10", 10.0,Calc.round(10.01,0));
		
		assertEquals("10.01 rounded with 1 post-comma decimals should be 10", 10.0,Calc.round(10.01,1));
		assertEquals("10.09 rounded with 1 post-comma decimals should be 10.1", 10.1,Calc.round(10.09,1));

		assertEquals("10.01 rounded with 2 post-comma decimals should be 10.01", 10.01,Calc.round(10.01,2));
		assertEquals("10.09 rounded with 2 post-comma decimals should be 10.09", 10.09,Calc.round(10.09,2));
		
		assertEquals("10.0000001 rounded with 4 post-comma decimals should be 10.0", 10.0,Calc.round(10.0000001,4));
		assertEquals("10.0009 rounded with 4 post-comma decimals should be 10.0", 10.0009,Calc.round(10.0009,4));
		
	
	}
	
	public void testEquals() {
		assertTrue("10.00000001 should equal 10", Calc.equalsTolerantly(10.00000001, 10));
		assertTrue("10 should equal 10.00000001", Calc.equalsTolerantly(10, 10.00000001));
		assertFalse("10 should not equal 10.1", Calc.equalsTolerantly(10, 10.1));
		assertFalse("10.1 should not equal 10", Calc.equalsTolerantly(10.1, 10));
	}

}
