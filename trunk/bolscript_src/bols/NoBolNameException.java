package bols;

public class NoBolNameException extends Exception {

	/**
	 * This is the serial VersionUID
	 */
	private static final long serialVersionUID = 1L;

	public NoBolNameException() {
		super("bolName not found in bolBase!");
		// TODO Auto-generated constructor stub
	}

	/**
	 * Occurs when a BolName was not found
	 */
	public NoBolNameException(String bolname) {
		super("bolName not found in bolBase: " + bolname + "!");
		// TODO Auto-generated constructor stub
	}

	/**
	 * Occurs when a BolName was not found
	 * @param text Output Message
	 * @param bolname The BolName that was not found
	 * @see BolBase, BolName
	 */
	public NoBolNameException(String text, String bolname) {
		super(text + ", bolName not found in bolBase: " + bolname + "!");
		// TODO Auto-generated constructor stub
	}
	
}
