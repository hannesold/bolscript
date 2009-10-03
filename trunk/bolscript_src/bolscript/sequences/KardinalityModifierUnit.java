package bolscript.sequences;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bolscript.packets.TextReference;
import bolscript.scanner.Parser;
import bolscript.scanner.SequenceToken;

public class KardinalityModifierUnit extends Unit implements Representable {

	private int multiplication;
	private int truncation;

	private static String KARDINALITY_MODIFIER_UNIT = 
		"(?:x"+Parser.SN+"*(\\d+))?(?:"+Parser.SN+"*<"+Parser.SN+"*(\\d+))?";
	private static Pattern pattern = Pattern.compile(KARDINALITY_MODIFIER_UNIT);
	
	public KardinalityModifierUnit(int multiplication, int truncation, TextReference textReference) {
		super(Representable.KARDINALITY_MODIFIER, "[x"+multiplication+"<"+truncation+"]", textReference);
		this.multiplication = multiplication;
		this.truncation = truncation;
	}

	public int getMultiplication() {
		return multiplication;
	}

	public int getTruncation() {
		return truncation;
	} 
	
	public static Representable parseToken(SequenceToken input) {
		Matcher m = pattern.matcher(input.text);
		if (m.find()) {
			int mult = 1;
			int trunc = 0;
			if (m.group(1) != null) {
				mult = Integer.parseInt(m.group(1));
			}
			if (m.group(2) != null) {
				trunc = Integer.parseInt(m.group(2));
			}
			
			return new KardinalityModifierUnit(mult, trunc, input.textReference);
		}
		
		return new FailedUnit(input, "");
	}
}
