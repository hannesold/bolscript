package bols;

import basics.Rational;
import bolscript.packets.TextReference;
import bolscript.sequences.Representable;
import bolscript.sequences.RepresentableSequence;

/**
 * BolBundle is a short subsequence with a name in each language (stored in a bolNameBundle)
 * @author hannes
 */
public class BolBundle implements Representable, HasPlayingStyle {

	private RepresentableSequence sequence;
	private BolNameBundle name;
	private PlayingStyle style;
	protected TextReference textReference;
	
	/**
	 * Uses a copy of the given style.
	 * @param sequence
	 * @param name
	 * @param style
	 */
	public BolBundle(RepresentableSequence sequence, BolNameBundle name,
			PlayingStyle style) {
		super();
		this.sequence = sequence;
		this.name = name;
		this.style = style.getCopy();
	}

	public BolNameBundle getBolNameBundle() {
		return name;
	}
	
	public int getType() {
		return BUNDLE;
	}
	
	public RepresentableSequence getSequence() {
		return sequence;
	}
	
	public PlayingStyle getPlayingStyle() {
		return style;
	}
	
	public void setPlayingStyle (PlayingStyle style) {
		this.style = style;
	}
	
	public String toString() {
		return this.name.toString();
	}

	/**
	 * Returns complete information, composed of the playing style and the various names.
	 * @return
	 */
	public String toStringComplete() {
		
		return "style: " + this.style + ", name: " + this.name.toStringComplete();
	}

	public TextReference getTextReference() {
		return textReference;
	}

	public void setTextReference(TextReference textReference) {
		this.textReference = textReference;
	}

	@Override
	public void addFlattenedToSequence(RepresentableSequence seq) {
		seq.add(this);
		
	}
}
