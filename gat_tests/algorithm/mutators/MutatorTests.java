package algorithm.mutators;

import junit.framework.Test;
import junit.framework.TestSuite;

public class MutatorTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for algorithm.mutators");
		//$JUnit-BEGIN$
		suite.addTestSuite(MutatorDoublifierTest.class);
		suite.addTestSuite(MutatorPermutateTest.class);
		suite.addTestSuite(CrossOverOnePointTest.class);
		suite.addTestSuite(MutatorSpeedChangeTest.class);
		suite.addTestSuite(MutatorDoublifyAllTest.class);
		//$JUnit-END$
		return suite;
	}

}
