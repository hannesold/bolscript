package algorithm.raters;

import config.Themes;
import junit.framework.TestCase;
import algorithm.composers.kaida.Feature;
import algorithm.composers.kaida.Individual;
import bols.BolBase;
import bols.BolSequence;
import bols.PlayingStyle;
import bols.SubSequenceAdvanced;
import bols.Variation;

public class RaterAverageSpeedTest extends TestCase {
	BolBase bb =BolBase.getStandard();
	
	public void testRaterAverageSpeed() throws Exception {
		RaterAverageSpeed rater = new RaterAverageSpeed(bb);		
	}
	
	public void testRateAverageSpeed() throws Exception {
		Variation var1 = Themes.getTheme01(bb);
		Variation var2 = Themes.getTheme01(bb);
		
		Individual in1 = new Individual(var1);
		Individual in2 = new Individual(var2);
		
		RaterAverageSpeed rater = new RaterAverageSpeed(bb);
		Feature f1 = rater.rate(in1);
		Feature f2 = rater.rate(in2);
		System.out.println("f1: " + f1.toString());
		assertTrue("two similar individuals should get the same features: ",f1.equals(f2));
		
		var1 = new Variation("Dha Ge - -", bb);
		var2 = new Variation("Dha Ge - Dha", bb);
		
		in1 = new Individual(var1);
		in2 = new Individual(var2);
		
		Feature f3 = rater.rate(in1);
		Feature f4 = rater.rate(in2);
		System.out.println("f3: " + f3.toString() + ", f4: " + f4.toString());
		
		assertTrue("f3 should be rated as 0.5 ", f3.value == 0.5);
		assertTrue("f3 and f4 should have different ratings: ", (!f3.equals(f4)));		
		
		var1 = new Variation("Dha Ge Dhin Na, Ge Na Na Na", bb);
		BolSequence seq1 = new BolSequence("Dha Ge Dhin Na Ge Na Na Na", bb);
		var2 = new Variation(seq1);
		var2.addSubSequence(new SubSequenceAdvanced(seq1,0,8, new PlayingStyle(2,1)));
		
		in1 = new Individual(var1);
		in2 = new Individual(var2);
		
		Feature f5 = rater.rate(in1);
		Feature f6 = rater.rate(in2);
		
		System.out.println("f5: " + f5.toString() + ", f6: " + f6.toString());
		assertTrue("f5 should be rated as 1.0 ", f5.value == 1.0);	
		assertTrue("f6 should be rated as 2.0 ", f6.value == 2.0);	
	
		var1 = new Variation("- - Dha Ge Dha Ge", bb);
		var2 = new Variation("Dha Ge Dha Ge", bb);
		
		in1 = new Individual(var1);
		in2 = new Individual(var2);
		
		Feature f7 = rater.rate(in1);
		Feature f8 = rater.rate(in2);
		System.out.println("f7: " + f7.toString() + ", f8: " + f8.toString());
		assertEquals("a leading pause should make no difference! ", f7.value, f8.value);
		
	}
	public void testNormalisedDistance() {
		Variation var1 = Themes.getTheme01(bb);
		Variation var2 = Themes.getTheme01(bb);
		
		Individual in1 = new Individual(var1);
		Individual in2 = new Individual(var2);
		
		RaterAverageSpeed rater = new RaterAverageSpeed(bb);
		Feature f1 = rater.rate(in1);
		Feature f2 = rater.rate(in2);
		System.out.println("f1: " + f1.toString());
		assertEquals("distance should be 0 : ",0f,rater.normalisedDistanceToGoal(in1,f1.value,f2.value));
		
		BolSequence seq1 = new BolSequence("Dha Dha Dhin Dhin Na Na", bb);
		var1 = new Variation(seq1);
		var2 = new Variation(seq1);
		
		var1.addSubSequence(new SubSequenceAdvanced(seq1,0,2,new PlayingStyle(1.0f,1.0f)));
		var1.addSubSequence(new SubSequenceAdvanced(seq1,0,4,new PlayingStyle(4.0f,1.0f)));
		in1 = new Individual(var1);
		
		var2.addSubSequence(new SubSequenceAdvanced(seq1,0,2,new PlayingStyle(2.0f,1.0f)));
		var2.addSubSequence(new SubSequenceAdvanced(seq1,0,4,new PlayingStyle(8.0f,1.0f)));
		in2 = new Individual(var2);
		
		f1 = rater.rate(in1);
		f2 = rater.rate(in2);
		System.out.println("f1: " + f1 + ", f2:" + f2);
		assertEquals("f1 should be rated 2: ",2.0f,f1.value);
		assertEquals("f2 should be rated 4: ",4.0f,f2.value);
		assertEquals("distance between f1,f2 should be 0.25 (1 duplication)",0.25f, rater.normalisedDistanceToGoal(in1,f1.value,f2.value));

		assertEquals("distance between 0.25,2.0 should be 0.75 (3 dups)",0.75f, rater.normalisedDistanceToGoal(in1,0.25f,2.0f));
		assertEquals("distance between 0.25,4.0 should be 1 (4 dups)", 1.0f, rater.normalisedDistanceToGoal(in1,0.25f,4.0f));
		assertEquals("distance between 0.25,100.0 should be 1 ceiled(4 dups)", 1.0f, rater.normalisedDistanceToGoal(in1,0.25f,100.0f));		
		System.out.println("dist(1,2): "+rater.normalisedDistanceToGoal(in1,1f,2f)+", dist(2,1): "+ rater.normalisedDistanceToGoal(in1,2f,1f));
		assertEquals("distance between 1,2 should be same as 2,1",rater.normalisedDistanceToGoal(in1,1f,2f),rater.normalisedDistanceToGoal(in1,2f,1f));
	}
}
