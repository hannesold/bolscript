package bolscript.filters;

import java.util.ArrayList;
import java.util.Collections;

import bolscript.compositions.Composition;
import bolscript.packets.types.PacketTypeFactory;

public class SpeedFilter extends MetaValueFilter implements Filter {

	public SpeedFilter () {
		super(PacketTypeFactory.SPEED);
	}
	
	/**
	 * Returns an ArrayList of unique Strings which were found in the given compositions.
	 * @param comps
	 * @param andSort 
	 * @return
	 */
	public ArrayList<String> collectStringSamples(ArrayList<Composition> comps, boolean andSort) {
		ArrayList collection = super.collectStringSamples(comps, false);
		if (andSort) Collections.sort(collection, new RationalComparator());
		return collection;
	}
	
	
}
