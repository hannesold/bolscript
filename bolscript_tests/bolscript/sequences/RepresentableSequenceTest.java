package bolscript.sequences;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import basics.Debug;
import basics.Rational;
import bols.BolBase;
import bols.BundlingDepthToSpeedMap;
import bolscript.Reader;
import bolscript.config.Config;
import bolscript.scanner.SequenceParser;

public class RepresentableSequenceTest {

	@BeforeClass
	public static void init() {
		Debug.init();
		Config.init();
		BolBase.init(RepresentableSequenceTest.class);
	}
	
	@Ignore
	public void testGetBundled() {
		//TODO ADAPT BUNDLING TO WORK WITH NEW ENCODING!!!
		BolBase bb = BolBase.getStandard();
		Debug.setExclusivelyMapped(false);
		RepresentableSequence seq1 = Reader.getRepresentableSequence(" 1! Dhin Dhin 2! Dha Ge 4! Ti Re Ke Te 3! Dha Ti Te Dhe Ti Te 6! Ka Ta Ga Di Ge Na ", new Rational(1), bb);
		Debug.out("sequence1: " + seq1.toString());
		
		Debug.out("\nbundled: " + seq1.getBundled(new Rational(2), true) + "\n\n");
		Debug.out("\nbundled: " + seq1.getBundled(new Rational(1), true) + "\n\n");
		
		RepresentableSequence seq2 = Reader.getRepresentableSequence(" 4! Dha Ge Ti Te Dha Ti ", new Rational(1), bb);
		BundlingDepthToSpeedMap map = Config.getBundlingDepthToSpeedMap(seq2.getMaxSpeed());
		Debug.out("\nbundled directly to targsp 1: " + seq2.getBundled(new Rational(1), true));
		Debug.out("\nbundled to maxDepth: " + seq2.getBundled(map, map.getMaxDepth(), true));
		
	}
	
	@Test
	public void testWrapSubsequence() {
		String input = "Dha Ge ( Dhin Na )";
		SequenceParser parser = new SequenceParser(0, null);
		RepresentableSequence seq = parser.parseUnits(null, input);
		
		assertEquals(6, seq.size());
		RepresentableSequence subseq = seq.wrapAsSubSequence(2, 5);
		assertEquals(3, seq.size());
		assertEquals(4, subseq.size());
		
		RepresentableSequence subseq2 = seq.wrapAsSubSequence(0, seq.size()-1);
		assertEquals(3, subseq2.size());
		assertEquals(1, seq.size());
	}
	
	@Test
	public void testFlattening() {
		String input = "Dha Ge ( Dhin Na )x2<2";
		SequenceParser parser = new SequenceParser(0, null);
		RepresentableSequence seq = parser.parseSequence(null, input);
		Debug.temporary(this, "before flattening: " + seq.toStringAll());
		RepresentableSequence flat = seq.flatten();
		Debug.temporary(this, "after flattening: " + flat.toStringAll());
		
	}
	
	@Test 
	public void testTruncating() {
		String input = "Dha , Ge ( Dhin Na ) Dha Dha Tun Na ";
		SequenceParser parser = new SequenceParser(0, null);
		int[] tried = new int []{1,2,3,4,5,6};
		int[] expected = new int []{1,2,3,4,4,4};
		
		for (int i=0; i < tried.length; i++) {
			RepresentableSequence seq = parser.parseSequence(null, input);
			//Debug.temporary(this, "before truncating: " + seq.toStringAll());
			int trunced = seq.truncateFromEnd(tried[i]);
			//Debug.temporary(this, "after truncating ("+trunced+"/"+tried[i]+"): " + seq.toStringAll());
			assertEquals(expected[i],trunced);
		}
		
		
		input = "Dha , Ge ( Dhin Na ) Dha Dha Tun Na ";
		//parser = new SequenceParser(0, null);
		tried = new int []{1,2,3,4,5,6};
		expected = new int []{1,2,3,4,5,6};
		
		for (int i=0; i < tried.length; i++) {
			RepresentableSequence seq = parser.parseSequence(null, input).flatten();
			//Debug.temporary(this, "before truncating: " + seq.toStringAll());
			int trunced = seq.truncateFromEnd(tried[i]);
			//Debug.temporary(this, "after truncating ("+trunced+"/"+tried[i]+"): " + seq.toStringAll());
			assertEquals(expected[i],trunced);
		}
		
	}

}
