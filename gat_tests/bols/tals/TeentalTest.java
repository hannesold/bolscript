package bols.tals;

import org.junit.Test;

import basics.Debug;
import bols.BolBase;
import bols.BolSequence;
import bols.Variation;

public class TeentalTest {

	@Test
	public void testGetThekaAsVariation() {
		Tal tt = new Teental(BolBase.getStandard());
		Debug.out(tt.getTheka());
		Debug.out("bols: " + tt.getTheka().getBols());
		Debug.out("bolseq: " + new BolSequence(tt.getTheka()));
		Debug.out(Variation.fromTal(tt));
	}

}
