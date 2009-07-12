package bols.tals;

import java.util.ArrayList;

import bols.BolBaseGeneral;
import bols.Variation;
import bolscript.sequences.RepresentableSequence;

public interface Tal {
	
	/**
	 * Get the name of the tal, for example Teental or Jhaptal or Roopak
	 * @return
	 */
	public String getName();
	
	/**
	 * Get as Representable Sequence
	 * @return
	 */
	public RepresentableSequence getTheka ();
	
	/**
	 * Length of Tal in Matras (Beats)
	 * @return
	 */
	public int getLength();
	
	/**
	 * Return vibhags
	 */
	public Vibhag[] getVibhags();
	
	/**
	 * Return Layout 
	 */
	public LayoutChooser getLayoutChooser();
	
	/**
	 * Get a String version of a vibhag description
	 */
	public String getVibhagsAsString();
	
	

}
