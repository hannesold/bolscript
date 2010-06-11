package bolscript;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import basics.Debug;
import basics.Rational;
import bols.BolBase;
import bols.BolName;
import bols.tals.Teental;
import bolscript.config.Config;
import bolscript.packets.Packet;
import bolscript.packets.Packets;
import bolscript.packets.types.PacketTypeDefinitions;
import bolscript.scanner.Parser;
import bolscript.sequences.RepresentableSequence;

public class ReaderTest {

	BolBase bolBase;
	
	@Before
	public void setUp() throws Exception {
		Debug.init();
		Config.init();
		BolBase.init(this.getClass());
		bolBase = BolBase.getStandard();
	}

	@Ignore
	public void testInsertingOfBolBaseStandardReplacements() {
		Packets packets = Parser.compilePacketsFromString(" Theme: Dha tIr kit tir kit Dha ");
		Debug.out(packets);	
	}
	
	@Ignore
	public void testSplitIntoPackets() {
		Packets packets = Parser.splitIntoPackets(Teental.TEENTAL);
	}
	
	@Ignore
	public void testFootNotes() {
		String s = "A: Dha ge ti re ki te\n B: Dhin Na Ge \"Footnote A for test\" Na";
		Packets packets = Parser.compilePacketsFromString(s);
		for (Packet p:packets) {
			if (p.getType() == PacketTypeDefinitions.FOOTNOTE) {
				assertEquals("A should remain and not be inserted", "Footnote A for test", p.getValue());
			}
		}
	}
	
	//public void te
	@Test
	public void testGetBolStringAroundCaret() {
		String testChars = "abcdefghijklmnop(){}//\\<4+!?\n";
		
		String input = "";
		for (int i=0; i<100;i++) {
			input += testChars.charAt((int)Math.round(Math.random()*((double)testChars.length()-1)));
		}
		assertEquals(100, input.length());
		int currentPosition = input.length();
		
		//currentPosition = input.length()-1;
		input += "  Dha Ge! Na (T ?!//&&%¤) DhaGe";
		assertEquals('D', input.charAt(currentPosition+2));
		
		int[]carretPositions 	= new int[]
		                     	          {2,3,4,5,	
											6,7,8,	
											10,11,12,
											14,15,
											22,
											30,31};
		String[]expectedResults = new String[]{"Dha","Dha","Dha","Dha",
											"Ge","Ge","Ge",
											"Na","Na","Na",
											"T","T",
											null,
											"DhaGe", "DhaGe"};
		int maxExperiments = 100;
		for (int i=0; i < Math.min(carretPositions.length,maxExperiments); i++) {
			int caretPosition =currentPosition+carretPositions[i];
			//Debug.temporary(this,"input at caretPosition is: " + input.charAt(caretPosition));
			String result = Parser.determineBolStringAroundCaret(input, caretPosition);
			assertEquals(expectedResults[i],result);
		}
		
		
		
		
		
	}
	
	@Test
	public void testNewCompiler() {
		String s = "Speed: 1\n" +
				"A: Dha Ge Ti Re Ki Te, Dha - Dha - Dha Ge Ti Re Ki Te\n" +
				"Speed: 2\n" +
				"B: A A Ge Dhin Na Ge Na x2\n";
		Packets packets = Parser.compilePacketsFromString(s);
		
	}
	

	
	
	
		/*String s = new String ("2 3 [ 2/3 [ 2 ] 2! 1 [ 3 ] ] [ 2 ]");
		
		String t = Reader.alterSpeeds(s, new Rational(1,1));
		assertEquals(t,"2! 3 [ 4/3! [ 8/3! ] 4! 2! [ 6! ] ] [ 4! ]");
*/
	

}
