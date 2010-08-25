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
	
	
	@Test
	public void testUpperLowerCase() {
		BolBase bolBase = new BolBase();
		bolBase.addBolName(new BolName("goppi"));
		bolBase.addBolName(new BolName("guppi"));
		bolBase.addBolName(new BolName("Guppi"));
		bolBase.addBolName(new BolName("gappi"));
		bolBase.addBolName(new BolName("Gappi"));
		bolBase.addBolName(new BolName("GAppi"));
		assertEquals(BolName.CaseSensitivityModes.None, bolBase.getBolName("goppi").getCaseSensitivityMode());
		assertEquals(BolName.CaseSensitivityModes.FirstLetter, bolBase.getBolName("guppi").getCaseSensitivityMode());
		assertEquals(BolName.CaseSensitivityModes.FirstLetter, bolBase.getBolName("Guppi").getCaseSensitivityMode());
		assertEquals(BolName.CaseSensitivityModes.ExactMatch, bolBase.getBolName("gappi").getCaseSensitivityMode());
		assertEquals(BolName.CaseSensitivityModes.ExactMatch, bolBase.getBolName("Gappi").getCaseSensitivityMode());
		assertEquals(BolName.CaseSensitivityModes.ExactMatch, bolBase.getBolName("GAppi").getCaseSensitivityMode());
		assertEquals(bolBase.getBolName("goppi"), bolBase.getResemblingBol("gop"));
		assertEquals(bolBase.getBolName("goppi"), bolBase.getResemblingBol("gop"));
		assertEquals(bolBase.getBolName("guppi"), bolBase.getResemblingBol("gup"));
		assertEquals(bolBase.getBolName("Guppi"), bolBase.getResemblingBol("Gup"));
		assertEquals(bolBase.getBolName("gappi"), bolBase.getResemblingBol("gap"));
		assertEquals(bolBase.getBolName("Gappi"), bolBase.getResemblingBol("Gap"));
		assertEquals(bolBase.getBolName("GAppi"), bolBase.getResemblingBol("GAp"));
		
	}
}
