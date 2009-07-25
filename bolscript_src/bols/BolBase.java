package bols;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import basics.Debug;
import basics.FileReadException;
import basics.Rational;
import basics.Tools;
import bolscript.Reader;
import bolscript.config.Config;


public class BolBase extends BolBaseGeneral {
	
	/**
	 * An auxilliary private class for storig prototype properties of a
	 * potential bundle during parsing a bolbase file.
	 * @author hannes
	 */
	private class PotentialBundle {
		
		String[] labels;
		String description;
		ArrayList<String> bolNameStrings;
	}
	private class PotentialCombinedBolName {
		
		public PotentialCombinedBolName(BolName bolName,
				String[] bolNamesToBeCombined) {
			super();
			this.bolName = bolName;
			this.bolNamesToBeCombined = bolNamesToBeCombined;
		}
		BolName bolName;
		String[] bolNamesToBeCombined;
	}
	
	private static BolBase standard = null;
	public static boolean standardInitialised = false;
	
	public static void init(Class caller) {
		try {
			Debug.debug(BolBase.class, "init called by: " + caller.getSimpleName());
			setStandard(new BolBase());
			standardInitialised = true;
		} catch (Exception e) {
			Debug.critical(BolBase.class, "Error initialising BolBase!");
			System.out.println(e);
			e.printStackTrace();
			System.exit(1);
		}	
	}
	
	public static void initOnce(Class caller) {
		if (!standardInitialised)
			init(caller);
	}
	
	public static BolBaseGeneral standard() {
		return getStandard();
	}
	
	public static void setStandard(BolBase standard) {
		BolBase.standard = standard;
	}
	public static BolBase getStandard() {
		if (standard == null) init(BolBase.class);
		return standard;
	}
	public BolBase() {
		super();
		DEBUG = false;
		generalNoteOffset = 36;
		
		try {
			addBolNames("-");
			addBolNamesFromFile(Config.pathToBolBase);
			addBolNames("SurNa GeHigh");
		} catch (FileReadException ex) {
			Debug.critical(this, "BolBase could not read bolbase file. Using defaults. " + ex);
			addBolNames("Dha Ge Ti Ri Ke Te Tin Na Ne Ta Ke Dhin SurNa Tun Dhun GeHigh");
		}
		Debug.temporary(this, "bols inited: " + Tools.toString(bolNames));
		Debug.temporary(this, "bundles : " + bundleMap);
		
		
		
		try {
			initMidiMaps();
		} catch (NoBolNameException ex) {
			Debug.critical(this, "Midi maps could not be initialised. " + ex);
		}
		try{
			initBolMaps();
		} catch (NoBolNameException ex) {
			Debug.critical(this, "Bol maps could not be initialised. " + ex);
		}

		try {
			setEmptyBol("-");
		} catch (NoBolNameException ex) {
			Debug.critical(this, "Empty Bol '-' could not be set. " + ex);
		}

		try {
			initKaliMaps();
		} catch (NoBolNameException ex) {
			Debug.critical(this, "Kali maps could not be initialised. " + ex);
		}
		
		
	}
	
	private void addBolNamesFromFile(String filename) throws FileReadException {
		HashMap<String, Integer> handMap = new HashMap<String, Integer> ();
		handMap.put("LEFT", BolName.LEFT);
		handMap.put("RIGHT", BolName.RIGHT);
		handMap.put("OTHER", BolName.OTHER);
		handMap.put("COMBINED", BolName.COMBINED);
		
		ArrayList<PotentialCombinedBolName> potentialCombined = new ArrayList<PotentialCombinedBolName>();
		ArrayList<PotentialBundle> potentialBundles = new ArrayList<PotentialBundle>();
		
		Scanner scanner = new Scanner(Reader.getContents(new File(filename)));
		
		String seperator = "\\s*,\\s*";
		
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (!line.startsWith("#")) {
				//String line = scanner.nextLine();
				String [] entries = line.split(seperator);
				String description = "";
				
				if (entries.length >= BolName.languagesCount) {
					String[] labels = new String[BolName.languagesCount];
					for (int i = 0; i < labels.length; i++) {
						labels[i] = entries[i].replaceAll(Reader.SNatBeginningOrEnd, "");
					}
					//Debug.temporary(this, "exact name scanned: '" + labels[BolName.EXACT]+"'");

					if (entries.length >= BolName.languagesCount+1) {
						description = entries[BolName.languagesCount].replaceAll(Reader.SNatBeginningOrEnd,"");
						
						if (entries.length >= BolName.languagesCount+2) {
							String typeField = entries[BolName.languagesCount+1].replaceAll(Reader.SNatBeginningOrEnd,"");
							Integer handType = handMap.get(typeField.toUpperCase());

							if (handType != null) {
								//this is a bol with a known hand type
								BolName newBolName = new BolName(labels);
								newBolName.setDescription(description);
								newBolName.setHandType(handType);
								addBolName(newBolName);
								
							} else {
								//check if it is combined bol or a bundle
								
								//	Debug.temporary(this, newBolName + " looking for combined bols");
								Pattern combinedPattern = Pattern.compile("([A-Za-z0-9]+)\\s*\\+\\s*([A-Za-z0-9]+)");
								Matcher combinedMatcher = combinedPattern.matcher(typeField);

								if (combinedMatcher.find()) {
									//found a combined Bol
									BolName newBolName = new BolName(labels);
									newBolName.setDescription(description);
									newBolName.setHandType(BolName.COMBINED);
									addBolName(newBolName);
									//	Debug.temporary(this,newBolName + " found hand: " + m.group(1));
									//	Debug.temporary(this,newBolName + " found hand: " + m.group(2));
									String hand1 = combinedMatcher.group(1);
									String hand2 = combinedMatcher.group(2);
									if (hand1 != null && hand2 != null) {
										potentialCombined.add(
												new PotentialCombinedBolName(
														newBolName,
														new String[]{hand1, hand2}));
									}
								} else {
									//check if it is a bundle
									PotentialBundle potentialBundle = new PotentialBundle();
									potentialBundle.labels = entries;
									potentialBundle.description = description;
									
									Pattern bundlePattern = Pattern.compile("[A-Za-z0-9]+");
									Matcher m = bundlePattern.matcher(typeField);
									potentialBundle.bolNameStrings = new ArrayList<String>();
									while (m.find()) {
										potentialBundle.bolNameStrings.add(m.group(0));							
									}
									
									potentialBundles.add(potentialBundle);
								}
								
							}
						}
					}
					
				}
			}


		}
		
		//process combined bols
		for (int i = 0; i < potentialCombined.size(); i++) {
			PotentialCombinedBolName comb = potentialCombined.get(i);
//			Debug.temporary(this, "searching for " + bolCombinations.get(i)[0]);
			BolName hand1 = getBolName(comb.bolNamesToBeCombined[0]);
//			Debug.temporary(this, "found: " + hand1);
//			Debug.temporary(this, "searching for " + bolCombinations.get(i)[1]);
			BolName hand2 = getBolName(comb.bolNamesToBeCombined[1]);
//			Debug.temporary(this, "found: " + hand2);
			if ((hand1 != null) && (hand2 != null)) {
				//Debug.temporary(this,combinedBols.get(i) + " adding hand: " + hand1);
				//Debug.temporary(this,combinedBols.get(i) + " adding hand: " + hand2);
				comb.bolName.setHands(hand1, hand2);
			} else {
				comb.bolName.setHandType(BolName.UNKNOWN);
			}
		}
		
		//process bundles
		for (int i = 0; i < potentialBundles.size(); i++) {
			PotentialBundle potBundle = potentialBundles.get(i);
			//potentialBundleLabels.add(names);
			//potentialBundleDescriptions.add(description);
			//potentialBundles.add(currentBundle);
			
			//the first entry must be a number
			if (potBundle.bolNameStrings.size() > 1) {
				
				Rational bundleSpeed = new Rational(1);
				if (potBundle.bolNameStrings.get(0).matches("[0-9]+")) {
					bundleSpeed = Rational.parseNonNegRational(potBundle.bolNameStrings.get(0));
				}
				boolean failed = false;
				ArrayList<BolName> currentBundlesBolNames = new ArrayList<BolName>();
				for (int j = 1; j < potBundle.bolNameStrings.size(); j++) {
					BolName bn = getBolName(potBundle.bolNameStrings.get(j));
					if (bn == null) {
						failed = true;
						break;
					} else {
						currentBundlesBolNames.add(bn);
					}
				}
				if (!failed) {
					BolName[] currentArray = new BolName[currentBundlesBolNames.size()]; 
					currentArray = currentBundlesBolNames.toArray(currentArray);
					BolNameBundle bundle = new BolNameBundle(currentArray, potBundle.labels);
					bundle.setDescription(potBundle.description);
					
					addBolNameBundle(bundle);
					//Debug.temporary(this, "setting replacement bundle name to '" + bundle.getName(BolName.EXACT).replaceAll(Reader.SNatBeginningOrEnd,"") +"'");
					addReplacementPacket(bundle.getName(BolName.EXACT), 
							" ( " + bundleSpeed + " " + bundle.getExactBolNames() + " ) ");
				}
			}
		}
	}

	/**
	 * BolMaps are maps from one Bol to a Midinote, coordinates and a hand
	 * @throws Exception
	 */
	private void initMidiMaps() throws NoBolNameException {

		// Parameters: name, coordinate, midinote, hand
		
		//pause
		addMidiMap("-",  0, 0, MidiMap.NONE);
		
		//righthand only
		addMidiMap("Ti", 1, 3, MidiMap.RIGHT);
		addMidiMap("Ta", 1, 3, MidiMap.RIGHT);
		addMidiMap("Ri", 1, 4, MidiMap.RIGHT);
		addMidiMap("Te", 1, 5, MidiMap.RIGHT);
		addMidiMap("Ne", 1, 6, MidiMap.RIGHT);
		
		addMidiMap("SurNa", 2, 1, MidiMap.RIGHT);
		addMidiMap("Tun", 2, 2, MidiMap.RIGHT);
		addMidiMap("Na", 3, 0, MidiMap.RIGHT);
		
		//addMidiMap("Ta", 3, 0, MidiMap.RIGHT);

		
		//lefthand only
		addMidiMap("Ke", 1, 31, MidiMap.LEFT);
		addMidiMap("Ge", 2, 28, MidiMap.LEFT);
		addMidiMap("GeHigh",2,29, MidiMap.LEFT);
		
		Debug.debug(this, "midimaps set: " + Tools.toString(midiMaps));

	}
		
	/**
	 * BolMaps are maps from one Bol to multiple Midimaps
	 * @throws Exception
	 */
	private void initBolMaps() throws NoBolNameException {
		
		// Parameters: Bol name, left hand, right hand
		addBolMap("-", getMidiMap("-"), getMidiMap("-"));
		
		//one hand bols
		addBolMap("Ti", getMidiMap("-"), getMidiMap("Ti"));
		addBolMap("Ri", getMidiMap("-"), getMidiMap("Ri"));
		addBolMap("Ke", getMidiMap("Ke"), getMidiMap("-"));
		addBolMap("Te", getMidiMap("-"), getMidiMap("Te"));
		addBolMap("Ne", getMidiMap("-"), getMidiMap("Ne"));
		addBolMap("Ta", getMidiMap("-"), getMidiMap("Ta"));
		addBolMap("Na", getMidiMap("-"), getMidiMap("Na"));
		addBolMap("Ge", getMidiMap("Ge"), getMidiMap("-"));
		
		//two hand bols
		addBolMap("Dha", getMidiMap("Ge"), getMidiMap("Na"));
		addBolMap("Tin", getMidiMap("Ke"), getMidiMap("SurNa"));
		addBolMap("Dhin", getMidiMap("Ge"), getMidiMap("SurNa"));
		addBolMap("Tun", getMidiMap("Ke"), getMidiMap("Tun"));
		addBolMap("Dhun", getMidiMap("Ge"), getMidiMap("Tun"));		
		addBolMap("SurNa", getMidiMap("-"), getMidiMap("SurNa"));
		
		Debug.debug(this, "bolsMaps set: " + Tools.toString(bolMaps));
	}
	
	public void initKaliMaps() throws NoBolNameException {
		//auto-add KaliMaps where left hand is empty or nonresonant
		for (BolName bolName : getBolNames()) {
			try {
				BolMap bm = getBolMap(bolName);
				if ((bm.getLeftHand().getBolName() == emptyBol)||(bm.getLeftHand().getCoordinate()==1)) {
					//left hand is empty or nonresonant, keep in kali
					addKaliMap(bolName, bolName);
				} 	
			} catch (Exception e) {
				out("not adding kaliMap, no BolMap for " + bolName);
			}
							
		}	
		//add remaining
		addKaliMap("Ge", "Ke");
		addKaliMap("Dha", "Na");
		addKaliMap("Dhin", "Tin");
	}

}
