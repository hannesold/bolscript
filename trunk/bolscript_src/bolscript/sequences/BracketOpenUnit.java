package bolscript.sequences;

import bolscript.packets.TextReference;

public class BracketOpenUnit extends Unit {
	public static String BRACKET_OPEN_STRING = "(";
	
	public BracketOpenUnit(TextReference textReference) {
		super(Representable.BRACKET_OPEN, BRACKET_OPEN_STRING, textReference);
	}
}
