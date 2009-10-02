package bolscript.sequences;

import bolscript.packets.TextReference;

public class BracketClosedUnit extends Unit {
	public static String BRACKET_CLOSED_STRING = ")";
	public BracketClosedUnit(TextReference textReference) {
		super(Representable.BRACKET_CLOSED, BRACKET_CLOSED_STRING, textReference);
	}
}
