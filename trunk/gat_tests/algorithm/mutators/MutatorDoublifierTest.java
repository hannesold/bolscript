package algorithm.mutators;

import config.Themes;
import junit.framework.TestCase;
import algorithm.composers.kaida.Feature;
import algorithm.composers.kaida.Individual;
import algorithm.raters.RaterAverageSpeed;
import bols.BolBase;
import bols.Variation;

public class MutatorDoublifierTest extends TestCase {

	/*
	 * Test method for 'algorithm.mutators.MutatorDoublifier.mutate(Individual)'
	 */
	public void testMutate() throws Exception {
		BolBase bolBase = new BolBase();	
		Variation var1 = Themes.getTheme01(bolBase);
		RaterAverageSpeed rater = new RaterAverageSpeed(bolBase);
		Individual in1 = new Individual(var1);
		
		
		Mutator m = new MutatorDoublifier(1f, 1f);
		
		Feature avSpeed1 = rater.rate(in1);
		
		Individual in2 = in1.getCopyKeepBolSequenceStripFeatures();
		assertEquals("duration should be the same before",in1.getVariation().getDuration(),in2.getVariation().getDuration());
		System.out.println("Variaion now: " + in2.getVariation());
		System.out.println("avSpeed of in1 before mutating: " + avSpeed1);
		for (int i = 0; i < 10; i++) {
			m.mutate(in2);
			assertEquals("duration should stay the same",in1.getVariation().getDuration(),in2.getVariation().getDuration());			
		}
		Feature avSpeed2 = rater.rate(in2);
		System.out.println("avSpeed of in2 after mutating: " + avSpeed2);
		assertTrue("average Speed should have increased after 1000 runs", avSpeed2.value > avSpeed1.value);

	}

}
