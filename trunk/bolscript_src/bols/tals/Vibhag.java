package bols.tals;

/**
 * Describes a vibhag (a subsection of a tal). 
 * Contains start, length and type.
 * @author hannes
 */
public class Vibhag {
	public static int SAM = 0;
	public static int TALI = 1;
	public static int KALI = 2;

	public static String[] typeNames;

	static {
		typeNames = new String[3];
		typeNames[SAM] = "Sam";
		typeNames[TALI] = "Tali";
		typeNames[KALI] = "Kali";
	}
	
	public static String[] typeSymbols;

	static {
		typeSymbols = new String[3];
		typeSymbols[SAM]  = "+";
		typeSymbols[TALI] = "-";
		typeSymbols[KALI] = "o";
	}	
	
	private int start;
	private int length;
	private int type;
	
	public Vibhag(int start, int length, int type) {
		super();
		// TODO Auto-generated constructor stub
		this.length = length;
		this.start = start;
		this.type = type;
	}

	//getterssetters
	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public String getName() {
		return typeNames[type];
	}
	
	public String toString() {
		return typeNames[type] + " from " + start + " to " + (start+length);
	}

	public String getSymbol() {
		return Vibhag.typeSymbols[type];
	}
}
