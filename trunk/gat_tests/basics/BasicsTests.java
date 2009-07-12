package basics;

import junit.framework.Test;
import junit.framework.TestSuite;

public class BasicsTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for basics");
		//$JUnit-BEGIN$
		suite.addTestSuite(CalcTest.class);
		//$JUnit-END$
		return suite;
	}

}
