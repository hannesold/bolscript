package bols.tals;

import java.util.ArrayList;

import bols.BolBase;
import bols.BolBaseGeneral;
import bolscript.Reader;
import bolscript.packets.Packets;
import bolscript.sequences.RepresentableSequence;

public class Teental extends bols.tals.TalDynamic implements Tal {

	public static TalDynamic defaultTeental = null;
	
	public Teental(BolBaseGeneral bolBase) {
		super("Type: \n"+
		"Tal \n"+
		""+
		"Gharanas:\n"+
		"Punjab\n"+
		""+
		"Name: \n"+
		"Teental\n"+
		""+
		"Length:\n"+
		"16\n"+
		""+
		"Tal:\n"+
		"Teental\n"+
		""+
		"Theka:\n"+
		"Dha! Dhin Dhin Dha,\n"+
		"Dha! Dhin Dhin Dha, \n"+
		"Na Tin Tin Na, \n"+
		"Na Dhin! Dhin Dha, \n"+
		""+
		"Vibhags:\n"+
		"4 + 4 + 4K + 4\n"+
		""+
		"Layout:\n"+
		"1 1\n"+
		"2 2\n"+
		"4 4\n"+
		"8 8\n"+
		"16 16\n"+
		"32 16\n");
	}
	
	public static Tal getDefaultTeental(BolBaseGeneral bolBase) {
		if (defaultTeental == null) defaultTeental = new Teental(BolBase.getStandard());
		return defaultTeental;
	}


}
