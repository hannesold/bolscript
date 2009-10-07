package bolscript.scanner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import basics.Debug;
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
import bolscript.sequences.ReferencedBolPacketUnit;
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
	//private static DecimalFormat threeDigitFormat = new DecimalFormat("000");
	//regex for a Rational Nr
	/**
	 * Regex: A nonnegative Rational number bounded to 2 digits in numerator and denominator each.
	 */
	public static String RATIONAL = "\\d{1,2}+(?:/\\d{1,2}+)?";

	public static Packet defaultSpeedPacket = new Packet("Speed", "1",PacketTypeFactory.SPEED,false);
	
	/**
	 * A Regular expression to split an input String into a series of packets.
	 * Each match corresponds to one packet, where
	 * group(1) is optional and means that the packet is hidden
	 * group(2) is the packets key
	 * group(3) is the packets value
	 */
	public static String packetSplittingRegex = 
	"(\\$)?"+ //Hidden
	"((?:[^:\n\r\f])+):" + //Key
	"([^:]*)" +  //Value
	"(?=$|[\n\r\f]+(?:(?:(?:(?:[^:\n\r\f]*):)|(?:[\n\r\f]*\\s)*\\z)))" //following Key or End of Input (not captured)
	;
	
	public static Pattern packetSplittingPattern = Pattern.compile(packetSplittingRegex);
	
	public static Packets updatePacketsFromString(Packets oldPackets, String input) {
		//Packets oldoldPackets = oldPackets.clone();
		//oldoldPackets.addAll(oldPackets);
		
		Packets newPackets = splitIntoPackets(input);
		
		newPackets.add(0,defaultSpeedPacket);
		newPackets.addAll(1,BolBase.getStandard().getReplacementPackets());
		
		Packet currentSpeedPacket = defaultSpeedPacket;
		
		SequenceParser sequenceParser = new SequenceParser(1, newPackets);
		
		int lastUsedOldPacket = -1;
		int footnoteNr = 1;
		int i = 0;
		
		while (i<newPackets.size()) {
			Packet current = newPackets.get(i);
			String currentKey = current.getKey();
			String currentValue = current.getValue();
			Packet added = null;
			
			Packet correspondingOldPacket = null;
			for (int j=lastUsedOldPacket+1; j < oldPackets.size();j++) {
				Packet old = oldPackets.get(j);
				
				if (currentKey.equalsIgnoreCase(old.getKey())) {
					if (currentValue.equalsIgnoreCase(old.getValue())) {
						//this is like the old packet
						lastUsedOldPacket = j;
						correspondingOldPacket = old;
						break;
					}
				}
			}
			
			if (correspondingOldPacket != null) {
				//Packet correspondingOldPacket = correspondingOldPacket;
				//update exact content (key/value/visibility) and textreferences from the new version
				correspondingOldPacket.setKey(currentKey);
				correspondingOldPacket.setValue(currentValue);
				correspondingOldPacket.setTextReferencePacket(current.getTextReference());
				correspondingOldPacket.setTextRefKey(current.getTextRefKey());
				correspondingOldPacket.setTextRefValue(current.getTextRefValue());
				correspondingOldPacket.setVisible(current.isVisible());
				correspondingOldPacket.setHighlighted(current.isHighlighted());
				
				
				//set the old packet in favor of the new packet.
				newPackets.set(i, correspondingOldPacket);
				
				added = correspondingOldPacket;
				
				if (correspondingOldPacket.getType() == PacketTypeFactory.BOLS) {
					RepresentableSequence seq = (RepresentableSequence) correspondingOldPacket.getObject();
					// check for references
					ArrayList<ReferencedBolPacketUnit> referencedPacketUnits = seq.getReferencedBolPacketUnits();
					for (ReferencedBolPacketUnit unit: referencedPacketUnits) {
						//if (newPackets.conunit.getReferencedPacket())
						
						Packet referencedPacket = newPackets.findReferencedBolPacket(correspondingOldPacket, unit.getReferencedPacket().getKey());
						if (referencedPacket == null) {
							//needs to be reparsed, since the reference seems to be gone
							RepresentableSequence newSeq = sequenceParser.parseSequence(correspondingOldPacket, currentValue);
							correspondingOldPacket.setObject(newSeq);
						} else {
							unit.setReferencedPacket(referencedPacket);
							seq.clearCache();
						}
					}
					
					Rational newSpeed = ((Rational) currentSpeedPacket.getObject());
					Rational oldSpeed = null;
					if (seq.getReferencedSpeedPacket() != null) {
						if (seq.getReferencedSpeedPacket() != currentSpeedPacket) {
							oldSpeed = ((Rational) seq.getReferencedSpeedPacket().getObject());
						}
					}
					
					seq.setReferencedSpeedPacket(currentSpeedPacket);
					if (!newSpeed.equals(oldSpeed)) {
						seq.clearCache();
					}
					

				} //== BOLS
				
				
			} // correspondingOldPacket != null
			else { //correspondingOldPacket == null
				//this is a new packet!
				if (current.getType() == PacketTypeFactory.BOLS) {
					//add a bol packet
					RepresentableSequence seq = sequenceParser.parseSequence(current, current.getValue());
					seq.setReferencedSpeedPacket(currentSpeedPacket);
					current.setObject(seq);
					added = current;
				} else {
					//add a meta packet
					processMetaPacket(current);
					added = current;
				}
			}
			
			
			if (added != null) {
				if (added.getType() == PacketTypeFactory.SPEED) {
				
					currentSpeedPacket = added;
				
				} else if (added.getType() == PacketTypeFactory.BOLS) {
					
					//add footnote packets
					RepresentableSequence seq = (RepresentableSequence) added.getObject();
					ArrayList<FootnoteUnit> footnotes = seq.getFootnoteUnits();
					
					for (int j = 0; j < footnotes.size(); j++) {
						FootnoteUnit fu = footnotes.get(j);
						fu.setFootnoteNrGlobal(footnoteNr);
						Packet fp = new Packet("Footnote "+ footnoteNr, fu.getFootnoteText(), PacketTypeFactory.FOOTNOTE, true);
						fp.setObject(fu);
						footnoteNr++;
						newPackets.add(i+1,fp);
						i++;
					}
				}
			}
			
			i++;
			
		} //while i < newpackets.size()

		//debug.temporary(newPackets.toString());
		
		return newPackets;
		
	}
	
	
	public static Packets compilePacketsFromString(String input) {
	
		Packets packets = splitIntoPackets(input);

		packets.add(0,defaultSpeedPacket);
		packets.addAll(1, BolBase.getStandard().getReplacementPackets());
	
		Parser.processMetaPackets(packets);
	
		Packet currentSpeedPacket = defaultSpeedPacket;
		//Packet currentSpeedPacket = new Packet("Speed","1",PacketTypeFactory.SPEED, false);
	
		SequenceParser parser = new SequenceParser(1, packets);
		
		int i = 0;
	
		while (i <packets.size()) {	
			Packet p = packets.get(i);
	
			if (p.getType() == PacketTypeFactory.BOLS) {
				RepresentableSequence seq = parser.parseSequence(p, p.getValue());
				seq.setReferencedSpeedPacket(currentSpeedPacket);
				p.setObject(seq);
				ArrayList<FootnoteUnit> footnotes = seq.getFootnoteUnits();
				
				for (int j = 0; j < footnotes.size(); j++) {
					FootnoteUnit fu = footnotes.get(j);
					Packet fp = new Packet("Footnote "+ fu.getFootnoteNrGlobal(), fu.getFootnoteText(), PacketTypeFactory.FOOTNOTE, true);
					fp.setObject(fu);
					packets.add(i+1,fp);
					i++;
				}
				
			} else if (p.getType() == PacketTypeFactory.SPEED) {
				currentSpeedPacket = p;
			}
			i++;
		}
	
		//debug.temporary(packets.toString());
		return packets;
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

		Matcher m = packetSplittingPattern.matcher(input);
	
		Packets packets = new Packets();
	
		int i=0;
		
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
				boolean isVisible = type.displayInCompositionView() && (m.group(1) == null);
	
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
	private static void processMetaPackets(Packets packets) {
		Iterator<Packet> i = packets.listIterator();
		while (i.hasNext()) {
			processMetaPacket(i.next());
		} //while
	}
	
	private static void processMetaPacket(Packet p) {
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
