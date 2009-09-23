package bols;

import junit.framework.TestCase;

public class BolBaseOldTests extends TestCase {



	public BolBaseOldTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'bols.BolBase.BolBase()'
	 */
	public void testBolBase() {
		BolBase bolBase;
		try {
			 bolBase = new BolBase();
		} catch (Exception e) {
			fail("BolBase threw an Exception when running Constructor");
		}
	}
	
	public void testEmptyBol() throws Exception {
		BolBase bolBase = new BolBase();
		
		BolName emptyBol = bolBase.getEmptyBol();
		assertNotNull("emptyBol should be no null", emptyBol);
		assertEquals("empty bolname.name should be - ", "-",  emptyBol.getNameShort());
		
	
	}
	
	public void testBolDiffs() throws Exception {
		BolBase bb = new BolBase();
		BolName emptyBol = bb.getEmptyBol();
		assertEquals("Difference between the same Bol should be 0",0d, bb.getDifference("Dha", "Dha"));
		assertEquals("Difference should be commutative",bb.getDifference("Ge", "Dha"), bb.getDifference("Dha", "Ge"));
		assertTrue("Difference between Dha,Ge should be smaller than Dha,Ke ",bb.getDifference("Ge", "Dha") < bb.getDifference("Dha", "Ke"));
		assertTrue("Difference between Na,- should be smaller than Dha,- ",bb.getDifference("Na", "-") < bb.getDifference("Dha", "-"));
		System.out.println("diff(Dha,Ge)=" + bb.getDifference("Dha", "Ge") + ", diff(Dha, Ke)="+ bb.getDifference("Dha", "Ke"));
		//System.out.println("diff(Dha,Na)=" + bb.getDifference("Dha", "Na") + ", diff(Dhin, Tin)="+ bb.getDifference("Dhin", "Tin"));		
		//System.out.println("diff(Dha,Ti)=" + bb.getDifference("Dha", "Ti") + ", diff(Ti, Ke)="+ bb.getDifference("Ti", "Ke"));		
	}

	public void testKaliMaps() throws Exception {
		BolBase bb = new BolBase();
		
		BolName bn1 = bb.getBolName("Dha");
		BolName bn2 = bb.getBolName("Na");
		BolName bn3 = bb.getBolName("Ge");
		BolName bn4 = bb.getBolName("Ke");
		
		assertEquals("KaliMap of Ge should be Ke", bn4, bb.getKaliBolName(bn3));
		assertEquals("KaliMap of Dha should be Na", bn2, bb.getKaliBolName(bn1));
		//assertEquals("KaliMap of Ta should be Ta", bn2, bb.getKaliBolName(bn2));
		
		
		
		
	}
}
