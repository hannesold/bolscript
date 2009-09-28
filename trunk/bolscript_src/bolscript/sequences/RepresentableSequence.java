package bolscript.sequences;

import java.util.ArrayList;
import java.util.Collection;

import basics.Rational;
import bols.Bol;
import bols.BolBase;
import bols.BolBundle;
import bols.BolName;
import bols.BolNameBundle;
import bols.BundlingDepthToSpeedMap;
import bols.HasPlayingStyle;
import bols.PlayingStyle;
import bolscript.Reader;
import bolscript.packets.TextReference;

public class RepresentableSequence extends ArrayList<Representable> implements Representable {

	private TextReference textReference;

	public void setTextReference(TextReference textReference) {
		this.textReference = textReference;
	}

	public TextReference getTextReference() {
		return textReference;
	}

	public RepresentableSequence() {
		super();
	}

	public RepresentableSequence(Collection<? extends Representable> c) {
		super(c);
	}

	public RepresentableSequence(int initialCapacity) {
		super(initialCapacity);
	}

	private double duration;

	public int getType() {
		return Representable.SEQUENCE;
	}

	/**
	 * Strips brackets, removes double collons and double speeds
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
			return this.getCompact();
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
						String exactName = currentBundle.toString(true, false,false,false,false,false, BolName.EXACT);

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

						bundled.add(new Unit(Representable.SPEED, targetSpeed, null));
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
				currentPosition = currentPosition.plus(bundle.getPlayingStyle().getSpeed().reciprocal());

				if (currentlyBundling) {
					//bundling failed
					//Debug.temporary(this, "stopped bundling at: " + r);
					bundled.addAll(currentBundle);
					currentlyBundling = false;
					currentBundle.clear();
				} 
				bundled.add(r);

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
						Bol pause = new Bol(BolBase.standard().getEmptyBol(), new PlayingStyle(pauseSpeed,1));
						currentBundle.add(pause);
					}
					String exactName = currentBundle.toString(true, false,false,false,false,false, BolName.EXACT);
					BolNameBundle name = BolBase.getStandard().getBolNameBundle(exactName);
					if (name == null) {
						name = BolNameBundle.getDefault(currentBundle);
						BolBase.getStandard().addBolNameBundle(name);
					}
					BolBundle bundle = new BolBundle(currentBundle, name, new PlayingStyle(targetSpeed,1));
					//Debug.temporary(this, "bundle: " + bundle.toStringComplete());
					bundled.add(new Unit(Representable.SPEED, targetSpeed, null));
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

		return bundled.getCompact();
	}

	public Rational getDurationR() {
		Rational sum = new Rational(0);
		for (Representable r : this) {
			if (r.getType() == Representable.BOL || r.getType() == Representable.BUNDLE)  {
				sum = sum.plus(((Rational) ((HasPlayingStyle)r).getPlayingStyle().getSpeed()).reciprocal());				
			}
		}
		return sum;
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
		return getCompact().toString(true, true, true, true, true, true, BolName.EXACT, 10000);
	}

	public String toString(boolean bols, boolean brackets, boolean colons, boolean footnotes, boolean speed, boolean other, int language) {
		return toString(bols,brackets,colons,footnotes,speed,other,language,10000);
	}

	public String toStringAll() {
		return toString(true,true,true,true,true,true,BolName.EXACT,10000);
	}


	public String toString(boolean bols, boolean brackets, boolean commas, boolean footnotes, boolean speed, boolean other, int language, int maxBols) {
		StringBuilder s = new StringBuilder();
		//int a = 0; int b = 0;
		int nrOfBols = 0;

		for (int i=0; i < size(); i++) {
			switch (get(i).getType()) {

			case Representable.BOL:
				if (bols) {
					s.append(((Bol)get(i)).getBolName().getName(language)+ " ");
					nrOfBols++;
					if (nrOfBols > maxBols) return s.toString();
				}
				break;
			case Representable.BUNDLE:
				if (bols) {
					s.append(((BolBundle)get(i)).getBolNameBundle().getName(language)+ " ");
					nrOfBols++;
					if (nrOfBols > maxBols) return s.toString();					
				}
				break;
			case Representable.FOOTNOTE:
				if (footnotes) {
					s.append((((FootnoteUnit) get(i)).getFootnoteNrGlobal()+1) + ") ");
				}
				break;
			case Representable.BRACKET_CLOSED:
				if (brackets) s.append(get(i).toString() + " ");
				break;
			case Representable.BRACKET_OPEN:
				if (brackets) s.append(get(i).toString() + " ");
				break;
			case Representable.SPEED:
				if (speed) s.append(get(i).toString() + " ");
				break;
			case Representable.COMMA:
				if (commas) {
					if ((s.length() > 0) && (s.charAt(s.length()-1) != '\n')) s.deleteCharAt(s.length()-1);

					s.append(get(i).toString() + " ");
				}
				break;
			case Representable.SEQUENCE:
				s.append("subsequence{"+((RepresentableSequence) get(i)).toString(bols,brackets,commas, footnotes, speed, other, language, maxBols)+"} ");
				break;
			case Representable.LINE_BREAK:
				s.append("[linebreak]\n");
				break;
			default:
				if (other) s.append(get(i).toString() + " ");
			}

			/*b = s.length() - a;
			if (b > 50) {
				s.append ("\n\t\t");
				a = s.length();
				b = 0;
			}*/

		}

		return s.toString().replaceAll(Reader.SN + "$", ""); 

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
		return toString(true, false, true, false, false, false, BolName.SIMPLE, 20);
	}

	public String generateShortSnippet() {
		String s = toString(true, false, true, false, false, false, BolName.INITIALS, 20);
		//s = s.replaceAll("[^A-Z\\-,]", "");
		s = s.replaceAll(" ", "");
		s = s.replaceAll(",+", " ");
		return s;
	}

	public RepresentableSequence flatten() {
		if (size()==0) return this;

		int j=0;
		RepresentableSequence flat = new RepresentableSequence();

		Representable current = this.get(0);
		Representable next;
		for (int i=1; i < size(); i++) {
			next = this.get(i);
			if (current.getType() != Representable.KARDINALITY_MODIFIER) {
				if (next.getType() != Representable.KARDINALITY_MODIFIER) {
					current.addFlattenedToSequence(flat);
				} else {
					KardinalityModifierUnit kard = (KardinalityModifierUnit) next;

					for (int k=1; k <= kard.getMultiplication();k++) {
						current.addFlattenedToSequence(flat);
					}
					if (kard.getTruncation()>0) {
						flat.truncateFromEnd(kard.getTruncation());
					}

				}
			}
			current = next;	
		}
		if (current.getType() != Representable.KARDINALITY_MODIFIER) {
			current.addFlattenedToSequence(flat);
		}

		return flat;
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
	public void addFlattenedToSequence(RepresentableSequence seq) {
		seq.addAll(this.flatten());
	}



}
