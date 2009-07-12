package bols;

import java.util.ArrayList;

import basics.Debug;
import bolscript.sequences.Representable;
import bolscript.sequences.RepresentableSequence;

/**
 * @author Hannes
 *
 */
public class SubSequenceAtomic implements SubSequence{
	protected BolSequence basicBolSequence;
	protected int start;
	protected int length;
	protected PlayingStyle style;
	protected String label;
	protected boolean hasSubSequences;
	protected double duration;
	protected boolean hasChanged;
	protected BolSequence asBolSequence = null;
	protected RepresentableSequence asRepresentableSequence = null;

	public SubSequenceAtomic(BolSequence sequence, int start, int length, PlayingStyle style) {
		super();
		this.basicBolSequence = sequence;
		
		this.start = start;
		this.length = length;
		
		this.style = style;
		this.label = "_";
		
		this.hasSubSequences = false;
		this.asBolSequence = getAsSequence();
		
		hasChanged = false;
	}
	
	public SubSequenceAtomic(BolSequence sequence, PlayingStyle style2) {
		this.basicBolSequence = sequence;
		this.length = 0;
		this.start = 0;
		this.style = style2;
		this.label = "_";
		this.hasSubSequences = false;
		this.asBolSequence = new BolSequence();
		this.duration = 0;
		hasChanged = false;
	}
	
	public SubSequenceAtomic(BolSequence sequence) {
		this.basicBolSequence = sequence;
		this.length = 0;
		this.start = 0;
		this.style = new PlayingStyle(1,1);
		this.label = "_";
		this.hasSubSequences = false;
		this.asBolSequence = new BolSequence();
		this.duration = 0;
		hasChanged = false;
	}

	/** 
	 * @return the compiled BolSequence
	 */
	public BolSequence getAsSequence () {
		if ((hasChanged)||(asBolSequence==null)) {
			asBolSequence = new BolSequence();
			
			for (int i=start; i < (start+length); i++) {
				//take bol from base sequence, and adjust speed to subsequence speed
				Debug.out("getting Bol nr " + i + " from basicbolseq.");
				Bol b = new Bol (basicBolSequence.getBol(i).getBolName(), 
						style.getProduct(basicBolSequence.getBol(i).getPlayingStyle()));
				
				asBolSequence.addBol(b);
			}
			return asBolSequence;
		} else {
			return asBolSequence;
		}
	}
	
	public RepresentableSequence getAsRepresentableSequence() {

		if ((hasChanged)||(asRepresentableSequence==null)) {
			asRepresentableSequence = new RepresentableSequence(); 

			for (int i=start; i < (start+length); i++) {
				//take bol from base sequence, and adjust speed to subsequence speed
				Bol currentBol  = new Bol (basicBolSequence.getBol(i).getBolName(), 
						style.getProduct(basicBolSequence.getBol(i).getPlayingStyle()));
				asRepresentableSequence.add(currentBol);

			} 
			return asRepresentableSequence;
		} else {
			return asRepresentableSequence;
		}

	} 		
	
	
	public BolSequence getAsSequenceRetaining () {
		//gives back the real bols
		BolSequence asSequence = new BolSequence();
		
		for (int i=start; i < (start+length); i++) {
			//take bol from base sequence, and adjust speed to subsequence speed
			Bol b = basicBolSequence.getBol(i);
			
			asSequence.addBol(b);
		}
		return asSequence;
	}
	
	public SubSequenceAtomic getCopy() {
		return new SubSequenceAtomic(basicBolSequence, start, length, style);
	}
	public SubSequenceAdvanced getCopyKeepBolSequence() {
		return new SubSequenceAdvanced(basicBolSequence, start, length, style.getCopy());
	}
	public SubSequenceAtomic getCopyFull() {
		return new SubSequenceAtomic(basicBolSequence.getCopy(), start, length, style.getCopy());			
	}	

	
	//get / set
	
	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
		hasChanged = true;
	}
	
	public PlayingStyle getPlayingStyle() {
		return style;
	}
	public void setPlayingStyle(PlayingStyle style) {
		this.style = style;
		hasChanged = true;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
		hasChanged = true;
	}

	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}


	public BolSequence getBasicBolSequence() {
		return basicBolSequence;
	}


	public void setBasicBolSequence(BolSequence bolSequence) {
		this.basicBolSequence = bolSequence;
	}
	
	public String toString () {
		return getAsSequence().toString();
	}
	
	public boolean equals(SubSequence s) {
		if ((s.getStart() == start) && (s.getLength() == length) &&
				(s.getBasicBolSequence() == basicBolSequence) && (s.getPlayingStyle().getSpeedValue() == style.getSpeedValue())) {
			return true;
		} else {
			return s.getAsSequence().equals(this.getAsSequence());
		}
	}

	public boolean hasSubSequences() {
		return hasSubSequences;
	}
	
	public ArrayList<SubSequence> getSubSequencesRecursive(int depth) {
		return getSubSequencesRecursive(depth, 1, new PlayingStyle(1f,1f));
	}
	public ArrayList<SubSequence> getSubSequencesRecursive(int depth, int step, PlayingStyle playingStyle) {
		PlayingStyle styleProduct = style.getProduct(playingStyle);
		PlayingStyle newStyle = style.equals(styleProduct) ? style : styleProduct;
		
		ArrayList<SubSequence> subSeqs = new ArrayList<SubSequence>(1);
		subSeqs.add(this.getCopyNewStyle(newStyle));
		return subSeqs;
	}
	
	public double getDuration() {
		return getAsSequence().getDuration();
	}
	
	public SubSequence getCopyNewStyle(PlayingStyle style) {
		SubSequence copy = this.getCopyKeepBolSequence();
		copy.setPlayingStyle(style);
		return copy;
	}

	
}
