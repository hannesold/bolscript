package bols;

import java.util.ArrayList;

import basics.Debug;
import bolscript.sequences.RepresentableSequence;

import config.Themes;

import algorithm.tools.RouletteWheel;

import junit.framework.TestCase;

public class VariationTest extends TestCase {

	BolBase bb;
	

	public VariationTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		Debug.init();
		Debug.setMute(true);
		BolBase.init(this.getClass());
		bb = BolBase.getStandard();
		Debug.setMute(false);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'bols.Variation.Variation(String, BolBaseGeneral)'
	 */
	public void testVariationStringBolBaseGeneral() {
		//setBolNames("Dha Ge Ti Ri Ke Te Tin - Na Ta Ke Dhin Ta3 Tun Dhun Ge2");
		 
		Variation var1 = new Variation("Dha Ge Ti Ri Ke Te, Dha -, Dha -, Dha Ge Ti Ri Ke Te", bb);
		
		assertEquals("The Variation should have 4 Subsequences!", 4, var1.getSubSequences().size());
		
		assertEquals("The 1. subseq should be of length 6!", 6, var1.getSubSequence(0).getLength());
		assertEquals("The 2. subseq should be of length 2", 2, var1.getSubSequence(1).getLength());
		assertEquals("The last subseq should be of length 6!", 6, var1.getSubSequence(3).getLength());
		assertEquals("Variation should be of duration 16", 16.0, var1.getDuration());
		assertEquals("Variation should be of length 16", 16, var1.getLength());
		
		System.out.println("var1: " + var1.toString());
		System.out.println("var1.getSeq " + var1.getAsSequence());
	}
	
	/*
	 * Test method for 'bols.Variation.getTestVariation(BolBaseGeneral)'
	 */
	public void testVariationGetTestVariation() {
		//setBolNames("Dha Ge Ti Ri Ke Te Tin - Na Ta Ke Dhin Ta3 Tun Dhun Ge2");
		 
		Variation var1 = Themes.getTheme01(bb);
		
		assertEquals("The Variation should have 4 Subsequences!", 4, var1.getSubSequences().size());
		assertEquals("The 1. subseq should be of length 6!", 6, var1.getSubSequence(0).getLength());
		assertEquals("The 2. subseq should be of length 2", 2, var1.getSubSequence(1).getLength());
		assertEquals("The last subseq should be of length 6!", 6, var1.getSubSequence(3).getLength());
		
		System.out.println(var1.toString());
	}

	public void testVariationGetDuration() {
		ArrayList<Variation> vars = new ArrayList<Variation>();
		ArrayList<BolName> bolNames = bb.getBolNames();
		RouletteWheel wheel = new RouletteWheel();
		RouletteWheel wheel2 = new RouletteWheel();
		try {
		for (BolName name : bolNames) {
			wheel.put(1,name.getNameShort());
		}
		wheel2.put(6," ");
		wheel2.put(1,", ");
		
		for (int i=0; i<50; i++) {
			int l = (int) (Math.random() * 50) + 1;
			//generate a randomized sequence
			String strSeq = "";
			for(int j=0; j<l; j++) {
				strSeq += wheel.getRandom();
				if (j<(l-1)) {
					strSeq += (String) wheel2.getRandom();
				}
			}
			Variation var1 = new Variation(strSeq, bb);
			assertEquals(".getDuration() and .getAsSequence().getDuration() should be equal",
					var1.getAsSequence().getDuration(),(double)var1.getDuration());

		}
		
		
		} catch (Exception e) {
			e.printStackTrace();
			fail("some exception: " + e.getMessage());
		}
		
	}
	
	public void testAddSubSequence() {
		BolSequence seq1 = new BolSequence("Dha Dhin Dhin Dha Dha Dhin Dhin Dha Dha Tin Tin Na Na Dhin Dhin Dha", bb);
		Variation var1 = new Variation(seq1);
		var1.addSubSequence(0,4);
		var1.addSubSequence(4,4);
		var1.addSubSequence(8,4);
		var1.addSubSequence(12,4);
		
		Variation var2 = new Variation(seq1);
		var2.addSubSequences(new int[]{0,4},
				new int[]{4,4},
				new int[]{8,4},
				new int[]{12,4});
		
		Variation var3 = new Variation(seq1);
		var3.addSubSequence(new SubSequenceAdvanced(seq1,0,4,new PlayingStyle(1,1)));
		var3.addSubSequence(new SubSequenceAdvanced(seq1,4,4,new PlayingStyle(1,1)));
		var3.addSubSequence(new SubSequenceAdvanced(seq1,8,4,new PlayingStyle(1,1)));
		var3.addSubSequence(new SubSequenceAdvanced(seq1,12,4,new PlayingStyle(1,1)));
		
		assertEquals("var1 should equal var2 in sequence ",var1.getAsSequence(), var2.getAsSequence());
		assertEquals("var2 should equal var3 in sequence ",var2.getAsSequence(), var3.getAsSequence());		
	}
	
	public void testGetCopyWithPausesMerged() {
		BolSequence seq1 = new BolSequence ("- Dha Ge Ti Ri Ke Te Dha -", bb);
		BolSequence seq2 = seq1.getCopyWithMergedPauses(bb);
		
		System.out.println("seq1:"+seq1.toStringFull()+",\nseq2(merged):"+seq2.toStringFull());
		
		assertEquals("duration should be same ", seq1.getDuration(), seq2.getDuration());
		assertEquals("length should decrease by 1", seq1.getLength()-1, seq2.getLength());
		assertTrue("first 7 bols should be same", new SubSequenceAdvanced(seq1,0,7,new PlayingStyle(1,1)).getAsSequence().equals(new SubSequenceAdvanced(seq2,0,7,new PlayingStyle(1,1)).getAsSequence()));
		
	}
	
	public void testGetAsSequence() {
		
	}
	
	public void testNewVariationFromRepresentableSeq () {
		RepresentableSequence r = new RepresentableSequence();
		r.add(new Bol(bb.getBolName("Dha"), new PlayingStyle(1d), null, false));
		r.add(new Bol(bb.getBolName("Dhin"), new PlayingStyle(1d), null, false));
		r.add(new Bol(bb.getBolName("Dhin"), new PlayingStyle(1d), null, false));
		r.add(new Bol(bb.getBolName("Dha"), new PlayingStyle(1d), null, false));
		
		Variation var = new Variation(r);
		var.addSubSequence(0, 4);
		assertEquals(4, var.getLength());
		assertEquals(4d, var.getDuration());
	}
	
}
