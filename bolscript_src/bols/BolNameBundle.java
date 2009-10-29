package bols;

import java.util.ArrayList;
import java.util.Arrays;

import basics.GUI;
import basics.Tools;
import bolscript.sequences.Representable;
import bolscript.sequences.RepresentableSequence;

/**
 * Contains an array of BolNames, and a bundled String representation for each language of BolName.
 * @author hannes
 *
 */
public class BolNameBundle implements NamedInLanguages {
	
	/**
	 * The displayable String version of this bundle in the different languages set in BolName
	 */
	private String[] labels;
	
	/**
	 * The bolnames which build the sequence of this bundle
	 */
	private BolName[] bolNames;
	
	/**
	 * A String built of the EXACT bolnames, which gives us a unique identifier
	 */
	private String exactBolNames;
	
	private String description;
	
	/**
	 * @param bolNames
	 * @param labels
	 */
	public BolNameBundle (BolName[] bolNames, String[] labels){
		this.bolNames = bolNames;
		this.labels = labels;
		description = "";
		generateExactBolNames();
	}
	
	/**
	 * Generates a unique representation of the contained bolNames as a String by
	 * concatenating their exact names, seperated by whitespaces.
	 */
	private void generateExactBolNames() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < bolNames.length; i++) {
			b.append(bolNames[i].getName(BolName.EXACT));
			if (i < bolNames.length-1) {
				b.append(" ");
			}
		}
		exactBolNames = b.toString();	
	}

	
	/**
	 * Returns the bundles representation of the bolnames.
	 * @param language
	 * @return
	 */
	public String getName(int language) {
		return labels[language];
	}
	
	public String toString() {
		return labels[BolName.SIMPLE];
	}
	
	public String toStringComplete() {
		return "("+labels[BolName.SIMPLE]+", "+labels[BolName.EXACT]+", "+labels[BolName.DEVANAGERI]+", "+labels[BolName.TRANSLITERATION]+"), ["+Arrays.toString(bolNames)+"]";
	}
	
	public BolName[] getBolNames () {
		return bolNames;
	}
	
	/**
	 * Two bundles are equal if both have the same array of bolnames.
	 */
	public boolean equals(BolNameBundle b) {
		BolName[] othersNames = b.getBolNames();
		if (othersNames.length != bolNames.length) {
			for (int i = 0; i<bolNames.length; i++) {
				if (!bolNames[i].equals(othersNames[i])) return false;
			}
			return true;
		} else return false;
	}

	/**
	 * Returns a unique representation of the contained bolnames as a String,
	 * containing their BolName.EXACT names.
	 */
	public String getExactBolNames() {
		return exactBolNames;
	}
	
	
	/**
	 * A simple bundle builder. Simply concatenates all bolnames.
	 * @param bolNames
	 * @return
	 */
	public static BolNameBundle getDefault(BolName[] bolNames) {
		StringBuilder[] labels = new StringBuilder[BolName.languagesCount];
		String[] slabels = new String[BolName.languagesCount];
		BolName emptyBol = BolBase.getStandard().getEmptyBol();
		
		for (int i=0; i < BolName.languagesCount; i++) {
			labels[i] = new StringBuilder();
			
			int lastNonEmptyBolIndex = 0;
			
			for (int j = 0; j < bolNames.length;j++) {
				labels[i].append(bolNames[j].getName(i));
				
				if (!emptyBol.equals(bolNames[j])) {
					//is no pause
					lastNonEmptyBolIndex = labels[i].length();
				} else {
					//is a pause
					if (j<bolNames.length-1) {
						labels[i].append(" ");
					}
				}
				
			}
			if (lastNonEmptyBolIndex == 0) {
				//this means the bundle consists only of pauses
				//it will be replaced by one pause
				slabels[i] = emptyBol.getName(i);
			} else {
				//empty bols at the end are cut off
				slabels[i] = labels[i].substring(0, lastNonEmptyBolIndex);
				slabels[i] = BolName.formatString(slabels[i], i);
			}
			
		}
		
		return new BolNameBundle(bolNames,slabels);

	}


	/**
	 * A simple bundle builder. Simply concatenates all bolnames.
	 * @param bolNames
	 * @return
	 */
	public static BolNameBundle getDefault(RepresentableSequence currentBundle) {
		ArrayList<BolName> names = new ArrayList<BolName>();
			
		for (Representable r : currentBundle) {
			if (r.getType() == Representable.BOL) {
				names.add(((Bol) r).getBolName());
			}
		}
		BolName[] namesArray = new BolName[names.size()];
		namesArray = names.toArray(namesArray);
		
		return getDefault(namesArray);
	}



	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	
	/*
	
	private String getExactBolNames(RepresentableSequence seq) {
		StringBuilder b = new StringBuilder();
		
		for (int i = 0; i < seq.size(); i++) {
			Representable rep = seq.get(i);
			if (rep.getType() == Representable.BOL) {
				b.append(((Bol) rep).getBolName().getName(BolName.EXACT));
				if (i < seq.size()-1) {
					b.append(" ");
				}
			}
			
		}
		exactBolNames = b.toString();	
	}*/
	
	
}
