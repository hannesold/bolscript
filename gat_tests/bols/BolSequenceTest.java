package bols;

import junit.framework.TestCase;

import org.junit.BeforeClass;
import org.junit.Test;

import basics.Debug;
import bolscript.config.Config;
import bolscript.sequences.RepresentableSequence;

public class BolSequenceTest extends TestCase {
	BolBaseGeneral bb;

	
	@BeforeClass
	public void setUp() throws Exception {
		super.setUp();

		bb = BolBase.getStandard();
	}
	@Test
	public void testDurationStuff () throws Exception{

		BolSequence seq1 = new BolSequence();
		seq1.addBol(new Bol(bb.getBolName("Dha"), new PlayingStyle(1,1), null, false));
		seq1.addBol(new Bol(bb.getBolName("Ge"), new PlayingStyle(1,1), null, false));
		seq1.addBol(new Bol(bb.getBolName("Dha"), new PlayingStyle(1,1), null, false));
		seq1.addBol(new Bol(bb.getBolName("-"), new PlayingStyle(1,1), null, false));
		
		assertEquals("duration of seq1 should be 4", 4.0d, seq1.getDuration());
		
		
		seq1 = new BolSequence();
		seq1.addBol(new Bol(bb.getBolName("Dha"), new PlayingStyle(1,1), null, false));
		seq1.addBol(new Bol(bb.getBolName("Ge"), new PlayingStyle(1,1), null, false));
		seq1.addBol(new Bol(bb.getBolName("Dha"), new PlayingStyle(1,1), null, false));
		
		assertEquals("duration should be 3", 3.0d, seq1.getDuration());
		
		seq1 = new BolSequence();
		seq1.addBol(new Bol(bb.getBolName("Dha"), new PlayingStyle(2,1), null, false));
		seq1.addBol(new Bol(bb.getBolName("Ge"), new PlayingStyle(2,1), null, false));
		seq1.addBol(new Bol(bb.getBolName("Dha"), new PlayingStyle(2,1), null, false));
		seq1.addBol(new Bol(bb.getBolName("-"), new PlayingStyle(2,1), null, false));		
		
		assertEquals("duration should be 2.0f", 2.0d, seq1.getDuration());		
		
		seq1 = new BolSequence();
		seq1.addBol(new Bol(bb.getBolName("Dha"), new PlayingStyle(2,1), null, false));
		seq1.addBol(new Bol(bb.getBolName("Ge"), new PlayingStyle(2,1), null, false));
		seq1.addBol(new Bol(bb.getBolName("Dha"), new PlayingStyle(2,1), null, false));
		
		assertEquals("duration should be 1.5f", 1.5d, seq1.getDuration());				
		
	}
	@Test	
	public void testCopyPausesMerged() throws Exception {
		BolSequence seq1 = new BolSequence();
		seq1.addBol(new Bol(bb.getBolName("Dha"), new PlayingStyle(1,1), null, false));
		seq1.addBol(new Bol(bb.getBolName("Ge"), new PlayingStyle(1,1), null, false));
		seq1.addBol(new Bol(bb.getBolName("Dha"), new PlayingStyle(1,1), null, false));
		seq1.addBol(new Bol(bb.getBolName("-"), new PlayingStyle(1,1), null, false));
		
		BolSequence seq2 = new BolSequence();
		seq2.addBol(new Bol(bb.getBolName("-"), new PlayingStyle(1,1), null, false));		
		seq2.addBol(new Bol(bb.getBolName("Dha"), new PlayingStyle(1,1), null, false));
		seq2.addBol(new Bol(bb.getBolName("-"), new PlayingStyle(2,1), null, false));
		seq2.addBol(new Bol(bb.getBolName("-"), new PlayingStyle(2,1), null, false));
		seq2.addBol(new Bol(bb.getBolName("Dha"), new PlayingStyle(1,1), null, false));
		seq2.addBol(new Bol(bb.getBolName("-"), new PlayingStyle(1,1), null, false));		
		
		BolSequence seq3 = seq1.getCopyWithMergedPauses(bb);
		assertEquals("duration of seq3 should be same as of seq1", seq3.getDuration(), seq1.getDuration());	
		assertEquals("seq3 should have three bols left",3, seq3.getLength());
		
		BolSequence seq4 = seq2.getCopyWithMergedPauses(bb);	
		assertEquals("duration of seq4 should be same as of seq2", seq2.getDuration(), seq4.getDuration());	
		assertEquals("duration of seq4 should be 5", 5.0d, seq4.getDuration());	
		assertEquals("seq4 should have three bols left",3, seq4.getLength());
		
		
	}
	@Test	
	public void testEquals() {
		BolSequence seq1 = new BolSequence("Dha Dhin Dhin Dha", bb);
		BolSequence seq2 = new BolSequence("Dha Dhin Dhin Dha", bb);
		assertTrue("seq1 should be equals to seq1", seq1.equals(seq2));
		seq2 = new BolSequence("Dha Dhin Na Dha", bb);
		assertFalse("seq1 should differ from seq2", seq1.equals(seq2));
		seq2 = new BolSequence("Dha Dhin Dhin Dha Dha", bb);
		assertFalse("seq1 should differ from seq2", seq1.equals(seq2));
		seq1 = new BolSequence("Dha Dhin Dhin Dha Dha -", bb);
		seq2 = new BolSequence("Dha Dhin Dhin Dha Dha", bb);
		assertFalse("seq1 should differ from seq2", seq1.equals(seq2));
		
	}
	
	@Test
	public void testBolSequenceFromRepresentableSeq() {
		RepresentableSequence r = new RepresentableSequence();
		r.add(new Bol(bb.getBolName("Dha"), new PlayingStyle(1d), null, false));
		r.add(new Bol(bb.getBolName("Dhin"), new PlayingStyle(1d), null, false));
		r.add(new Bol(bb.getBolName("Dhin"), new PlayingStyle(1d), null, false));
		r.add(new Bol(bb.getBolName("Dha"), new PlayingStyle(1d), null, false));
		
		BolSequence bs = new BolSequence(r);
		assertEquals(4, bs.getLength());
		assertEquals(4d, bs.getDuration());
	}

}
