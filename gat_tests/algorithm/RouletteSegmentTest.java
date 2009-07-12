package algorithm;

import config.Themes;
import junit.framework.TestCase;
import algorithm.composers.kaida.Individual;
import algorithm.tools.RouletteSegment;
import bols.BolBase;
import bols.Variation;

public class RouletteSegmentTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * Test method for 'algorithm.RouletteSegment.equals(Object)'
	 */
	public void testIncludesAndEquals() throws Exception {
		BolBase bolBase = new BolBase();
		Variation var1 = Themes.getTheme01(bolBase);
		Variation var2 = Themes.getTheme01(bolBase);
		Individual in1 = new Individual(var1);
		Individual in2 = new Individual(var2);
		
		RouletteSegment rs1 = new RouletteSegment(0.d,0.5, in1);
		RouletteSegment rs2 = new RouletteSegment(0.5,1.0, in2);
		System.out.println(rs1.toString());
		System.out.println(rs2.toString());
			
		assertTrue("rs1 should 'equal' 0.2 ", rs1.equals(0.2));
		assertTrue("rs2 should 'equal' 0.7 ", rs2.equals(0.7));		
	}

}
