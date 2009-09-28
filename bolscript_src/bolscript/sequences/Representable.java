package bolscript.sequences;

import bolscript.packets.TextReference;

public interface Representable {
	public static final int FAILED = -3;
	public static final int UNDEFINED = -2;
	public static final int BOL_CANDIDATE = -1;
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
	
	int getType();
	
	TextReference getTextReference();
	
	void setTextReference(TextReference textReference);
	
	/**
	 * Add a flat version of this Representable to the end of the given sequence.
	 * @param seq The Representable Sequence.
	 */
	void addFlattenedToSequence(RepresentableSequence seq);
	
}
