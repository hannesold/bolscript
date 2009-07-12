package gui.composers.kaida.pilots;

import gui.bols.VariationItem;

import javax.swing.JComboBox;

import config.Pilots;

import algorithm.pilots.Pilot;
import bols.BolBase;
import bols.BolBaseGeneral;
import bols.Variation;
import bols.tals.Tal;

public class PilotSelector extends JComboBox {
	
	public PilotSelector() {
		super();	
		initByStandards();

	}
	
	public PilotSelector(boolean autoInit) {
		super();	
		if (autoInit) {
			initByStandards();
		}
	}
	
	public void initByStandards() {
		addItems(Pilots.getPilotsAsMenuItems());
	}
	
	public void addItem(String name, Pilot p) {
		this.addItem(new PilotItem(name, p));
	}
	
	public void addItems(PilotItem[] items) {
		for (int i=0; i < items.length;i++) {
			this.addItem(items[i]);
		}
	}
	
	public Pilot getSelectedPilot() {
		return ((PilotItem)getSelectedItem()).pilot;
	}

	public void setSelected(PilotItem item) {
		this.setSelectedItem(item);
	}
	
}
