package bolscript.sequences;

import java.util.regex.Pattern;

import bolscript.Reader;
import bolscript.scanner.SequenceToken;

public class BolCandidateUnit extends Unit implements Representable {

	public static String BOL_CANDIDATE_REGEX = "([A-Za-z\\-]+\\d*)("+Reader.SN+"*\\?)?("+Reader.SN+"*!)?";
	public static Pattern pattern = Pattern.compile(BOL_CANDIDATE_REGEX);
	
	public static Representable parseToken(SequenceToken token) {
	
		return new FailedUnit(token, "");
	}
}