package bolscript.filters;

import java.util.ArrayList;
import java.util.Collections;

import basics.Debug;
import basics.Tools;
import bolscript.compositions.Composition;

public abstract class StringArrayFilter implements Filter, VisibleCompositionDonator{
	protected boolean acceptAll = false;
	protected VisibleCompositionDonator source;
	
	/**
	 * Stores the compositions that were accepted by the filter.
	 */
	protected ArrayList<Composition> filteredCompositions;
	
	/**
	 * Stores the strings, that this particular filter collected from input.
	 */
	protected ArrayList<String> collectedStrings;
	
	
	public StringArrayFilter() {
		acceptAll = true;
		filteredCompositions = new ArrayList<Composition>();
		collectedStrings = new ArrayList<String>();
	}
	
	/**
	 * this HAS TO BE RUN if the input-free methods filter and collect shall be used!
	 * @param donator The provider of the compositions that shall be treated.
	 */
	public void setCompositionSource(VisibleCompositionDonator donator) {
		this.source = donator;
	}
	
	/**
	 * Returns true if the Filter is set to accept any composition, even if it is not accepted by the Filters actual algorithm.
	 * @return
	 */
	public boolean acceptsAll() {
		return acceptAll;
	}

	/**
	 * Sets the Filter to accept all compositions, even if it is not accepted by the Filters actual algorithm.
	 */
	public void setAcceptAll(boolean acceptAll) {
		this.acceptAll = acceptAll;
	}
	

	/**
	 * Returns true if at least the one of the samples matches one of the search patterns.
	 * @param Samples An arraylist in which to be searched.
	 * @param searchPatterns An array of strings for which is searched.
	 * @return
	 */
	protected boolean accepts(ArrayList<String> samples, String []searchPatterns) {
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

	/**
	 * Filters the input from its source (VisibleCompositionDonator) and makes the result
	 * accessible by getVisibleCompositions()
	 * @param searchPattern
	 */
	public ArrayList<Composition> filter(String[] searchPattern) {
		return filter(source.getVisibleCompositions(), searchPattern);
	}
	
	/**
	 * Sets the filterResult to all available visible compositions
	 * given by the filters source. All internal filtering is ignored.
	 */
	public void runBypass() {
		if (source != null) {
			filteredCompositions = source.getVisibleCompositions();
		}
	}
	
	/**
	 * Returns an ArrayList of all compositions that match the search pattern.
	 * @param input
	 * @param searchPattern The search pattern. An Array of Strings of which at least one has to be found in the sample that the filter extracts from a composition for it to be accepted.
	 */
	private ArrayList<Composition> filter(ArrayList<Composition> input, String[]searchPattern) {
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
	 * Collects an ArrayList of all unique Strings which were found in the compositions 
	 * that are provided by the filters source (VisibleCompositionDonator).
	 * @param andSort
	 */
	public ArrayList<String> collectStringSamples(boolean andSort) {
		return collectStringSamples(source.getVisibleCompositions(), andSort);
	}
	
	/**
	 * Returns an ArrayList of all unique Strings samples which were found in the given compositions.
	 * @param input The compositions to search in.
	 * @param andSort 
	 * @return
	 */
	protected ArrayList<String> collectStringSamples(ArrayList<Composition> input, boolean andSort) {
		ArrayList<String> collection = new ArrayList<String>();
		for (Composition c: input) {
			if (getSamples(c) != null) {
			for (String sample: getSamples(c)){
				boolean alreadyContained = false;
				for (String existing : collection) {
					if (existing.equalsIgnoreCase(sample)) {
						alreadyContained = true;
						break;
					}
				}
				if (!alreadyContained) {
					collection.add(sample);
				}				
			}
			}
		}
		
		if (andSort) Collections.sort(collection);
		collectedStrings = collection;
		return collectedStrings;
	}
	
	/**
	 * Returns the Strings that the filter collected from the compositions provided by its source during the last call of the collect method.
	 */
	public ArrayList<String> getCollectedStringSamples() {
		return collectedStrings;
	}

	/**
	 * Returns the composition which were accepted by the filter during the last call of the filter method.
	 * @see filter(String[])
	 */
	public ArrayList<Composition> getVisibleCompositions() {
		return filteredCompositions;
	}

	/**
	 * Returns a list the strings which will be used for comparison with the searchpattern.
	 * @param comp
	 * @return
	 */
	protected abstract ArrayList<String> getSamples(Composition comp);

	
	
}
