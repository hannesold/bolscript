package algorithm.mutators;

import junit.framework.TestCase;
import algorithm.composers.kaida.Individual;
import algorithm.raters.RaterAverageSpeed;
import bols.BolBase;
import bols.BolSequence;
import bols.PlayingStyle;
import bols.SubSequenceAdvanced;
import bols.Variation;

public class CrossOverOnePointTest extends TestCase {

	/*
	 * Test method for 'algorithm.mutators.CrossOverOnePoint.crossOver(Individual, Individual)'
	 */
	public void testCrossOver() {
		try {
		BolBase bb = new BolBase();	
		Variation var1 = new Variation("Dha Ge, Dhin Na", bb);
		Variation var2 = new Variation("Ke Ke, Ti Te", bb);
		RaterAverageSpeed rater = new RaterAverageSpeed(bb);
		Individual in1 = new Individual(var1);
		Individual in2 = new Individual(var2);
		
		CrossOver co = new CrossOverOnePoint(1.0f);
		
		Individual in3 = in1.getCopyKeepBolSequence();
		Individual in4 = in2.getCopyKeepBolSequence();

		System.out.println("in1:" + in1);
		System.out.println("in2:" + in2);
		
		for (int i = 0; i < 3; i++) {
			co.crossOver(in3,in4);
			assertEquals("duration should stay the same",in1.getVariation().getAsSequence().getDuration(),in3.getVariation().getAsSequence().getDuration());
			assertEquals("duration should be the same",in3.getVariation().getAsSequence().getDuration(),in4.getVariation().getAsSequence().getDuration());	
			//System.out.println("in3:" + in3);
			//System.out.println("in4:" + in4);
		}
		
		BolSequence seq1 = new BolSequence("Dha Ge Dhin Na Ke Te Te Ke", bb);
		Variation var3 = new Variation(seq1);
		var3.addSubSequence(new SubSequenceAdvanced(seq1,0,2,new PlayingStyle(1,1)));
		var3.addSubSequence(new SubSequenceAdvanced(seq1,2,2,new PlayingStyle(2,1)));
		var3.addSubSequence(new SubSequenceAdvanced(seq1,2,2,new PlayingStyle(2,1)));		
		var3.addSubSequence(new SubSequenceAdvanced(seq1,4,4,new PlayingStyle(4,1)));
		var3.addSubSequence(new SubSequenceAdvanced(seq1,4,4,new PlayingStyle(4,1)));
		Variation var4 = new Variation(seq1);
		var4.addSubSequence(new SubSequenceAdvanced(seq1,0,2,new PlayingStyle(2,1)));
		var4.addSubSequence(new SubSequenceAdvanced(seq1,0,2,new PlayingStyle(2,1)));		
		var4.addSubSequence(new SubSequenceAdvanced(seq1,2,2,new PlayingStyle(1,1)));
		var4.addSubSequence(new SubSequenceAdvanced(seq1,4,4,new PlayingStyle(2,1)));		

		in3 = new Individual(var3);
		in4 = new Individual(var4);
		
		System.out.println("--------");
		
		System.out.println("in3:" + in3);
		System.out.println("in4:" + in4);
		
		for (int i = 0; i < 10; i++) {
			co.crossOver(in3,in4);
//			assertEquals("duration should stay the same",in1.getVariation().getAsSequence().getDuration(),in3.getVariation().getAsSequence().getDuration());
			assertEquals("duration should be the same",in3.getVariation().getAsSequence().getDuration(),in4.getVariation().getAsSequence().getDuration());	
			System.out.println("in3:" + in3);
			System.out.println("in4:" + in4);
		}		
		
		} catch (Exception e){
			fail("something went wrong in testCrossOver");
			e.printStackTrace();
		}
	}

}
