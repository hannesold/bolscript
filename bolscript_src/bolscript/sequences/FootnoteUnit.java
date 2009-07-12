package bolscript.sequences;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bolscript.Reader;

public class FootnoteUnit implements Representable {
	
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
	
	public int getType() {
		return Representable.FOOTNOTE;
	}
	
	public FootnoteUnit(int packetNr, int commentNrInPacket, int commentNrGlobal) {
		super();
		this.packetNr = packetNr;
		this.footnoteNrInPacket = commentNrInPacket;
		this.footnoteNrGlobal = commentNrGlobal;
		this.footnoteCode = Reader.getFootnoteCode(packetNr, commentNrInPacket, commentNrGlobal);
	}
	
	public FootnoteUnit(String footnoteCode) throws Exception{
		this.footnoteCode = footnoteCode;
		Matcher m = Pattern.compile(".*(\\d+)[^0-9]+(\\d+)[^0-9]+(\\d+).*").matcher(footnoteCode);
		if (m.find()) {
			
			this.footnoteNrInPacket = Integer.parseInt(m.group(3));
			this.packetNr = Integer.parseInt(m.group(2));
			this.footnoteNrGlobal = Integer.parseInt(m.group(1));
		} else {
			throw new Exception("Footnotecode " + footnoteCode + " could not be parsed!");
		}
	}

	public String toString() {
		return footnoteCode;
	}
	
	
	
	

}
