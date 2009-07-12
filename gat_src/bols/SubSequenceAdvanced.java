package bols;

import java.util.ArrayList;

import algorithm.tools.Calc;
import bolscript.sequences.Representable;
import bolscript.sequences.RepresentableSequence;

public class SubSequenceAdvanced extends SubSequenceAtomic implements SubSequence{
	protected ArrayList<SubSequence> subSequences = null;
	protected ArrayList<Double> positions = null;
	
	public SubSequenceAdvanced(BolSequence sequence, int start, int length, PlayingStyle style) {
		super(sequence,start,length,style);
		this.hasChanged = true;
		this.hasSubSequences = false;
	}
	
	public SubSequenceAdvanced(BolSequence sequence, PlayingStyle style, boolean hasSubSequences) {
		super(sequence, style);
		this.hasSubSequences = hasSubSequences;
		
		this.basicBolSequence = sequence;
		this.length = 0;
		this.start = 0;
		this.style = style;
		this.label = "_";
		
		this.subSequences = new ArrayList<SubSequence>(10);
		this.positions = new ArrayList<Double>(10);
		
		hasChanged = true;
	}
	
	public SubSequenceAdvanced(PlayingStyle style, ArrayList<SubSequence> subSequences) {
		this(style, subSequences.toArray());
		hasChanged = true;
	}
	
	public SubSequenceAdvanced(PlayingStyle style, Object ... subSequences) {
		super(((SubSequence)subSequences[0]).getBasicBolSequence(), style);
		this.hasSubSequences = true;
		
		this.subSequences = new ArrayList<SubSequence> (subSequences.length);
		this.positions = new ArrayList<Double> (subSequences.length);
		
		for (int i=0; i < subSequences.length; i++) {
			addSubSequence((SubSequence)subSequences[i]);
		}
	}
	
	/**
	 * Parses its subsequences from a String.
	 * @param strSequence A sequence of Bols like "Dha Dha, Ge Na". A comma seperates the subsequences. 
	 * @param bolBase The Bolbase which maps the syllables to BolNames.
	 */
	public SubSequenceAdvanced(String strSequence, BolBaseGeneral bolBase) {
		// establish basic BolSequence
		super(new BolSequence(strSequence.replace(",","").replace(";",""), bolBase));
		this.hasSubSequences = true;		
				
		//split string by commatas into subseq strings
		String[] subSequenceStrings = strSequence.split(", ");
		
		subSequences = new ArrayList<SubSequence>();
		positions = new ArrayList<Double>();
		
		//establish subseqs
		int pos = 0;
		for (int i = 0; i < subSequenceStrings.length; i++) {
			if (subSequenceStrings[i].contains("; ")) {
				//has subSequences
				String [] subSubStrings = subSequenceStrings[i].split("; ");
				
				SubSequenceAdvanced subSubSeq = new SubSequenceAdvanced(basicBolSequence,new PlayingStyle(1f,1f),true);
				//build inner subsequences...
				for (int j = 0; j < subSubStrings.length; j++) {
					String [] subSeq = subSubStrings[j].split(" ");
					int len = subSeq.length;
					//add subsequence
					subSubSeq.addSubSequence(new SubSequenceAtomic(basicBolSequence, pos, len, new PlayingStyle(1f,1f)));
					pos += len;
				}
				//add subSeq containing subseqs...
				addSubSequence(subSubSeq);
			} else {
				//directly add atomic subseq
				String [] subSeq = subSequenceStrings[i].split(" ");
				int len = subSeq.length;
				//add subsequence
				addSubSequence(new SubSequenceAtomic(basicBolSequence, pos, len, new PlayingStyle(1f,1f)));
				pos += len;
			}
		}
		
	}
	/*public SubSequenceAdvanced(BolSequence bolSeq, String strSequence, String demarker, BolBaseGeneral bolBase) {
		// establish basic BolSequence
		super(bolSeq);
		this.hasSubSequences = true;		
				
		//split string by commatas into subseq strings
		String[] subSequenceStrings = strSequence.split(", ");
		
		subSequences = new ArrayList<SubSequence>();
		positions = new ArrayList<Double>();
		
		//establish subseqs
		int pos = 0;
		for (int i = 0; i < subSequenceStrings.length; i++) {
			String [] subSeq = subSequenceStrings[i].split(" ");
			int len = subSeq.length;
			//add subsequence
			addSubSequence(new SubSequenceAtomic(basicBolSequence, pos, len, new PlayingStyle(1f,1f)));
			pos += len;
		}
		
	}*/
	
	public void addSubSequence(SubSequence subSequence) {
		subSequences.add(subSequence);
		positions.add(new Double(duration));
		duration += subSequence.getDuration();
		duration = Calc.roundIfClose(duration);
		hasChanged = true;
	}
	
	public void addSubSequence(int pos, int len, PlayingStyle style) {
		SubSequence subSequence = new SubSequenceAtomic(basicBolSequence, pos, len, style);
		subSequences.add(subSequence);
		positions.add(new Double(duration));
		duration += subSequence.getAsSequence().getDuration();
		duration = Calc.roundIfClose(duration);
		hasChanged = true;
	}
	
	public void addSubSequence(int pos, int len) {
		addSubSequence(pos,len, new PlayingStyle(1,1));
	}
	
	public void addSubSequence(int pos, int len, double speed) {
		addSubSequence(pos,len, new PlayingStyle(speed,1));
		
	}
	
	public void setSubSequences(ArrayList<SubSequence> subSeqs) {
		this.hasSubSequences = true;
		
		int len = subSeqs.size();
		subSequences = new ArrayList<SubSequence>(len);
		positions = new ArrayList<Double>(len);

		for (int i=0; i < len; i++) {
			addSubSequence(subSeqs.get(i));
		}
		
	}
	
	public void addSubSequences(int[] ... posLenPairs) {
		for (int i=0; i < posLenPairs.length; i++) {
			int pos = posLenPairs[i][0];
			int len = posLenPairs[i][1];
			addSubSequence(pos,len);
		}
	}
	
	public ArrayList<SubSequence> getSubSequences() {
		return subSequences;
	}

	public ArrayList<SubSequence> getSubSequencesRecursive(int depth) {
		return getSubSequencesRecursive(depth, 0, new PlayingStyle(1f,1f));
	}
	
	public ArrayList<SubSequence> getSubSequencesRecursive(int depth, int step, PlayingStyle playingStyle) {
		PlayingStyle styleProduct = style.getProduct(playingStyle);
		PlayingStyle newStyle = style.equals(styleProduct) ? style : styleProduct;
		
		if ((step >= depth)||(!hasSubSequences)) {
			ArrayList<SubSequence> subSeqs = new ArrayList<SubSequence>(1);
			subSeqs.add(this.getCopyNewStyle(newStyle));
			return subSeqs;
		} else {
			ArrayList<SubSequence> subSeqs = new ArrayList<SubSequence>();
			for (int i=0; i < subSequences.size(); i++) {
				SubSequence s = subSequences.get(i);
				if (s.hasSubSequences()) {
					subSeqs.addAll(s.getSubSequencesRecursive(depth,step+1, newStyle));
				} else {
					subSeqs.add(s.getCopyNewStyle(newStyle.getProduct(s.getPlayingStyle())));
				}				
			}
			return subSeqs;
		}
	}
	
	

	public SubSequence getSubSequence (int i) {
		try {
		  return subSequences.get(i);
		}
		catch (Exception e) {
		  return null;
		}
	}
	
	//get set
	public BolSequence getBasicBolSequence() {
		return basicBolSequence;
	}
	
	public void setBasicBolSequence(BolSequence basicBolSequence) {
		
		this.basicBolSequence = basicBolSequence;
		if (hasSubSequences) {
			for (int i=0; i < subSequences.size(); i++) {
				getSubSequence(i).setBasicBolSequence(basicBolSequence);
			}
		}
	}
	
	public BolSequence getAsSequence() {
		if ((hasChanged)||(asBolSequence==null)) {
			asBolSequence = new BolSequence(); 
	
			if (hasSubSequences) {
				for (SubSequence subSequence: subSequences) {
					BolSequence subSeq = subSequence.getAsSequence();
					for (int j=0; j < subSeq.getLength(); j++) {
						Bol currentBol = ((Bol)subSeq.getBol(j)).getCopy();
						currentBol.setPlayingStyle(currentBol.getPlayingStyle().getProduct(this.style));
						asBolSequence.addBol(currentBol);
					}	
				} 
				return asBolSequence;
			} else {
				return super.getAsSequence();
			}
			
		} else {
			return asBolSequence;
		}
	}
	

	public RepresentableSequence getAsRepresentableSequence() {

		if ((hasChanged)||(asRepresentableSequence==null)) {
			asRepresentableSequence = new RepresentableSequence(); 

			if (hasSubSequences) {
				for (SubSequence subSequence: subSequences) {
					RepresentableSequence subSeq = subSequence.getAsRepresentableSequence();
					for (int j=0; j < subSeq.size(); j++) {
						if (subSeq.get(j).getType() == Representable.BOL) {
							Bol currentBol = ((Bol) subSeq.get(j)).getCopy();
							if (j==0) currentBol.setEmphasized(true);
							currentBol.setPlayingStyle(currentBol.getPlayingStyle().getProduct(this.style));
							asRepresentableSequence.add(currentBol);
						}

					}	
				} 
				return asRepresentableSequence;
			} else {
				return super.getAsRepresentableSequence();
			}

		} else return asRepresentableSequence;	
	}

	/**
	 * assigns the start points (in time) to each subSequence 
	 */
	public void assignSubSeqPositions() {
		positions = new ArrayList<Double>(subSequences.size());
		duration = 0.0;
		
		for (SubSequence subSeq : subSequences) {
			positions.add(new Double(duration));
			duration += subSeq.getAsSequence().getDuration();
			duration = Calc.roundIfClose(duration);
		}			
	}
	
	public int getIndexOfSplitPoint (double time) {			
		boolean found = false;
		int foundAt = 0;
		for (int i=0; i< subSequences.size(); i++) {
			if (positions.get(i) == time) {
				foundAt = i;
				found = true;
			}
		}
		if (found) {
			return foundAt;
		} else return -1;
	}
	
	public String toString(){
		if (hasSubSequences) {
			String s = "";
			for (int i = 0; i < subSequences.size(); i++) {
				s += subSequences.get(i).getAsSequence().toString(positions.get(i));
				s += ", ";
			}
			return s;
		} else {
			return getAsSequence().toString();
		}
	}
	
	public String toStringCompact() {
		// TODO Auto-generated method stub
		return getAsSequence().toStringCompact();
	}
	

	public ArrayList<Double> getPositions() {
		// TODO Auto-generated method stub
		return positions;
	}
	
	/**
	 * @return A SubSequence using the same basicBolSequence and style
	 */
	public SubSequenceAdvanced getCopy() {
		
		if (hasSubSequences) {
			SubSequenceAdvanced s = new SubSequenceAdvanced(basicBolSequence, style, hasSubSequences);
			
			for (int i = 0; i < subSequences.size(); i++) {
				//could be optimised
				s.addSubSequence(getSubSequence(i).getCopy());
			}
			
			return s;
		} else {
			return new SubSequenceAdvanced(basicBolSequence, start, length, style);			
		}
	}	

	/**
	 * @return A SubSequence using the same basicBolSequence, but real clones of style
	 */
	public SubSequenceAdvanced getCopyKeepBolSequence() {
		
		if (hasSubSequences) {
			SubSequenceAdvanced s = new SubSequenceAdvanced(basicBolSequence, style.getCopy(), hasSubSequences);
			
			for (int i = 0; i < subSequences.size(); i++) {
				SubSequence subSeq = getSubSequence(i).getCopy();
				subSeq.setBasicBolSequence(basicBolSequence);
				s.addSubSequence(subSeq);
			}
			return s;	
		} else {
			return new SubSequenceAdvanced(basicBolSequence, start, length, style.getCopy());
		}
	}
	
	/**
	 * @return A SubSequence using thourough copies of basicBolSequence and style
	 */
	public SubSequenceAdvanced getCopyFull() {
		
		BolSequence basicBolsCopy = basicBolSequence.getCopy();
		if (hasSubSequences) {
			SubSequenceAdvanced s = new SubSequenceAdvanced(basicBolsCopy, style.getCopy(), hasSubSequences);
			
			for (int i = 0; i < subSequences.size(); i++) {
				SubSequence subSeq = getSubSequence(i).getCopy();
				subSeq.setBasicBolSequence(basicBolsCopy);
				s.addSubSequence(subSeq);
			}
			
			return s;
			
		} else {
			return new SubSequenceAdvanced(basicBolsCopy, start, length, style.getCopy());
		}
		
	}

	public int getLength() {
		if (hasSubSequences) {
			return getAsSequence().getLength();
		} else return super.getLength();
	}

	public void setLength(int length) {
		if (!hasSubSequences) {
			super.setLength(length);
		}
	}

	public void setStart(int start) {
		if (!hasSubSequences) {
			super.setStart(start);
		}
	}
}
