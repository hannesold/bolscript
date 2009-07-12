/**
 * 
 */
package bols;


import java.util.ArrayList;

import basics.Debug;
import bols.tals.Tal;
import bolscript.sequences.RepresentableSequence;



/**
 * @author Hannes
 *
 * positions/overlapping?
 * 
 */
public class Variation extends SubSequenceAdvanced implements SubSequence{
	
	public Variation(BolSequence sequence, int start, int length, PlayingStyle style) {
		super(sequence, start, length, style);
	}

	public Variation(BolSequence sequence, PlayingStyle style) {
		super(sequence, style, true);
	}
	public Variation(BolSequence basicBolSequence) {
		this(basicBolSequence, new PlayingStyle(1f,1f));
	}
	
	/**
	 * Simply constructs a Variation with the bols from the representable sequence 
	 * as a basic bolsequence.
	 * TODO this is not tested much
	 */
	public Variation(RepresentableSequence seq) {
		super(new BolSequence(seq), new PlayingStyle(1f,1f), true);
	}
	
	/**
	 * Constructs a Variation with the bols from the representable sequence 
	 * as a basic bolsequence, and one SubSequence covering the whole sequence.
	 */
	public static Variation variationWithOneSubsequence(RepresentableSequence seq) {
		Variation v = new Variation(seq);
		v.addSubSequence(0, v.getBasicBolSequence().getLength());
		return v;
	 
	}
	
	/**
	 * Constructs a Variation from the 'Theka' - Representable Sequence of a tal
	 * using Tal.getTheka(). Currently a simple variation with one Subsequence is made.
	 * 
	 * @param style
	 * @param subSequences
	 */
	public static Variation fromTal(Tal tal) {
		return variationWithOneSubsequence(tal.getTheka());
	}

	public Variation(PlayingStyle style, ArrayList<SubSequence> subSequences) {
		super(style, subSequences);
	}

	public Variation(PlayingStyle style, Object... subSequences) {
		super(style, subSequences);
	}

	public Variation(String strSequence, BolBaseGeneral bolBase) {
		super(strSequence, bolBase);
	}
	/**
	 * @return A SubSequence using the same basicBolSequence and style
	 */
	public Variation getCopy() {
		
		if (hasSubSequences) {
			Variation s = new Variation(basicBolSequence, style);
			
			for (int i = 0; i < subSequences.size(); i++) {
				//could be optimised
				s.addSubSequence(getSubSequence(i).getCopy());
			}
			
			return s;
		} else {
			return new Variation(basicBolSequence, start, length, style);			
		}
	}	

	/**
	 * @return A SubSequence using the same basicBolSequence, but real clones of style
	 */
	public Variation getCopyKeepBolSequence() {
		
		if (hasSubSequences) {
			Variation s = new Variation(basicBolSequence, style.getCopy());
			
			for (int i = 0; i < subSequences.size(); i++) {
				SubSequence subSeq = getSubSequence(i).getCopy();
				subSeq.setBasicBolSequence(basicBolSequence);
				s.addSubSequence(subSeq);
			}
			return s;	
		} else {
			return new Variation(basicBolSequence, start, length, style.getCopy());
		}
	}
	
	/**
	 * @return A SubSequence using thourough copies of basicBolSequence and style
	 */
	public Variation getCopyFull() {
		
		BolSequence basicBolsCopy = basicBolSequence.getCopy();
		if (hasSubSequences) {
			Variation s = new Variation(basicBolsCopy, style.getCopy());
			
			for (int i = 0; i < subSequences.size(); i++) {
				SubSequence subSeq = getSubSequence(i).getCopy();
				subSeq.setBasicBolSequence(basicBolsCopy);
				s.addSubSequence(subSeq);
			}
			
			return s;
			
		} else {
			return new Variation(basicBolsCopy, start, length, style.getCopy());
		}
		
			
	}
	
	

	
}
