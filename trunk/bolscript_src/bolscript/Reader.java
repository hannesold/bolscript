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
import bolscript.sequences.FootnoteUnit;
import bolscript.sequences.Representable;
import bolscript.sequences.RepresentableSequence;
import bolscript.sequences.Unit;

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

	
	private static DecimalFormat threePlaces = new DecimalFormat("000");
	//regex for a Rational Nr
	
	public static String RATIONAL = "\\d{1,10}+(?:/\\d{1,10}+)?";
	
	
	public static boolean DEBUG = false;
	
	public static Debug debug = new Debug(Reader.class);
	
	public static void main(String[] args) throws Exception {
		BolBase bolBase = new BolBase();
			
		Packets packets = compilePacketsFromFile(config.Config.pathToCompositions+"tukra1.txt", bolBase);

		println(packets);
	}

	public static Packets compilePacketsFromFile(String filename, BolBaseGeneral bolBase) throws FileReadException {
		String fileContents = getContents(filename);
		
		return compilePacketsFromString(fileContents, bolBase);
	}
	
	public static Packets compilePacketsFromString(String input, BolBaseGeneral bolBase) {

		Packets rawPackets = splitIntoPackets(input);
		
		Packets packets = spliceFootnotesAndDoCosmetics(rawPackets);
		
		
		packets.addAll(0, BolBase.getStandard().getReplacementPackets());
		Debug.temporary(Reader.class, "packets with replacementpackets: " + packets);
		
		insertValuesOfKeys(packets);		
		
		solveMultiplesAndBrackets(packets);
		
		processMetaPackets(packets);
		
		try {
			packets = makeSpeedsAbsolute(packets);
		} catch (Exception e) {
			println(e.getStackTrace());
		}
		println(packets);
		
		addSequencesToBolPackets(packets, bolBase);
		
		
		return packets;
	}
	
	public static void solveMultiplesAndBrackets(Packets packets) {
		Matcher m;
		for(int k=0; k < packets.size(); k++) {
			Packet p = packets.get(k);
			if (p.getType() == Packet.BOLS) {
				println(p.getKey());
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
						if (multiples > config.Config.BOLSCRIPT_MAXIMUM_REPETITIONS) {
							multiples = 1;
						}
					} 
					int tailCut = 0;
					
					println("count: " + m.groupCount());
					if (m.group(3) != null) {
						try {
							tailCut = Integer.parseInt(m.group(3));
						} catch (NumberFormatException e) {
							
						}
						println("tailcut: " + tailCut);
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
	private static void insertValuesOfKeys (
			Packets packets) {
		
		for (int j=0; j < packets.size(); j++){
			Packet p = packets.get(j);
			
			if (p.getType() == Packet.BOLS) {
				
				//run replacements, starting at the next packet
				int k = j+1;
				boolean stop = false;
				while ((k < packets.size()) &! stop) {
					if	((packets.get(k).getType() == Packet.BOLS) && 
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

	public static Packets spliceFootnotesAndDoCosmetics(
			Packets packets) {
		
		Packets newPackets = new Packets();
		ArrayList<String> [] footnotes = new ArrayList[packets.size()];
		
		Matcher m;
		int j= 0;
		int footnoteCounter = 0;
		
		while (j < packets.size()) {
			Packet p = packets.get(j);
			footnotes[j] = new ArrayList<String>();
			
			if (p.getType() == Packet.BOLS) {
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
			
				//put spaces around colons
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
					newPackets.add(new Packet(getFootnoteCode(j,k, footnoteCounter+k),footnotes[j].get(k),Packet.FOOTNOTE, true));	
				}
				
				footnoteCounter+=footnotes[j].size();
				
				
			} else {
				newPackets.add(p);
			}
		j++;
		}
		return newPackets;
	}

	public static String getFootnoteCode(int packetNr, int footnoteNrInPacket, int footnoteNr) {
		return "FOOTNOTE_GLOBALNR_"+threePlaces.format(footnoteNr)+"_FROMPACKET_"+threePlaces.format(packetNr) +"_AT_"+ threePlaces.format(footnoteNrInPacket);
	}	
	
	public static String getFootnoteRegex () {
		return "FOOTNOTE_GLOBALNR_(\\d\\d\\d)_FROMPACKET_(\\d\\d\\d)_AT_(\\d\\d\\d)";
	}
	
	public static Packets splitIntoPackets(String input) {
		String regex = "(\\$)?((?:[^:\n\r\f])+):" + //Key
		"((?:[^:]|[\n\r\f])*)" +  //Value
		"(?=$|[\n\r\f]+(?:(?:(?:(?:[^:\n\r\f]*):)|(?:[\n\r\f]*\\s)*\\z)))" //following Key or End of Input (not captured)
		;
		
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(input);

		Packets packets = new Packets();
		
		int i=0;
		boolean isVisible;
		int type;
		
		while (m.find()) {
			//isVisible
			
			
			String key = m.group(2).replaceAll(SNatBeginningOrEnd, "");
			if (key.length()>0) { //ignore packets with empty keys
			
			if (Packet.keyPacketTypes.containsKey(m.group(2).toUpperCase())) {
				type = (Integer) Packet.keyPacketTypes.get(m.group(2).toUpperCase());
			} else {
				type = Packet.BOLS;
			}
			isVisible = Packet.visibilityMap[type] && (m.group(1) == null);
			Packet packet = new Packet(m.group(2), m.group(3), type, isVisible);
			packet.setTextReference(m.start(), m.end());
			
			packets.add(packet);
			i++;
			}
		}
		return packets;
	}
	
	/**
	 * This processes the Values of the bol packets
	 * based on the basic speed 1.
	 * The speed tags are considerred later in addSequencesToBolPackets
	 * 
	 * @param packets
	 * @return
	 * @throws Exception
	 */
	public static Packets makeSpeedsAbsolute(Packets packets) throws Exception {
		Packets newPackets = new Packets();
		
		Rational basicSpeed = new Rational(1);
		
		for (int i = 0; i < packets.size(); i++) {
			Packet p = packets.get(i);
			if (p.getType() == Packet.BOLS) {
				String newSequence = 
					makeSpeedsAbsolute(p.getValue(), new Rational(basicSpeed));
				
				newPackets.add(p.replaceValue(newSequence));
			} else {
				if (p.getType() == Packet.SPEED) {
					basicSpeed = (Rational) p.getObject();
				}
				newPackets.add(p);
			}
		}
		return newPackets;
	}	

	
	/**
	 * Processes a String and replaces relative speed changes by absolute speed changes.
	 * After each closing bracket the speed before opening the bracket is inserted.
	 * Also the outerSpeed is inserted at the beginning as absolute speed.
	 * Example  2 Dha Ge [ 2 Ti Te ] <1 Dha Ge     outerSpeed = 2
	 * -> 2! 4! Dha Ge [ 8! Ti Te ] 4! <1 Dha Ge
	 * @param input A String of the format " 2 Dha Ge [ 2 Ti Te ] <1 Dha Ge "
	 * @param outerSpeed The general absolute speed, that was set before this sequence.
	 * It will be multiplied with any relative speed.
	 * @return The processed String containing only absolute speeds.
	 * @throws Exception When something went wrong with parsing.
	 */
	public static String makeSpeedsAbsolute(String input, Rational outerSpeed) throws Exception {
		
	
		println("making speeds\n" + input);
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
			//println(i+"(0): '" + m.group(0) +"'");
			//println(i+"(1): '" + m.group(1)+"'");
			
			String token = new String(m.group(1));
			
			if (token.matches(RATIONAL)) {
				//println("new rel speed");
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
					throw new Exception("ERROR! wrong bracketing, close to " + m.end());
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
		//println(output);
		return finalOutput;
	}
	
	public static Rational getSpeedFromSpeedPacket(Packet p) throws Exception {
		String s = p.getValue();
		s = s.replaceAll(SN + "+", "");
		
		Rational speedCandidate = Rational.parseNonNegRational(s);
		if (speedCandidate.compareTo(config.Config.BOLSCRIPT_MAXIMUM_SPEED_R)<=0) {
			//the resulting speed is not too high
			return speedCandidate;
		} else {
			return new Rational(1);
		}
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
					if (speedCandidate.compareTo(config.Config.BOLSCRIPT_MAXIMUM_SPEED_R)<=0) {
						//the resulting speed is not too high
						currentSpeed = speedCandidate;
					}
					
					seq.add(new Unit(Representable.SPEED, currentSpeed));
				} catch (Exception e) {
					println("Speed could not be read out of: '" + all[i]+"'");
					e.printStackTrace();
				}				
			} else if (all[i].matches("<\\d+")) {
				//remove previous bols
				int bolsToRemove=0;
				try {
					bolsToRemove= Integer.parseInt(all[i].substring(1));			
				} catch (Exception e) {
					println("Nr of Bols to Remove could not be read out of: '" + all[i]+"'");
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
					println(e.getMessage());
					println("Footnote will be ignored.");
				}
				
			} else if (all[i].matches("[A-Za-z\\-]+(\\d)*(\\?)?!?")){
				//println("Bolcandidate " + all[i]);
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
					println("Bol '"+candidate+"' not found in BolBase, adding placeholder");
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
	 * Processes Meta-packets, such as
	 * SPEED, TAL, TYPE, NAME packets
	 * Adds according objects to the packets.
	 * @param packets
	 * @throws Exception
	 */
	public static void processMetaPackets(Packets packets) {
		Iterator<Packet> i = packets.listIterator();
		Packet p;
		while (i.hasNext()) {
			p = i.next();
			int type = p.getType();
			switch (type) {
			case Packet.TAL:
				String regex = SN + "*([^\\s\\n\\r\\f]+)" + SN +"*";
				Matcher m = Pattern.compile(regex).matcher(p.getValue());
				if (m.find()) {
					String talName = m.group(1);
					if (TalBase.standardInitialised()) {
						Tal tal = TalBase.standard().getTalFromName(talName);
						if (tal != null) {
							println("Tal " + tal + " added to talpacket");
							p.setObject(tal);
						}					
					}
				} else {
					println("Tal could not be parsed: " + p.getValue());
					p.setType(Packet.FAILED);
				}
				break;
			case Packet.TYPE:
				ArrayList<String> types = new ArrayList<String>();
				
				String[] splittedTypes = p.getValue().split(",");
				for (int j = 0; j < splittedTypes.length; j++) {
					String stripped = splittedTypes[j].replaceAll(SNatBeginningOrEnd, "");
					stripped = stripped.replaceAll(N, " ");
					stripped = stripped.replaceAll("\\s+", " ");
					if (!stripped.equals("")) types.add(stripped);
				}
				if (types.size()>0) {
					p.setObject((Object[]) types.toArray());
				} else p.setType(Packet.FAILED);
				break;
			case Packet.NAME:
				p.setObject(p.getValue().replaceAll(SNatBeginningOrEnd, ""));
				break;
			case Packet.SPEED:
				String input = p.getValue().replaceAll(SN +"*", "");
				try {
					Rational speed = Rational.parseNonNegRational(input);
					p.setObject(speed);
					println("read speed " + speed);
				} catch (Exception e) {
					println("failed to parse Speed, will be ignored!");
					p.setType(Packet.FAILED);
				}
				break;
			
			case Packet.FOOTNOTE:
				p.setObject(p.getValue().replaceAll(SNatBeginningOrEnd, ""));
				break;
			case Packet.EDITOR:
				ArrayList<String> editors = new ArrayList<String>();
				
				String[] splittedEditors = p.getValue().split(",");
				for (int j = 0; j < splittedEditors.length; j++) {
					String stripped = splittedEditors[j].replaceAll(SNatBeginningOrEnd, "");
					stripped = stripped.replaceAll(N, " ");
					stripped = stripped.replaceAll("\\s+", " ");
					if (!stripped.equals("")) editors.add(stripped);
				}
				if (editors.size()>0) {
					p.setObject((Object[]) editors.toArray());
				} else p.setType(Packet.FAILED);

				break;				
			case Packet.GHARANA:
				ArrayList<String> gharanas = new ArrayList<String>();
				
				String[] splitted = p.getValue().split(",");
				for (int j = 0; j < splitted.length; j++) {
					String stripped = splitted[j].replaceAll(SNatBeginningOrEnd, "");
					stripped = stripped.replaceAll(N, " ");
					stripped = stripped.replaceAll("\\s+", " ");
					if (!stripped.equals("")) gharanas.add(stripped);
				}
				if (gharanas.size()>0) {
					p.setObject((Object[]) gharanas.toArray());
				} else p.setType(Packet.FAILED);
				break;	
			 
			case Packet.COMMENT:
				String obj = p.getValue().replaceAll(SNatBeginningOrEnd, "");
				if (!obj.equals("")) {
					p.setObject(obj);
				} else p.setType(Packet.FAILED);
				break;
			}
			//switch (p.getType)
			
		}
		
	}
	
	/**
	 * Expects preprocessed bol packets in the final synthax, including preprocessed absolute speeds
	 * @param packets
	 * @param bolBase
	 */
	public static void addSequencesToBolPackets(Packets packets, BolBaseGeneral bolBase) {
		
		
		for (int i=0; i < packets.size(); i++) {
			Packet p = packets.get(i);
			
			if (p.getType() == Packet.BOLS) {
				try {
					p.setObject(getRepresentableSequence(p.getValue(), new Rational(1), bolBase));
				} catch (Exception e) {
					println("Failed to process Sequence in " + p.getKey() + ", will be ignored : " + p.getValue());
					p.setType(Packet.FAILED);
				}
			} 
		}
	}
	
	public static void print(String [] strings) {
		if (DEBUG) {
		for (int i = 0; i < strings.length; i++) {
			println(i +" = " + strings[i]);
		}
		}
	}

	public static void print(Object o) {
		if (DEBUG) {
		System.out.print(o);
		}
	}
	
	public static void print(Packets packets) {
		if (DEBUG) {
 		for (int i=0; i < packets.size(); i++) {
			println(packets.get(i));
		}
		}
	}
	
	public static void println(Object o) {
		if (DEBUG) {
		println(o);
		}
	}
	
	/**
	 * http://www.javapractices.com/topic/TopicAction.do?Id=42
	 * @param aFile
	 * @return
	 */
	 
	static public String getContents(String filename) throws FileReadException{
		return getContents(new File(filename));
	}
	
	static public String getContents(File aFile) throws FileReadException {
		 //...checks on aFile are elided
		    StringBuilder contents = new StringBuilder();
		    
		    try {
		      //use buffering, reading one line at a time
		      //FileReader always assumes default encoding is OK!
		      
			    FileInputStream fin = new FileInputStream(aFile);
			    InputStreamReader in = new InputStreamReader(fin, "UTF-8");
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
		    	throw new FileReadException (aFile, ex);
		    }
		    catch (IOException ex){
		    	throw new FileReadException (aFile, ex);
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
	 * @throws FileWriteException if anything goes wrong
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
