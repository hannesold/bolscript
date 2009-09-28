package bolscript.sequences;

import static org.junit.Assert.*;

import org.junit.Test;

import basics.Rational;
import bolscript.scanner.SequenceToken;


public class SpeedUnitTest {

	@Test
	public void testParsing() {
		SequenceToken token= new SequenceToken(Representable.SPEED,"3!",1,1);
		Representable r = SpeedUnit.parseToken(token);
		SpeedUnit s = (SpeedUnit) r;
		assertEquals(new Rational(3), (Rational) s.getObject());
		assertTrue(s.isAbsolute());
		
		token = new SequenceToken(Representable.SPEED,"2",1,1);
		 r = SpeedUnit.parseToken(token);
			 s = (SpeedUnit) r;
			assertEquals(new Rational(2), (Rational) s.getObject());
			assertFalse(s.isAbsolute());
	}
}
