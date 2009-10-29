package bolscript.scanner;

import static bolscript.sequences.Representable.BOL;
import static bolscript.sequences.Representable.FOOTNOTE;
import static bolscript.sequences.Representable.KARDINALITY_MODIFIER;
import static bolscript.sequences.Representable.LINE_BREAK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.Test;

import basics.Debug;
import bols.Bol;
import bolscript.packets.Packets;
import bolscript.sequences.FailedUnit;
import bolscript.sequences.FootnoteUnit;
import bolscript.sequences.Representable;
import bolscript.sequences.RepresentableSequence;
import bolscript.sequences.SpeedUnit;

public class SequenceParserTest {

	@Test
	public void simpleParseUnits() {
		String input = 
			"A: dha dha ti te\n"+
			"B: dha dha tun na\n"+
			"C: A B ";
		Packets packets = Parser.splitIntoPackets(input);
		SequenceParser parser = new SequenceParser(0,packets);
		RepresentableSequence seq = parser.parseUnits(null, "3 A (dha 2! dha) x2<1 tix2 te");
		Debug.temporary(this, seq.toStringAll());
		assertEquals(11,seq.size());

		packets.get(0).setObject(parser.parseUnits(packets.get(0), packets.get(0).getValue()));

		seq = parser.parseUnits(packets.get(2), "3 A (dha 2! dha) x2<1 tix2 te");

		assertSame(packets.get(0), packets.findReferencedBolPacket(packets.get(2), "A"));
		Debug.temporary(this, seq.toStringAll());
		assertEquals(11,seq.size());
		assertEquals(Representable.REFERENCED_BOL_PACKET, seq.get(1).getType());

		for (int i=0; i < seq.size(); i++) {
			assertNotNull("unit "+i+" of type " + seq.get(i).getType() +": "+seq.get(i) + " should have a textreference", seq.get(i).getTextReference());
		}
	}

	@Test
	public void testParsingWithResultingSubsequences() {
		SequenceParser parser = new SequenceParser(0,null);

		//first input
		String input = "Dha Ge";
		RepresentableSequence seq = parser.parseUnits(null, input);
		assertEquals(2, seq.size());

		
		input = "# Dha _## Ge";
		seq = parser.parseUnits(null, input);
		assertEquals(Representable.FAILED, seq.get(0).getType());
		assertEquals(0, seq.get(0).getTextReference().start());
		assertEquals(1, seq.get(0).getTextReference().end());
		
		assertEquals(Representable.FAILED, seq.get(2).getType());
		assertEquals("_##", ((FailedUnit)seq.get(2)).getObject());
		assertEquals(6, seq.get(2).getTextReference().start());
		assertEquals(9, seq.get(2).getTextReference().end());
		
		input = "### Ge";
		seq = parser.parseUnits(null, input);
		assertEquals(Representable.FAILED, seq.get(0).getType());
		
		input = " Dha Ge";
		seq = parser.parseUnits(null, input);
		Debug.temporary(this, seq.toStringAll());
		for (int i=0; i < seq.size(); i++) {
			Debug.temporary(this, "'"+ seq.get(i) + "': "+seq.get(i).getTextReference().start()+"-" +seq.get(i).getTextReference().end());
		}
		assertEquals(2, seq.size());
		
		
		input = " Dha ### Ge";
		seq = parser.parseUnits(null, input);
		assertEquals(3, seq.size());
		assertEquals(Representable.FAILED, seq.get(1).getType());
		

		
	}
	
	@Test
	public void simpleSubsequenceParsing () {

		SequenceParser parser = new SequenceParser(0,null);

		//first input
		String input = " Dha Ge Dhin Na ( 2! Dha Ge ( 2 Ti Re Ki Te ) ) x2";
		RepresentableSequence seq = parser.parseUnits(null, input);
		assertEquals(17, seq.size());
		RepresentableSequence subseq = parser.buildInnermostSubsequence(seq);		
		assertEquals(7, subseq.size());
		assertEquals(seq.toStringAll(),11, seq.size());

		RepresentableSequence subseq2 = parser.buildInnermostSubsequence(seq);
		assertEquals(6, subseq2.size());
		assertEquals(6, seq.size());

		//none left to build now
		assertNull(parser.buildInnermostSubsequence(seq));
		assertNull("assumed no more wrappable inner subsequences in " + subseq.toStringAll(),parser.buildInnermostSubsequence(subseq));
		assertNull(parser.buildInnermostSubsequence(subseq2));

		///now alltogether
		seq = parser.parseUnits(null, input);
		parser.buildSubsequences(seq);
		assertNull(parser.buildInnermostSubsequence(seq));
		assertEquals(6, seq.size());
		assertEquals(Representable.SEQUENCE, seq.get(4).getType());
		subseq = (RepresentableSequence) seq.get(4);
		assertEquals(Representable.SEQUENCE, subseq.get(4).getType());
		assertEquals(6,subseq.size());


	}

	@Test
	public void testKardinality() {
		SequenceParser parser = new SequenceParser(0,null);

		//first input
		String input = " Dha Ge Dhin Na ( 2! Dha Ge ( 2 Ti Re Ki Te ) ) x2";
		RepresentableSequence seq = parser.parseUnits(null, input);
		parser.buildSubsequences(seq);
		assertEquals(6, seq.size());
		RepresentableSequence subseq = parser.packOneWholeLineWithKardinalityModifier(seq);
		assertNull(subseq);

		input = " Dha Ge Dhin Na x2<2\n\n" +
		" Ti Re x2 Ki Te\n" +
		" Dhin Na Ge Dhin Na x2 ";

		seq = parser.parseUnits(null, input);
		assertEquals(Representable.KARDINALITY_MODIFIER,seq.get(4).getType());
		assertEquals(Representable.LINE_BREAK,seq.get(5).getType());
		assertEquals(18, seq.size());
		subseq = parser.packOneWholeLineWithKardinalityModifier(seq);
		assertNotNull("parsing for kardinality on : " + seq.toStringAll() + " should give us a subseq.", subseq);
		assertEquals(4, subseq.size());

		RepresentableSequence subseq2 = parser.packOneWholeLineWithKardinalityModifier(seq);
		assertNotNull("parsing for kardinality on : " + seq.toStringAll() + " should give us a subseq2.", subseq2);
		assertEquals(5, subseq2.size());		

		Debug.temporary(this, seq.toStringAll());

		//second input
		input = " Dha Ge Dhin Na ( Dha Ge ) x2 \n " +
		" Ti Te x2 \n";
		seq = parser.parseUnits(null, input);
		assertEquals(14, seq.size());

		parser.buildSubsequences(seq);
		assertEquals(11, seq.size());

		subseq = parser.packOneWholeLineWithKardinalityModifier(seq);
		assertEquals(10, seq.size());
		assertEquals(2, subseq.size());
		assertNotNull(subseq);

		subseq = parser.packOneWholeLineWithKardinalityModifier(seq);
		assertNull(subseq);
	}

	@Test 
	public void completeTest() {
		String input[] = new String[]{
				"Dha Ge 2 Dhin! Na \"pronounced tirket!\" ( 2! ti re ki te ) x2<2\n" +
		"Dha ti (ki te ta ke) Dha x3"};
		SequenceParser parser = new SequenceParser(0, null);	

		for (int i=0; i < input.length; i++) {
			RepresentableSequence seq = parser.parseSequence(null, input[i]);
			for (int j = 0; j < seq.size();j++) {
				boolean iscorrect = false;
				try {
					iscorrect = isCorrect(seq.get(j),j);

				} catch(Exception e) {
					e.printStackTrace();
					iscorrect = false;

				}
				if (!iscorrect) fail("failed at position " + j + "., repr: " + seq.get(j));

			}
			//assertTrue(isCorrect(seq.get(j),j));
		}
	}


	private boolean isCorrect(Representable r, int i) {
		switch (i){
		case 0:
			return r.getType() == BOL;
		case 1:
			return r.getType() == BOL;
		case 2:
			SpeedUnit s = (SpeedUnit) r;
			return (s.isAbsolute());
		case 3:
			Bol b3 = (Bol) r;
			return b3.isEmphasized();
		case 4:
			return r.getType() == BOL;
		case 5:
			if (r.getType() != FOOTNOTE) return false;
			FootnoteUnit f= (FootnoteUnit) r;
			if (!f.getFootnoteText().equals("pronounced tirket!")) return false;
			return true;
		case 6:
			RepresentableSequence seq6 = (RepresentableSequence) r;
			return seq6.size() == 7;
		case 7:
			return r.getType() == KARDINALITY_MODIFIER;
		case 8:
			return r.getType() == LINE_BREAK;
		case 9:
			RepresentableSequence seq7 = (RepresentableSequence) r;
			return seq7.size() == 4;
		case 10:
			return r.getType() == KARDINALITY_MODIFIER;
		default:
			return false;
		}
	}

	@Test
	public void testErrorHandling() {
		String input = "### dha ###";
		
		/*SequenceParser parser = new SequenceParser(0,null);

		//first input
		String input = " Dha Ge Dhin Na ( 2! Dha Ge ( 2 Ti Re Ki Te ) ) x2";
		RepresentableSequence seq = parser.parseUnits(null, input);	*/
	}


}
