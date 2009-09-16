package bolscript;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import basics.Debug;
import basics.FileReadException;
import basics.FileWriteException;
import basics.Rational;
import bols.Bol;
import bols.BolBase;
import bols.BolBaseGeneral;
import bols.BolName;
import bols.PlayingStyle;
import bols.tals.Tal;
import bols.tals.TalBase;
import bolscript.packets.Packet;
import bolscript.packets.Packets;
import bolscript.packets.TextReference;
import bolscript.packets.types.PacketType;
import bolscript.packets.types.PacketTypeFactory;
import bolscript.packets.types.PacketType.ParseMode;
import bolscript.sequences.FootnoteUnit;
import bolscript.sequences.Representable;
import bolscript.sequences.RepresentableSequence;
import bolscript.sequences.Unit;

/**
 * This class contains the essential methods for reading and parsing bolscript files,
 * converting them into a series of typed Packets and extracting a RepresentableSequence from each
 * Packet of type BOL.
 * The most important methods is compilePacketsFromString.
 * 
 * @author hannes
 * @see Packet, Packets
 */
public class Reader {
	/**
	 * Regex: Whitespaces and newline characters
	 */
	public static final String SN = "(?:\\s|[\n\r\f])";
	
	/**
	 * Regex: Newline caracters;
	 */
	public static final String N = "[\n\r\f]";
	
	/**
	 * Regex: Whitespaces and TABS
	 */
	public static final String S = "\\s";
	
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
	
	public static Debug debug = new Debug(Reader.class);
	
	public static Packets compilePacketsFromString(String input, BolBaseGeneral bolBase) {

		Packets rawPackets = splitIntoPackets(input);
		
		debug.temporary(rawPackets);
		
		Packets packets = spliceFootnotesAndDoCosmetics(rawPackets);
		
		packets.addAll(0, BolBase.getStandard().getReplacementPackets());
		
		
		
		insertValuesOfKeys(packets);		
		
		solveMultiplesAndBrackets(packets);
		
		processMetaPackets(packets);
		
		try {
			packets = makeSpeedsAbsolute(packets);
		} catch (IllegalArgumentException e) {
			debug.critical(e.getStackTrace());
		}
		debug.debug(packets);
		
		addSequencesToBolPackets(packets, bolBase);
		
		
		return packets;
	}
	
	protected static void solveMultiplesAndBrackets(Packets packets) {
		Matcher m;
		for(int k=0; k < packets.size(); k++) {
			Packet p = packets.get(k);
			if (p.getType() == PacketTypeFactory.BOLS) {
				debug.debug(p.getKey());
				// "()x3 (?:<(\\d+))
				String regex = "\\(([^\\(\\)]+)\\)(?:x(\\d+))?(?:<(\\d+))?";
				// 1 klammerinhalt, 2 multfaktor, 3 tailcut?
				
				
				Pattern multiplications= Pattern.compile(regex);
				m = multiplications.matcher(p.getValue());
				
				while (m.find()) {
					
					String seq = m.group(0);
					String nseq = new String();
					int multiples = 1;
					
					if (m.group(2) != null) {
						try {
							multiples = Integer.parseInt(m.group(2));
						}
						catch (NumberFormatException e) {
							
						}
						//Do not allow too high multiplications
						if (multiples > bolscript.config.Config.BOLSCRIPT_MAXIMUM_REPETITIONS) {
							multiples = 1;
						}
					} 
					int tailCut = 0;
					
					debug.debug("count: " + m.groupCount());
					if (m.group(3) != null) {
						try {
							tailCut = Integer.parseInt(m.group(3));
						} catch (NumberFormatException e) {
							
						}
						debug.debug("tailcut: " + tailCut);
					}

					// insert up to last multiple
					for (int j=1; j <= multiples; j++) {
						nseq = nseq + " [ "  + m.group(1) + " ] ";
					}
					
					// add tailcut 
					if (tailCut > 0) {
						nseq = nseq + "<"+tailCut+" ";
					}
					
					String newVal = "";
					/*boolean failed = false;
					seq = seq.replace("\\$", " ");
					nseq = nseq.replace("\\$", " ");
					newVal = p.getValue().replace("\\$", " ");
					*/
					//try {
						newVal = p.getValue().replaceAll("\\Q"+seq+"\\E", nseq);
					/*} catch( Exception e) {
						Debug.critical(Reader.class, " error inserting Value " + nseq + " for " + seq);
						Debug.critical(Reader.class, "m.group(0) = " + m.group(0));
						//Debug.critical(Reader.class, )
						newVal = p.getValue();
						System.exit(0);
					}*/
					
					
					packets.set(k, p.replaceValue(newVal));

					p = packets.get(k);
					m = multiplications.matcher(p.getValue());
				}
			}
		}
		
	}

	/**
	 * Inserts the values of keys which are used within bol sequences.
	 * Example:
	 * Before
	 * A: bla bla
	 * B: and then A
	 * After
	 * A: bla bla
	 * B: and then bla bla
	 * @param packets
	 */
	protected static void insertValuesOfKeys (
			Packets packets) {
		
		for (int j=0; j < packets.size(); j++){
			Packet p = packets.get(j);
			
			if (p.getType() == PacketTypeFactory.BOLS) {
				
				//run replacements, starting at the next packet
				int k = j+1;
				boolean stop = false;
				while ((k < packets.size()) &! stop) {
					if	((packets.get(k).getType() == PacketTypeFactory.BOLS) && 
						(packets.get(k).getKey().equals(p.getKey()))) {
						//stop if a packet with an equal key is found (the key is then sort of overwritten)
						stop = true;
					} else {
					
					//before insertion
					String s = packets.get(k).getValue();
					
					//remove the footnotes before copying into further repetitions
					String replacement = new String(p.getValue()).replaceAll(getFootnoteRegex(), "");
					
					// replace all keys by existing values defined before, but only if they stand alone,
					// like " A " or at the beginning or with brackets or with an x3 afterwards
					try {
//						Debug.temporary(Reader.class, "replacing '" +  p.getKey() + "' with '" + replacement +"'");
//						Pattern pat = Pattern.compile("(?<="+SN+"|[\\(]|^)+" + p.getKey() + "(?="+SN+"|\\z|$|[x\\)])", Pattern.CASE_INSENSITIVE);
//						Matcher mat = pat.matcher(s);
//						while (mat.find()) {
//							Debug.temporary(Reader.class, "found: " + mat.group(0));
//						}
						
					s = s.replaceAll("(?i)(?<="+SN+"|[\\(]|^)+" + p.getKey() + "(?="+SN+"|\\z|$|[x\\)])", replacement);
					} catch (IllegalArgumentException e) {
						debug.critical("Error inserting predifinedvalues");
						debug.critical("While inserting value of " + p.getKey() + " in " + packets.get(k));
						debug.critical("value = " + replacement);
						debug.critical(e);
						
						
					}
					
					/*String regex = "(?:"+SN+"|^|(\\())"+SN+"*" + p.getKey() + SN + "*(?:(\\))|"+SN+"|\\z|$|x)";
					
					Matcher m = Pattern.compile(regex).matcher(s);
					while (m.find()) {
						if ((m.group(1) != null) && (m.group(2) != null))  {
							//replace once
						} else {
							//replace once but add brackets
						}
					}*/
					packets.set(k,packets.get(k).replaceValue(s));
					k++;
				}
					
					
				}
			}
		}
	}

	/**
	 * String-based. Replaces footnotes in a string bol sequence by a footnote code and adds a footnote-packet after the bol-packet containing the footnote's text.
	 * Corrects several malformed expressions. Examples (incomplete list!)
	 * <li>removes leading and ending whitespaces or line-breaks</li>
	 * <li>removes whitespaces or line-breaks within multiplication syntax, e.g. <code> x 3 < 2</code> -> <code>x3<2</code></li>
	 * <li>removes $ signs (they only belong at the beginning of keys)</li>
	 * <li>adds brackets around single multiplied expressions, e.g. <code>Ax3</code> -> <code>(A)x3</code>.</li>
	 * <li>makes sure there are whitespaces around commas</li>
	 * @param packets
	 * @return A new Packets containing the improved version.
	 */
	protected static Packets spliceFootnotesAndDoCosmetics(
			Packets packets) {
		
		Packets newPackets = new Packets();
		ArrayList<String> [] footnotes = new ArrayList[packets.size()];
		
		Matcher m;
		int j= 0;
		int footnoteCounter = 0;
		
		while (j < packets.size()) {
			Packet p = packets.get(j);
			footnotes[j] = new ArrayList<String>();
			
			if (p.getType() == PacketTypeFactory.BOLS) {
				String s = new String(p.getValue());
				
				
				//gather footnotes
				Pattern patternFootnotes = Pattern.compile("\"([^\"]*)\"");
				m = patternFootnotes.matcher(s);
				
				while (m.find()) {
					footnotes[j].add(m.group(1));
				}
				
				//replace footnotes by placeholders
				for (int k = 0; k < footnotes[j].size(); k++) {
					s = s.replaceAll("\""+footnotes[j].get(k)+"\"", " " + getFootnoteCode(j,k, footnoteCounter+k) + " ");

				}
			
				//put spaces around comas
				s = s.replaceAll(",", " , ");
				//SN + "*,"+SN + "*"
				
				//remove $
				s = s.replaceAll("\\$", " ");
				
				// x 4 at the end of a line means take everything x3
				// "bla blax2 bla x 3 " -> "(bla blax2 bla)x3"
				s = s.replaceAll("(?<="+N+"|^)(.*)\\s+x\\s*(\\d+)(?:\\z|$|"+N+")","($1)x$2");
						
				// "  x  3" -> "x3"
				s = s.replaceAll(SN+"+x\\s*(\\d+)", "x$1");
				
				// x3 directly at a certain BOL or KEY means take this x3
				// " Ax3 " -> "(A)x3"
				s = s.replaceAll("(?<="+SN+")([A-Za-z0-9< &&[^x]]+)(?=x\\d)","($1)");
					
				// "bla x3 < 2" -> "bla x3<2"
				s = s.replaceAll("x(\\d+)\\s*<\\s*(\\d+)", "x$1<$2");
				
				// remove all newline chars and multiple whitespaces
				s = s.replaceAll(SN+"+"," ");
				
				// remove leading and ending whitespaces
				s = s.replaceAll("(^\\s+)|(\\s+$)","");
				
				s = " ( " + s + " ) ";				
				//add packet
				newPackets.add(p.replaceValue(s));
				
				//add footnote packets:
				for (int k = 0; k < footnotes[j].size(); k++) {
					newPackets.add(new Packet(getFootnoteCode(j,k, footnoteCounter+k),footnotes[j].get(k),PacketTypeFactory.FOOTNOTE, true));	
				}
				
				footnoteCounter+=footnotes[j].size();
				
				
			} else {
				newPackets.add(p);
			}
		j++;
		}
		return newPackets;
	}

	/**
	 * Returns a placeholder-string for replacing footnotes in bol sequences 
	 * while they are in String form.
	 * Each footnoteCode is composed of a unique string containing various 
	 * indices that code its position in the bolscript document.
	 * 
	 * @param packetNr The index of the Packet in which the footnote lies.
	 * @param footnoteNrInPacket The index of the footnote within the Packet
	 * @param footnoteNr The index of the footnote amoung all footnotes
	 *  of the current bolscript document.
	 * @return A code for the given footnote numbers.
	 */
	public static String getFootnoteCode(int packetNr, int footnoteNrInPacket, int footnoteNr) {
		return "FOOTNOTE_GLOBALNR_"+threeDigitFormat.format(footnoteNr)+"_FROMPACKET_"+threeDigitFormat.format(packetNr) +"_AT_"+ threeDigitFormat.format(footnoteNrInPacket);
	}	
	
	/**
	 * Returns a regular expression to find and extract footnote codes in a string.
	 * @return A regular expression for footnote codes.
	 */
	public static String getFootnoteRegex () {
		return "FOOTNOTE_GLOBALNR_(\\d\\d\\d)_FROMPACKET_(\\d\\d\\d)_AT_(\\d\\d\\d)";
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
	protected static Packets splitIntoPackets(String input) {
		String regex = "(\\$)?((?:[^:\n\r\f])+):" + //Key
		"((?:[^:]|[\n\r\f])*)" +  //Value
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
				packetReference = new TextReference(result.start(0),result.end(0));
				keyReference = new TextReference(result.start(2),result.end(2));
				valueReference = new TextReference(result.start(3),result.end(3));
				
				PacketType type = PacketTypeFactory.getType(m.group(2).toUpperCase());
				debug.temporary(m.group(2).toUpperCase() + " => " + type);
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
	
	/**
	 * String-based. Processes the values of the bol packets based on the basic speed 1.
	 * The speed tags are considered later in addSequencesToBolPackets.
	 * 
	 * @param packets
	 * @return A Packets container featuring the processed version.
	 * @throws IllegalArgumentException This Exception may be caused in subroutines 
	 * due to parsing errors of Rationals or bracketing errors.
	 */
	protected static Packets makeSpeedsAbsolute(Packets packets) throws IllegalArgumentException {
		Packets newPackets = new Packets();
		
		Rational basicSpeed = new Rational(1);
		
		for (int i = 0; i < packets.size(); i++) {
			Packet p = packets.get(i);
			if (p.getType() == PacketTypeFactory.BOLS) {
				String newSequence = 
					makeSpeedsAbsolute(p.getValue(), new Rational(basicSpeed));
				
				newPackets.add(p.replaceValue(newSequence));
			} else {
				if (p.getType() == PacketTypeFactory.SPEED) {
					basicSpeed = (Rational) p.getObject();
				}
				newPackets.add(p);
			}
		}
		return newPackets;
	}	

	
	/**
	 * String-based. Processes a String and replaces relative speed changes by absolute speed changes.
	 * After each closing bracket the speed before opening the bracket is inserted.
	 * Also the outerSpeed is inserted at the beginning as absolute speed.
	 * Example  2 Dha Ge [ 2 Ti Te ] <1 Dha Ge     outerSpeed = 2
	 * -> 2! 4! Dha Ge [ 8! Ti Te ] 4! <1 Dha Ge
	 * @param input A String of the format " 2 Dha Ge [ 2 Ti Te ] <1 Dha Ge "
	 * @param outerSpeed The general absolute speed, that was set before this sequence.
	 * It will be multiplied with any relative speed.
	 * @return The processed String containing only absolute speeds.
	 * @throws IllegalArgumentException This Exception may be caused when parsing Rationals or thrown when brackets are malformed.
	 */
	protected static String makeSpeedsAbsolute(String input, Rational outerSpeed) throws IllegalArgumentException {
		
	
		debug.debug("making speeds\n" + input);
		// catches '[' ']' and 3 and 3! if there are spaces around it
		Pattern p = Pattern.compile("(?<=\\s+)([\\[\\]]|"+RATIONAL+"(?:\\!)?)(?=\\s)");
		Matcher m = p.matcher(input);
		StringBuilder output = new StringBuilder();
		ArrayList<Rational> speedStack = new ArrayList<Rational>();
		speedStack.add(outerSpeed.getCopy()); //depth = 0
		speedStack.add(outerSpeed.getCopy()); //depth = 1
		int depth = 1;
		
		int i = 0;
		int cursor = 0;
		
		output.append(" " + outerSpeed.toString() + "! ");
		while (m.find()) {
			//debug.debug(i+"(0): '" + m.group(0) +"'");
			//debug.debug(i+"(1): '" + m.group(1)+"'");
			
			String token = new String(m.group(1));
			
			if (token.matches(RATIONAL)) {
				//debug.debug("new rel speed");
				Rational r = speedStack.get(depth-1).times(Rational.parseNonNegRational(token));
				speedStack.set(depth,r);
				output.append(input.substring(cursor, m.start(1))
						 + " " + r.toString() + "! ");
				cursor = m.end(1)+1;
				
			} else if (token.matches(RATIONAL +"!$")){
				
				Rational r = Rational.parseNonNegRational(token.substring(0, token.length()-1));
				Debug.temporary(Reader.class, "new fixed speed " + r);
				speedStack.set(depth,r);
				output.append(input.substring(cursor, m.end(1)+1));
				cursor = m.end(1)+1;
				
			} else if (token.equals("[")) {
				depth++;
				speedStack.add(speedStack.get(depth-1));
			} else if (token.equals("]")) {
				speedStack.remove(depth);
				depth--;
				if (depth <0) {
					throw new IllegalArgumentException("ERROR! wrong bracketing, close to " + m.end());
				}
				Rational r = speedStack.get(depth);
				output.append(input.substring(cursor, m.end(1)+1)
				+ " " + r.toString() + "! ");
				cursor = m.end(1)+1;
				
			}
			
			i++;
		}
		
		if (cursor < input.length()) {
			output.append(input.substring(cursor, input.length()));
		}
		
		
		String finalOutput = output.toString().replaceAll("\\s+", " ") 
			.replaceAll("(?<=(?:(?:"+RATIONAL+")!\\s){1,10}+)("+RATIONAL+")", "$1"); //replace multiple chains of speeds by the last one
		//debug.debug(output);
		return finalOutput;
	}

	/**
	 * Expects a String that only contains ABSOLUTE Speeds 4! 2! etc.
	 * @param input
	 * @param basicSpeed
	 * @param bolBase
	 * @return
	 */
	public static RepresentableSequence getRepresentableSequence (String input, Rational basicSpeed, BolBaseGeneral bolBase) {
		
		RepresentableSequence seq = new RepresentableSequence();
		
		String [] all = input.split("\\s");
		
		int i = 0;
		Rational currentSpeed = new Rational(basicSpeed);
		boolean colonAddedAlready = true;
		
		//seq.add(new Unit(Representable.SPEED, currentSpeed));
		
		while (i < all.length) {
			if (all[i].matches(RATIONAL + "!")) {
				try {
					
					Rational speedCandidate = basicSpeed.times(Rational.parseNonNegRational(all[i].substring(0, all[i].length()-1)));
					if (speedCandidate.compareTo(bolscript.config.Config.BOLSCRIPT_MAXIMUM_SPEED_R)<=0) {
						//the resulting speed is not too high
						currentSpeed = speedCandidate;
					}
					
					seq.add(new Unit(Representable.SPEED, currentSpeed));
				} catch (Exception e) {
					debug.debug("Speed could not be read out of: '" + all[i]+"'");
					e.printStackTrace();
				}				
			} else if (all[i].matches("<\\d+")) {
				//remove previous bols
				int bolsToRemove=0;
				try {
					bolsToRemove= Integer.parseInt(all[i].substring(1));			
				} catch (Exception e) {
					debug.debug("Nr of Bols to Remove could not be read out of: '" + all[i]+"'");
					e.printStackTrace();					
				}
				// remove bols
				int k = seq.size()-1;
				while ((bolsToRemove > 0) && (k>=0)) {
					if (seq.get(k).getType()==Representable.BOL) {
						seq.remove(k);
						bolsToRemove--;							
					}
					k--;
				}
			} else if (all[i].matches("\\[")) {
				seq.add(new Unit(Representable.BRACKET_OPEN,all[i]));
				if (!colonAddedAlready) {
					seq.add(new Unit(Representable.COMMA,","));
					colonAddedAlready = true;
				}
				
			} else if (all[i].matches("\\]")) {
				seq.add(new Unit(Representable.BRACKET_CLOSED,all[i]));
				if (!colonAddedAlready) {
					seq.add(new Unit(Representable.COMMA,","));
					colonAddedAlready = true;
				}
				
			} else if (all[i].matches(",")) {
				Representable c = new Unit(Unit.COMMA, ","); 
				if (!colonAddedAlready) {
					seq.add(new Unit(Representable.COMMA,","));
					colonAddedAlready = true;
				}				
			}
			else if (all[i].matches("FOOTNOTE_.*")) {
				
				try {
					Representable c = new FootnoteUnit(all[i]);
					seq.add(c);	
				} catch (Exception e) {
					debug.debug(e.getMessage());
					debug.debug("Footnote will be ignored.");
				}
				
			} else if (all[i].matches("[A-Za-z\\-]+(\\d)*(\\?)?!?")){
				//debug.debug("Bolcandidate " + all[i]);
				String candidate = new String(all[i]);
				
				boolean emphasized;
				double velocity;
				
				if (candidate.charAt(all[i].length()-1) == '!') {
					velocity = 1.2;
					emphasized = true;
					candidate = candidate.substring(0, candidate.length()-1);
					
				} else {
					velocity = 1;
					emphasized = false;
				}
				
				BolName bolName = bolBase.getBolName(candidate);
				if (bolName==null) {
					debug.debug("Bol '"+candidate+"' not found in BolBase, adding placeholder");
					bolName = new BolName(candidate);
					bolName.setWellDefinedInBolBase(false);
					bolBase.addBolName(bolName);
				}
				
				Bol bol = new Bol(bolName, new PlayingStyle(currentSpeed,velocity));
				bol.setEmphasized(emphasized);
				
				seq.add(bol);
				
				colonAddedAlready = false;
				
			}
			i++;
		}
		
		return seq;
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
	protected static void processMetaPackets(Packets packets) {
		
		Iterator<Packet> i = packets.listIterator();
		Packet p;
		
		while (i.hasNext()) {
			p = i.next();
		
			int type = p.getType();
			debug.temporary(p.getKey() + " => " + p.getValue() + ", type: " + p.getPType());
			ParseMode parseMode = p.getPType().getParseMode();
			
			if (parseMode == ParseMode.string) {
				setObjFromString(p);
			} else if (parseMode == parseMode.commaSeperated) {
				setObjFromCommaSeperated(p);
			} else if (parseMode == parseMode.other) switch (type) {

			case PacketTypeFactory.TAL:
				String regex = SN + "*([^\\s\\n\\r\\f]+)" + SN +"*";
				Matcher m = Pattern.compile(regex).matcher(p.getValue());
				if (m.find()) {
					String talName = m.group(1);
					if (TalBase.standardInitialised()) {
						Tal tal = TalBase.standard().getTalFromName(talName);
						if (tal != null) {
							debug.debug("Tal " + tal + " added to talpacket");
							p.setObject(tal);
						}					
					}
				} else {
					debug.debug("Tal could not be parsed: " + p.getValue());
					p.setType(PacketTypeFactory.FAILED);
				}
				break;

			case PacketTypeFactory.SPEED:
				String input = p.getValue().replaceAll(SN +"*", "");
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
	 * Expects preprocessed bol packets in the final synthax, including preprocessed absolute speeds
	 * @param packets
	 * @param bolBase
	 */
	protected static void addSequencesToBolPackets(Packets packets, BolBaseGeneral bolBase) {
		
		
		for (int i=0; i < packets.size(); i++) {
			Packet p = packets.get(i);
			
			if (p.getType() == PacketTypeFactory.BOLS) {
				try {
					p.setObject(getRepresentableSequence(p.getValue(), new Rational(1), bolBase));
				} catch (Exception e) {
					debug.debug("Failed to process Sequence in " + p.getKey() + ", will be ignored : " + p.getValue());
					p.setType(PacketTypeFactory.FAILED);
				}
			} 
		}
	}
	
	
	/**
	 * Returns the contents of a textfile, but only if it does not exceed a specified maximum file size. In case of exceeding or
	 * in case of some reading error a FileReadException is thrown.
	 * @param file The file.
	 * @param maxFileSize The maximum allowed file size in bytes.
	 * @return
	 * @throws FileReadException Is thrown if it is larger than maxFileSize or there is some readError.
	 */
	static public String getContents(File file, int maxFileSize) throws FileReadException{
		if (file.length() < maxFileSize) {
			debug.debug("reading file "+file.getName()+" with file size: " + file.length() + " bytes.");
	    	return getContents(file, "UTF-8");
	    } else {
	    	throw new FileReadException(file, new Exception("File exceeds maximum allowed filesize of: " + maxFileSize + "B, it has size:" + file.length()));
	    } 
	}
	
	/**
	 * Original version from http://www.javapractices.com/topic/TopicAction.do?Id=42
	 * Returns the contents of a textfile. In case of some reading error a FileReadException is thrown.
	 * @param file The file.
	 * @param encoding TODO
	 * @param maxFileSize The maximum allowed file size in bytes.
	 * @return
	 * @throws FileReadException Is thrown if it is larger than maxFileSize or there is some readError.
	 */
	static public String getContents(File file, String encoding) throws FileReadException {
		 //...checks on aFile are elided
		    StringBuilder contents = new StringBuilder();
		    
		    try {
		      //use buffering, reading one line at a time
		      //FileReader always assumes default encoding is OK!
		      
			    FileInputStream fin = new FileInputStream(file);
			    InputStreamReader in = new InputStreamReader(fin, encoding);
			    BufferedReader input = new BufferedReader(in);
			    
		      try {
		        String line = null; //not declared within while loop
		        /*
		        * readLine is a bit quirky :
		        * it returns the content of a line MINUS the newline.
		        * it returns null only for the END of the stream.
		        * it returns an empty String if two newlines appear in a row.
		        */
		        while (( line = input.readLine()) != null){
		          contents.append(line);
		          contents.append(System.getProperty("line.separator"));
		        }
		      }
		      finally {
		        input.close();
		      }
		    }
		    catch (FileNotFoundException ex) {
		    	throw new FileReadException (file, ex);
		    }
		    catch (IOException ex){
		    	throw new FileReadException (file, ex);
		    }
		    
		    return contents.toString();
		  }
	
	/**
	 * Write the contents to a file with the given filename.
	 * If the file already exists it is overwritten.
	 * If not, it will be created first.
	 * 
	 * @param filename
	 * @param contents
	 * @throws FileWriteException if anything goes wrong.
	 */
	static public void writeFile(String filename, String contents) throws FileWriteException {
		File file = new File(filename);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException ex) {
				throw new FileWriteException(file, ex);
			}
		} else if (!file.isFile()) {
		    throw new FileWriteException( file , 
		    		new IllegalArgumentException("Should not be a directory: " + file));
		}
		try {
			Debug.debug(Reader.class, "setting contents of " + file + "...");
			setContents(file, contents);
			Debug.debug(Reader.class, "done...");
		} catch (Exception ex) {
			throw new FileWriteException(file, ex);
		}
	}
	
	
	/**
	  * Change the contents of text file in its entirety, overwriting any
	  * existing text.
	  *
	  * This style of implementation throws all exceptions to the caller.
	  *
	  * @param aFile is an existing file which can be written to.
	  * @throws IllegalArgumentException if param does not comply.
	  * @throws FileNotFoundException if the file does not exist.
	  * @throws IOException if problem encountered during write.
	  */
	  static public void setContents(File aFile, String aContents)
	                                 throws FileNotFoundException, IOException {
	    if (aFile == null) {
	      throw new IllegalArgumentException("File should not be null.");
	    }
	    
	    if (!aFile.exists()) {
	      throw new FileNotFoundException ("File does not exist: " + aFile);
	    }
	    if (!aFile.isFile()) {
	      throw new IllegalArgumentException("Should not be a directory: " + aFile);
	    }
	    if (!aFile.canWrite()) {
	      throw new IllegalArgumentException("File cannot be written: " + aFile);
	    }

	    //use buffering
	    FileOutputStream fos = new FileOutputStream(aFile);
	    OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");

	    try {
	      //FileWriter always assumes default encoding is OK!
	      out.write(aContents);
	    }
	    finally {
	      out.close();
	      fos.close();
	    }
	  }



}

