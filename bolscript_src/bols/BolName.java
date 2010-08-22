/**
 * 
 */
package bols;

import basics.Debug;
import basics.Tools;

/**
 * Represents one general Tabla Bol with its different names in different languages.
 * No Speed or playing style or speed is included in this class.
 * @see Bol
 * @author Hannes
 */
public class BolName implements NamedInLanguages {
	
	public static final int EXACT = 0;
	public static final int SIMPLE = 1;
	public static final int DEVANAGERI = 2;
	public static final int TRANSLITERATION = 3;
	public static final int INITIALS = 4;
	
	public static final int languagesCount = 5;
	
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int COMBINED = 2;
	public static final int OTHER = 3;
	public static final int UNKNOWN = 4;
	
	public static final String[] languageNames = {"Exact","Simple", "Devanagari", "Transliteration", "Initials"};
	public static final String[] handTypes = {"Left", "Right", "Combined", "Other" , "Unknown"};
	
	public boolean wellDefinedInBolBase = true;
	
	private String[] labels;
	private int handType;
	private BolName leftHand = null;
	private BolName rightHand = null;
	private String description = null;
	
	public enum CaseSensitivityModes {
		None,
		FirstLetter,
		ExactMatch
	}
	
	private CaseSensitivityModes caseSensitivityMode = CaseSensitivityModes.None;
	
	
	public CaseSensitivityModes getCaseSensitivityMode() {
		return caseSensitivityMode;
	}

	public void setCaseSensitivityMode(CaseSensitivityModes caseSensitivityMode) {
		this.caseSensitivityMode = caseSensitivityMode;
	}

	/**
	 * Compares the bolnames exact version to the one given as a parameter, based
	 * on the BolName's CaseSensitivityMode
	 * @param exactName
	 * @return
	 */
	public boolean matchesExactName(String exactName) {
		return matchesExactName(exactName, caseSensitivityMode);
	}

	/**
	 * Compares the bolnames exact version to the one given as a parameter, based
	 * on the given CaseSensitivityMode
	 * @param exactName
	 * @param caseSensitivityMode TODO
	 * @return
	 */
	public boolean matchesExactName(String exactName, CaseSensitivityModes caseSensitivityMode) {
		if (labels[EXACT].length() != exactName.length()) return false;
		
		switch (caseSensitivityMode) {
			case None:
				return this.labels[EXACT].equalsIgnoreCase(exactName);
			case FirstLetter:
				try {

					if (labels[EXACT].charAt(0) == exactName.charAt(0)) {
						if (labels[EXACT].length() >=2) {
							return labels[EXACT].substring(1).equalsIgnoreCase(exactName.substring(1));
						} else return true;
					}
				} catch (Exception ex) {
					return false;
				}
			default:
				return labels[EXACT].equals(exactName);
		}
	}
	
	public BolName () {
		this("Unknown");
	}
	
	public BolName (String simpleName) {
		labels = new String[languagesCount];
		for(int i=0; i<languagesCount;i++) {
			labels[i] = simpleName;
		}
		this.handType = UNKNOWN;
		this.description = "";
	}
	
	
	public BolName (String[] labels) {
		this.labels = labels;
		this.handType = UNKNOWN;
		this.description = "";
	}

	public int getHandType() {
		return handType;
	}
	
	/**
	 * Do not use this for COMBINED
	 * use setHands for Combined
	 * @param handType
	 */
	public void setHandType(int handType) {
		this.handType = handType;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String d) {
		description = d;
	}
	
	public void setHands(BolName bol1, BolName bol2) {
//		Debug.temporary(this, "setHands " + bol1.toStringComplete() + ", " + bol2.toStringComplete() );
		addHand(bol1); 
		addHand(bol2);
		
		if ((leftHand!=null) && (rightHand != null) ) {
			handType = COMBINED;
		} else handType = UNKNOWN;
	}
	
	private void addHand(BolName handBol) {
		if (handBol.getHandType() != COMBINED) {
			handType = COMBINED;
			if (handBol.getHandType() == LEFT) {
//				Debug.temporary(this, "addHand " + handBol);
				leftHand = handBol;
			} else if (handBol.getHandType() == RIGHT) {
//				Debug.temporary(this, "addHand " + handBol);
				rightHand = handBol;
			}
		}
	}
	
	/**
	 * Use this only if type is COMBINED!
	 * @return
	 */
	public BolName getLeftHand() {
		if (handType == COMBINED) {
			return leftHand;
		} else return null;
	}
	
	/**
	 * Use this only if type is COMBINED!
	 * @return
	 */
	public BolName getRightHand() {
		if (handType == COMBINED) {
			return rightHand;
		} else return null;
	}
	
	public String toString () {
		return labels[SIMPLE];
	}
	
	
	
	public String toStringForToolTip() {
		return //"("+labels[SIMPLE]+", "+labels[EXACT]+", "+labels[DEVANAGERI]+", "+labels[TRANSLITERATION]+")<br>" +
		"Description: " + description + ", "+ 
		"Hands: " + handTypes[handType] + 
		((handType == COMBINED)?", left: " + leftHand + ", right: " + rightHand + " ":"");
	}
	public String toStringComplete() {
		return "("+labels[SIMPLE]+", "+labels[EXACT]+", "+labels[DEVANAGERI]+", "+labels[TRANSLITERATION]+")\n" +
				", Description: " + description + ", "+ 
				"Hands: " + handTypes[handType] + 
				((handType == COMBINED)?", left: " + leftHand + ", right: " + rightHand + " ":"");
	}
	
	public String getNameShort() {
		return labels[SIMPLE];
	}
	
	public String getName(int language) {
		return language < languagesCount ? labels[language] : labels[SIMPLE];
	}
	
	public String getNameForDisplay(int language) {
		if (caseSensitivityMode != CaseSensitivityModes.None) {
			return getName(language);	
		} else {			
			String candidate;
			if (language >= languagesCount) {
				candidate = labels[SIMPLE];   
			} else candidate = labels[language];
			
			switch (language) {
			case DEVANAGERI:
				return candidate;
			case TRANSLITERATION:
				return candidate;
			default:
				return Tools.formatFirstCapital(candidate);	
			}
		}
	}
	
	public String toString(int language) {
		return language < languagesCount ? labels[language] : labels[SIMPLE];
	}
	
	public void setAllLabels(String pLabel) {
		labels[SIMPLE] = pLabel;
		labels[EXACT] = pLabel;
		labels[DEVANAGERI] = pLabel;
		labels[TRANSLITERATION] = pLabel;
	}
	
	public boolean equals(Object obj) {
		return (this.toString() == obj.toString());
	}
	
	public boolean equals(BolName b) {
		return (labels[EXACT].equals(b.getName(BolName.EXACT)));
	}

	public boolean isWellDefinedInBolBase() {
		return wellDefinedInBolBase;
	}

	public void setWellDefinedInBolBase(boolean wellDefinedInBolBase) {
		this.wellDefinedInBolBase = wellDefinedInBolBase;
	}
	
	/**
	 * Formats a string according to the formatting rules of the language,
	 * i.e. transliterated is formed to lower case, while simple is formated like a name with
	 * capitalization of the first letter.
	 * @param s
	 * @param language
	 * @return the formatted String
	 * @deprecated
	 */
	public static String formatString(String s, int language) {
		switch (language) {	
		case (DEVANAGERI) :
			return s;
		case (TRANSLITERATION) :
			return s.toLowerCase();
		default:
			return Tools.formatName(s);
		}
	}
}
