package bols;

import java.util.ArrayList;

import bolscript.sequences.RepresentableSequence;

public interface SubSequence {

	public BolSequence getAsSequence();
	
	/**
	 * Get the Variation as RepresentableSequence (as compatible to Bolscript).
	 */
	public RepresentableSequence getAsRepresentableSequence();
	
	public BolSequence getBasicBolSequence();
	
	public SubSequence getCopy();
	public SubSequence getCopyKeepBolSequence();
	public SubSequence getCopyFull();
	public SubSequence getCopyNewStyle(PlayingStyle style);
	
	public boolean hasSubSequences();
	
	public String getLabel();
	
	public PlayingStyle getPlayingStyle();
	
	public int getStart();
	public int getLength();
	public double getDuration();
	
	public void setBasicBolSequence(BolSequence bolSequence);
	public void setLabel(String label);
	public void setLength(int length);
	public void setPlayingStyle(PlayingStyle style);
	public void setStart(int start);
	
	public boolean equals(SubSequence s);
	public String toString();
	public ArrayList<SubSequence> getSubSequencesRecursive(int depth);
	public ArrayList<SubSequence> getSubSequencesRecursive(int depth, int step, PlayingStyle playingStyle);
}
