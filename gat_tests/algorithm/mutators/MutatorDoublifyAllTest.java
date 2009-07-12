package algorithm.mutators;

import config.Themes;
import algorithm.composers.kaida.Feature;
import algorithm.composers.kaida.Individual;
import algorithm.raters.RaterAverageSpeed;
import algorithm.raters.RaterSpeedStdDeviation;
import bols.BolBase;
import bols.Variation;
import junit.framework.TestCase;

public class MutatorDoublifyAllTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * Test method for 'algorithm.mutators.MutatorDoublifyAll.mutate(Individual)'
	 */
	public void testMutate() throws Exception {
		BolBase bolBase = new BolBase();	
		Variation var1 = Themes.getTheme02(bolBase);
		
		RaterAverageSpeed rater = new RaterAverageSpeed(bolBase);
		RaterSpeedStdDeviation raterDev = new RaterSpeedStdDeviation(bolBase);
		
		Individual in1 = new Individual(var1);
		
		
		Mutator m = new MutatorDoublifyAll(1f);
		
		Feature avSpeed1 = rater.rate(in1);
		Feature stdDev1 = raterDev.rate(in1);
		
		Individual in2 = in1.getCopyKeepBolSequenceStripFeatures();
		assertEquals("duration should be the same before",in1.getVariation().getAsSequence().getDuration(),in2.getVariation().getAsSequence().getDuration());
		System.out.println("Variaion now: " + in2.getVariation());
		System.out.println("before mutating: " + avSpeed1 + ", " + stdDev1);
		for (int i = 0; i < 1; i++) {
			m.mutate(in2);
			assertEquals("duration should stay the same",in1.getVariation().getAsSequence().getDuration(),in2.getVariation().getAsSequence().getDuration());			
		}
		Feature avSpeed2 = rater.rate(in2);
		Feature stdDev2 = raterDev.rate(in2);
		System.out.println("before mutating: " + avSpeed2 + ", " + stdDev2);
		assertTrue("average Speed should have increased after 1000 runs", avSpeed2.value > avSpeed1.value);

	}
	
	public void testMutateComplexerVariations() {
		Variation var1 = new Variation("Dha Ge; Ti Ri Ke Te, Dha -, Dha -, Dha Ge; Ti Ri Ke Te", BolBase.getStandard());
		Variation var2 = var1.getCopyFull();
		Individual in1 = new Individual(var2);
		
		Mutator mutator1 = new MutatorDoublifyAll(1f);
		System.out.println("var before: " + in1.getVariation());
		
		mutator1.mutate(in1);
		System.out.println("var after: " + in1.getVariation());
		assertEquals("should be of same duration: ", var1.getDuration(), var2.getDuration());
		
	}

}
