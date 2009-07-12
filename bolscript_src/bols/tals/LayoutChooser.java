package bols.tals;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import basics.Debug;
import bolscript.Reader;

/**
 * Stores a number of layoutCylces, which gan be retrieved according to
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
	
	//a list that has to go from 1 to matrasintal at least
	private ArrayList<LayoutCycle> layoutCycles;
	
	
	public LayoutChooser (ArrayList<LayoutCycle> layoutCycles, int matrasInTal) {
		this.layoutCycles = layoutCycles;	
		this.matrasInTal = matrasInTal;
		this.biggestDescribedLayout = layoutCycles.size()-1;
	}
	
	public LayoutCycle getLayoutCycle(int maxDisplayableCellsPerRow, int maxCellsPerRowWanted) {
		
		int n= Math.max(1, Math.min(maxDisplayableCellsPerRow, maxCellsPerRowWanted));
		//System.out.println("n = " + n);
		
		if (((n/matrasInTal)>=1) && (biggestDescribedLayout < n)) {
			//System.out.println("generating");
			//auto mode for sizes bigger than one tal cycle
			return new LayoutCycle((n/matrasInTal)*matrasInTal);
		} else {
			//System.out.println("choosing");
			return layoutCycles.get(n);
		}
		
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
		
		String regex = Reader.SN + "*(\\d+)\\s*" +
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
		
		for (int i = 0; i < layoutCycles.size(); i++) {
			s.append(i + " -> " + layoutCycles.get(i).toString() + "\n");
		}
		
		return s.toString();
	}
	
	
}
