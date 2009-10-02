/**
 * 
 */
package bols;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This needs to be changed, so that duration depends only 
 * on the last bols position and its duration.
 * @author Hannes
 *
 */
public class BolSequenceAdvanced extends basics.Basic {
	private ArrayList<BolPositionedAndWeighted> bols;
	private double duration;
	private HashMap<Double, BolPositionedAndWeighted> positionMap;
	
	public BolSequenceAdvanced () {
		bols = new ArrayList<BolPositionedAndWeighted>();
		positionMap = new HashMap<Double, BolPositionedAndWeighted>(64);
		duration = 0;
	}
	
	public BolSequenceAdvanced (String strSeq, BolBaseGeneral bolBase) {
		
		bols = new ArrayList<BolPositionedAndWeighted>();
		positionMap = new HashMap<Double, BolPositionedAndWeighted>(64);
		duration = 0;

		String[] sequenceAsArray = strSeq.split(" ");
			
		//go through stringarray to build up bol-sequence
		//use BolBase to get real "BolName"s
		
		for (int i=0; i < sequenceAsArray.length; i++) {
			Bol bol = null;
			BolName bolName = bolBase.getBolName(sequenceAsArray[i]);

			if (bolName != null) {
				bol = new Bol(bolName, new PlayingStyle(1,1), null, false); 
				appendBol(bol);
			} else {
				System.out.println(sequenceAsArray[i] + " not found in bolNames");
			}
		}
		
		
		
	}
	
	
	public BolSequenceAdvanced(BolSequence sequence) {
		bols = new ArrayList<BolPositionedAndWeighted>();
		positionMap = new HashMap<Double, BolPositionedAndWeighted>(64);
		
		duration = 0;
		
		for (int i = 0; i < sequence.getLength(); i++) {
			appendBol(sequence.getBol(i));
		}
		
	}

	/**
	 * Appends a bol to the sequence. Position is then calculated and assigned.
	 * @param bol
	 */
	public void appendBol(Bol bol){
		
		BolPositionedAndWeighted newBol = new BolPositionedAndWeighted(bol.bolName, bol.style, (double) duration);
		bols.add(newBol);
		positionMap.put(new Double(duration), newBol);
		duration += (1.0 / ((double)bol.getPlayingStyle().getSpeedValue()));
		duration = algorithm.tools.Calc.roundIfClose(duration);
	}	
	/**
	 * Appends a weighted bol to the sequence. Position and duration is then newly assigned.
	 * @param bol
	 */
	public void appendBol(BolPositionedAndWeighted bol){
		
		BolPositionedAndWeighted newBol = new BolPositionedAndWeighted(bol.bolName, bol.style, (double) duration, bol.weight);
		bols.add(newBol);
		positionMap.put(new Double(duration), newBol);
		duration += (1.0 / ((double)bol.getPlayingStyle().getSpeedValue()));
		duration = algorithm.tools.Calc.roundIfClose(duration);
	}	
	
	/**
	 * Inserts a positioned and weighted bol into the list.
	 * @param bol
	 */
	public void insertBol(BolPositionedAndWeighted bol){
		
		if (positionMap.get(bol.position) == null) {
			bols.add(getPreviousIndex(bol.position)+1,bol);
			
			positionMap.put(bol.position, bol);
			duration = Math.max(duration, algorithm.tools.Calc.roundIfClose(bol.position+(1.0 /((double)bol.getSpeed()))));
		} else {
			out("there is already a bol at position " + bol.position + ", bol wont be added");
			out("positionMap " + positionMap);
		}
	}	
	
	/**
	 * Removes the bol at the specified index from the sequence. 
	 * Other bols are not effected, but total duration might change, 
	 * if the bol was the last bol.
	 * @param index
	 */
	public void removeBol(int index) {
		if ((index < bols.size()) && (index>=0)) { 
			BolPositionedAndWeighted bolToRemove = bols.get(index);
			if (bolToRemove != null) {
				
				// if it was the last one, update duration
				if (index == (bols.size()-1)) {
					if ((index-1)>=0) {
						duration = bols.get(index-1).position + 1.0/bols.get(index-1).getSpeed();
					}
				}
				
				positionMap.remove(bolToRemove.position);
				bols.remove(index);
				
			} else {
				out("Warning: bol couldnt be removed, is null");
			}
		} else {
			out("Warning: bol out of bounds...");
		}
	}
	
	/**
	 * Removes the bol from the sequence.
	 * Other bols are not effected, but total duration might change, 
	 * if the bol was the last bol.
	 * @param bol the Bol to remove
	 */
	public void removeBol(BolPositionedAndWeighted bol) {
		removeBol(bols.indexOf(bol));
	}
	
	
	public String toStringAdvanced (){
		String s = "";
		double position = 0.0;
		for (int i=0; i < bols.size(); i++) {
			BolPositionedAndWeighted currentBol = ((BolPositionedAndWeighted)bols.get(i));
			String space = "";
			
			int nrOfSpaces = (int) Math.floor(6.0f / currentBol.getSpeed());
			
			for (int j = 1; j < nrOfSpaces; j++) {
				space += " ";
			}
			
			s += ((position % 1.0 ==0.0)?"|":"") + currentBol.toString() + space; 
			 
			position += (1.0 / (double)(currentBol.getSpeed()));
			position = algorithm.tools.Calc.roundIfClose(position);
		}
		return s;
	}
	
	public String toString (){
		return toString(0, true, false);
	}
	
	public String toString (double position){
		return toString(position, true, false);
	}
	public String toString (double position, boolean showPositionAndWeights, boolean showStyle){
		String s = "";
		for (int i=0; i < bols.size(); i++) {
			BolPositionedAndWeighted currentBol = ((BolPositionedAndWeighted)bols.get(i));
			position = algorithm.tools.Calc.roundIfClose(currentBol.position);

			String space = "";
			
			int nrOfSpaces = (int) Math.floor(6.0f / currentBol.getSpeed());
			
			for (int j = 1; j < nrOfSpaces; j++) {
				space += " ";
			}
			
			s += ((position % 1.0 ==0.0)?"| ":"") + currentBol.toString();
			if (showPositionAndWeights) {
				s += (",p:"+currentBol.position+",w:"+currentBol.weight);
			}
			if (showStyle) {
				s += "," + currentBol.getPlayingStyle().toString();
			}
			s += space; 
			
		}
		return s;
	}

	public String toStringFull (){
		return toString(0, true, true);
	}	
	
	public int getLength() {
		return bols.size();
	}
	
	public BolPositionedAndWeighted getBol(int index){
		return (BolPositionedAndWeighted)bols.get(index);
	}
	
	/**
	 * Returns the Bol at the position in time. (or null, if none is found)
	 * @param position Relative to the beginning, counted in matra.
	 * @return
	 */
	public BolPositionedAndWeighted getBol(double position){
		return positionMap.get(position);
	}
	
	
	/**
	 * Finds the Index of the Bol that comes before the specified position, or -1 if none was found.
	 * @param position
	 */
	private int getPreviousIndex(double position) {
		int index = -1;
		if (bols.size()>0) {
			if (position > bols.get(bols.size()-1).position) {
				return bols.size()-1;
			}
			if (position < bols.get(0).position) {
				return -1;
			}
			for (int i = 0; i < (bols.size()-1); i++) {
				if ((bols.get(i).position < position) &&
						(bols.get(i+1).position) >= position) {
					index = i;
					break;
				}
			}
		} 
		return index;
	}
	
	/**
	 * Returns an array with all bols that are between the startPosition and endPosition.
	 * @param startPosition
	 * @param endPosition
	 * @return The Array of positioned and weighted bols
	 */
	public BolPositionedAndWeighted[] getBolsBetween(double startPosition, double endPosition) {
		/*
		 * make sure the array will be of size 0 if something goes wrong
		 */
		int firstIndex = -1;
		int lastIndex  = -1;
		
		/*
		 * determine the first index between the positions
		 */
		if (positionMap.get(startPosition) != null) {
			firstIndex = bols.indexOf(positionMap.get(startPosition)) + 1;
		} else {
			firstIndex = getPreviousIndex(startPosition) + 1;
		}
		/*
		 * determine the last index between the positions
		 */
		if (positionMap.get(endPosition) != null) {
			lastIndex = bols.indexOf(positionMap.get(endPosition)) - 1;
		} else {
			lastIndex = getPreviousIndex(endPosition);
		}
		
		BolPositionedAndWeighted[] theBols = new BolPositionedAndWeighted[Math.max((lastIndex - firstIndex)+1, 0)]; 
		
		for (int i = firstIndex, j=0; i <= lastIndex; i++, j++) {
			if (i < bols.size()) {
				theBols[j] = bols.get(i);
			}
		}
		
		return theBols;
	}
	
	public double getDuration() {
		return (double) duration;
	}

	public BolSequenceAdvanced getCopy() {
		BolSequenceAdvanced bolSeq = new BolSequenceAdvanced();
	
		for (int i=0; i < bols.size(); i++) {
			bolSeq.insertBol(((BolPositionedAndWeighted)bols.get(i)).getCopy());
		}
		
		return bolSeq;
	}	
	
	public BolSequenceAdvanced getCopyWithMergedPauses(BolBase bolBase) {
		BolSequenceAdvanced bolSeq = new BolSequenceAdvanced();
		BolName emptyBol = bolBase.getEmptyBol();
		ArrayList<BolPositionedAndWeighted> newBolsReversed = new ArrayList<BolPositionedAndWeighted>(bolSeq.getLength());
		
		double pauseDurationBuffer = 0; 
		for (int i = bols.size()-1; i>=0; i--) {
			BolPositionedAndWeighted bol = bols.get(i);
			if ((bol.getBolName() == emptyBol) && (i!=0)) {
				pauseDurationBuffer += (1.0f / bol.getSpeed());
			} else {
				BolPositionedAndWeighted newBol;
				if (pauseDurationBuffer!=0) {
					//merge bol with pauses
					double duration = (1.0f / bol.getSpeed()) + pauseDurationBuffer;
					newBol = bol.getCopy();
					newBol.setSpeedValue(1.0f / duration);
					pauseDurationBuffer=0;
				} else {
					//just copy bol
					newBol = bol.getCopy();
				}
				newBolsReversed.add(newBol);
			}
		}
		for (int i = newBolsReversed.size()-1; i>=0;i--) {
			bolSeq.insertBol(newBolsReversed.get(i));
		}
		
		return bolSeq;
	}
	
	public boolean equals (BolSequenceAdvanced seq) {
		int len = seq.getLength();
		if (len == bols.size()) {
			for (int i=0;i<len;i++) {
				if (!seq.getBol(i).equals(bols.get(i))) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
		
	}
	
	public boolean equals (Object seq) {
		return equals((BolSequenceAdvanced)seq);
	}

	public String toStringCompact() {
		String s = "";
		for (int i=0; i < bols.size(); i++) {
			BolPositionedAndWeighted currentBol = ((BolPositionedAndWeighted)bols.get(i));
			String space = "";
			
			//int nrOfSpaces = (int) Math.floor(6.0f / currentBol.getSpeed());
			//for (int j = 1; j < nrOfSpaces; j++) {
				space += " ";
			//}
			
			s += currentBol.toString() + space;// currentBol.getPlayingStyle() + space; 
		}
		return s;
	}
	
	
}
