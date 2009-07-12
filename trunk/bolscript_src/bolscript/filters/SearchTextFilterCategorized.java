package bolscript.filters;

import java.util.ArrayList;

import basics.Debug;
import basics.Tools;
import bolscript.compositions.Composition;

public class SearchTextFilterCategorized extends StringArrayFilterGeneral implements Filter {


	
	public SearchTextFilterCategorized () {
		super();
	}
	
	public SearchTextFilterCategorized(String[] searchStrings){
		super(searchStrings);	
	}
	
	public SearchTextFilterCategorized(String searchString) {
		super(searchString);
	}
	
	@Override
	public ArrayList<String> getSamples(Composition comp) {
		ArrayList<String> samples = new ArrayList<String> ();
		samples.add(comp.getSnippet());
		samples.add(Tools.toString(comp.getTals()));
		samples.add(Tools.toString(comp.getGharanas()));
		samples.add(Tools.toString(comp.getTypes()));
		samples.add(Tools.toString(comp.getSpeeds()));
		samples.add(Tools.toString(comp.getComments()));
		samples.add(Tools.toString(comp.getKeys()));
		samples.add(comp.getName());
//		samples.add(comp.getLi)
		//samples.add(comp.getName());
		
		return samples;
	}

	@Override
	public boolean accepts(ArrayList<String> samples, String[] searchPatterns) {
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
		
		// TODO Auto-generated method stub
		
		//return super.accepts(samples, searchPatterns);
	}


}
