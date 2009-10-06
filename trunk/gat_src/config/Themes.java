package config;

import gui.bols.VariationItem;
import algorithm.composers.kaida.Individual;
import algorithm.mutators.Mutator;
import algorithm.mutators.MutatorDoublifier;
import basics.Debug;
import basics.FileReadException;
import bols.BolBaseGeneral;
import bols.BolSequence;
import bols.PlayingStyle;
import bols.SubSequence;
import bols.Variation;
import bols.tals.Jhaptal;
import bols.tals.Tal;
import bols.tals.Teental;

public class Themes {

	public static Variation getTheme01(BolBaseGeneral bolBase) {
		return new Variation("Dha Ge Ti Re Ke Te, Dha -, Dha -, Dha Ge Ti Re Ke Te", bolBase);
	}
	public static Variation getTheme01b(BolBaseGeneral bolBase) {
		return new Variation("Dha Ge; Ti Re Ke Te, Dha -, Dha -, Dha Ge; Ti Re Ke Te", bolBase);
	}
	public static Variation getTheme02(BolBaseGeneral bolBase) {
		return new Variation("Ge Na Ti Te Dha Ge Na, Dha Ti, Dha Ge Na, Tun Na Ge Na", bolBase);
	}
	public static Variation getTheme03(BolBaseGeneral bolBase) {
		return new Variation("Ge Na, Ti Te, Dha Ge Na, Dha, Ti Te, Dha Ge, Tun Na Ge Na", bolBase);
	}
	public static Variation getTheme04(BolBaseGeneral bolBase) {
		Variation var = new Variation("Ge Ne Na, Ge Ne Na, Ge Ge Ne Na, Ge Na, Tun Na Ge Na", bolBase);
		return var;
	}
	public static Variation getTheme05(BolBaseGeneral bolBase) {
		Variation var = new Variation("Dha -, Ti Re Ke Te, Ta Ke Ti Re Ke Te, Dha Ti Dha Ge, Tin Na Ge Na", bolBase);
		return var;
	}
	public static Variation getTheme06(BolBaseGeneral bolBase) {
		Variation var = new Variation("Dha -, Ge Te Na Ke, Ta Ke Ti Re Ke Te, Dhin Ne Ge Ne", bolBase);
		return var;
	}	
	public static Variation getTheme07(BolBaseGeneral bolBase) {
		Variation var = new Variation("Dha Ti Te, Dha Ti Te, Dha, Dha, Dha Ti, Dha Ge, Tun Na Ge Na", bolBase);
		return var;
	}
	public static VariationItem[] getKaidaThemesAsMenuItems(BolBaseGeneral bolBase) {
		
		Tal teental = new Teental();
		Tal jhaptal = new Jhaptal();
		return new VariationItem[]{
				new VariationItem("Kaida1", getTheme01(bolBase), teental),
				//new VariationItem("Kaida1'", getTheme01b(bolBase), teental),
				//new VariationItem("Kaida1''", getTestVariationNoGe(bolBase), teental),
				new VariationItem("Kaida2", getTheme02(bolBase), teental),
				new VariationItem("Kaida3", getTheme03(bolBase), teental),
				new VariationItem("Kaida4", getTheme04(bolBase), teental),
				new VariationItem("Kaida5", getTheme05(bolBase), jhaptal),
				new VariationItem("Kaida6", getTheme06(bolBase), teental),
				new VariationItem("Kaida7", getTheme07(bolBase), teental)
				};
		
	}

	
	
	
	
	public static Variation getTheme01DbSpeed(BolBaseGeneral bolBase) {
		Variation var = new Variation("Dha Ge Ti Re Ke Te, Dha -, Dha -, Dha Ge Ti Re Ke Te", bolBase);
		Mutator doubler = new MutatorDoublifier(1.0f, 1.0f);
		Individual in1= new Individual(var);
		doubler.mutate(in1);
		return in1.getVariation();
	}

	public static Variation getTheme01NoGe(BolBaseGeneral bolBase) {
		return new Variation("Dha - Ti Re Ke Te, Dha -, Dha -, Dha Ge, Ti Re Ke Te", bolBase);
	}
	

	public static Variation getTheme2DbSpeed(BolBaseGeneral bolBase) {
		Variation var2 = getTheme02(bolBase);
		Variation var3 = new Variation(var2.getBasicBolSequence());
		for(int i=0; i<2;i++) {
			for (SubSequence subSeq: var2.getSubSequences()) {
				SubSequence s = subSeq.getCopy();
				s.setPlayingStyle(new PlayingStyle(2f,1f));
				var3.addSubSequence(s);
			}
		}
		return var3;
	}
	
	public static Variation getTheme05b(BolBaseGeneral bolBase) {
		BolSequence seq1 = new BolSequence("Dha - Ti Ri Ke Te Ta Ke Ti Re Ke Te Dha Ti Dha Ge Tin Na Ge Na", bolBase);
		Variation var = new Variation(seq1);
		var.addSubSequence(0,6, 1f);
		var.addSubSequence(6,6, 1f);
		var.addSubSequence(12,8, 1f);
		return var;
	}


}
