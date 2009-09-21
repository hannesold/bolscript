package algorithm;

import java.util.ArrayList;

import config.Themes;

import junit.framework.TestCase;
import algorithm.composers.kaida.KaidaComposer;
import algorithm.composers.kaida.Individual;
import algorithm.statistics.FitnessStats;
import algorithm.tools.Timer;
import bols.BolBase;
import bols.tals.Teental;

public class AlgorithmTest extends TestCase {


	public AlgorithmTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testAlgorithmNew() throws Exception {
		BolBase bolBase = new BolBase();
		KaidaComposer al = new KaidaComposer(bolBase, new Teental(), Themes.getTheme01(bolBase));
		assertEquals("After Constructor al should have 1 generation", 1l, al.getGenerationNr());
		try {
			ArrayList<Individual> gen1 = al.getCurrentGeneration();
		} catch (NullPointerException e) {
			fail("the generation should not be empty" + e.getMessage());
			
		}
	}
	
	public void testAlgorithmDoEvolution() throws Exception {
		BolBase bolBase = new BolBase();
		KaidaComposer al = new KaidaComposer(bolBase, new Teental(), Themes.getTheme01(bolBase));
		assertEquals("After Constructor al should have 1 generation", 1l, al.getGenerationNr());
		int generationCount = 1;
		//al.setDEBUG(true);
		int nrOfRuns = 50;
		Timer t = new Timer("" + nrOfRuns + "cycles");
		t.start();
		for (int i=0; i<nrOfRuns; i++) {
			try {
				if (al.isCycleCompleted()) {
					al.startNextCycle();
				}
				al.doOneEvolutionCycle();
			} catch (Exception e) {
				e.printStackTrace();
				fail("al threw some Exception");
				System.exit(1);
			}
			assertEquals("one generation should be added after each cycle",++generationCount,al.getGenerationNr());
		}
		t.stopAndPrint();
		System.out.println("Per cycle: " + ((double)t.getDuration() / (double)nrOfRuns) + "ms");
		assertTrue("The overall fitness after "+nrOfRuns+" iterations should be higher than after the first.",al.getGenerations().get(nrOfRuns-2).getStat(FitnessStats.SUMMEDFITNESS) > al.getGenerations().get(0).getStat(FitnessStats.SUMMEDFITNESS));
			
		
	}
	

}
