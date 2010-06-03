package bols.tals;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stores a cyclic layout pattern like (3 cells per row, then 2 cells per row)
 * @author hannes
 *
 */
public class LayoutCycle {
	
	private static Pattern integerRegexp = Pattern.compile("\\d+");
	
	private int[] cycle;
	
	/**
	 * row, column coordinates for each cell
	 */
	private Point[] coordinates;
	
	private int maxRowLength = 0;
	
	public LayoutCycle() {
		super();
		cycle =null;
		maxRowLength = 0;
		coordinates = null;
	}
	
	public LayoutCycle(int[] cycle) {
		super();
		this.cycle = cycle;
		for (int i=0; i < cycle.length; i++) {
			maxRowLength = Math.max(maxRowLength, cycle[i]);
		}
		initCoordinates(cycle.length*maxRowLength);
	}
	
	public LayoutCycle(int constantCycleLength) {
		super();
		cycle = new int[] {constantCycleLength};
		maxRowLength = constantCycleLength;
		initCoordinates(cycle.length*maxRowLength);
		
	}
	
	public int getRowLength(int rowNumber) {
		return cycle[rowNumber % cycle.length];
	}
	
	public int getMaxRowLength() {
		  return maxRowLength;
	}
	
	public void initCoordinates(int upToCellNr) {
		coordinates = new Point[1];
		coordinates[0] = new Point(0,0);
		getCoordinates(upToCellNr);
	}
	
	/**
	 * <p>Returns the row and column of a cell when using this layout cycle.<br>
	 * <b>The first cell is addressed by cell number 0.</b></p>
	 * <p><b>Examples:</b><br>If the layout cycle is (3 cells in first row, 2 cells in second row),
	 * then getCoordinates(0) returns (0,0), getCoordinates(4) returns (0,1), etc.</p>
	 * @param cellNumber
	 * @return
	 */
	public Point getCoordinates(int cellNumber) {
		
		if (cellNumber < coordinates.length) {
			//if the coordinates for the cell are already cached they are returned
			return coordinates[cellNumber];
		} else {
			//else the coordinate cache is enlarged 
			//(a little too big, just to be prepared for the future...)
			Point[] newCoords = new Point[cellNumber + 100];
			
			//the existing cache is copied
			for (int i=0; i< coordinates.length;i++) {				
				newCoords[i] = coordinates[i];
			}
			
			//the remaining cache is generated
			for (int i=coordinates.length; i < cellNumber + 100; i++) {
				newCoords[i] = new Point(newCoords[i-1].x, newCoords[i-1].y);
				newCoords[i].x++;
				if (newCoords[i].x >= getRowLength(newCoords[i].y)) {
					newCoords[i].y++;
					newCoords[i].x = 0;
				}
				
			}
			coordinates = newCoords;
			return coordinates[cellNumber];
		}
	}
	
	/**
	 * Checks out all cell coordinates and returns the maximum x as width 
	 * and the maximum y as height
	 * @param nrOfCells
	 * @return
	 */
	public Dimension getExactDimensions(int nrOfCells){

		if (nrOfCells > 0) {
			
			int collumns=1;
			Point coords = getCoordinates(nrOfCells-1);
			int rows = coords.y+1;
			
			
			int n = Math.min(coords.y, cycle.length);
			for (int i=0;i < n; i++){
				collumns = Math.max(collumns, cycle[i%cycle.length]);
			}
			
			collumns = Math.max(collumns, coords.x+1);
			
			return new Dimension(collumns, rows);
			
		} else {
			return new Dimension(0,0);
		}
		
	}
	
	
	public static LayoutCycle fromString(String input) throws NumberFormatException {
		
		ArrayList<Integer> c = new ArrayList<Integer>();
		
		
		Matcher m = integerRegexp.matcher(input);
		
		while (m.find()) {
			c.add(Integer.parseInt(m.group(0)));
		}
		
		int[] cycleAsArray = new int[c.size()];
		for (int i = 0; i < c.size();i++) {
			cycleAsArray[i] = (int) c.get(i);
		}
		
		LayoutCycle lc = new LayoutCycle(cycleAsArray);
		return lc;
	}
	
	public String toString () {
		String s = "(";
		for (int i = 0; i < cycle.length-1; i++) {
			s = s + cycle[i] + ", ";
		}
		if (cycle.length>0) s = s + cycle[cycle.length-1] + ")";
		return s;
	}
}
