package bols.tals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import basics.Debug;
import bolscript.scanner.Parser;

/**
 * Stores a number of layoutCylces, which can be retrieved according to
 * the number of cells per row and a wanted limit.
 * @see getLayoutCycle(int, int)
 * @author hannes
 *
 */
public class LayoutChooser {
	/**
	 * the number of matras (beats) in the tal
	 */
	private int matrasInTal;
	private int biggestDescribedLayout = 0;
	private HashMap<Integer, LayoutCycle> allCachedLayoutCycles; 

	//a list that has to go from 1 to matrasintal at least
	private ArrayList<LayoutCycle> storedLayoutCycles;


	public LayoutChooser (ArrayList<LayoutCycle> layoutCycles, int matrasInTal) {
		this.storedLayoutCycles = layoutCycles;	
		this.matrasInTal = matrasInTal;
		this.biggestDescribedLayout = layoutCycles.size()-1;

		allCachedLayoutCycles = new HashMap<Integer,LayoutCycle>(32);
	}

	public LayoutCycle getLayoutCycle(int maxDisplayableCellsPerRow, int maxCellsPerRowWanted) {

		int n= Math.max(1, Math.min(maxDisplayableCellsPerRow, maxCellsPerRowWanted));		
		return getLayoutCycle(n);

	}

	private LayoutCycle getLayoutCycle(int n) {
		if (allCachedLayoutCycles.containsKey(n)) {
			return allCachedLayoutCycles.get(n);
		} else {
			LayoutCycle chosenCycle;
			if (((n/matrasInTal)>=1) && (biggestDescribedLayout < n)) {
				//auto mode for sizes bigger than one tal cycle
				//generate a new one				
				chosenCycle = new LayoutCycle((n/matrasInTal)*matrasInTal);				
			} else {
				// choose from the list
				chosenCycle =  storedLayoutCycles.get(n);
			}
			allCachedLayoutCycles.put(n,chosenCycle);
			return chosenCycle;

		}
	}

	
	/**
	 * Searches for the first layoutCycle which has a shorter largest row than the given one.
	 * @param currentCycle
	 * @return
	 */
	public LayoutCycle getNextSmallerCycle(LayoutCycle currentCycle) {

		int currentMaxCellsPerRow = currentCycle.getMaxRowLength();
		if (currentMaxCellsPerRow<=1) {
			return currentCycle;
		}		
		int allowedCellsPerRow = Math.max(1,currentMaxCellsPerRow-1);		
		LayoutCycle nextCycle = null;
		int counter = 0;
		
		while (nextCycle == null && counter < 1000 && allowedCellsPerRow >=1) {
			LayoutCycle potentiallyNextCycle = getLayoutCycle(allowedCellsPerRow);
			
			if (potentiallyNextCycle.getMaxRowLength() < currentMaxCellsPerRow) {
				nextCycle = potentiallyNextCycle;
			} else {
				allowedCellsPerRow--;
			}
			counter++;
		}
		return nextCycle;
	}
	
	/**
	 * Searches for the first layoutCycle which has a longer largest row than the given one.
	 * @param currentCycle
	 * @return
	 */
	public LayoutCycle getNextLargerCycle(LayoutCycle currentCycle) {

		int currentMaxCellsPerRow = currentCycle.getMaxRowLength();
		int allowedCellsPerRow = currentMaxCellsPerRow+1;
		LayoutCycle nextCycle = null;
		int counter = 0;
		while (nextCycle == null && counter < 1000) {
			LayoutCycle potentiallyNextCycle = getLayoutCycle(allowedCellsPerRow);
			if (potentiallyNextCycle.getMaxRowLength() > currentMaxCellsPerRow) {
				nextCycle = potentiallyNextCycle;
			} else {
				allowedCellsPerRow++;
			}
			counter++;
		}
		return nextCycle;
	}








/**
 * Generates a layoutChooser from a String of the Form found in a bolbase Layout packet.
 * as found in the existing tal bolscript files.
 * @param input
 * @param matrasInTal
 * @return
 */
public static LayoutChooser fromString (String input, int matrasInTal) {
	//System.out.println(matrasInTal);
	ArrayList<LayoutCycle> layoutCycles = new ArrayList<LayoutCycle>(matrasInTal);

	for (int i=0; i <= matrasInTal; i++) {
		layoutCycles.add(null);
	}

	String regex = Parser.SN + "*(\\d+)\\s*" +
	"((?:\\d+\\s*,\\s*)*\\d+\\s*)";
	Matcher m = Pattern.compile(regex).matcher(input);


	while (m.find()) {
		int index = Integer.parseInt(m.group(1));
		if (index >= layoutCycles.size()) {
			for (int i=layoutCycles.size(); i < index; i++) {
				layoutCycles.add(null);
			}			
			layoutCycles.add(LayoutCycle.fromString(m.group(2)));
		} else {
			layoutCycles.set(index, LayoutCycle.fromString(m.group(2)));
		}


	}

	LayoutCycle nextSmallerExisting = new LayoutCycle(1);

	for (int i=0; i< layoutCycles.size();i++) {
		if (layoutCycles.get(i)==null){
			layoutCycles.set(i, nextSmallerExisting);
		} else {
			nextSmallerExisting = layoutCycles.get(i);
		}
	}

	return new LayoutChooser(layoutCycles, matrasInTal);
}

public String toString () {
	StringBuffer s = new StringBuffer();

	for (int i = 0; i < storedLayoutCycles.size(); i++) {
		s.append(i + " -> " + storedLayoutCycles.get(i).toString() + "\n");
	}

	return s.toString();
}


}
