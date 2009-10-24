package bols;

import java.util.ArrayList;
import java.util.HashMap;

import basics.Rational;
import bolscript.config.CacheClearable;

/**
 * This class contains a mapping of bundling depths to bundling speeds.
 * It is used in CompositionPanel and Config.
 * @author hannes
 */
public class BundlingDepthToSpeedMap {
	
	private int maxBundlingDepth;
	//min bundlingdepth is always 0
	
	private Rational[] bundlingSpeeds;

	/**
	 * A Map from maximum Speeds ocurring to depth-speed-maps.
	 */
	public static HashMap<Rational, BundlingDepthToSpeedMap> bundlingMaps;
	
	
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


	/**
	 * Returns a Map of bundling depths to bundling speeds, according to the given maxSpeed.
	 * @see BundlingDepthToSpeedMap
	 */
	public static BundlingDepthToSpeedMap getBundlingDepthToSpeedMap(Rational maxSpeed) {
		if (bundlingMaps == null) {
			BundlingDepthToSpeedMap.bundlingMaps = new HashMap<Rational, BundlingDepthToSpeedMap> ();
		}
		BundlingDepthToSpeedMap map = BundlingDepthToSpeedMap.bundlingMaps.get(maxSpeed);
		if (map == null) {
			map = getDefault(maxSpeed);
			BundlingDepthToSpeedMap.bundlingMaps.put(maxSpeed, map);
		}
		return map;
	}

}
