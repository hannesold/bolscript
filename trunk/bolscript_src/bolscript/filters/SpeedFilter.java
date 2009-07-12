package bolscript.filters;

import java.util.ArrayList;
import java.util.Collections;

import bolscript.compositions.Composition;

public class SpeedFilter extends StringArrayFilterGeneral implements Filter {

	public SpeedFilter () {
		super();
	}
	
	public SpeedFilter(String[] speeds){
		super(speeds);	
	}
	
	/**
	 * Sees a composition if it has a tal in a given array of tals
	 * @param tal: The tal.
	 */
	public SpeedFilter(String speed) {
		super(speed);
	}
	
	/**
	 * Returns an ArrayList of unique Strings which were found in the given compositions.
	 * @param comps
	 * @param andSort 
	 * @return
	 */
	public ArrayList<String> collect(ArrayList<Composition> comps, boolean andSort) {
		ArrayList collection = super.collect(comps, false);
		if (andSort) Collections.sort(collection, new RationalComparator());
		return collection;
	}
	
	@Override
	public ArrayList<String> getSamples(Composition comp) {
		return comp.getSpeeds();
	}
	
	
}
