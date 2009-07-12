package algorithm.raters;

import config.Themes;
import algorithm.composers.kaida.Feature;
import algorithm.composers.kaida.Individual;
import bols.BolBase;
import bols.BolSequence;
import bols.Variation;
import junit.framework.TestCase;

public class RaterOffBeatnessTest extends TestCase {
	BolBase bb = BolBase.getStandard();
	
	public void testNew() throws Exception {
		RaterOffBeatness rater = new RaterOffBeatness(bb);		
	}
	
	public void testRateOffBeatness() {
		
		Variation var1 = Themes.getTheme01(bb);
		Variation var2 = Themes.getTheme01(bb);
		
		Individual in1 = new Individual(var1);
		Individual in2 = new Individual(var2);
		
		RaterOffBeatness rater = new RaterOffBeatness(bb);
		Feature f1 = rater.rate(in1);
		Feature f2 = rater.rate(in2);
		System.out.println("f1: " + f1.toString());
		assertTrue("two similar individuals should get the same features: ",f1.equals(f2));
		
		
		var1 = new Variation("- Na, - Dha, Ge -, Dha -", bb);
		BolSequence seq = var1.getBasicBolSequence();
		in1 = new Individual(var1);
		
		assertEquals("var1 should have no offbeats ", 0f, rater.rate(in1).value);
		
		var1 = new Variation(seq);
		var1.addSubSequence(0,2,2);
		var1.addSubSequence(0,2,2);
		var1.addSubSequence(0,2,2);
		var1.addSubSequence(0,2,2);	
		in1 = new Individual(var1);
		
		assertEquals("var1 should have 8 offbeats ", 8f, rater.rate(in1).value);
		
		var1 = new Variation(seq);
		var1.addSubSequence(0, 2, 8);
		var1.addSubSequence(0, 2, 8);
		var1.addSubSequence(0, 2, 8);
		var1.addSubSequence(0, 2, 8);	
		in1 = new Individual(var1);
		
		assertEquals("var1 should have 32 offbeats ", 32f, rater.rate(in1).value);
		
		var1 = new Variation(new BolSequence("Na Dha - Dha Dha -",bb));
		var1.addSubSequence(0, 2, 2);
		var1.addSubSequence(0, 2, 2);
		var1.addSubSequence(0, 2, 2);
		var1.addSubSequence(0, 2, 2);	
		in1 = new Individual(var1);
				
		assertEquals("var1 should have 0 offbeats ", 0f, rater.rate(in1).value);
		
		var1 = new Variation(new BolSequence("Na Dha - Dha Dha -",bb));
		var1.addSubSequence(0, 1, 2);
		var1.addSubSequence(1, 1, 2);
		var1.addSubSequence(0, 2, 2);
		var1.addSubSequence(0, 2, 2);
		var1.addSubSequence(0, 2, 2);	
		in1 = new Individual(var1);
				
		assertEquals("var1 should have offbeatness 2 ", 2f, rater.rate(in1).value);
		
		var1 = new Variation(new BolSequence("Na Dha - Dha Dha -",bb));
		var1.addSubSequence(0, 1, 2);
		var1.addSubSequence(1, 1, 2);
		var1.addSubSequence(2, 2, 2);
		var1.addSubSequence(0, 2, 2);
		var1.addSubSequence(0, 2, 2);	
		in1 = new Individual(var1);
				
		assertEquals("var1 should have offbeatness 1 ", 1f, rater.rate(in1).value);
		
	}
	
	public void testSpecial1() {

	}
}
