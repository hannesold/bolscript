package bolscript.filters;

import java.util.ArrayList;

import bols.tals.Tal;
import bolscript.compositions.Composition;

public class GharanaFilter extends StringArrayFilterGeneral implements Filter {



	public GharanaFilter () {
		super();
	}
	
	/**
	 * Sees a composition if it has a gharana in a given array of gharana
	 * @param tals: An array of gharana names as strings.
	 */
	public GharanaFilter(String[] gharanas){
		super(gharanas);
	}
	
	/**
	 * Sees a composition if it has a gharana in a given array of gharana
	 * @param tal: The gharana.
	 */
	public GharanaFilter(String gharana) {
		super(gharana);
	}
	
	@Override
	public ArrayList<String> getSamples(Composition comp) {
		return comp.getGharanas();
	}

}
