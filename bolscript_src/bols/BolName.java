/**
 * 
 */
package bols;

import basics.Debug;

/**
 * Represents one general Tabla Bol with its different names in different languages.
 * No Speed or playing style or speed is included in this class.
 * @see Bol
 * @author Hannes
 */
public class BolName implements NamedInLanguages {
	public static final int SIMPLE = 0;
	public static final int EXACT = 1;
	public static final int DEVANAGERI = 2;
	public static final int TRANSLITERATION = 3;
	public static final int INITIALS = 4;
	
	public static final int languagesCount = 5;
	
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int COMBINED = 2;
	public static final int OTHER = 3;
	public static final int UNKNOWN = 4;
	
	public static final String[] languageNames = {"Simple", "Exact", "Devanagari", "Transliteration", "Initials"};
	public static final String[] handTypes = {"Left", "Right", "Combined", "Other" , "Unknown"};
	
	public boolean wellDefinedInBolBase = true;
	
	private String[] labels;
	private int handType;
	private BolName leftHand = null;
	private BolName rightHand = null;
	private String description = null;
	
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
	
}
