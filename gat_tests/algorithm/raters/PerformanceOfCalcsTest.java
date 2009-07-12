package algorithm.raters;

import junit.framework.TestCase;
import algorithm.tools.Timer;
import bols.BolBase;

public class PerformanceOfCalcsTest extends TestCase {
	private static double lg2precalced = Math.log(2);
	BolBase bb;
	
	public void setUp() throws Exception {
		super.setUp();
		bb = new BolBase();
	}
	
	public void testClassStuff () {
		long nrOfRuns = 1000000;
		Timer t0 = new Timer("doing nothing");
		t0.start();
		int j=1;
		for (long i=0; i<nrOfRuns;i++) {
			//double nr = Math.log(j) / Math.log(2);
		}
		t0.stopAndPrint();
		Rater rater = new RaterAverageSpeed(bb);
		Rater rater2 = new RaterAverageSpeed(bb);
				
		Timer t1 = new Timer("comparing rater with rater");
		t1.start();
		
		for (long i=0; i<nrOfRuns;i++) {
			if (rater == rater2) {
				;
			}
		}
		t1.stopAndPrint();
		
		Timer t2 = new Timer("comparing rater.getClass with class");
		t2.start();
		
		Class raterClass = RaterAverageSpeed.class;
		
		for (long i=0; i<nrOfRuns;i++) {
			if (rater.getClass() == raterClass) {
				;
			}
		}
		t2.stopAndPrint();
		
		Class raterClass2 = RaterAverageSpeed.class;
		Timer t3 = new Timer("comparing Class with Class");
		t3.start();
		for (long i=0; i<nrOfRuns;i++) {
			if (raterClass2 == raterClass) {
				;
			}
		}
		t3.stopAndPrint();
		
		
		Timer t4 = new Timer("comparing .class with Class");
		t4.start();
		for (long i=0; i<nrOfRuns;i++) {
			if (RaterAverageSpeed.class == raterClass) {
				;
			}
		}
		t4.stopAndPrint();
	}
	
	public void testLog2() {
		long nrOfRuns = 10000;
		Timer t0 = new Timer("doing nothing");
		t0.start();
		int j=1;
		for (long i=0; i<nrOfRuns;i++) {
			//double nr = Math.log(j) / Math.log(2);
		}
		t0.stopAndPrint();
		
		Timer t1 = new Timer("using private double log2");
		t1.start();
		
		for (long i=0; i<nrOfRuns;i++) {
			double nr = log2(j);
		}
		t1.stopAndPrint();
		
		Timer t2 = new Timer("using inline");
		t2.start();

		for (long i=0; i<nrOfRuns;i++) {
			double nr = Math.log(j) / Math.log(2);
		}
		t2.stopAndPrint();
		
		Timer t3 = new Timer("using log2faster");
		t3.start();
		for (long i=0; i<nrOfRuns;i++) {
			double nr = log2faster(j);
		}
		t3.stopAndPrint();
		
		
		Timer t4 = new Timer("using inline precalced log2");
		t4.start();
		double lg2 = Math.log(2);
		for (long i=0; i<nrOfRuns;i++) {
			double nr = Math.log(j) / lg2;
		}
		t4.stopAndPrint();
	}
	
	private double log2( double x ) {
		  return Math.log(x) / Math.log(2);
	}
	
	private double log2faster(double x) {
		return Math.log(x) / lg2precalced;
	}
	
	
}

