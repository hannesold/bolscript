package bols;

import junit.framework.TestCase;

public class SubSequenceAtomicTest extends TestCase {

	BolBase bb = BolBase.getStandard();
	
	public void testSubSequenceAtomic() {
		BolSequence seq1 = new BolSequence("Dha Ge Ti Ri Ke Te", bb);
		SubSequenceAtomic sub1 = new SubSequenceAtomic(seq1,0,6,new PlayingStyle(1f,1f));
		assertEquals("sub1 should have length 6", 6, sub1.getLength());
		assertEquals("sub1 should have duration 6", 6.0, sub1.getDuration());
		assertFalse("sub1 has no subsequences",sub1.hasSubSequences());
		
		sub1.setPlayingStyle(new PlayingStyle(2f,1f));
		assertEquals("sub1 should have length 6", 6, sub1.getLength());
		assertEquals("sub1 should have duration 3", 3.0, sub1.getDuration());
		assertFalse("sub1 has no subsequences",sub1.hasSubSequences());
		
	}

}
