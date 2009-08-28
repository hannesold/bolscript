package bolscript.sequences;

public interface Representable {
	public static final int BOL = 0;
	public static final int FOOTNOTE = 1;
	public static final int SPEED = 2;
	public static final int BRACKET_OPEN = 3;
	public static final int BRACKET_CLOSED = 4;
	public static final int OTHER = 5;
	public static final int COMMA = 6;
	public static final int BUNDLE = 7;
	public static final int SEQUENCE = 8;
	public int getType();
	
}
