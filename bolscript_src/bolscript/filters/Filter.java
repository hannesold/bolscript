package bolscript.filters;

import java.util.ArrayList;

import bolscript.compositions.Composition;

public interface Filter {

	public final int OR = 0;
	public final int AND = 1;
	
	public final int GHARANA = 2;
	public final int SPEED = 3;
	public final int TAL = 4;
	public final int TYPE = 5;
	
	/**
	 * Checks if the item fullfills the Filters criteria.
	 * @param Composition the examined CompositionInfo
	 * @return true or false.
	 */
	//public boolean accepts(Composition composition);
	
	/**
	 * Sets a filter on or off bypass mode. If bypass mode is on
	 * the filter sees everything.
	 * 
	 * @param bypass
	 */
	public void setAcceptAll(boolean acceptAll);
	
	public String getFilterHeader();
	
}
