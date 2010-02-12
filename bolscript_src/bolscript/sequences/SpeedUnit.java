package bolscript.sequences;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import basics.Rational;
import bolscript.config.Config;
import bolscript.packets.TextReference;
import bolscript.scanner.Parser;
import bolscript.scanner.SequenceToken;

public class SpeedUnit extends Unit implements Representable{
	
    /**
     * Regex: A nonnegative rational number
     * As in SequenceScanner.flex
     * {Numerator} ( {WhiteSpace}* "/" {WhiteSpace}* {Denominator} )? {WhiteSpace}* "!"?
     */
    private static String NONNEG_RATIONAL_WITH_SPACES = "(\\d+)"+Parser.SN+"*(?:/"+Parser.SN+"*(\\d+))?(?:"+Parser.SN+"*(!)?)";
    /**
     * Regex Pattern: for parsing nonnegative rationals.
     */
    private static Pattern nonNegativeRationalPattern = Pattern.compile(NONNEG_RATIONAL_WITH_SPACES);
    
	private boolean absolute;
		
	private static SpeedUnit defaultSpeedUnit = new SpeedUnit(Rational.ONE, true, null);
	
	public SpeedUnit(Rational r, boolean absolute, TextReference textReference) {
		super(Representable.SPEED, r, textReference);
		this.absolute = absolute;
	}
	
	public boolean isAbsolute() {
		return absolute;
	}
	
	public String toString() {
		return obj.toString() + ((absolute)?"abs":"rel");
	}
		
	public Rational getSpeed() {
		return (Rational) obj;
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
    		boolean absolute = (m.group(3) == null);
    		
    		Rational speed = new Rational(num,den);
    		if (speed.toDouble() >= Config.BOLSCRIPT_MINIMUM_SPEED && speed.toDouble() <= Config.BOLSCRIPT_MAXIMUM_SPEED) {
    			return new SpeedUnit(speed,absolute, input.textReference);
    		} else {
    			return new FailedUnit(input, "Speed is to large.");
    		}
    	} else {
    		return new FailedUnit(input, "Speed could not be parsed");
    	}
	}
	
	/**
	 * Returns a default Speed Unit with absolute Speed = Rational.ONE, and no text reference.
	 * @return
	 */
	public static SpeedUnit getDefaultSpeedUnit() {
		return defaultSpeedUnit;
	}


	
}
