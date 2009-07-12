package testsuites;

import junit.framework.Test;
import junit.framework.TestSuite;
import basics.BasicsTests;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("All Tests");
		//$JUnit-BEGIN$
		suite.addTest(BolTests.suite());
		suite.addTest(BasicsTests.suite());
		suite.addTest(AlgorithmTests.suite());
		//$JUnit-END$
		return suite;
	}

}
