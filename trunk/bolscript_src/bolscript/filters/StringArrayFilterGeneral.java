package bolscript.filters;

import java.util.ArrayList;
import java.util.Collections;

import basics.Debug;
import basics.Tools;
import bolscript.compositions.Composition;

public abstract class StringArrayFilterGeneral implements Filter, VisibleCompositionDonator{
	protected boolean acceptAll = false;
	protected String name = "Unnamed filter";
	protected String[] searchPatterns = null;
	protected boolean recording = false;
	protected ArrayList<String> record = null;
	
	protected VisibleCompositionDonator source;
	
	/**
	 * Stores the compositions that were accepted by the filter.
	 */
	protected ArrayList<Composition> filteredCompositions;
	
	/**
	 * Stores the strings, that this particular filter collected from input.
	 */
	protected ArrayList<String> collectedStrings;
	
	
	public StringArrayFilterGeneral() {
		searchPatterns = new String[0];
		acceptAll = true;
		record = new ArrayList<String>();
		filteredCompositions = new ArrayList<Composition>();
		collectedStrings = new ArrayList<String>();
	}
	
	public StringArrayFilterGeneral(String[] searchPatterns) {
		this.searchPatterns = searchPatterns;
		acceptAll = true;
		record = new ArrayList<String>();
		filteredCompositions = new ArrayList<Composition>();
		collectedStrings = new ArrayList<String>();
	}
	
	public StringArrayFilterGeneral(String searchPattern) {
		this.searchPatterns = new String[]{searchPattern};
		acceptAll = true;
		record = new ArrayList<String>();
		filteredCompositions = new ArrayList<Composition>();
		collectedStrings = new ArrayList<String>();
	}
	
	/**
	 * this HAS TO BE RUN if the input-free methods filter and collect shall be used!
	 * @param donator
	 */
	public void setCompositionSource(VisibleCompositionDonator donator) {
		this.source = donator;
	}
	
	public boolean acceptsAll() {
		return acceptAll;
	}

	public void setAcceptAll(boolean acceptAll) {
		this.acceptAll = acceptAll;
	}
	
	public boolean in(Composition comp, ArrayList<String> samples) {
		if (acceptAll) return true;
		
		for (int i=0; i< searchPatterns.length; i++) {
			for (int j=0; j < samples.size();j++) {
				 if (searchPatterns[i].equalsIgnoreCase(samples.get(j))) {
					 return true;
				 }
			}	
		}
		return false;
	}
	
	public boolean accepts(Composition c) {
		return accepts(getSamples(c), searchPatterns);
	}
	
	/**
	 * Returns true if the one of the samples is found in one of the search patterns
	 * @param Samples an arraylist in which to be searched.
	 * @param searchPatterns
	 * @return
	 */
	public boolean accepts(ArrayList<String> samples, String []searchPatterns) {
		if (acceptAll) {
			//Debug.debug(this, "accepting all, so also " + samples);
			return true;
		}
		
		for (int i=0; i< searchPatterns.length; i++) {
			//Debug.debug(this, "comparing " + searchPatterns[i] + "...");
			for (int j=0; j < samples.size();j++) {
				//Debug.debug(this, "with " + samples.get(j));
				 if (searchPatterns[i].equalsIgnoreCase(samples.get(j))) {
					 return true;
				 }
			}	
		}
		return false;
	}

	public void resetRecording() {
		record = new ArrayList<String>();
		recording = false;
	}
	public void startRecording() {
		recording = true;
		
	}

	/**
	 * Filters the input from its source (VisibleCompositionDonator) and makes the result
	 * accessible by getVisibleCompositions()
	 * @param searchPattern
	 */
	public ArrayList<Composition> filter(String[] searchPattern) {
		return filter(source.getVisibleCompositions(), searchPattern);
	}
	
	public void bypass() {
		if (source != null) {
			filteredCompositions = source.getVisibleCompositions();
			
		}
	}
	
	/**
	 * Returnes an ArrayList of all compositions, which match the search pattern.
	 * @param input
	 * @param searchPattern
	 * @return
	 */
	public ArrayList<Composition> filter(ArrayList<Composition> input, String[]searchPattern) {
		if (!acceptAll) {
			ArrayList<Composition> filtered = new ArrayList<Composition>();
			for (Composition c: input) {
				if (accepts(getSamples(c), searchPattern)) {
					filtered.add(c);
					Debug.debug(this, "accepted " + Tools.toString(getSamples(c)));
				}
			}
			filteredCompositions = filtered;
		} else filteredCompositions = input;
		return filteredCompositions;
	}
	
	/**
	 * Collects an ArrayList of unique Strings which were found in the compositions 
	 * that were given by the source (VisibleCompositionDonator)
	 * @param andSort
	 */
	public ArrayList<String> collect(boolean andSort) {
		return collect(source.getVisibleCompositions(), andSort);
	}
	
	/**
	 * Returns an ArrayList of unique Strings which were found in the given compositions.
	 * @param input
	 * @param andSort 
	 * @return
	 */
	public ArrayList<String> collect(ArrayList<Composition> input, boolean andSort) {
		ArrayList<String> collection = new ArrayList<String>();
		for (Composition c: input) {
			if (getSamples(c) != null) {
			for (String sample: getSamples(c)){
				if (!collection.contains(sample)) {
					collection.add(sample);
				}
				
			}
			}
		}
		if (andSort) Collections.sort(collection);
		collectedStrings = collection;
		return collectedStrings;
	}
	
	public ArrayList<String> getCollectedStrings() {
		return collectedStrings;
	}

	/**
	 * Returns the composition which were accepted by the filter.
	 */
	public ArrayList<Composition> getVisibleCompositions() {
		return filteredCompositions;
	}

	/**
	 * Returns a list the strings which will be used for comparison with the searchpattern.
	 * @param comp
	 * @return
	 */
	public abstract ArrayList<String> getSamples(Composition comp);

	
	
}
