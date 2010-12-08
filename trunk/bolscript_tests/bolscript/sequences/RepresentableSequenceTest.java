package bolscript.sequences;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import basics.Debug;
import basics.Rational;
import bols.Bol;
import bols.BolBase;
import bolscript.config.Config;
import bolscript.scanner.SequenceParser;

public class RepresentableSequenceTest {

	@BeforeClass
	public static void init() {
		Debug.init();
		Config.init();
		BolBase.init(RepresentableSequenceTest.class);
	}
	
	/*
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
		
	}*/
	
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
		String input = "Dha Ge ( 2 Dhin (2Dha Ge)x2 Na )x2<1";
		SequenceParser parser = new SequenceParser(0, null);
		RepresentableSequence seq = parser.parseSequence(null, input);
		Debug.temporary(this, "before flattening: " + seq.toStringAll());
		RepresentableSequence flat = seq.flatten(new SpeedUnit(new Rational(2),true,null), 0);
		Debug.temporary(this, "after flattening: " + flat.toStringAll());
		
		input = "Dha 2! x3<1 \n Ge ( 2 Dhin 3! Na )x2<2";
		seq = parser.parseSequence(null, input);
		Debug.temporary(this, "before flattening: " + seq.toStringAll());
		flat = seq.flatten(new SpeedUnit(Rational.ONE,true,null));
		Debug.temporary(this, "after flattening: " + flat.toStringAll());
		//add relative speeds
		
		input = "(3 ta ki te 1 ge na (3 Dha ti te)x2 )x2";
		seq = parser.parseSequence(null, input);
		Debug.temporary(this, "before flattening: " + seq.toStringAll());
		flat = seq.flatten();
		Debug.temporary(this, "after flattening: " + flat.toStringAll());
		
		input = "(3 (3 ta ki te 1)x2)";
		seq = parser.parseSequence(null, input);
		Debug.temporary(this, "before flattening: " + seq.toStringAll());
		flat = seq.flatten();
		//.getBundled(bundlingMap,bundlingDepth, true);
		Debug.temporary(this, "after flattening: " + flat.toStringAll());
		flat = seq.flatten().getBundled(new Rational(2), false);
		//.getBundled(bundlingMap,bundlingDepth, true);
		Debug.temporary(this, "after bundling: " + flat.toStringAll());		
		String out = "";
		for (Representable r : flat) {
			if (r.getType() == Representable.BOL) {
				out += (r.toString()+((Bol)r).getSpeed() + " ");				
			}
		}
		Debug.temporary(this, out);
		
	}
	
	
	
	@Ignore 
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
			RepresentableSequence seq = parser.parseSequence(null, input).flatten(new SpeedUnit(Rational.ONE,true, null), 0);
			//Debug.temporary(this, "before truncating: " + seq.toStringAll());
			int trunced = seq.truncateFromEnd(tried[i]);
			//Debug.temporary(this, "after truncating ("+trunced+"/"+tried[i]+"): " + seq.toStringAll());
			assertEquals(expected[i],trunced);
		}
	}
	
	@Ignore
	public void testLastAbsSpeedUnit() {
		String[] input = new String[]{"Dha , Ge ( Dhin Na ) Dha Dha Tun Na ",
				"Dha Ne Dha 4!",
				"Dha 2! Ne Dha",
				"Dha 3!, 4! Ge "};
		SequenceParser parser = new SequenceParser(0, null);
		int[] externalSpeeds= new int[] {1,1,1,1};
		
		int[] expectedLastSpeeds = new int []{1,4,2,4};
		
		for (int i=0; i < input.length; i++) {
			RepresentableSequence seq = parser.parseSequence(null, input[i]);
			SpeedUnit last = seq.lastAbsoluteSpeedUnit(new SpeedUnit(new Rational(externalSpeeds[i]),true,null));
			assertEquals(new Rational(expectedLastSpeeds[i]), last.getSpeed());
		}
	}
	
	@Test
	public void testGetUnitAtCaretPosition () {
		String input = "Dha Dhin Dhin ( 4 Ti Re )";
		SequenceParser parser = new SequenceParser(0, null);
		RepresentableSequence seq = parser.parseSequence(null, input);
		
		String [] expectedBolNames  = new String[]
		         {"Dha", 
				"Dhin", 
				"Dhin", 
				"Ti", 
				"Re"};
		int[][] caretPositions = new int[5][];
		caretPositions[0] = new int[]{0,1,2,3};
		caretPositions[1] = new int[]{4,5,6,7,8};
		caretPositions[2] = new int[]{9,10,11,12,13};
		caretPositions[3] = new int[]{18,19,20};
		caretPositions[4] = new int[]{21,22,23};
		
		for (int i = 0; i < expectedBolNames.length; i++) {
			for (int j = 0; j < caretPositions[i].length; j++) {
				int caretPosition = caretPositions[i][j];
				assertEquals(
						Representable.BOL, 
						seq.getUnitAtCaretPosition(caretPosition).getType());
				Bol bol = (Bol) seq.getUnitAtCaretPosition(caretPosition);
				assertEquals(
						BolBase.getStandard().getBolName(expectedBolNames[i]), 
						bol.getBolName());	
				//Debug.temporary(this, "found the expected bol " + expectedBolNames[i]);
			}
		}
		
		
		
		
		//assertEquals(Representable.BOL, );
		
		
	}

}
