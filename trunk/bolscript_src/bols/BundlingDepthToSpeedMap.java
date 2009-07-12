package bols;

import java.util.ArrayList;

import basics.Rational;

/**
 * This class contains a mapping of bundling depths to bundling speeds.
 * It is used in CompositionPanel and Config.
 * @author hannes
 */
public class BundlingDepthToSpeedMap {
	
	private int maxBundlingDepth;
	//min bundlingdepth is always 0
	
	private Rational[] bundlingSpeeds;
	
	
	public BundlingDepthToSpeedMap(int maxBundlingDepth, Rational[] bundlingSpeeds) {
		super();
		this.maxBundlingDepth = maxBundlingDepth;
		this.bundlingSpeeds = bundlingSpeeds;
	}
	

	public int getMaxDepth () {
		return maxBundlingDepth;
	}
	
	public Rational getBundlingSpeed(int depth) {
		return bundlingSpeeds[depth];
	}
	
	/**
	 * Generates a BundlingOption by consequently halving the passed maximumspeed.
	 * @param maximumSpeedOccurring
	 * @return
	 */
	public static BundlingDepthToSpeedMap getDefault(Rational maximumSpeedOccurring) {
		ArrayList<Rational> bundlingSpeeds = new ArrayList<Rational>();

		bundlingSpeeds.add(maximumSpeedOccurring); // <-> depth=0 means no bundling
		
		int currentSpeed = maximumSpeedOccurring.integerPortion();
		int maxDepth = 0;
		currentSpeed /= 2;
		while (currentSpeed > 0) {
			maxDepth++;
			bundlingSpeeds.add(new Rational(currentSpeed));
			currentSpeed /= 2;
		}
		
		Rational[] bundlingSpeedsArray = new Rational[bundlingSpeeds.size()];
		bundlingSpeedsArray = bundlingSpeeds.toArray(bundlingSpeedsArray);
		
		return new BundlingDepthToSpeedMap(maxDepth, bundlingSpeedsArray);
		
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append ("BundlingMap with maxBundlingDepth: " + maxBundlingDepth + "\n");
		for (int i = 0; i <= maxBundlingDepth;i++) {
			s.append(i + " -> " + bundlingSpeeds[i] + "\n");
		}
		return s.toString();
	}
}
