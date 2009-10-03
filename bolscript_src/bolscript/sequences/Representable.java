package bolscript.sequences;

import basics.Rational;
import bolscript.packets.TextReference;

public interface Representable {

	public static final int BOL = 0;
	public static final int FOOTNOTE = 1;
	public static final int SPEED = 2;
	public static final int BRACKET_OPEN = 3;
	public static final int BRACKET_CLOSED = 4;
	public static final int OTHER = 5;
	public static final int COMMA = 6;
	public static final int BUNDLE = 7;
	public static final int SEQUENCE = 8;
	public static final int KARDINALITY_MODIFIER = 9;
	public static final int REFERENCED_BOL_PACKET = 10;
	public static final int LINE_BREAK = 11;
	public static final int FAILED = 12;
	public static final int UNDEFINED = 13;
	public static final int BOL_CANDIDATE = 14;
	public static final int WHITESPACES = 15;
	public static final int nrOfTypes = 16;
	
	int getType();
	
	TextReference getTextReference();
	
	void setTextReference(TextReference textReference);
	
	/**
	 * Add a flat version of this Representable to the end of the given sequence,
	 * internal relative speeds are made absolute by multiplying with the given basicSpeed 
	 * @param seq The Representable Sequence to which the flattened Representable[Sequence] shall be added.
	 * @param basicSpeedUnit the basic speed before this representable
	 * @param currentDepth TODO
	 * @return TODO
	 * @return Returns the speedUnit active at the end of the flattened Representable[Sequence]Ê
	 */
	SpeedUnit addFlattenedToSequence(RepresentableSequence seq, SpeedUnit basicSpeedUnit, int currentDepth);
	
}
