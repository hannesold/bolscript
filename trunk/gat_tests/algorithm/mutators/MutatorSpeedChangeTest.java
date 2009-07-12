package algorithm.mutators;

import config.Themes;
import junit.framework.TestCase;
import algorithm.composers.kaida.Feature;
import algorithm.composers.kaida.Individual;
import algorithm.raters.RaterAverageSpeed;
import bols.BolBase;
import bols.Variation;

public class MutatorSpeedChangeTest extends TestCase {

	/*
	 * Test method for 'algorithm.mutators.MutatorSpeedChange.mutate(Individual)'
	 */
	public void testMutate() {
		try {
		BolBase bolBase = new BolBase();	
		Variation var1 = Themes.getTheme01(bolBase);
		RaterAverageSpeed rater = new RaterAverageSpeed(bolBase);
		Individual in1 = new Individual(var1);

		// a speed change mutator that has the full
		Mutator m = new MutatorSpeedChange(1f, 1f);
		
		Feature avSpeed1 = rater.rate(in1);
		
		Individual in2 = in1.getCopyKeepBolSequenceStripFeatures();
		assertEquals("duration should be the same before",in1.getVariation().getAsSequence().getDuration(),in2.getVariation().getAsSequence().getDuration());
		System.out.println("Variaion before: " + in2.getVariation());
		System.out.println("avSpeed of in1 before mutating: " + avSpeed1);
		int nrOfRuns = 10;
		for (int i = 0; i < nrOfRuns; i++) {
			m.mutate(in2);
			System.out.println("Variaion now: " + in2.getVariation());	
			assertEquals("duration should stay the same",in1.getVariation().getAsSequence().getDuration(),in2.getVariation().getAsSequence().getDuration());				
		}
		Feature avSpeed2 = rater.rate(in2);
		System.out.println("avSpeed of in2 after mutating: " + avSpeed2);
		
		} catch (Exception e) {
			fail("some exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
