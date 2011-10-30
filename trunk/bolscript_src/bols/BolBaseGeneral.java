package bols;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import basics.Debug;
import bolscript.packets.Packet;
import bolscript.packets.Packets;
import bolscript.packets.types.PacketTypeDefinitions;

public abstract class BolBaseGeneral {

	protected ArrayList<BolName> bolNames, wellDefinedBolNames;
	protected HashMap<String, BolNameBundle> bundleMap;
	protected ArrayList<BolMap> bolMaps;
	protected ArrayList<MidiMap> midiMaps;
	protected HashMap<BolName,BolName> kaliMaps;

	protected Packets standardReplacements;
	protected ArrayList<BolNameBundle> standardBolNameBundles;


	protected int generalNoteOffset;

	protected BolName emptyBol;

	protected int nrOfWellDefinedBolNames;

	// bolNames = new ArrayList();

	public BolBaseGeneral () {
		bolNames = new ArrayList<BolName>();
		wellDefinedBolNames = new ArrayList<BolName>();

		bolMaps = new ArrayList<BolMap>();
		midiMaps = new ArrayList<MidiMap>();
		kaliMaps = new HashMap<BolName,BolName>();
		bundleMap = new HashMap<String,BolNameBundle>();
		standardReplacements = new Packets();
		standardBolNameBundles = new ArrayList<BolNameBundle>();

		generalNoteOffset = 0;
	}

	public BolName getEmptyBol() {
		//pause bol
		return emptyBol;
	}

	protected void setEmptyBol(BolName emptyBol) {
		this.emptyBol = emptyBol;
	}

	/**
	 * Marks the Bol with BolName.EXACT equaling the name as the empty Bol, i.e. an explicit pause.
	 * @param name
	 * @throws NoBolNameException If there is no Bol with the given name.
	 */
	protected void setEmptyBol(String name) throws NoBolNameException {
		BolName bolName = getBolName(name);
		if (bolName == null) {
			throw new NoBolNameException("emptyBol not set", name);
		} else {
			emptyBol = bolName;
		}
	}


	public void addBolNames(String strBolNames) {
		//strBolNames like "Dha Ge Dhin "...

		Pattern p = Pattern.compile(" ");
		String[] nameArray = p.split(strBolNames);

		//in constructor?
		//bolNames.clear();

		for (int i=0; i < nameArray.length; i++) {
			addBolName(new BolName(nameArray[i]));
			//bolNames.add(new BolName(nameArray[i]));
		}

	}

	/**
	 * Adds a BolName to the BolBase, overwrites any existing with an equal exact name.
	 * @param bolName
	 * @param addIfDifferentCase TODO
	 */
	public void addBolName(BolName bolName, boolean addIfDifferentCase) {
		addBolName(bolName);
	}

	/**
	 * Adds a BolName to the BolBase, overwrites any existing with an equal exact name.
	 * Determines and assigns the weakest CaseSensitivityMode needed to distinguish similar bols.
	 * @param bolName
	 */
	public void addBolName(BolName bolName) {
		boolean abort = false;

		String exactName = bolName.getName(BolName.EXACT);

		ArrayList<BolName> existingCandidates = new ArrayList<BolName>();
		BolName existingExactMatch = null;
		for(BolName existingBolName : bolNames) {
			if (existingBolName.matchesExactName(exactName, BolName.CaseSensitivityModes.None)) {
				existingCandidates.add(existingBolName);
			}
			if (existingBolName.matchesExactName(exactName, BolName.CaseSensitivityModes.ExactMatch)) {
				existingExactMatch = existingBolName;
			}
		}
		if (existingCandidates.size() == 0) {	

		} else if (existingExactMatch != null) {
			bolNames.remove(existingExactMatch);
			if (existingExactMatch.isWellDefinedInBolBase()) {
				wellDefinedBolNames.remove(existingExactMatch);
			}			
			// is this ok?
			bolName.setCaseSensitivityMode(existingExactMatch.getCaseSensitivityMode());

		}  else {
			//determine a case sensitivity mode, at which all existing are 
			//distinguishable from the new bolname
			BolName.CaseSensitivityModes[] modeStrictnesses = new BolName.CaseSensitivityModes[]{
					BolName.CaseSensitivityModes.FirstLetter,
					BolName.CaseSensitivityModes.ExactMatch
			};
			boolean areDistinguishable = false; 
			int strictnessIndex = 0;
			while (areDistinguishable == false && strictnessIndex < modeStrictnesses.length) {
				areDistinguishable = true;
				for (BolName existing : existingCandidates) {
					if (existing.matchesExactName(exactName, modeStrictnesses[strictnessIndex])) {
						areDistinguishable = false;
					}
				}
				if (!areDistinguishable) strictnessIndex++;
			}
			if (!areDistinguishable) {
				Debug.debug(this, "BolName " + bolName.toString(BolName.EXACT) + 
						" will not be added, because no case strictness " +
				"was strong enough to distinguish it from existing bolnames.");
				abort = true;
			} else {
				//all are set to the determined mode
				BolName.CaseSensitivityModes determinedMode = modeStrictnesses[strictnessIndex];
				for (BolName existing : existingCandidates) {
					existing.setCaseSensitivityMode(determinedMode);
				}
				bolName.setCaseSensitivityMode(determinedMode);
			}
		}
		if (!abort){
			bolNames.add(bolName);
			if (bolName.isWellDefinedInBolBase()) {
				wellDefinedBolNames.add(bolName);
			}
		}
	}


	public ArrayList<BolName> getBolNames() {
		return bolNames;
	}


	/**
	 * Returns the BolName with the given exact Name. If none is found a second attempt is made 
	 * ignoring the case.
	 * Returns null if none was found.
	 * @param exactName
	 * @return
	 */
	public BolName getBolName(String exactName) {		
		for(BolName bolName: bolNames) {
			if (bolName.matchesExactName(exactName)) return bolName;
		}		
		return null;
	}

	/**
	 * Returns the bolNameBundle fitting to the String containing a sequence of exact whitespace-seperated bolnames,
	 * or Null if none exists.
	 * @param exactNames
	 * @return
	 */
	public BolNameBundle getBolNameBundle(String exactNames) {
		//Debug.temporary(this,"getBolNameBundle for '" + exactNames + "'");
		//Debug.temporary(this,"result: " + bundleMap.get(exactNames));
		return bundleMap.get(exactNames);
	}

	/*public BolNameBundle getOrGenerateBolNameBundle() {

	}*/

	/**
	 * Adds a bolname bundle. Unlike the other (older) methods, this overwrites existing.
	 * @param bundle
	 */
	public void addBolNameBundle(BolNameBundle bundle) {
		bundleMap.put(bundle.getExactBolNames(), bundle);
	}

	public void addReplacementPacket(String key, String val, BolNameBundle bolNameBundle) {
		Packet replacementPacket = new Packet(key, val, PacketTypeDefinitions.BOLS, false);
		replacementPacket.setExcludedFromSearch(true);
		replacementPacket.setObject(bolNameBundle);
		standardReplacements.add(replacementPacket);
	}

	/**
	 * Returns packets of type BOL, which are supposed to be used
	 * for key -> value inserting in Reader.
	 */
	public Packets getReplacementPackets () {
		return standardReplacements;
	}

	/**
	 * Returns packets of type BOL, which are supposed to be used
	 * for key -> value inserting in Reader.
	 */
	public Packets getReplacementPacketClones() {
		Packets r = new Packets();
		for (Packet p: standardReplacements) {
			r.add(p.cloneClearObjAndTextRef());
		}
		return r;
	}

	public ArrayList<BolNameBundle> getStandardBolNameBundles() {
		return standardBolNameBundles;
	}

	public BolMap getBolMap(BolName bolName) {

		boolean found=false;
		int i=0;
		while ((i < bolMaps.size())&&(!found)) {
			if (bolMaps.get(i).getBolName().equals(bolName)) {
				found = true;
			}
			i++;
		}

		if (found) {
			return ((BolMap) bolMaps.get(i-1));
		} else {
			return null;
		}
	}

	public MidiMap getMidiMap(BolName bolName, int hand) {

		MidiMap map = null;

		boolean found=false;
		int i=0; 
		while ((i < midiMaps.size())&&(!found)) {
			MidiMap currentMap = ((MidiMap) midiMaps.get(i));
			if ( (currentMap.getBolName().equals(bolName))
					&& (currentMap.getHand() == hand) ) {
				found = true;
				map = (MidiMap) midiMaps.get(i);
			}
			i++;
		}

		if (found) {
			return map;
		} else {
			//out("midiMap with bolName: " + bolName.toString() + " not found");
			return null;
		}
	}

	public MidiMap getMidiMap(BolName bolName) {

		MidiMap map = null;

		int i=0; 
		while (i < midiMaps.size()) {
			MidiMap currentMap = midiMaps.get(i);
			//Debug.temporary(this, "comparing " + bolName + " to " + currentMap.getBolName());
			if (currentMap.getBolName().equals(bolName)) {
				//Debug.temporary(this, "are equal");
				map = midiMaps.get(i);
				return map;
			}
			i++;
		}
		return null;
	}

	public MidiMap getMidiMap(String name) {
		BolName bolName = getBolName(name);
		if (bolName != null) {
			return getMidiMap(bolName);
		} else {
			//out("midimap not found, bolName " + name + " not found");
			return null;
		}	
	}


	public void addMidiMap(MidiMap map){
		MidiMap existingMap = getMidiMap(map.getBolName());
		if (existingMap!=null) {
			midiMaps.remove(existingMap);
		}
		midiMaps.add(map);
	}

	public MidiMap addMidiMap(String name, int coordinate, int noteValue, int hand)  {

		BolName bolName = getBolName(name);
		//Debug.temporary(this, "adding Midimap, getBolName("+name+") = " + bolName);
		if (bolName != null) {
			MidiMap map = new MidiMap(bolName, coordinate, noteValue, hand);
			//Debug.temporary(this, "generated midimap: " + map);
			addMidiMap(map);
			return map;
		} else {
			return null;
		}
		/*else {
			throw new NoBolNameException("midiMap not added", name);
		}	*/
	}

	public void addKaliMap(BolName name1, BolName name2){
		//check if already exists...
		BolName existingKaliBol = kaliMaps.get(name1);

		if (existingKaliBol==null) {
			kaliMaps.put(name1,name2);
			Debug.debug(this, "kaliBol: " + name1 + " -> " + name2 + " added");
		} else {
			Debug.debug(this, "kaliBol for bolName: " + name1 + " already exists");
		}

	}

	public void addKaliMap(String name, String name2) throws NoBolNameException {
		BolName bolName = getBolName(name);
		BolName bolName2 = getBolName(name2);
		if ((bolName != null)&&(bolName2 != null)) {
			kaliMaps.put(bolName, bolName2);
		} else {
			throw new NoBolNameException("kaliMap not added", name);
		}	
	}

	public BolName getKaliBolName(BolName bolName) {
		BolName kb = kaliMaps.get(bolName);
		if (kb!=null) {
			return kb;
		} else {
			Debug.debug(this, "no kali map found for " + bolName);
			return bolName;
		}
	}

	public void addBolMap(BolMap map) {
		//check if already exists...
		BolMap existingMap = getBolMap(map.getBolName());

		if (existingMap==null) {
			bolMaps.add(map);
			Debug.debug(this, map + "added");
		} else {
			Debug.debug(this, "BolMap with bolName: " + map.getBolName().toString() + " already exists");
		}
	}

	public void addBolMap(String name, MidiMap hand1, MidiMap hand2) throws NoBolNameException {
		BolName bolName = getBolName(name);
		if (bolName != null) {
			BolMap map = new BolMap(bolName, hand1, hand2);
			addBolMap(map);
		} else {
			throw new NoBolNameException("bolMap not added", name);
		}	
	}

	public void addBolMap(String name, MidiMap hand1, MidiMap hand2, double scale1, double scale2) throws NoBolNameException {
		BolName bolName = getBolName(name);
		if (bolName != null) {
			BolMap map = new BolMap(bolName, hand1, hand2, scale1, scale2);
			addBolMap(map);
		} else {
			throw new NoBolNameException("bolMap not added", name);
		}	
	}

	public int getGeneralNoteOffset() {
		return generalNoteOffset;
	}

	public void setGeneralNoteOffset(int generalNoteOffset) {
		this.generalNoteOffset = generalNoteOffset;
	}

	public void printAll () {
		String strBolNames= "\n----\n"+bolNames.size()+" BolNames\n";
		String strMidiMaps = "\n\n----\n"+midiMaps.size()+" MidiMaps\n";
		String strBolMaps = "\n----\n"+bolMaps.size()+" BolMaps\n";

		for (int i=0; i < bolNames.size();i++){
			strBolNames += ((BolName)bolNames.get(i)).toString() + ", ";		
		}

		for (int i=0; i < midiMaps.size();i++){
			strMidiMaps += ((MidiMap)midiMaps.get(i)).toString()+ "\n";		
		}

		for (int i=0; i < bolMaps.size();i++){
			strBolMaps += ((BolMap)bolMaps.get(i)).toString()+ "\n";		
		}

		Debug.debug(this, strBolNames + strMidiMaps + strBolMaps);
	}

	public double getDifference(BolMap bm1, BolMap bm2) {
		int diffLeft = bm1.getLeftHand().getCoordinate() - bm2.getLeftHand().getCoordinate();
		int diffRight = bm1.getRightHand().getCoordinate() - bm2.getRightHand().getCoordinate();
		double diff = Math.sqrt(diffLeft*diffLeft + diffRight*diffRight);
		return diff;
	}

	public double getDifference(String name1, String name2) {
		return getDifference(getBolMap(getBolName(name1)), getBolMap(getBolName(name2)));
	}
	public double getDifference(BolName name1, BolName name2) {
		return getDifference(getBolMap(name1), getBolMap(name2));
	}

	public ArrayList<BolName> getWellDefinedBolNames() {
		return wellDefinedBolNames;
	}

}
