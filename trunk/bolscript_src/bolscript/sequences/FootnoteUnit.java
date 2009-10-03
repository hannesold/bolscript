package bolscript.sequences;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bolscript.packets.Packet;
import bolscript.packets.TextReference;
import bolscript.scanner.SequenceToken;

public class FootnoteUnit extends Unit implements Representable {
	
	private Packet packet;
	
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
	
	public FootnoteUnit(String footnoteText, TextReference textReference, Packet containingPacket){
		super(Representable.FOOTNOTE, footnoteText, textReference);
		this.footnoteText = footnoteText;
		this.packet = containingPacket;
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
	
	public Object getObject() {
		return footnoteText;
	}
	
	public Packet getContainingPacket() {
		return packet;
	}
	
	/**
	 * !Expects token.text of the Form "text"
	 * @param token
	 * @return
	 */
	public static Representable parseToken(SequenceToken token, Packet containingPacket) {
		Matcher m = pattern.matcher(token.text);
		if (m.matches()) {
			return new FootnoteUnit(m.group(1), token.textReference, containingPacket);
		}
		
		return new FailedUnit(token, "");
	}
	
	
	
	
	
	

}
