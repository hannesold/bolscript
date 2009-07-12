package bolscript.filters;

import java.util.ArrayList;

import bols.tals.Tal;
import bolscript.compositions.Composition;

public class TalFilter extends StringArrayFilterGeneral implements Filter {

	
	public TalFilter () {
		super();
	}
	
	/**
	 * Sees a composition if it has a tal in a given array of tals
	 * @param tals: An array of tal names as strings.
	 */
	public TalFilter(String[] tals){
		super(tals);	
	}
	
	/**
	 * Sees a composition if it has a tal in a given array of tals
	 * @param tal: The tal.
	 */
	public TalFilter(String tal) {
		super(tal);
	}
	
	@Override
	public ArrayList<String> getSamples(Composition comp) {
		return comp.getTals();
	}


}
