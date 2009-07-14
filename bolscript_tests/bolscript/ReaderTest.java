package bolscript;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


import basics.Debug;
import basics.Rational;
import bols.BolBase;
import bols.BolName;
import bolscript.Reader;
import bolscript.config.Config;
import bolscript.packets.Packets;
import bolscript.sequences.RepresentableSequence;

public class ReaderTest {

	BolBase bolBase;
	
	@Before
	public void setUp() throws Exception {
		Config.init();
		BolBase.init(this.getClass());
		bolBase = BolBase.getStandard();
	}
	
	@Ignore
	public void testAlterSpeeds() throws Exception{
	String s = " 2 [ 2 [ 3 ] [ 3! ] ] <2 1 ";
	int outerspeed = 1;
	String t = " 1! 2! [ 4! [ 12! ] 4! [ 3! ] 4! ] 2! <2 1! ";
	assertEquals(Reader.makeSpeedsAbsolute(s, new Rational(outerspeed)),t);
	
	String c = " 3! 2! ";
	outerspeed = 1;
	String d = " 1! 3! 2! ";
	assertEquals(Reader.makeSpeedsAbsolute(c, new Rational(outerspeed)),d);

	String a = " Dha Ge 2 Ti Te 1 Dha [ 2 Ti Ri Ki Te ] 3! Dha Ti Te Dhi Ti Te 1 Dha ";
	outerspeed = 2;
	String b = " 2! Dha Ge 4! Ti Te 2! Dha [ 4! Ti Ri Ki Te ] 2! 3! Dha Ti Te Dhi Ti Te 2! Dha ";
	assertEquals(Reader.makeSpeedsAbsolute(a, new Rational(outerspeed)),b);
	
	String e = " Dha 2! Ge [ 2 Dha Ge [ 2 Dha Ge [ 2 Dha Ge [ 2! Dha Ge ] ] ] ] ";
	outerspeed = 2;
	String f = " 2! Dha 2! Ge [ 4! Dha Ge [ 8! Dha Ge [ 16! Dha Ge [ 2! Dha Ge ] 16! ] 8! ] 4! ] 2! ";
	
	assertEquals(Reader.makeSpeedsAbsolute(e, new Rational(outerspeed)), f);
	
	String g = "[ Dha! Ge Ti Ri Ki Te Dha! - - - Ti! Ri Ki Te Dha! -  [ Ti Re Ki Te Dha Ge Ti Te Gi! Re Na Ge Dhin Na Ge Na Dha! - - - - - - - ]  [ Ti Re Ki Te Dha Ge Ti Te Gi! Re Na Ge Dhin Na Ge Na Dha! - - - - - - - ]  [ Ti Re Ki Te Dha Ge Ti Te Gi! Re Na Ge Dhin Na Ge Na Dha! - - - - - - - ]  ]  [ Dha! Ge Ti Ri Ki Te Dha! - - - Ti! Ri Ki Te Dha! -  [ Ti Re Ki Te Dha Ge Ti Te Gi! Re Na Ge Dhin Na Ge Na Dha! - - - - - - - ]  [ Ti Re Ki Te Dha Ge Ti Te Gi! Re Na Ge Dhin Na Ge Na Dha! - - - - - - - ]  [ Ti Re Ki Te Dha Ge Ti Te Gi! Re Na Ge Dhin Na Ge Na Dha! - - - - - - - ]  ]  [ Dha! Ge Ti Ri Ki Te Dha! - - - Ti! Ri Ki Te Dha! -  [ Ti Re Ki Te Dha Ge Ti Te Gi! Re Na Ge Dhin Na Ge Na Dha! - - - - - - - ]  [ Ti Re Ki Te Dha Ge Ti Te Gi! Re Na Ge Dhin Na Ge Na Dha! - - - - - - - ]  [ Ti Re Ki Te Dha Ge Ti Te Gi! Re Na Ge Dhin Na Ge Na Dha! - - - - - - - ]  ] <7 ";
	outerspeed = 2;
	String h = Reader.makeSpeedsAbsolute(g, new Rational(outerspeed));
	//System.out.println(Reader.makeSpeedsAbsolute(g, new Rational(2)));
	}
	
	@Test
	public void testInsertingOfBolBaseStandardReplacements() {
		
		Packets packets = Reader.compilePacketsFromString(" Theme: Dha tIr kit tir kit Dha ", bolBase);
		Debug.out(packets);	
	}
	
	@Ignore
	public void testRepresentableSequence() throws Exception {
		BolBase b = new BolBase();
		String input = " 1! Dha Ge Ti Te [ 2! Ti Ri Ki Te ]x2 1! "+ Reader.getFootnoteCode(1, 1, 2) + " Dha Ti Ge Na ";
		Rational basicSpeed = new Rational(2);
		
		String s = "[ Dha! Ge Ti Ri Ki Te Dha! - - - Ti! Ri Ki Te Dha! -  " +
				"[ Ti Re Ki Te Dha Ge Ti Te Gi! Re Na Ge Dhin Na Ge Na Dha! - - - - - - - ]  " +
				"[ Ti Re Ki Te Dha Ge Ti Te Gi! Re Na Ge Dhin Na Ge Na Dha! - - - - - - - ]  " +
				"[ Ti Re Ki Te Dha Ge Ti Te Gi! Re Na Ge Dhin Na Ge Na Dha! - - - - - - - ]  ]  " +
				"[ Dha! Ge Ti Ri Ki Te Dha! - - - Ti! Ri Ki Te Dha! -  " +
				"[ Ti Re Ki Te Dha Ge Ti Te Gi! Re Na Ge Dhin Na Ge Na Dha! - - - - - - - ]  " +
				"[ Ti Re Ki Te Dha Ge Ti Te Gi! Re Na Ge Dhin Na Ge Na Dha! - - - - - - - ]  " +
				"[ Ti Re Ki Te Dha Ge Ti Te Gi! Re Na Ge Dhin Na Ge Na Dha! - - - - - - - ]  ]  " +
				"[ Dha! Ge Ti Ri Ki Te Dha! - - - Ti! Ri Ki Te Dha! -  " +
				"[ Ti Re Ki Te Dha Ge Ti Te Gi! Re Na Ge Dhin Na Ge Na Dha! - - - - - - - ]  " +
				"[ Ti Re Ki Te Dha Ge Ti Te Gi! Re Na Ge Dhin Na Ge Na Dha! - - - - - - - ]  " +
				"[ 4! Ti Re Ki Te Dha Ge Ti Te Gi! Re Na Ge Dhin Na Ge Na Dha! - - - - - - - ]  ] <7 ";
		
		input = Reader.makeSpeedsAbsolute(s, new Rational(2));
		//System.out.println(input);
		RepresentableSequence seq = Reader.getRepresentableSequence(input, new Rational(1), b);
		System.out.println(seq.toString(true,true,true,true, true, true, BolName.EXACT));
		
		
		
	}
	
	
	
	
		/*String s = new String ("2 3 [ 2/3 [ 2 ] 2! 1 [ 3 ] ] [ 2 ]");
		
		String t = Reader.alterSpeeds(s, new Rational(1,1));
		assertEquals(t,"2! 3 [ 4/3! [ 8/3! ] 4! 2! [ 6! ] ] [ 4! ]");
*/
	

}
