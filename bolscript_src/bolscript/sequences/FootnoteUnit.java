package bolscript.sequences;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bolscript.Reader;
import bolscript.packets.TextReference;
import bolscript.scanner.SequenceToken;

public class FootnoteUnit extends Unit implements Representable {
	
	/**
	 * the number of the packet in which the footnote was defined first
	 */
	public int packetNr;
	
	/**
	 * the number of the footnote within its defining packet
	 */
	public int footnoteNrInPacket;
	
	/**
	 * the number of the footnote
	 */
	public int footnoteNrGlobal;
	
	private String footnoteCode;
	
	private String footnoteText;
	
	private static String FOOTNOTE_REGEX = "\"([^\"]*)\"";
	private static Pattern pattern = Pattern.compile(FOOTNOTE_REGEX);
	
	public int getType() {
		return Representable.FOOTNOTE;
	}
	
	public FootnoteUnit(int packetNr, int commentNrInPacket, int commentNrGlobal, TextReference textReference, String footnoteText) {
		this.packetNr = packetNr;
		this.footnoteNrInPacket = commentNrInPacket;
		this.footnoteNrGlobal = commentNrGlobal;
		this.footnoteCode = Reader.getFootnoteCode(packetNr, commentNrInPacket, commentNrGlobal);
		this.footnoteText = footnoteText;
	}
	
	public FootnoteUnit(String footnoteText, TextReference textReference){
		this.footnoteText = footnoteText;
		this.textReference = textReference;
		this.type = Representable.FOOTNOTE;
	}
	
	public FootnoteUnit(String footnoteCode, TextReference textReference, String footnoteText) throws Exception{
		this.footnoteCode = footnoteCode;
		Matcher m = Pattern.compile(".*(\\d+)[^0-9]+(\\d+)[^0-9]+(\\d+).*").matcher(footnoteCode);
		if (m.find()) {
			
			this.footnoteNrInPacket = Integer.parseInt(m.group(3));
			this.packetNr = Integer.parseInt(m.group(2));
			this.footnoteNrGlobal = Integer.parseInt(m.group(1));
			this.footnoteText = footnoteText;
		} else {
			throw new Exception("Footnotecode " + footnoteCode + " could not be parsed!");
		}
	}

	public int getFootnoteNrGlobal() {
		return footnoteNrGlobal;
	}

	public void setFootnoteNrGlobal(int footnoteNrGlobal) {
		this.footnoteNrGlobal = footnoteNrGlobal;
	}

	public String getFootnoteText() {
		return footnoteText;
	}

	public String toString() {
		return footnoteCode;
	}
	
	public Object getObject() {
		return footnoteText;
	}
	
	/**
	 * !Expects token text of the Form "text"
	 * @param token
	 * @return
	 */
	public static Representable parseToken(SequenceToken token) {
		Matcher m = pattern.matcher(token.text);
		if (m.matches()) {
			return new FootnoteUnit(m.group(1), token.textReference);
		}
		
		return new FailedUnit(token, "");
	}
	
	
	
	
	
	

}
