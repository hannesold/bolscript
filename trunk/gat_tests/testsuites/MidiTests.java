package testsuites;

import junit.framework.Test;
import junit.framework.TestSuite;
import midi.MidiStationTest;

public class MidiTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for bols and midi");
		//$JUnit-BEGIN$
		suite.addTestSuite(MidiStationTest.class);
		//$JUnit-END$
		return suite;
	}
}
