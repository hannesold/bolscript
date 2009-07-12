package gui.bols;

import bols.Variation;
import bols.tals.Tal;

public class VariationItem {
	public String name;
	public Variation variation;
	public Tal tal;
	
	public VariationItem(String name, Variation variation, Tal tal) {
		super();
		// TODO Auto-generated constructor stub
		this.name = name;
		this.variation = variation;
		this.tal = tal;
	}
	
	public String toString() {
		return name + ": "+ variation.toStringCompact().substring(0,25) +"... " + tal.getName();
	}
	
	
}
