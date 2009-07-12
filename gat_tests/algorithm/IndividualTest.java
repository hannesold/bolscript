package algorithm;

import config.Themes;
import junit.framework.TestCase;
import algorithm.composers.kaida.Feature;
import algorithm.composers.kaida.Individual;
import algorithm.raters.Rater;
import algorithm.raters.RaterAverageSpeed;
import bols.BolBase;

public class IndividualTest extends TestCase {
	BolBase bb;
	
	protected void setUp() throws Exception {
		super.setUp();
		bb = new BolBase();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testIndividual() throws Exception {
		 
		Individual in1 = new Individual(Themes.getTheme01(bb));
		assertEquals("0 features should be existent.", 0, in1.getFeatures().size());
		assertFalse("sould not be rated yet", in1.isRated());
		Rater rater1 = new RaterAverageSpeed(bb);
		rater1.rate(in1);
		assertEquals("Only one feature should be assigned.", 1, in1.getFeatures().size());
		assertTrue("should be rated now", in1.isRated());
		
		
	}
	
	public void testGetFeature() {
		Individual in1 = new Individual(Themes.getTheme01(bb));
		assertEquals("0 features should be existent.", 0, in1.getFeatures().size());
		assertFalse("sould not be rated yet", in1.isRated());
		Rater rater1 = new RaterAverageSpeed(bb);
		Feature f1 = rater1.rate(in1);
		System.out.println("f1:"+ f1 + ", in1.getF1:" + in1.getFeature(rater1));
		assertEquals("should be same", f1, in1.getFeature(rater1));
		assertEquals("should be same", f1, in1.getFeature(rater1.getClass()));
		
	}
	
	public void testIndividualGetCopy() throws Exception {
		Individual in1 = new Individual(Themes.getTheme01(bb));
		Rater rater1 = new RaterAverageSpeed(bb);
		rater1.rate(in1);
		Individual in2 = in1.getCopyKeepBolSequence();
		assertNotSame("they should have a differing features ArrayList ", in1.getFeatures(), in2.getFeatures());
		assertNotSame("they should have a differing features ", in1.getFeatures().get(0), in2.getFeatures().get(0));
		assertEquals("but the features should have same value  ", in1.getFeatures().get(0).value, in2.getFeatures().get(0).value);
		
	}	
	
}
