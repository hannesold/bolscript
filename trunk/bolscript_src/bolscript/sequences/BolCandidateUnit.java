package bolscript.sequences;

import java.util.regex.Pattern;

import bolscript.scanner.Parser;
import bolscript.scanner.SequenceToken;

/**
 * This class is not used very much. tokens of type BOL_CANDIDATE
 * are constructed by SequenceScanner, SequenceParser only uses the pattern defined here.
 * @author hannes
 *
 */
public class BolCandidateUnit extends Unit implements Representable {
	
	public static String BOL_CANDIDATE_REGEX = "([A-Za-z\\-]+\\d*)("+Parser.SN+"*\\?)?("+Parser.SN+"*!)?";
	public static Pattern pattern = Pattern.compile(BOL_CANDIDATE_REGEX);

}