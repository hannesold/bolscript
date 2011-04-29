package bolscript.filters;

import java.util.ArrayList;

import basics.Debug;
import basics.GUI;
import bolscript.compositions.Composition;

public class FullTextSearchFilter extends StringArrayFilter implements Filter {
	
	public FullTextSearchFilter () {
		super();
	}
	
	@Override
	protected ArrayList<String> getSamples(Composition comp) {
		ArrayList<String> samples = new ArrayList<String> ();
		samples.add(comp.getFulltextSearchString());
		return samples;
	}

	/**
	 * Returns true if at least one of the sample strings contained one of the search patterns.
	 */
	@Override
	protected boolean accepts(ArrayList<String> samples, String[] searchPatterns) {
		if (acceptAll) return true;
		
		for (int i=0; i< searchPatterns.length; i++) {
			//Debug.debug(this, "comparing " + searchPatterns[i] + "...");
			for (int j=0; j < samples.size();j++) {
				//Debug.debug(this, "with " + samples.get(j));
				if (samples.get(j) != null) {
					if (samples.get(j).toLowerCase().contains(searchPatterns[i].toLowerCase())) {
						return true;
					}
				}
			}	
		}
		return false;
		
	}
	
	public String getFilterHeader() {
		return "Fulltext Search";
	}


}
