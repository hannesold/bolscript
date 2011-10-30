package bolscript.scanner;

import static bolscript.sequences.Representable.*;

import java.io.IOException;
import java.util.regex.Matcher;

import basics.Debug;
import bols.Bol;
import bols.BolBase;
import bols.BolName;
import bols.PlayingStyle;
import bolscript.packets.Packet;
import bolscript.packets.Packets;
import bolscript.packets.TextReference;
import bolscript.sequences.BracketClosedUnit;
import bolscript.sequences.BracketOpenUnit;
import bolscript.sequences.CommaUnit;
import bolscript.sequences.FailedUnit;
import bolscript.sequences.FootnoteUnit;
import bolscript.sequences.KardinalityModifierUnit;
import bolscript.sequences.LineBreakUnit;
import bolscript.sequences.ReferencedBolPacketUnit;
import bolscript.sequences.Representable;
import bolscript.sequences.RepresentableSequence;
import bolscript.sequences.SpeedUnit;

/**
 * Generates Real representables from a Scanners Tokens
 * @author hannes
 *
 */
public class SequenceParser {


	private SequenceScanner scanner;
	private int footnoteNrGlobal=0;
	private Packets packets;
	//private Packet packet;
	private BolBase bolBase;

	public SequenceParser(int footnoteNrGlobal, Packets packets) {
		super();
		this.footnoteNrGlobal = footnoteNrGlobal;
		this.packets = packets;
		this.bolBase = BolBase.getStandard();
	}

	public RepresentableSequence parseSequence(Packet currentPacket, String input) {
		RepresentableSequence seq = parseUnits(currentPacket, input);
		buildSubsequences(seq);
		packWohleLinesWithKardinalityModifier(seq);
		return seq;
	}

	public int buildSubsequences(RepresentableSequence seq) {
		RepresentableSequence result = buildInnermostSubsequence(seq);
		int counter = 0;

		while (result != null && counter < 1000) {
			counter++;
			result = buildInnermostSubsequence(seq);

		}
		return counter;
	}


	public int packWohleLinesWithKardinalityModifier(RepresentableSequence seq) {
		RepresentableSequence result = packOneWholeLineWithKardinalityModifier(seq);
		int counter = 0;
		
		while (result != null && counter < 1000) {
			counter++;
			result = packOneWholeLineWithKardinalityModifier(seq);
		}
		return counter;
	}
	
	public RepresentableSequence packOneWholeLineWithKardinalityModifier(RepresentableSequence seq) {

		int firstRepresentableAfterLineBreak = 0;

		boolean validContentBeforeKardinalityModifier = false;
		boolean contentAfterKardinalityModifier = false;

		int kardModifierIndex = -1;

		boolean kardModifierFoundInThisLine = false;

		int i = 0;
		int endlessProhibitor = 0;
		while (i < seq.size() && endlessProhibitor < 1000) {
			Representable r = seq.get(i);
			
			switch(r.getType()) {

			case LINE_BREAK:
				if (kardModifierFoundInThisLine) {
					if (validContentBeforeKardinalityModifier &! contentAfterKardinalityModifier) {
						int subseqBeginIndex = Math.max(0,firstRepresentableAfterLineBreak);
						return seq.wrapAsSubSequence(subseqBeginIndex, kardModifierIndex-1);
						//i = subseqBeginIndex; 	//remove comment for inner iterative mode									
					}
				} 
				firstRepresentableAfterLineBreak = i+1;
				kardModifierFoundInThisLine = false;
				validContentBeforeKardinalityModifier = false;
				break;

			case KARDINALITY_MODIFIER:
				kardModifierIndex =  i;
				kardModifierFoundInThisLine = true;
				contentAfterKardinalityModifier = false;
				break;

			case Representable.BOL:
				if (!kardModifierFoundInThisLine) {
					validContentBeforeKardinalityModifier = true;
				} else {
					contentAfterKardinalityModifier = true;
				}	
				break;
			case REFERENCED_BOL_PACKET:
				if (!kardModifierFoundInThisLine) {
					validContentBeforeKardinalityModifier = false;
				} else {
					contentAfterKardinalityModifier = true;
				}	
				break;

			case SEQUENCE:
				if (!kardModifierFoundInThisLine) {
					validContentBeforeKardinalityModifier = false;
				} else {
					contentAfterKardinalityModifier = true;
				}
				break;
			}
			i++;
			endlessProhibitor++;
		}

		//process the end (this is a stripped copy of the case LINE_BREAK)
		if (kardModifierFoundInThisLine) {
			if (validContentBeforeKardinalityModifier &! contentAfterKardinalityModifier) {
				int subseqBeginIndex = Math.max(0,firstRepresentableAfterLineBreak);
				return seq.wrapAsSubSequence(subseqBeginIndex, kardModifierIndex-1);
			}
		} 
		
		
		return null;
	}

	public RepresentableSequence buildInnermostSubsequence(RepresentableSequence seq) {
		int openIndex = -1;
		int closeIndex = -1;
		for (int i=0; i < seq.size(); i++) {
			Representable r = seq.get(i);
			switch (r.getType()) {
			case BRACKET_OPEN:
				openIndex = i;
				break;
			case BRACKET_CLOSED:
				closeIndex = i;
				if (openIndex != -1) {		//if there has been an opening bracket			
					if (openIndex == 0 && closeIndex == seq.size()-1){  
						//the brackets are not around the entire sequence, this is not considered a subsequence 
						return null;
					}
					RepresentableSequence subseq = seq.wrapAsSubSequence(openIndex,closeIndex);
					return subseq;
				}
				break;
			}
		}
		if (openIndex != -1) {
			seq.set(openIndex, new FailedUnit(seq.get(openIndex),"No matching closed Bracket found."));
		}
		if (closeIndex != -1) {
			seq.set(closeIndex, new FailedUnit(seq.get(closeIndex), "No matching opening Bracket found."));
		}
		return null;
	}

	public RepresentableSequence parseUnits(Packet currentPacket, String input) {
		RepresentableSequence seq = new RepresentableSequence();
		seq.setTextReference(new TextReference(0, input.length()-1, 0));

		scanner = new SequenceScanner(input);

		int assumedTokenStartPosition = 0;
		
		SequenceToken token = null;
		try {
			token = scanner.nextToken();
		} catch (IOException e) {
			Debug.critical(this, "scanner threw an exception!");
			e.printStackTrace();
			return null;
		}

		
		while (token != null) {

			if (token.textReference.start() > assumedTokenStartPosition) {
				//String candidate = input.substring(assumedTokenStartPosition, token.textReference.start());
				//if (!candidate.matches("[\\t\\s\\f]+")) {
				SequenceToken failedToken = new SequenceToken(Representable.FAILED, 
						input.substring(assumedTokenStartPosition, 
										token.textReference.start()), 
										assumedTokenStartPosition, 
										token.textReference.line());
				seq.add(new FailedUnit(failedToken, "Could not be parsed."));
				//}
				
			} 
			assumedTokenStartPosition = token.textReference.end();
			
			switch (token.type) {
			case BOL_CANDIDATE:
				seq.add(parseBolCandidate(currentPacket, token));
				break;

			case SPEED:
				seq.add(SpeedUnit.parseToken(token));
				break;

			case FOOTNOTE:
				Representable f = FootnoteUnit.parseToken(token, currentPacket);
				if (f.getType() != Representable.FAILED) {
					((FootnoteUnit) f).setFootnoteNrGlobal(getAndIncreaseGlobalFootnoteNr());
				}
				//Debug.temporary(this, "footnote nr: " + ((FootnoteUnit) f).getFootnoteNrGlobal());
				seq.add(f);
				break;

			case COMMA:
				seq.add(new CommaUnit(token.textReference));
				break;				

			case BRACKET_OPEN:
				seq.add(new BracketOpenUnit(token.textReference));
				break;

			case BRACKET_CLOSED:
				seq.add(new BracketClosedUnit(token.textReference));
				break;

			case KARDINALITY_MODIFIER:
				seq.add(KardinalityModifierUnit.parseToken(token));
				break;

			case LINE_BREAK:
				seq.add(new LineBreakUnit(token.textReference));
				break;

			case FAILED:
				seq.add(new FailedUnit(token, "Could not be parsed."));
				break;
				
			case WHITESPACES:
				//Debug.temporary(this, "added whitespace");
				break;
				
			default:
				seq.add(new FailedUnit(token, "Could not be parsed."));
			}


			try {
				token = scanner.nextToken();
			} catch (IOException e) {
				Debug.critical(this, "scanner threw an exception!");
				e.printStackTrace();
				return null;
			}

		}

		return seq;
	}
	private int getAndIncreaseGlobalFootnoteNr() {
		//footnoteNrGlobal++;
		return footnoteNrGlobal++;
	}



	private Representable parseBolCandidate(Packet currentPacket, SequenceToken token) {

		Matcher m = Parser.BolCandidatePattern.matcher(token.text);
		if (m.find()) {
			String name = m.group(1);
			boolean questioned = m.group(2) != null;
			boolean emphasized = m.group(3) != null;

			if (name !=null) {
				//1 search for packets with the bolcandidates name as key (insertion)
				if (currentPacket != null && packets != null) {
					Packet referencedPacket = packets.findReferencedBolPacket(currentPacket, name);
					if (referencedPacket != null) {
						return new ReferencedBolPacketUnit(referencedPacket, token.textReference);
					}
				}

				//2 search bolbase for a fitting bolname
				BolName bolName = bolBase.getBolName(name);
				if (bolName == null) {
					bolName = new BolName(name);
					bolName.setWellDefinedInBolBase(false);
					//add to bolbase in some way ?
				}

				double velocity = (emphasized) ? 1.2 : 1;

				Bol bol = new Bol(bolName, new PlayingStyle(1, velocity), null, emphasized);
				bol.setEmphasized(emphasized);
				bol.setTextReference(token.textReference);

				return bol;
			}

		}

		return new FailedUnit(token, "");
	}

	


}
