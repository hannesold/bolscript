/**
 * 
 */
package bols;

import java.util.ArrayList;

import bolscript.sequences.Representable;
import bolscript.sequences.RepresentableSequence;

/**
 * @author Hannes
 *
 */
public class BolSequence {
	private ArrayList<Bol> bols;
	private double duration;
	
	public BolSequence () {
		bols = new ArrayList<Bol>();
		duration = 0;
	}
	
	/**
	 * Takes the bols from representableSequence.getBols()
	 * and copies its duration.
	 * TODO this is not tested at all!
	 * @param representableSequence
	 */
	public BolSequence (RepresentableSequence representableSequence) {
		this();
		bols = representableSequence.getBols();
		duration = representableSequence.getDuration();
	}
	
	public BolSequence (String strSeq, BolBaseGeneral bolBase) {
		bols = new ArrayList<Bol>();
		duration = 0;

		String[] sequenceAsArray = strSeq.split(" ");
			
		//go through stringarray to build up bol-sequence
		//use BolBase to get real "BolName"s
		
		for (int i=0; i < sequenceAsArray.length; i++) {
			Bol bol = null;
			BolName bolName = bolBase.getBolName(sequenceAsArray[i]);

			if (bolName != null) {
				bol = new Bol(bolName, new PlayingStyle(1,1)); 
				addBol(bol);
			} else {
				System.out.println(sequenceAsArray[i] + " not found in bolNames");
			}
		}
	}
	
	
	public void addBol(Bol pBol){
		bols.add(pBol);
		duration += (1.0 / ((double)pBol.getPlayingStyle().getSpeedValue()));
		duration = algorithm.tools.Calc.roundIfClose(duration);
	}	
	
	public String toString (){
		String s = "";
		double position = 0.0;
		for (int i=0; i < bols.size(); i++) {
			Bol currentBol = ((Bol)bols.get(i));
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
	
	public String toString (double position){
		String s = "";
		for (int i=0; i < bols.size(); i++) {
			Bol currentBol = ((Bol)bols.get(i));
			String space = "";
			
			int nrOfSpaces = (int) Math.floor(6.0f / currentBol.getSpeed());
			
			for (int j = 1; j < nrOfSpaces; j++) {
				space += " ";
			}
			
			s += ((position % 1.0 ==0.0)?"| ":"") + currentBol.toString() + space; 
			 
			position += (1.0 / (double)(currentBol.getSpeed()));
			position = algorithm.tools.Calc.roundIfClose(position);
		}
		return s;
	}	
	public String toStringFull (){
		String s = "";
		for (int i=0; i < bols.size(); i++) {
			Bol currentBol = ((Bol)bols.get(i));
			String space = "";
			
			int nrOfSpaces = (int) Math.floor(6.0f / currentBol.getSpeed());
			for (int j = 1; j < nrOfSpaces; j++) {
				space += " ";
			}
			
			s += currentBol.toString() + currentBol.getPlayingStyle() + space; 
		}
		return s;
	}
	
	public int getLength() {
		return bols.size();
	}
	
	public Bol getBol(int index){
		return (Bol)bols.get(index);
	}
	
	public double getDuration() {
		return duration;
	}
	
	public BolSequence getCopy() {
		BolSequence bolSeq = new BolSequence();
	
		for (int i=0; i < bols.size(); i++) {
			bolSeq.addBol(((Bol)bols.get(i)).getCopy());
		}
		
		return bolSeq;
	}	
	
	public BolSequence getCopyWithMergedPauses(BolBaseGeneral bolBase) {
		BolSequence bolSeq = new BolSequence();
		BolName emptyBol = bolBase.getEmptyBol();
		ArrayList<Bol> newBolsReversed = new ArrayList<Bol>(bolSeq.getLength());
		
		double pauseDurationBuffer = 0; 
		for (int i = bols.size()-1; i>=0; i--) {
			Bol bol = bols.get(i);
			if ((bol.getBolName() == emptyBol) && (i!=0)) {
				pauseDurationBuffer += (1.0f / bol.getSpeed());
			} else {
				Bol newBol;
				if (pauseDurationBuffer!=0) {
					//merge bol with pauses
					double duration = (1.0d / bol.getSpeed()) + pauseDurationBuffer;
					newBol = bol.getCopy();
					newBol.setSpeed(1.0f / duration);
					pauseDurationBuffer=0;
				} else {
					//just copy bol
					newBol = bol.getCopy();
				}
				newBolsReversed.add(newBol);
			}
		}
		for (int i = newBolsReversed.size()-1; i>=0;i--) {
			bolSeq.addBol(newBolsReversed.get(i));
		}
		
		return bolSeq;
	}
	
	public boolean equals (BolSequence seq) {
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
		return equals((BolSequence)seq);
		
	}

	public String toStringCompact() {
		String s = "";
		for (int i=0; i < bols.size(); i++) {
			Bol currentBol = ((Bol)bols.get(i));
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
