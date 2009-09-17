package bolscript.filters;

import java.util.ArrayList;

import bolscript.compositions.Composition;

/**
 * An interface that requires the ability to provide an ArrayList of Compositions for display.
 * This is intended especially for results of filtering, where not all Compositions are visible.
 * @author hannes
 */
public interface VisibleCompositionDonator {
	public ArrayList<Composition> getVisibleCompositions();
}
