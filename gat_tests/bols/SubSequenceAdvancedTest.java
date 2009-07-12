package bols;

import java.util.ArrayList;

import junit.framework.TestCase;
import algorithm.composers.kaida.Individual;
import algorithm.mutators.Mutator;
import algorithm.mutators.MutatorDoublifyAll;

public class SubSequenceAdvancedTest extends TestCase {
	BolBase bb = BolBase.getStandard();
	
	public void testSubSequenceSimple() {
		BolSequence seq1 = new BolSequence("Dha Ge Ti Ri Ke Te", bb);
		SubSequenceAdvanced sub1 = new SubSequenceAdvanced(seq1,0,6,new PlayingStyle(1f,1f));
		assertEquals("sub1 should have length 6", 6, sub1.getLength());
		assertEquals("sub1 should have duration 6", 6.0, sub1.getDuration());
		assertFalse("sub1 has no subsequences",sub1.hasSubSequences());
	}
	
	public void testSubSequenceComplex() {
		BolSequence seq1 = new BolSequence("Dha Ge Ti Ri Ke Te", bb);
		SubSequenceAdvanced subA1 = new SubSequenceAdvanced(seq1,0,6,new PlayingStyle(1f,1f));
		SubSequenceAdvanced subA2 = new SubSequenceAdvanced(seq1,0,2, new PlayingStyle(1f,1f));
		
		SubSequenceAdvanced sub1 = new SubSequenceAdvanced(new PlayingStyle(1f,1f),subA1,subA2);
		
		assertTrue("sub1 has subsequences",sub1.hasSubSequences());	
		assertEquals("sub1 should have duration 6", 8.0, sub1.getDuration());
		assertEquals("sub1 should have length 6", 8, sub1.getLength());	
	}

	
	public void testSubSequenceGetSubSequencesRecursive() {
		
		BolSequence seq1 = new BolSequence("Dha Ge Ti Ri Ke Te", bb);
		SubSequenceAdvanced subA1 = new SubSequenceAdvanced(seq1,0,6,new PlayingStyle(1f,1f));
		SubSequenceAdvanced subA2 = new SubSequenceAdvanced(seq1,0,2, new PlayingStyle(1f,1f));
		
		SubSequenceAdvanced sub1 = new SubSequenceAdvanced(new PlayingStyle(1f,1f),subA1,subA2);
		
		ArrayList<SubSequence> subs = sub1.getSubSequencesRecursive(1);
		ArrayList<SubSequence> subs2 = sub1.getSubSequencesRecursive(10);
		assertEquals("subs should be of same length as sub1", subs.size(), sub1.getSubSequences().size());
		assertEquals("subs2 should be of same length as sub1", subs2.size(), sub1.getSubSequences().size());
		for (int i=0; i < subs.size(); i++) {
			assertTrue("subSubSequences should be same", subs.get(i).equals(sub1.getSubSequence(i)));
			assertTrue("subSubSequences should be same", subs2.get(i).equals(sub1.getSubSequence(i)));
		}
		
		
		seq1 = new BolSequence("Dha Ge Ti Ri Ke Te", bb);
		
		SubSequence subB1 = new SubSequenceAtomic(seq1,0,2, new PlayingStyle(1f,1f));
		SubSequence subB2 = new SubSequenceAtomic(seq1,2,4, new PlayingStyle(1f,1f));
		
		subA1 = new SubSequenceAdvanced(new PlayingStyle(1f,1f), subB1, subB2);
		
		subA2 = new SubSequenceAdvanced(seq1,0,2, new PlayingStyle(1f,1f));
		
		sub1 = new SubSequenceAdvanced(new PlayingStyle(1f,1f),subA1,subA2);
		
		subs = sub1.getSubSequencesRecursive(2);
		
		assertTrue("1. should be subB1 ", subB1.equals(subs.get(0)));
		assertTrue("2. should be subB2 ", subB2.equals(subs.get(1)));
		assertTrue("3. should be subA2 ", subA2.equals(subs.get(2)));
		
	
	}
	
	public void testGetSubSequencesRecursiveAdvanced() {
		
		BolSequence seq1 = new BolSequence("Dha Ge Ti Ri Ke Te", bb);
		SubSequenceAdvanced subA1, subA2, subB1, subB2;
		SubSequenceAdvanced sub1;
		
		ArrayList<SubSequence> subs;
		ArrayList<SubSequence> subs2;
		
		
		subA1 = new SubSequenceAdvanced(seq1,0,2,new PlayingStyle(2f,1f));
		subA2 = new SubSequenceAdvanced(seq1,0,4,new PlayingStyle(1f,1f));
		subB1 = new SubSequenceAdvanced(new PlayingStyle(2f,1f), subA1, subA2);
		
		sub1  = new SubSequenceAdvanced(new PlayingStyle(2f,1f), subA1, subA2, subB1);
		
		subs = sub1.getSubSequencesRecursive(1);
		assertEquals("should have 3 subs at depth 1 ",3,subs.size());
		assertEquals("1. should have speed 4 ", 4f, subs.get(0).getPlayingStyle().getSpeedValue());
		assertEquals("2. should have speed 2 ", 2f, subs.get(1).getPlayingStyle().getSpeedValue());
		assertEquals("3. should have speed 4 ", 4f, subs.get(2).getPlayingStyle().getSpeedValue());
		
		subs = sub1.getSubSequencesRecursive(2);
		assertEquals("should have 4 subs at depth 2 ",4,subs.size());
		assertEquals("1. should have speed 4 ", 4f, subs.get(0).getPlayingStyle().getSpeedValue());
		assertEquals("2. should have speed 2 ", 2f, subs.get(1).getPlayingStyle().getSpeedValue());
		assertEquals("3. should have speed 8 ", 8f, subs.get(2).getPlayingStyle().getSpeedValue());
		assertEquals("4. should have speed 4 ", 4f, subs.get(3).getPlayingStyle().getSpeedValue());
	}

	public void testSubSequenceNewFromString() {
		SubSequenceAdvanced sub1 = new SubSequenceAdvanced("Dha Ge Ti Ri Ke Te, Dha -, Dha -, Dha Ge Ti Ri Ke Te", bb);
		
		assertEquals("The Variation should have 4 Subsequences!", 4, sub1.getSubSequences().size());
		
		assertEquals("The 1. subseq should be of length 6!", 6, sub1.getSubSequence(0).getLength());
		assertEquals("The 2. subseq should be of length 2", 2, sub1.getSubSequence(1).getLength());
		assertEquals("The last subseq should be of length 6!", 6, sub1.getSubSequence(3).getLength());
		assertEquals("Variation should be of duration 16", 16.0, sub1.getDuration());
		assertEquals("Variation should be of length 16", 16, sub1.getLength());
		
		System.out.println("var1: " + sub1.toString());
		System.out.println("var1.getSeq " + sub1.getAsSequence());
	}
	
	public void testSubSequenceNewFromStringComplex() {
		SubSequenceAdvanced sub1 = new SubSequenceAdvanced("Dha Ge; Ti Ri Ke Te, Dha -, Dha -, Dha Ge; Ti Ri Ke Te", bb);
		
		System.out.println("var1: " + sub1.toString());
		System.out.println("var1.getSeq " + sub1.getAsSequence());	
		
		assertEquals("The Variation should have 4 Subsequences!", 4, sub1.getSubSequences().size());
		
		assertEquals("The 1. subseq should be of length 6!", 6, sub1.getSubSequence(0).getLength());
		assertEquals("The 2. subseq should be of length 2", 2, sub1.getSubSequence(1).getLength());
		assertEquals("The last subseq should be of length 6!", 6, sub1.getSubSequence(3).getLength());
		assertEquals("Variation should be of duration 16", 16.0, sub1.getDuration());
		assertEquals("Variation should be of length 16", 16, sub1.getLength());
		
		
		assertEquals("The 1. subSeq should have 2 subSeqs ", 2, sub1.getSubSequence(0).getSubSequencesRecursive(1).size());
		assertEquals("The 2. subSeq should have 1 subSeqs ", 1, sub1.getSubSequence(1).getSubSequencesRecursive(1).size());
		assertEquals("The 3. subSeq should have 1 subSeqs ", 1, sub1.getSubSequence(2).getSubSequencesRecursive(1).size());
		assertEquals("The 4. subSeq should have 2 subSeqs ", 2, sub1.getSubSequence(3).getSubSequencesRecursive(1).size());
		
		ArrayList <SubSequence> subs = sub1.getSubSequencesRecursive(2);
		assertEquals("The 1. subsubseq should be of length 2!", 2, subs.get(0).getLength());
		assertEquals("The 2. subsubseq should be of length 4", 4, subs.get(1).getLength());
		assertEquals("The 3. subsubseq should be of length 6!", 2, subs.get(2).getLength());
		
		SubSequenceAdvanced subsAtomified = new SubSequenceAdvanced(new PlayingStyle(1f,1f), subs.toArray());
		System.out.println("subsAtomified: " + subsAtomified.toString());
		System.out.println("subsAtomified.getSeq " + subsAtomified.getAsSequence());	
	}
	

}
