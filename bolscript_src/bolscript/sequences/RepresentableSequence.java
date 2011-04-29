package bolscript.sequences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import basics.Debug;
import basics.Rational;
import bols.Bol;
import bols.BolBase;
import bols.BolBundle;
import bols.BolName;
import bols.BolNameBundle;
import bols.BundlingDepthToSpeedMap;
import bols.HasPlayingStyle;
import bols.PlayingStyle;
import bolscript.packets.Packet;
import bolscript.packets.TextReference;
import bolscript.scanner.Parser;


public class RepresentableSequence implements Representable, Collection<Representable>, List<Representable> {

	public static boolean[] SHOW_ALL = new boolean[Representable.nrOfTypes];
	public static boolean[] SNIPPET = 
		getToStringPattern(new int[]{
				Representable.BOL, 
				Representable.BUNDLE});
	public static boolean[] SHORT_SNIPPET = 
		getToStringPattern(new int[]{
				Representable.BOL,
				Representable.BUNDLE,
				Representable.BRACKET_CLOSED, 
				Representable.BRACKET_OPEN, 
				Representable.COMMA});
	public static boolean[] FOR_SEARCH_STRING = 
		getToStringPattern (new int[]{
				Representable.BOL, 
				Representable.BUNDLE});
	public static boolean[] FOR_SEARCH_STRING_NO_PAUSES = 
		getToStringPattern (new int[]{
				Representable.BOL, 
				Representable.BUNDLE});

	static {
		for (int i=0; i < Representable.nrOfTypes; i++) {
			SHOW_ALL[i] = true;
		}

	}
	/**
	 * Returns an boolean array which can be used in toStringByPattern(..),
	 * storing true or false the display of each representable type.
	 * @param visibleTypeNumbers The type nrs of the representable types which shall be shown by toStringByPattern
	 */
	public static boolean [] getToStringPattern(int[] visibleTypeNumbers) {
		boolean[] pattern = new boolean[Representable.nrOfTypes];
		for (int v : visibleTypeNumbers) {
			pattern[v] = true;
		}
		return pattern;
	}

	private boolean flattened = false;

	private boolean someThingsAreCurrentlyCached = false; 
	private String cachedSnippet = null;
	private String cachedShortSnippet = null;
	private Rational cachedDuration = null;
	private HashMap<Rational, RepresentableSequence> cachedFlattened = new HashMap<Rational, RepresentableSequence>();

	private ArrayList<Representable> cachedFailedUnits = null;
	private ArrayList<ReferencedBolPacketUnit> cachedReferencedBolPacketUnits = null;
	private ArrayList<FootnoteUnit> cachedFootnoteUnits = null;

	private TextReference textReference;

	private ArrayList<Representable> sequence;

	
	
	private Packet referencedSpeedUnitPacket = null;
	
	
	public void setTextReference(TextReference textReference) {
		this.textReference = textReference;
	}

	public TextReference getTextReference() {
		return textReference;
	}

	public RepresentableSequence() {
		sequence = new ArrayList<Representable>();
	}

	public RepresentableSequence(Collection<? extends Representable> c) {
		sequence = new ArrayList<Representable>(c);
	}

	public RepresentableSequence(int initialCapacity) {
		sequence = new ArrayList<Representable>(initialCapacity);
	}

	/**
	 * This is the only way of creating a sequence which is marked as flat.
	 * This should only be called in flatten(...)
	 * @param isFlattened
	 */
	public RepresentableSequence(boolean isFlattened) {
		this();
		this.flattened = isFlattened;
	}


	public int getType() {
		return Representable.SEQUENCE;
	}

	/**
	 * Empties all caches, including Duration, Snippets.
	 * Caches are only emptied however if setCacheEstablished
	 * was called previously at some point. 
	 * Also calls clearCache in all subsequences.
	 * 
	 */
	public void clearCache() {
		if (someThingsAreCurrentlyCached) {
			
			cachedDuration = null;
			cachedShortSnippet = null;
			cachedSnippet = null;
			cachedFlattened.clear();
			cachedFailedUnits = null;
			cachedReferencedBolPacketUnits = null;
			
			for (Representable r: sequence) {
				if (r.getType() == Representable.SEQUENCE) {
					((RepresentableSequence) r).clearCache();
				}
			}
			
			this.someThingsAreCurrentlyCached = false;
		}
	}

	/**
	 * Notifies that at least one cache object exists.
	 * This is used by clearCache() to determine quickly 
	 * if there is any cache to clear at all. 
	 */
	private void setCacheEstablished(){
		this.someThingsAreCurrentlyCached = true;
	}
	
	/**
	 * Rebuilds cache of 
	 * - failed units
	 * - referenced Bol packet units
	 * - footnote units
	 */
	private void rebuildUnitCache() {
		cachedFailedUnits = new ArrayList<Representable>();
		cachedReferencedBolPacketUnits = new ArrayList<ReferencedBolPacketUnit>();
		cachedFootnoteUnits = new ArrayList<FootnoteUnit>();
		
		for (int i=0; i < size(); i++) {
			Representable r = get(i);
			switch (r.getType()) {
			case Representable.BOL:
				if (!((Bol) r).getBolName().isWellDefinedInBolBase()) {
					cachedFailedUnits.add(r);
				}
				break;
			case Representable.FAILED:
				cachedFailedUnits.add(r);
				break;
			case Representable.SEQUENCE:
				cachedFailedUnits.addAll(((RepresentableSequence) r).getFailedUnits());
				cachedReferencedBolPacketUnits.addAll(((RepresentableSequence) r).getReferencedBolPacketUnits());
				cachedFootnoteUnits.addAll(((RepresentableSequence) r).getFootnoteUnits());
				break;
			case Representable.REFERENCED_BOL_PACKET:
				cachedReferencedBolPacketUnits.add((ReferencedBolPacketUnit) r);
				break;
			case Representable.FOOTNOTE:
				cachedFootnoteUnits.add((FootnoteUnit) r);
				break;
			}	
		}
		
		setCacheEstablished();
	}
	
	
	public void setReferencedSpeedPacket(Packet speedPacket) {
		this.referencedSpeedUnitPacket = speedPacket;		
	}
	
	public Packet getReferencedSpeedPacket() {
		return referencedSpeedUnitPacket;
	}
	
	
	/**
	 * Returns all referenced bol packet units.
	 * The entries are gathered from the sequence and its subsequences,
	 * but not in its referenced packets sequences.
	 */
	public ArrayList<ReferencedBolPacketUnit> getReferencedBolPacketUnits() {
		if (cachedReferencedBolPacketUnits == null) {
			rebuildUnitCache();
		}
		return cachedReferencedBolPacketUnits;
	}
	
	
	/**
	 * Returns all failed units and bols that were not in the bolbase.
	 * The entries are gathered from the sequence and its subsequences,
	 * but not in its referenced packets sequences.
	 */
	public ArrayList<Representable> getFailedUnits() {
		if (cachedFailedUnits == null) {
			rebuildUnitCache();
		}
		return cachedFailedUnits;
	}
	
	public ArrayList<FootnoteUnit> getFootnoteUnits() {
		if (cachedFootnoteUnits == null) {
			rebuildUnitCache();
		}
		return cachedFootnoteUnits ;
	}

	/**
	 * Strips brackets, removes double commas, double speeds, linebreaks, footnoes
	 * @return
	 */
	public RepresentableSequence getCompact() {
		RepresentableSequence compactSeq = new RepresentableSequence();
		Rational currentSpeed = new Rational(1);

		boolean commaAlreadyInserted = false;
		boolean bolsBetweenSpeeds = true;
		int indexOfLastBol = 0;

		for (int i=0; i < size(); i++) {
			Representable r = get(i);

			switch (r.getType()) {
			case Representable.BOL:
				commaAlreadyInserted = false;
				bolsBetweenSpeeds = true;
				compactSeq.add(r);
				break;
			case Representable.BUNDLE:
				commaAlreadyInserted = false;
				bolsBetweenSpeeds = true;
				compactSeq.add(r);
				break;				
			case Representable.FOOTNOTE:
				compactSeq.add(r);				
				break;			
			case Representable.COMMA:
				if (!commaAlreadyInserted) {
					compactSeq.add(r);
					commaAlreadyInserted = true;
				}
				break;
			case Representable.SPEED:
				Rational newSpeed = (Rational) ((Unit) r).getObject();

				if (! bolsBetweenSpeeds) {
					// go back and remove the last speed, as it will be 
					// overwritten without having affected any bols
					int j = compactSeq.size()-1;
					while ( (j>0) && (compactSeq.get(j).getType() != Representable.SPEED)) {
						j--;
					}
					compactSeq.remove(j);
					compactSeq.add(r);
					bolsBetweenSpeeds = false;

				} else if (!  newSpeed.equals(currentSpeed)) {
					//bols between, but only add new speed if it is different

					compactSeq.add(r);
					currentSpeed = newSpeed;
					bolsBetweenSpeeds = false;
				}

				break;

			}
		}

		//remove colons and speeds at the end
		int i = compactSeq.size() -1;
		while ((i>0) && (compactSeq.get(i).getType() != Representable.BOL) && 
				(compactSeq.get(i).getType() != Representable.BUNDLE)
				&& (compactSeq.get(i).getType() != Representable.FOOTNOTE)) {
			compactSeq.remove(i);
			i--;
		}

		return compactSeq;

	}


	/**
	 * Returns the maximum occurring speeds, also scanning bundles.
	 * For this only the Speedtags are searched.
	 * @return
	 */
	public Rational getMaxSpeed() {
		Rational maxSpeed = new Rational(1);
		for (Representable r: this) {
			if (r.getType() == Representable.SPEED) {
				maxSpeed = Rational.max(maxSpeed, (Rational) ((Unit) r).getObject());
			} else if (r.getType() == Representable.BUNDLE) {
				maxSpeed = Rational.max(maxSpeed, ((BolBundle)r).getSequence().getMaxSpeed());
			}
		}
		return maxSpeed;
	}

	/**
	 * Returns the maximum occurring speeds, also scanning bundles.
	 * It is searched for the speeds in the bols and the bols in the bundles,
	 * speed Units are ignored, because they are not mandatory.
	 * @return
	 */
	public Rational getMaxSpeedByCheckingBols() {
		Rational maxSpeed = new Rational(1);
		for (Representable r: this) {
			if (r.getType() == Representable.BOL) {
				maxSpeed = Rational.max(maxSpeed, ((Bol)r).getPlayingStyle().getSpeed());
			} else if (r.getType() == Representable.BUNDLE) {
				maxSpeed = Rational.max(maxSpeed, ((BolBundle)r).getSequence().getMaxSpeedByCheckingBols());
			}
		}
		return maxSpeed;
	}


	/**
	 * Bundles recursively using a map and an initial depth.
	 * The sequence is bundled with the speed given by map.getBundlingSpeed(depth).
	 * Then, recursively, the same procedure is run with decreasing depth, until depth == 0.
	 * @param map maps depths to bundling speeds
	 * @param depth the highest bundling depth
	 * @param allowedToFillLastBeat TODO
	 * @return a bundled version
	 */
	public RepresentableSequence getBundled(BundlingDepthToSpeedMap map, int depth, boolean allowedToFillLastBeat) {
		//		Debug.temporary(this, "getbundled with depth: " + depth);
		if (depth <= 0) {
			return this;
		} else {
			//			Debug.temporary(this, "before bundling: " + this);
			RepresentableSequence seq = getBundled(map.getBundlingSpeed(depth), allowedToFillLastBeat);
			//			Debug.temporary(this, "bundled with " +map.getBundlingSpeed(depth) + " : " + seq);
			return seq.getBundled(map, depth-1, allowedToFillLastBeat);
		}

	}
	/**
	 * Multiple single Bols that make up one unit in targetSpeed (for example 1/2 beat when targetSpeed is 2)
	 * are combined to bundles. If targetSpeed is 2 then 1! Dha Ge 4! Ti Re Ke Te -> 1! Dha Ge 2! TiRe KeTe
	 * 
	 * @param targetSpeed
	 * @param allowedToFillLastBeat TODO
	 * @return a bundled version
	 */
	public RepresentableSequence getBundled(Rational targetSpeed, boolean allowedToFillLastBeat) {
		
		RepresentableSequence bundled = new RepresentableSequence();
		
		boolean currentlyBundling = false;
		Rational currentSpeed = new Rational(1);
		Rational currentPosition = new Rational(0);
		Rational wantedBundleEnd = new Rational(0);
		
		RepresentableSequence currentBundle = new RepresentableSequence();
		
		Rational bundleDuration = targetSpeed.reciprocal();
		ArrayList<Representable> currentBundlesFootnotes = new ArrayList<Representable>();
		//int currentBundlesBeat = 0;

		for (int i=0; i < size(); i++) {
			Representable r = get(i);

			switch (r.getType()) {
			case Representable.BOL:
				HasPlayingStyle bol = (Bol) r;

				if (currentlyBundling) {
					//Debug.temporary(this, "currently bundling");
					Rational positionAfterThis = currentPosition.plus(bol.getPlayingStyle().getSpeed().reciprocal());
					int comparison = positionAfterThis.compareTo(wantedBundleEnd);
					if ((comparison > 0) || //it surpasses the possible bounds of the currently generated bundle
							(!bol.getPlayingStyle().getSpeed().equals(currentSpeed))) { //or has the wrong speed

						//bundling failed
						//Debug.temporary(this, "border surpassed, stopped bundling at: " + r);
						bundled.addAll(currentBundle);
						currentlyBundling = false;
						currentBundle.clear();
						currentBundlesFootnotes.clear();
						bundled.add(r);
					} else if (comparison < 0) {
						//Debug.temporary(this, "added to bundle: " + r);
						//it is within the current bundle
						currentBundle.add(r);
					} else {
						//the current bol ends with the bundle boundary - a bundle is completed
						//Debug.temporary(this, "completed bundle with : " + r);
						currentBundle.add(r);
						currentlyBundling = false;
						String exactName = currentBundle.toString(RepresentableSequence.SHOW_ALL, BolName.EXACT);

						BolNameBundle name = BolBase.getStandard().getBolNameBundle(exactName);
						if (name == null) {
							name = BolNameBundle.getDefault(currentBundle);
							BolBase.getStandard().addBolNameBundle(name);
						}
						//Debug.temporary(this, "bundle name: " + name.toStringComplete());

						//TODO add TextReferencing 
						BolBundle bundle = new BolBundle(currentBundle, name, new PlayingStyle(targetSpeed,1));

						//Debug.temporary(this, "bundle: " + bundle.toStringComplete());
						//bundled.add();

						bundled.add(new SpeedUnit(targetSpeed, true, null));
						bundled.add(bundle);
						bundled.addAll(currentBundlesFootnotes);

						//Debug.temporary(this, "last entry in bundled: " + bundled.get(bundled.size()-1));
						//Debug.temporary(this, "bundled version is now: " + bundled);
						currentBundle = new RepresentableSequence();
						currentBundlesFootnotes = new ArrayList<Representable>();

					}

				} else if (bundleDuration.divides(currentPosition) &&
						bol.getPlayingStyle().getSpeed().compareTo(targetSpeed)>0) {

					//start bundling
					//Debug.temporary(this, "starting Bundle with : " + r);
					currentlyBundling = true;

					wantedBundleEnd = currentPosition.plus(bundleDuration);
					currentSpeed = bol.getPlayingStyle().getSpeed();
					currentBundle.add(r);


				} else {
					//Debug.temporary(this, "just adding bol : " + r);
					//just add bol
					bundled.add(r);
				}
				currentPosition = currentPosition.plus(bol.getPlayingStyle().getSpeed().reciprocal());
				break;

			case Representable.BUNDLE:
				//Debug.temporary(this, "found bundle " + r);
				HasPlayingStyle bundle = (BolBundle) r;
				

				if (currentlyBundling) {
					//bundling failed
					//Debug.temporary(this, "stopped bundling at: " + r);
					bundled.addAll(currentBundle);
					currentlyBundling = false;
					currentBundle.clear();
				} 
				bundled.add(r);
				
				currentPosition = currentPosition.plus(bundle.getPlayingStyle().getSpeed().reciprocal());
				break;

			case Representable.FOOTNOTE:
				if (currentlyBundling) {
					//Debug.temporary(this, "adding to bundle: " + r);
					currentBundle.add(r);
					currentBundlesFootnotes.add(r);
				} else {
					//Debug.temporary(this, "just adding representable : " + r);
					bundled.add(r);
				}
				break;
			default:
				if (currentlyBundling) {
					//Debug.temporary(this, "adding to bundle: " + r);
					currentBundle.add(r);
				} else {
					//Debug.temporary(this, "just adding representable : " + r);
					bundled.add(r);
				}
				break;
			}

		}
		//check if it was still bundling when the sequence finished
		if (currentlyBundling) {
			//bundling failed

			if (allowedToFillLastBeat) {
				//attempt to complete bundle
				Rational currentBundleDuration = currentBundle.getDurationR();
				Rational missingDurationToCompleteBundle = wantedBundleEnd.minus(currentPosition);
				if (currentBundleDuration.divides(missingDurationToCompleteBundle)) {
					Rational pauseSpeed = currentBundleDuration.reciprocal();
					int nrOfPauses = missingDurationToCompleteBundle.dividedBy(currentBundleDuration).integerPortion();

					for (int i=0; i < nrOfPauses; i++) {
						Bol pause = new Bol(BolBase.standard().getEmptyBol(), new PlayingStyle(pauseSpeed,1), null, false);
						currentBundle.add(pause);
					}
					String exactName = currentBundle.toString(RepresentableSequence.SHOW_ALL, BolName.EXACT);
					BolNameBundle name = BolBase.getStandard().getBolNameBundle(exactName);
					if (name == null) {
						name = BolNameBundle.getDefault(currentBundle);
						BolBase.getStandard().addBolNameBundle(name);
					}
					BolBundle bundle = new BolBundle(currentBundle, name, new PlayingStyle(targetSpeed,1));
					//Debug.temporary(this, "bundle: " + bundle.toStringComplete());
					bundled.add(new SpeedUnit(targetSpeed, true ,null));
					bundled.add(bundle);
					bundled.addAll(currentBundlesFootnotes);

				} else {
					//Debug.temporary(this, "stopped bundling at end");
					bundled.addAll(currentBundle);
					currentlyBundling = false;
					currentBundle.clear();
				}
			} else {
				//Debug.temporary(this, "stopped bundling at end");
				bundled.addAll(currentBundle);
				currentlyBundling = false;
				currentBundle.clear();
			}
		}

		// TODO check out last bol if it is a single bol marking a new beat

		return bundled;
	}

	public Rational getDurationR() {

		if (cachedDuration == null) {
			cachedDuration = new Rational(0);
			setCacheEstablished();
			for (Representable r : this) {
				if (r.getType() == Representable.BOL || r.getType() == Representable.BUNDLE)  {
					cachedDuration = cachedDuration.plus(((Rational) ((HasPlayingStyle)r).getPlayingStyle().getSpeed()).reciprocal());				
				}
			}
			setCacheEstablished();
		}

		return cachedDuration;
	}



	public double getDuration () {
		return getDurationR().toDouble();
	}

	public int getNrOfBols () {
		int sum = 0;
		for (Representable r : this) {
			if (r.getType() == Representable.BOL || r.getType() == Representable.BUNDLE) {
				sum += 1;				
			}
		}
		return sum;
	}

	public String toString () {
		return toString(RepresentableSequence.SHOW_ALL,BolName.EXACT,10000);
		//getCompact().toString(true, true, true, true, true, true, BolName.EXACT, 10000);
	}

	public String toString(boolean[] displayPattern, int language) {
		return toString(displayPattern,language,10000);
	}

	public String toStringAll() {
		return toString(RepresentableSequence.SHOW_ALL,BolName.EXACT,10000);
	}

	public String toString(boolean[] displayPattern, int language, int maxBols) {
		StringBuilder s = new StringBuilder();
		//int a = 0; int b = 0;
		int nrOfBols = 0;

		for (int i=0; i < size(); i++) {
			int type = get(i).getType();
			if (displayPattern[type]) {
				switch (type) {

				case Representable.BOL:					
					s.append(((Bol)get(i)).getBolName().getName(language)+ " ");					
					nrOfBols++;
					if (nrOfBols > maxBols) return s.toString();
					break;
				case Representable.BUNDLE:
					s.append(((BolBundle)get(i)).getBolNameBundle().getName(language)+ " ");
					nrOfBols++;
					if (nrOfBols > maxBols) return s.toString();					
					break;
				case Representable.FOOTNOTE:
					s.append((((FootnoteUnit) get(i)).getFootnoteNrGlobal()+1) + ") ");
					break;
				case Representable.BRACKET_CLOSED:
					s.append(get(i).toString() + " ");
					break;
				case Representable.BRACKET_OPEN:
					s.append(get(i).toString() + " ");
					break;
				case Representable.SPEED:
					s.append(get(i).toString() + " ");
					break;
				case Representable.COMMA:

					if ((s.length() > 0) && (s.charAt(s.length()-1) != '\n')) s.deleteCharAt(s.length()-1);
					s.append(get(i).toString() + " ");

					break;
				case Representable.BOL_CANDIDATE:
					s.append(get(i).toString());
					break;
				case Representable.SEQUENCE:
					s.append("subsequence{"+((RepresentableSequence) get(i)).toString(displayPattern,language,maxBols)+"} ");
					break;
				case Representable.LINE_BREAK:
					s.append(get(i).toString());
					break;
				case Representable.WHITESPACES:
					s.append("whitespace{"+get(i).toString()+"}");
					break;
				case Representable.FAILED:
					s.append("failed{"+get(i).toString()+"}");
					break;
				default:
					s.append(get(i).toString() + " ");
				}
			}

			/*b = s.length() - a;
			if (b > 50) {
				s.append ("\n\t\t");
				a = s.length();
				b = 0;
			}*/

		}

		return s.toString().replaceAll(Parser.SN + "$", ""); 

	}

	/**
	 * Returns an arraylist of all contained bols.
	 * Bols are added and the Bols in Bundles are added aswell. 
	 */
	public ArrayList<Bol> getBols() {
		ArrayList<Bol> bols = new ArrayList<Bol>();

		for (int i = 0; i < size(); i++) {
			Representable r = get(i);
			if (r.getType() == Representable.BOL) {
				bols.add(((Bol) get(i)).getCopy());
			} else if (r.getType() == Representable.BUNDLE) {
				bols.addAll(((BolBundle) r).getSequence().getBols());
			}
		}
		return bols;
	}

	public String generateSnippet() {
		if (cachedSnippet==null) {
			cachedSnippet = flatten(SpeedUnit.getDefaultSpeedUnit())
			.toString(RepresentableSequence.SNIPPET, BolName.SIMPLE, 20);
			setCacheEstablished();
		}
		return cachedSnippet;
	}

	public String generateShortSnippet() {
		if (cachedShortSnippet == null) {
			cachedShortSnippet = flatten(SpeedUnit.getDefaultSpeedUnit())
			.toString(RepresentableSequence.SHORT_SNIPPET, BolName.INITIALS, 20);
			cachedShortSnippet = cachedShortSnippet.replaceAll(" ", "");
			cachedShortSnippet = cachedShortSnippet.replaceAll("(,|\\(|\\))+", " ");
			setCacheEstablished();
		}

		return cachedShortSnippet;
	}

	public RepresentableSequence flatten() {
		if (referencedSpeedUnitPacket == null) {
			referencedSpeedUnitPacket = Parser.defaultSpeedPacket;
		}
		Rational r = (Rational) referencedSpeedUnitPacket.getObject();
		SpeedUnit referencedSpeedUnit = new SpeedUnit(r, true, referencedSpeedUnitPacket.getTextReference());
		
		return flatten(referencedSpeedUnit);
	}
	
	public RepresentableSequence flatten(SpeedUnit basicSpeedUnit) {
		return flatten(basicSpeedUnit, 0);
	}

	public RepresentableSequence flatten(SpeedUnit basicSpeedUnit, int currentDepth) {

		RepresentableSequence fromCache = cachedFlattened.get(basicSpeedUnit.getSpeed());

		if (fromCache != null)  {
			return fromCache;
		} else {
			//else 

			if (size()==0) return new RepresentableSequence(true);

			//the flattened version of this sequence
			RepresentableSequence flat = new RepresentableSequence(true);
			SpeedUnit currentSpeedUnit = basicSpeedUnit;

			if (currentDepth == 0) {
				flat.add(currentSpeedUnit);
				//Debug.temporary(this, "adding initial speed unit " + basicSpeedUnit);
			}
			currentDepth++;

			Representable current = this.get(0);
			Representable next;

			for (int i=1; i < size(); i++) {
				next = this.get(i);
				//Debug.temporary(this, "flat: " + flat.toStringAll());
				//Debug.temporary(this, "current: " + current + ", next: " + next);
				if (current.getType() == Representable.SPEED) {
					SpeedUnit s = (SpeedUnit) current;

					if (s.isAbsolute()) { // &! (currentSpeedUnit.getSpeed().equals(s.getSpeed())) {
						//add new absolute speeds directly
						//Debug.temporary(this, "adding abs speed " + s + " (current speed was: " + currentSpeedUnit+")");
						flat.add(s);
						currentSpeedUnit = s;
					} else if (!s.isAbsolute()){
						//add relative speeds by multiplying with base speed
						
						Rational speedCandidate = s.getSpeed().times(basicSpeedUnit.getSpeed());
						if (!currentSpeedUnit.getSpeed().equals(speedCandidate)) {
							//only add if it differs from the current speed
							SpeedUnit newSpeedUnit = new SpeedUnit(speedCandidate,true,s.getTextReference());
							flat.add(newSpeedUnit);
							//Debug.temporary(this, "adding (multiplied rel) speed " + s + " (current speed was: " + currentSpeedUnit+")");
							currentSpeedUnit = newSpeedUnit;
						}
					}

				} else 	if (current.getType() != Representable.KARDINALITY_MODIFIER) {
					if (next.getType() != Representable.KARDINALITY_MODIFIER) {
						//add the flattened current representable to the flat sequence
						current.addFlattenedToSequence(flat, currentSpeedUnit, currentDepth);
					} else { 
						//the current representable is affected by the upcoming kardinality modifier
						KardinalityModifierUnit kard = (KardinalityModifierUnit) next;

						//insert flattened as often as multiplication is wanted
						for (int k=1; k <= kard.getMultiplication();k++) {
							current.addFlattenedToSequence(flat, currentSpeedUnit, currentDepth);
						}
						//truncate
						if (kard.getTruncation()>0) {
							flat.truncateFromEnd(kard.getTruncation());
						}		
					}

					if (!flat.lastAbsoluteSpeedUnit(basicSpeedUnit).getSpeed()
							.equals(currentSpeedUnit.getSpeed())) {
//						Debug.temporary(this, "After adding " +current+" last abs speed in flat is " + flat.lastAbsoluteSpeedUnit(basicSpeedUnit) +
//								", currentSpeed before was " + currentSpeedUnit);

						//if the last absolute speed occurring in the new
						//version of the flat sequence differs from the speed before
						//add the previous speed
						//this can only happen, when the inserted flat element is
						//complex enough to contain speeds
						flat.add(currentSpeedUnit);
					}
				}
				current = next;	
			}
			if (current.getType() != Representable.KARDINALITY_MODIFIER) {
				current.addFlattenedToSequence(flat, currentSpeedUnit, currentDepth);
			}

			flat = flat.getSpeedCompiledCopy(basicSpeedUnit.getSpeed()); ////sure to fix speeds at this point? 
			cachedFlattened.put(basicSpeedUnit.getSpeed(), flat);
			setCacheEstablished();

			return flat;
		} //(fromCache != null) ELSE
	}


	/**
	 * Determines if a sequence is already flat or not.
	 * Currently it can only be flat if it has been built in the flatten method.
	 * Flat sequences should not contain any of the following:
	 * - subsequences
	 * - sequences of referenced packets
	 * - relative speeds
	 */
	public boolean isFlattened() {
		return flattened;
	}

	/**
	 * Expects a flattened Sequence with absolute speeds only
	 * @return
	 */
	private RepresentableSequence getSpeedCompiledCopy(Rational baseSpeed) {
		RepresentableSequence seq = new RepresentableSequence(this.size());
		Rational currentSpeed = baseSpeed;
		for (int i=0; i < this.size(); i++) {
			Representable r = get(i);
			switch (r.getType()) {
			case BOL:
				Bol bol = ((Bol) r).getCopy();
				bol.getPlayingStyle().setSpeed(currentSpeed);
				seq.add(bol);
				break;
			case BUNDLE:
				BolBundle bundle = ((BolBundle) r).getCopy();
				bundle.getPlayingStyle().setSpeed(currentSpeed);
				seq.add(bundle);
				break;
			case SPEED:
				SpeedUnit s = (SpeedUnit) r;
				if (s.isAbsolute()) {
					currentSpeed = s.getSpeed();
				} else {
					Debug.critical(this, "getSpeedCompiledCopy does not expect relative speeds!!");
				}
			default:
				seq.add(r);
			}
		}
		return seq;
	}


	/**
	 * Attention, this is a destructive operation.
	 * It stops when it hits a referenced bol packet or a subsequence
	 * @param nrOfBols
	 * @return the number of bols that were removed eventually.
	 */
	public int truncateFromEnd(int nrOfBols) {
		//RepresentableSequence truncated = new RepresentableSequence(this);
		int i = size()-1;
		int bolsTruncated = 0;
		boolean hitBoundary = false;

		while (i>0 && bolsTruncated<nrOfBols &! hitBoundary) {
			int type = get(i).getType();
			if (type==Representable.BOL || type==Representable.BUNDLE) {
				this.remove(i);
				bolsTruncated++;
			} else if (type == Representable.REFERENCED_BOL_PACKET ||
					type == Representable.SEQUENCE) {
				hitBoundary = true;
			}
			i--;
		}

		return bolsTruncated;
	}

	/**
	 * Replaces the entries with the indices in {openIndex, ..., endIndex}
	 * By a RepresentableSequence with those entries.
	 * @param startIndex
	 * @param endIndex
	 * @return the subsequence
	 */
	public RepresentableSequence wrapAsSubSequence(int startIndex, int endIndex) {
		RepresentableSequence subseq = new RepresentableSequence();
		subseq.setTextReference(new TextReference(
				get(startIndex).getTextReference().start(),
				get(endIndex).getTextReference().end(), 
				get(startIndex).getTextReference().line()));

		int nrOfIndices = endIndex - startIndex + 1;
		for (int i=startIndex; i<=endIndex;i++) {	
			subseq.add(get(startIndex));
			this.remove(startIndex);
		}

		this.add(startIndex, subseq);

		return subseq;
	}

	@Override
	public SpeedUnit addFlattenedToSequence(RepresentableSequence seq, SpeedUnit basicSpeedUnit, int currentDepth) {
		RepresentableSequence flat = this.flatten(basicSpeedUnit, currentDepth);
		seq.addAll(flat);
		return flat.lastAbsoluteSpeedUnit(basicSpeedUnit);
	}

	/**
	 * This is intended for use on flat sequences with absolute speeds ONLY !
	 * @param basicSpeed
	 * @return The last detected absolute speed. If none was found the given basicSpeed.
	 */
	public SpeedUnit lastAbsoluteSpeedUnit(SpeedUnit basicSpeedUnit) {
		for (int i=size()-1; i >= 0; i--) {
			if (get(i).getType() == Representable.SPEED) {
				SpeedUnit s = (SpeedUnit) get(i);
				if (s.isAbsolute()) {
					return s;
				} 
			}
		}
		return basicSpeedUnit;
	}

	@Override
	public boolean add(Representable e) {
		clearCache();
		return sequence.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends Representable> c) {
		clearCache();
		return sequence.addAll(c);
	}

	@Override
	public void clear() {
		clearCache();
		sequence.clear();
	}

	@Override
	public boolean contains(Object o) {
		return sequence.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return sequence.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return sequence.isEmpty();
	}

	@Override
	public Iterator<Representable> iterator() {
		return sequence.iterator();
	}

	@Override
	public boolean remove(Object o) {
		clearCache();
		return sequence.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		clearCache();
		return sequence.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		clearCache();
		return sequence.retainAll(c);
	}

	@Override
	public int size() {
		return sequence.size();
	}

	@Override
	public Object[] toArray() {
		return sequence.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return sequence.toArray(a);
	}

	@Override
	public void add(int index, Representable element) {
		clearCache();
		sequence.add(index,element);

	}

	@Override
	public boolean addAll(int index, Collection<? extends Representable> c) {
		clearCache();
		return addAll(index, c);
	}

	@Override
	public Representable get(int index) {
		return sequence.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return sequence.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return sequence.lastIndexOf(o);
	}

	@Override
	public ListIterator<Representable> listIterator() {
		return sequence.listIterator();
	}

	@Override
	public ListIterator<Representable> listIterator(int index) {
		return sequence.listIterator(index);
	}

	@Override
	public Representable remove(int index) {
		clearCache();
		return sequence.remove(index);
	}

	@Override
	public Representable set(int index, Representable element) {
		clearCache();
		return sequence.set(index, element);
	}

	@Override
	public List<Representable> subList(int fromIndex, int toIndex) {
		return sequence.subList(fromIndex,toIndex);
	}

	public Representable getUnitAtCaretPosition(int relativeCaretPosition) {
		//Debug.temporary(this, "looking for unit at: " + relativeCaretPosition);
		for (int i=0; i < sequence.size(); i++) {
			Representable r = sequence.get(i);
			if (r.getTextReference() != null) {
				//Debug.temporary(this,"comparing to unit at " + r.getTextReference() + ", " + r);
				if (r.getTextReference().contains(relativeCaretPosition)) {
					//Debug.temporary(this,"match");
					if (r.getType() == Representable.SEQUENCE) {
						return ((RepresentableSequence) r).getUnitAtCaretPosition(relativeCaretPosition);
					} else {
						//Debug.temporary(this,"returning " + r);
						return r;
					}
				}
			}
		}
			
		
		return null;
	}



}
