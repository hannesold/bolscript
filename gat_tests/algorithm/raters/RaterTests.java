package algorithm.raters;

import junit.framework.Test;
import junit.framework.TestSuite;

public class RaterTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for algorithm.raters");
		//$JUnit-BEGIN$
		suite.addTestSuite(RaterSpeedStdDeviationTest.class);
		suite.addTestSuite(RaterSimilarEndTest.class);
		suite.addTestSuite(RaterAverageSpeedTest.class);
		suite.addTestSuite(FitnessRaterEuklidTest.class);
		suite.addTestSuite(RaterInnerRepetitivenessTest.class);
		suite.addTestSuite(RaterDifferentFromBeforeTest.class);
		//$JUnit-END$
		return suite;
	}

}
