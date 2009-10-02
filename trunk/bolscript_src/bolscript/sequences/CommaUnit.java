package bolscript.sequences;

import bolscript.packets.TextReference;

public class CommaUnit extends Unit {
	public static String COMMA_STRING = ",";
	
	public CommaUnit(TextReference textReference) {
		super(Representable.COMMA, COMMA_STRING, textReference);
	}
}
