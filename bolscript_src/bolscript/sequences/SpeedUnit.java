package bolscript.sequences;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import basics.Rational;
import bolscript.Reader;
import bolscript.packets.TextReference;
import bolscript.scanner.SequenceToken;

public class SpeedUnit extends Unit implements Representable{
	
    /**
     * Regex: A nonnegative rational number
     * As in SequenceScanner.flex
     * {Numerator} ( {WhiteSpace}* "/" {WhiteSpace}* {Denominator} )? {WhiteSpace}* "!"?
     */
    private static String NONNEG_RATIONAL_WITH_SPACES = "(\\d+)"+Reader.SN+"*(?:/"+Reader.SN+"*(\\d+))?(?:"+Reader.SN+"*(!)?)";
    /**
     * Regex Pattern: for parsing nonnegative rationals.
     */
    private static Pattern nonNegativeRationalPattern = Pattern.compile(NONNEG_RATIONAL_WITH_SPACES);
    
	private boolean absolute;
	
	public SpeedUnit(Rational r, boolean absolute, TextReference textReference) {
		super(Representable.SPEED, r, textReference);
		this.absolute = absolute;
	}
	
	public boolean isAbsolute() {
		return absolute;
	}
	
	public String toString() {
		return obj.toString() + ((absolute)?"!":"");
	}
		
	public static Representable parseToken(SequenceToken input) {
		Matcher m = nonNegativeRationalPattern.matcher(input.text);
    	if (m.find()) {
    		int num = Integer.parseInt(m.group(1));
    		int den;
    		if (m.group(2) != null) {
    			den = Integer.parseInt(m.group(2));
    		} else {
    			den = 1;
    		}
    		boolean absolute = (m.group(3) != null);
    		
    		return new SpeedUnit(new Rational(num,den),absolute, input.textReference);
    	} else {
    		return new FailedUnit(input, "");
    	}
	}
	
}
