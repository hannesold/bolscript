package bols;

import static org.junit.Assert.*;

import org.junit.Test;


public class BolBaseTest {

	@Test
	public void testGetResembling() {
		BolBase bolBase = new BolBase();
		
		assertEquals(bolBase.getBolName("Ga"), bolBase.getResemblingBol("G"));
		assertEquals(bolBase.getBolName("Ge"), bolBase.getResemblingBol("Ge"));
		assertEquals(bolBase.getBolName("Dha"), bolBase.getResemblingBol("D"));
		assertEquals(bolBase.getBolName("Dha"), bolBase.getResemblingBol("Dh"));
		assertEquals(bolBase.getBolName("Dha2"), bolBase.getResemblingBol("Dha25"));
		assertEquals(bolBase.getBolName("Dha"), bolBase.getResemblingBol("Dhaa"));
	}
}
