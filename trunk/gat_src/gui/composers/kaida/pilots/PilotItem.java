package gui.composers.kaida.pilots;

import algorithm.pilots.Pilot;
import bols.Variation;
import bols.tals.Tal;

public class PilotItem {
	public String name;
	public Pilot pilot;
		
	public PilotItem(String name, Pilot pilot) {
		super();
		this.name = name;
		this.pilot = pilot;
	}

	public String toString() {
		String s = pilot.toStringCompact();
		return name + ": "+ s.substring(0,Math.min(25,s.length())) +"... " + pilot.getDuration();
	}

}
