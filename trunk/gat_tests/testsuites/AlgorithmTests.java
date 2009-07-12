package testsuites;

import junit.framework.Test;
import junit.framework.TestSuite;
import algorithm.AlgorithmTest;
import algorithm.IndividualTest;
import algorithm.RouletteSegmentTest;
import algorithm.RouletteWheelTest;
import algorithm.interpreters.KaidaInterpreterTest;
import algorithm.mutators.MutatorTests;
import algorithm.raters.RaterTests;

public class AlgorithmTests {


	public static Test suite() {
		TestSuite suite = new TestSuite("Test for Algorithm and Raters");
		//$JUnit-BEGIN$
		suite.addTest(RaterTests.suite());
		
		suite.addTestSuite(IndividualTest.class);	
		
		suite.addTestSuite(KaidaInterpreterTest.class);
		
		suite.addTestSuite(RouletteSegmentTest.class);
		suite.addTestSuite(RouletteWheelTest.class);
		
		suite.addTest(MutatorTests.suite());
		
		suite.addTestSuite(AlgorithmTest.class);			
		
		//$JUnit-END$
		return suite;
	}

}
