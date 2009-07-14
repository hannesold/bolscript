package bols.tals;

import java.io.File;
import java.util.ArrayList;

import basics.Debug;
import basics.SuffixFilter;
import bols.BolBase;
import bols.BolBaseGeneral;
import bolscript.compositions.Composition;
import bolscript.config.Config;

public class TalBase extends basics.Basic {
	
	private BolBaseGeneral bolBase = null;
	private ArrayList<Tal> tals = null;
	
	private static TalBase standard = null;
	private static boolean standardInitialised = false;
	
	
	public static TalBase standard() {
		if (standard == null) {
			init();
		}
		return standard;
	}
	
	public static void init() {
		try {
			Debug.debug(TalBase.class, "init");
			standard = new TalBase(BolBase.standard());
			standard.initTalsFromDirectory(bolscript.config.Config.pathToTals);
			standardInitialised = true;
		} catch (Exception e) {
			Debug.critical(TalBase.class, "error initialising tals");
			e.printStackTrace();
		}		
	}
	
	public static boolean standardInitialised() {
		return standardInitialised;
	}
	
	public static void setStandard(TalBase talBase) {
		standard = talBase;
	}
	
	
	public TalBase(BolBaseGeneral bolBase) {
		this.bolBase = bolBase;
		tals = new ArrayList<Tal>();	
		this.addTal(Teental.getDefaultTeental(bolBase));
	}
	
	public TalBase(BolBaseGeneral bolBase, String directory) {
		this.bolBase = bolBase;
		tals = new ArrayList<Tal>();
		this.addTal(Teental.getDefaultTeental(bolBase));
		initTalsFromDirectory(directory);
	}

	public void initTalsFromDirectory(String pathToTalsNoSlash) {
		File talFolder = new File(pathToTalsNoSlash);
		File[] talFiles = talFolder.listFiles(new SuffixFilter(Config.talSuffix));
		
		for (int i = 0; i<talFiles.length; i++) {
			try {
				Debug.debug(this, "adding Tal " + talFiles[i]); 
				Tal tal = new TalDynamic(talFiles[i]);
				Debug.debug(this, "generated tal " + tal); 
				addTal(tal);
			} catch (Exception e) {
				Debug.critical(this, "Error reading tal " + e);
				e.printStackTrace();
			}
		}
	}
	
	public Tal getTalFromName(String name) {
		for (int i=0; i<tals.size(); i++) {
			Tal t;
			if (name.equalsIgnoreCase((t = tals.get(i)).getName())) {
				return t;
			}
		}
		return null;
	}
	
	
	/**
	 * Adds a tal, replaces existing ones.
	 * @param tal
	 */
	public void addTal(Tal tal) {
		
		// check if tal is initialised ?
		Tal existing = getTalFromName(tal.getName());
		if (existing != null) {
			Debug.debug(this,"Tal " + tal.getName() + " already exists, will be replaced.");
			tals.remove(existing);
		}
		
		tals.add(tal);
	}
	
	/**
	 * Adds a composition object, but only if it implements Tal,
	 * a good candidate currently is TylDynamic.
	 * @param comp
	 */
	public void addTal(Composition comp) {
		if (comp.isTal()) {
			if (Tal.class.isInstance(comp)) {
				addTal((Tal) comp);
			}
		}
	}
	
	public void removeTal(Tal tal) {
		tals.remove(tal);
	}
	
	public void removeTal(Composition comp) {
		if (Tal.class.isInstance(comp)) {
			removeTal((Tal) comp);
		}
	}
	
	public int getNrOfTals() {
		return tals.size();
	}
	
	public String toString() {
		String s = "TalBase containing " + tals.size() + " tals:\n";
		for (int i=0; i < tals.size(); i++) {
			
			s = s + tals.get(i) + "\n";
		}
		return s;
	}




}
