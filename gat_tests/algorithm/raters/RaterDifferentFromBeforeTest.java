package algorithm.raters;

import java.util.ArrayList;

import junit.framework.TestCase;
import algorithm.composers.kaida.Feature;
import algorithm.composers.kaida.Individual;
import bols.BolBase;
import bols.Variation;

public class RaterDifferentFromBeforeTest extends TestCase {

	BolBase bb;
	
	protected void setUp() throws Exception {
		super.setUp();
		bb = new BolBase();		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testRaterDifferentFromBefore() throws Exception {
		RaterDifferentFromBefore rater = new RaterDifferentFromBefore(bb);	
		
		Variation var1 = new Variation("Dha Dhin Dhin Dha", bb);
		Variation var2 = new Variation("Dha Dhin Dhin Ge", bb);
		
		Individual in1 = new Individual(var1);
		Individual in2 = new Individual(var2);
		ArrayList<Individual> previous = new ArrayList<Individual>();
		previous.add(in1);
		Feature f1 = rater.rate(in1, previous);
		
		assertEquals("two similar vars should be rated 0 diff",0f,f1.value);
		
		Feature f2 = rater.rate(in2, previous);
		assertEquals("two different vars should be rated 1 diff",1f, f2.value);
		
		
	}
}
