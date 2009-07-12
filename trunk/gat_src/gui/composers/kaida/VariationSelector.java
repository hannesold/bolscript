package gui.composers.kaida;


import gui.bols.VariationItem;

import javax.swing.JComboBox;

import config.Themes;

import bols.BolBase;
import bols.BolBaseGeneral;
import bols.Variation;
import bols.tals.Tal;

public class VariationSelector extends JComboBox {
	
	BolBaseGeneral bolBase;
	
	public VariationSelector() {
		super();	
		this.bolBase = BolBase.getStandard();
		initByStandards();

	}
	
	public VariationSelector(BolBaseGeneral bolBase, boolean autoInit) {
		super();	
		this.bolBase = bolBase;
		if (autoInit) {
			initByStandards();
		}
	}
	
	public void initByStandards() {
		addItems(Themes.getKaidaThemesAsMenuItems(bolBase));
	}
	
	public void addItem(String name, Variation var, Tal tal) {
		this.addItem(new VariationItem(name, var, tal));
	}
	
	public void addItems(VariationItem[] items) {
		for (int i=0; i < items.length;i++) {
			this.addItem(items[i]);
		}
	}
	
	public Variation getSelectedVariation() {
		return ((VariationItem)getSelectedItem()).variation;
	}

	public void setSelected(VariationItem item) {
		this.setSelectedItem(item);
	}
	
}
