package bolscript.scanner;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import basics.Debug;
import basics.FileManager;
import basics.Rational;
import bols.BolBase;
import bolscript.packets.Packet;
import bolscript.packets.Packets;
import bolscript.packets.TextReference;
import bolscript.packets.types.PacketType;
import bolscript.packets.types.PacketTypeFactory;
import bolscript.packets.types.PacketType.ParseMode;
import bolscript.sequences.BolCandidateUnit;
import bolscript.sequences.FootnoteUnit;
import bolscript.sequences.Representable;
import bolscript.sequences.RepresentableSequence;

/**
 * This class contains the essential methods for reading and parsing bolscript files,
 * converting them into a series of typed Packets and extracting a RepresentableSequence from each
 * Packet of type BOL.
 * The most important methods is compilePacketsFromString.
 * 
 * @author hannes
 * @see Packet, Packets
 */
public class Parser {
	public static Debug debug = new Debug(Parser.class);
	
	/**
	 * Regex: Newline caracters;
	 */
	public static final String N = "[\n\r\f]";
	/**
	 * Regex: Whitespaces and TABS
	 */
	public static final String S = "\\s";
	/**
	 * Regex: Whitespaces and newline characters
	 */
	public static final String SN = "(?:\\s|[\n\r\f])";
	/**
	 * this has 
	 */
	public static final String BOL = "[A-Za-z\\-]+(\\d)*(\\?)?!?";
	/**
	 * Regex: Whitespaces or newlines at beginning or end of sequence.
	 */
	public static final String SNatBeginningOrEnd = "^("+SN + ")*|("+SN+"*(?:$|\\z))";
	private static DecimalFormat threeDigitFormat = new DecimalFormat("000");
	//regex for a Rational Nr
	/**
	 * Regex: A nonnegative Rational number bounded to 2 digits in numerator and denominator each.
	 */
	public static String RATIONAL = "\\d{1,2}+(?:/\\d{1,2}+)?";
	

	public static Packets compilePacketsFromString(String input) {
	
		Packets packets = splitIntoPackets(input);
	
		packets.addAll(0, BolBase.getStandard().getReplacementPacketClones());
	
		Parser.processMetaPackets(packets);
	
		Packet currentSpeedPacket = new Packet("Speed","1",PacketTypeFactory.SPEED, false);
	
		SequenceParser parser = new SequenceParser(1, packets);
		
		int i = 0;
	
		while (i <packets.size()) {	
			Packet p = packets.get(i);
	
			if (p.getType() == PacketTypeFactory.BOLS) {
				RepresentableSequence seq = parser.parseSequence(p, p.getValue());
				p.setObject(seq);
				
				for (int j = 0; j < seq.size(); j++) {
					Representable r = seq.get(j);
					if (r.getType() == Representable.FOOTNOTE) {
						FootnoteUnit fu = (FootnoteUnit) r;
						//if (fu.getContainingPacket())
						fu.getFootnoteNrGlobal();
						Packet fp = new Packet("Footnote "+ fu.getFootnoteNrGlobal(), fu.getFootnoteText(), PacketTypeFactory.FOOTNOTE, true);
						fp.setObject(fu);
						debug.temporary("setting footnote packet '" + fp + "'");
						debug.temporary("with unit " + fp.getObject());
						debug.temporary("with footnotetext " + fu.getFootnoteText());
						packets.add(i+1,fp);
						i++;
					}
				}
				
			}
			i++;
		}
	
		debug.temporary(packets.toString());
		return packets;
	}

	/**
	 * <p>Processes meta-packets of the following types:
	 * <li>COMMENT</li>
	 * <li>EDITOR</li>
	 * <li>FOOTNOTE</li>
	 * <li>GHARANA</li>
	 * <li>NAME</li>
	 * <li>SPEED</li>
	 * <li>TAL</li>
	 * <li>TYPE</li>
	 * <li>COMPOSER</li>
	 * <li>SOURCE</li>
	 * </p>
	 * <p>The packets values are parsed and stored within the <code>obj</obj>-field 
	 * of the owning Packet. Packets where parsing failed get their <code>type</code> 
	 * changed to <code>FAILED</code>.
	 * </p>
	 * <p>Note that not all meta-packets are processed in this method: Talspecific packets 
	 * such as LAYOUT, LENGTH and VIBHAGS are processed in the overwritten method of the class TalDynamic. 
	 * @param packets The Packets to be processed. Note that these will be manipulated.
	 * @see Packet Packet, for a description of the meta-types.
	 */
	public static void processMetaPackets(Packets packets) {
	
		Iterator<Packet> i = packets.listIterator();
		Packet p;
	
		while (i.hasNext()) {
			p = i.next();
	
			int type = p.getType();
			//debug.temporary(p.getKey() + " => " + p.getValue() + ", type: " + p.getPType());
			ParseMode parseMode = p.getPType().getParseMode();
	
			if (parseMode == ParseMode.STRING) {
				setObjFromString(p);
			} else if (parseMode == ParseMode.COMMASEPERATED) {
				setObjFromCommaSeperated(p);
			} else if (parseMode == ParseMode.OTHER) switch (type) {
	
			case PacketTypeFactory.SPEED:
				String input = p.getValue().replaceAll(Parser.SN +"*", "");
				try {
					Rational speed = Rational.parseNonNegRational(input);
					p.setObject(speed);
					debug.debug("read speed " + speed);
				} catch (Exception e) {
					debug.debug("failed to parse Speed, will be ignored!");
					p.setType(PacketTypeFactory.FAILED);
				}
				break;
	
	
			} // switch	
		} //while
	
	}
	
	/**
	 * Sets a packet's object under the assumption, that its value is a comma
	 * seperated list. If the resulting entries are empty the Packet is set to FAILED.
	 * @param p the Packet
	 */
	private static void setObjFromCommaSeperated(Packet p) {
		String[] entries = getEntriesFromCommaSeperated(p.getValue());
		if (entries.length != 0) {
			p.setObject(entries);
		} else p.setType(PacketTypeFactory.FAILED);	
	}

	/**
	 * Sets a packet's object under the assumption, that its value is non-empty string.
	 * If it is empty the Packet is set to FAILED.
	 * Removes whitespaces and linebreaks at beginning and end.
	 * @param p the Packet
	 */
	private static void setObjFromString(Packet p) {
		String val = p.getValue().replaceAll(SNatBeginningOrEnd, "");
		if (val.length() != 0) {
			p.setObject(val);
		} else p.setType(PacketTypeFactory.FAILED);	
	}

	/**
	 * Builds a String array from a comma seperated list.
	 * Removes any superflouous white spaces and linebreaks,
	 * and ignores empty entries.
	 * 
	 * @param A comma seperated list of entries.
	 * @return The String array of entries.
	 */
	private static String[] getEntriesFromCommaSeperated(String input) {
		ArrayList<String> entries = new ArrayList<String>();

		String[] splittedEntries = input.split(",");
		for (int j = 0; j < splittedEntries.length; j++) {
			String stripped = splittedEntries[j].replaceAll(SNatBeginningOrEnd, "");
			stripped = stripped.replaceAll(N, " ");
			stripped = stripped.replaceAll("\\s+", " ");
			if (!stripped.equals("")) entries.add(stripped);
		}
		String[] entriesArray = new String[entries.size()];
		return entries.toArray(entriesArray);
	}

	/**
	 * String-based. Splits an input string in bolscript format into Packet objects 
	 * which are bundled in one Packets container.
	 * Packet types and visibilities are assigned by using the maps 
	 * Packet.keyPacketTypes and Packet.visibilityMap.
	 * The hide-symbol "$" at the beginning of a key is removed and 
	 * the packet is assigned invisible.
	 * 
	 * @param input An input string in bolscript format.
	 * @return A Packets container containing the Packets that were 
	 * found in the input.
	 * @see Packet, Packets
	 */
	public static Packets splitIntoPackets(String input) {
	
		String lineBreaks = "[:\n\r\f]+";
		input.replaceAll(lineBreaks, "\n");
		String regex = "(\\$)?((?:[^:\n\r\f])+):" + //Key
		"([^:]*)" +  //Value
		"(?=$|[\n\r\f]+(?:(?:(?:(?:[^:\n\r\f]*):)|(?:[\n\r\f]*\\s)*\\z)))" //following Key or End of Input (not captured)
		;
	
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(input);
	
		Packets packets = new Packets();
	
		int i=0;
		boolean isVisible;
		//PacketType type;
	
		while (m.find()) {
			//isVisible
	
			TextReference packetReference = null;
			TextReference keyReference = null;
			TextReference valueReference = null;
	
			MatchResult result = m.toMatchResult();
	
			String key = m.group(2).replaceAll(SNatBeginningOrEnd, "");
			
			if (key.length()>0) { //ignore packets with empty keys
				packetReference = new TextReference(result.start(0),result.end(0), 0);
				keyReference = new TextReference(result.start(2),result.end(2), 0);
				valueReference = new TextReference(result.start(3),result.end(3), 0);
	
	
				PacketType type = PacketTypeFactory.getType(m.group(2).toUpperCase());
				//debug.temporary(m.group(2).toUpperCase() + " => " + type);
				isVisible = type.displayInCompositionView() && (m.group(1) == null);
	
				Packet packet = new Packet(m.group(2), m.group(3), type, isVisible);
	
				packet.setTextReferencePacket(packetReference);
				packet.setTextRefKey(keyReference);
				packet.setTextRefValue(valueReference);
	
				packets.add(packet);
				i++;
			}
		}
		return packets;
	}

	public static String determineBolStringAroundCaret(String input, int caretPosition) {
		int RANGE = 20;
		int leftBorder = Math.max(0, caretPosition-RANGE);
		int rightBorder = Math.min(input.length(), caretPosition+RANGE);
	
		//debug.temporary("char after(?) caretPosition: " + input.charAt(caretPosition));
		String leftOfCaret = "(?<=\\(|\\)|"+SN+"|^)("+BOL+")?$";
		String rightOfCaret = "^"+ BOL + "(?>=\\(|\\)|"+SN+"|$)";
		String leftSubstring = input.substring(leftBorder, caretPosition);
		//debug.temporary("left substring = " + leftSubstring);
		String rightSubstring = input.substring(caretPosition, rightBorder);
		//debug.temporary("left substring = " + rightSubstring);
	
		Matcher mLeft = Pattern.compile(leftOfCaret).matcher(leftSubstring);
		Matcher mRight = Pattern.compile(rightOfCaret).matcher(rightSubstring);
	
		String leftSnippet;
		String rightSnippet;
		if (mLeft.find()) {
	
			leftSnippet = mLeft.group();
			//debug.temporary("found leftsnippet " + leftSnippet);
		} else leftSnippet = "";
		if (mRight.find()) {
			rightSnippet = mRight.group();
			//debug.temporary("found rightsnippet " + rightSnippet);
		} else rightSnippet = "";
		
		String candidate = leftSnippet + rightSnippet;
	
		Matcher m = BolCandidateUnit.pattern.matcher(candidate);
		if (m.find()) {
			candidate = m.group(1);
			//candidate is now the textsnippet to be searched for 
			return candidate;
		} else return null;
	}



}
