package bols;

import org.junit.Test;

import basics.Debug;
import basics.Rational;
import bols.BundlingDepthToSpeedMap;

public class BundlingDepthToSpeedMapTest {

	@Test
	public void testGetDefault() {
		Rational maxSpeed = new Rational(1);
		Debug.out(BundlingDepthToSpeedMap.getDefault(maxSpeed));
		
		maxSpeed = new Rational(2);
		Debug.out(BundlingDepthToSpeedMap.getDefault(maxSpeed));
		
		maxSpeed = new Rational(3);
		Debug.out(BundlingDepthToSpeedMap.getDefault(maxSpeed));
			
		maxSpeed = new Rational(8);
		Debug.out(BundlingDepthToSpeedMap.getDefault(maxSpeed));
	}

}
