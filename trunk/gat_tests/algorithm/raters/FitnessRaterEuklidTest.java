package algorithm.raters;

import java.util.ArrayList;

import config.Themes;

import junit.framework.TestCase;
import algorithm.composers.kaida.Feature;
import algorithm.composers.kaida.GoalSet;
import algorithm.composers.kaida.Individual;
import algorithm.mutators.MutatorDoublifyAll;
import bols.BolBase;
import bols.BolSequence;
import bols.PlayingStyle;
import bols.SubSequence;
import bols.Variation;

public class FitnessRaterEuklidTest extends TestCase {

	BolBase bb;
	ArrayList<Rater> raters;
	
	protected void setUp() throws Exception {
		super.setUp();
		bb = new BolBase();
	}

	/*
	 * Test method for 'algorithm.raters.FitnessRaterEuklid.rate(Individual)'
	 */
	public void testRateIndividual() {
		//set Raters:
		raters = new ArrayList<Rater>();
		raters.add(new RaterAverageSpeed(bb));
		raters.add(new RaterSpeedStdDeviation(bb));
		raters.add(new RaterSimilarEnd(bb));

		//set GoalSet
		GoalSet goalSet = new GoalSet();
		goalSet.addGoal(raters.get(0), 1f, 1.0f);
		goalSet.addGoal(raters.get(1), 0.00f, 1.0f);
		goalSet.addGoal(raters.get(2), 8.0f, 1.0f);
		

		//init FitnessRater
		FitnessRater rater = new FitnessRaterEuklid(goalSet);

		BolSequence seq1 = new BolSequence("Dha Dhin Dhin Dha Dha Dhin Dhin Dha", bb);
		Variation var1 = new Variation(seq1);
		var1.addSubSequence(0,8);
		
		Variation var2 = new Variation(seq1);
		var2.addSubSequence(0,1);
		var2.addSubSequence(0,1);
		var2.addSubSequence(2,6);
		
		Individual in1 = new Individual(var1);
		Individual in2 = new Individual(var2);
		
		ratersRate(in1);
		ratersRate(in2);
		
		Feature f1 = rater.rate(in1);
		Feature f2 = rater.rate(in2);
		System.out.println("f1: " + f1 + ", f2: " + f2);
		System.out.println("in1: " + in1);
		System.out.println("in2: " + in2);
		assertEquals("f1 should be rated 1 ", 1.0f, f1.value);
		
		assertTrue("f1 should be rated better than f2 ", f1.value>f2.value);
		
		goalSet.setGoalImportance(RaterSimilarEnd.class, 0.5f);
		Individual in3 = in2.getCopyKeepBolSequenceStripFeatures();
		ratersRate(in3);
		Feature f3 = rater.rate(in3);
		System.out.println("f2: " + f2 + ", f3: " + f3);		

		assertTrue("f3 should be rated better than f2, as similar end loses importance ", f3.value>f2.value);
		
		Feature f4 = rater.rate(in1.getCopyKeepBolSequenceStripFeatures());
		System.out.println("f1: " + f1 + ", f4: " + f4);
		assertEquals("f1 should be rated same as f4, even if similar end loses importance ", f1.value, f4.value);
		
		//////////////////////
		//set GoalSet
		goalSet = new GoalSet();
		goalSet.addGoal(raters.get(0), 2f, 1.0f);
		goalSet.addGoal(raters.get(1), 0.00f, 0.5f);
		//goalSet.addGoal(new Feature("Target Similar Bols At End", raters.get(2), 8.0f), 1.0f);
		
		//init FitnessRater
		rater = new FitnessRaterEuklid(goalSet);
		var1 = Themes.getTheme02(bb);
		in1 = new Individual(var1);
		in2 = in1.getCopyKeepBolSequenceStripFeatures();
		
		rater.rate(in1);
		new MutatorDoublifyAll(1f).mutate(in2);
		rater.rate(in2);
		System.out.println("in1:" + in1);
		System.out.println("in2:" + in2);

		//assertTrue("")
	}

	private void ratersRate(Individual in1) {
		for (Rater r : raters) {
			r.rate(in1);		
		}
	}
}
