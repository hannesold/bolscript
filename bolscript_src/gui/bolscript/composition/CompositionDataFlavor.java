package gui.bolscript.composition;

import java.awt.datatransfer.DataFlavor;

import bolscript.compositions.Composition;

public class CompositionDataFlavor extends DataFlavor {
	private CompositionDataFlavor() {
		super(Composition.class, "Bolscript Composition");
	}
	
	private static DataFlavor standard = null;
	
	public static DataFlavor getFlavor() {
		if (standard==null) {
			standard = new CompositionDataFlavor();
		}
		return standard;
	}
	
}
