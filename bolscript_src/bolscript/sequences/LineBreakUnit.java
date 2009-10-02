package bolscript.sequences;

import bolscript.packets.TextReference;

public class LineBreakUnit extends Unit {
	public static String LINEBREAK_STRING = "[linebreak]\n";
	public LineBreakUnit(TextReference textReference) {
		super(Representable.LINE_BREAK, LINEBREAK_STRING, textReference);
		
	}
}
