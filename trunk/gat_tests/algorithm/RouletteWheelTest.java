package algorithm;

import config.Themes;
import junit.framework.TestCase;
import algorithm.composers.kaida.Feature;
import algorithm.composers.kaida.GoalSet;
import algorithm.composers.kaida.Individual;
import algorithm.raters.FitnessRater;
import algorithm.raters.FitnessRaterAverageSpeed;
import algorithm.raters.Rater;
import algorithm.raters.RaterAverageSpeed;
import algorithm.tools.RouletteKey;
import algorithm.tools.RouletteWheel;
import bols.BolBase;
import bols.Variation;

public class RouletteWheelTest extends TestCase {
	BolBase bolBase;
	protected void setUp() throws Exception {
		super.setUp();
		bolBase = new BolBase();		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'java.util.HashMap.get(Object)'
	 */
	public void testGet() throws Exception {
		//Algorithm al = new Algorithm(bolBase, new Teental(bolBase), Variation.getTestVariation(bolBase));
		//assertEquals("After Constructor al should have 1 generation", 1l, al.getGenerationNr());
		Variation var1 = Themes.getTheme01(bolBase);
		Variation var2 = Themes.getTheme01(bolBase);
		Rater rater = new RaterAverageSpeed(bolBase);
		Individual in1 = new Individual(var1);
		Individual in2 = new Individual(var2);
		
		rater.rate(in1);
		rater.rate(in2);
		
		GoalSet goalSet = new GoalSet();
		goalSet.addGoal(rater, 4.0f, 1.0f);
		
		FitnessRater fitnessRater = new FitnessRaterAverageSpeed(goalSet);
		Feature fit1 = fitnessRater.rate(in1);
		Feature fit2 = fitnessRater.rate(in2);
		System.out.println("fit1: " + fit1.toString() + ", fit2: " + fit2.toString());
		double summedFitness = fit1.value + fit2.value;

		RouletteWheel wheel = new RouletteWheel();
		double prob = fit1.value / summedFitness; 
		System.out.println("putting ["+prob+") => in1");
		wheel.put(prob,in1);
		
		prob = fit2.value / summedFitness; 
		System.out.println("putting ["+prob+") => in2");
		wheel.put(prob, in2);
		
		System.out.println("getting(0.2f): " + wheel.get(new RouletteKey(0.2f)));
		System.out.println("getting(0.7f): " + wheel.get(new RouletteKey(0.7f)));
		assertEquals("0.2 should map to in1 !", in1, wheel.get(new RouletteKey(0.2f)));
		assertEquals("0.7 should map to in2 !", in2, wheel.get(new RouletteKey(0.7f)));
		
		try {
			for (int i = 0; i < 100; i++) {
				assertNotNull("getRandom should not return null! ", wheel.getRandom());
			}
		} catch (Exception e) {
			fail ("getRandom() should not throw an exception!");
		}
	}

}
