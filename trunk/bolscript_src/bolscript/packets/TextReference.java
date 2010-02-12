package bolscript.packets;

/**
 * Stores a reference to a section in a string,<br>
 * for example in the raw data of a composition.<br>
 * Stores start and endindex.
 * @author hannes
 *
 */
public class TextReference {
	
	/*public enum ReferenceType{
		PACKET, KEY, VALUE, OTHER
	}*/
	
	private int startIndex;
	private int endIndex;
	private int length;
	private int line;
	
	/**
	 * 
	 * @param startIndex the index of the first character of this reference
	 * @param endIndex the index of the first character AFTER this reference
	 * @param line
	 */
	public TextReference (int startIndex, int endIndex, int line) {
		this.setStart(startIndex);
		this.setEnd(endIndex);
		this.length = endIndex - startIndex;
		this.line = line;
	}
	
	public int length() {
		return length;
	}
	
	public String toString() {
		return "(Start: " + start() + ", End: " + end() + ")";
	}

	public void setStart(int startIndex) {
		this.startIndex = startIndex;
	}

	public int start() {
		return startIndex;
	}

	public void setEnd(int endIndex) {
		this.endIndex = endIndex;
	}

	/**
	 * the index of the first character AFTER this reference
	 * @return
	 */
	public int end() {
		return endIndex;
	}

	public int line() {
		return line;
	}
	
	public TextReference clone() {
		return new TextReference(startIndex, endIndex, line);
	}

	public boolean contains(int caretPosition) {
		return (startIndex<=caretPosition) && (caretPosition <= endIndex);
	}
	
	/**
	 * Returns a new TextReference, which is positioned relative to the given TextReference (startIndex and endIndex are added)
	 * @param other
	 */
	public TextReference translateBy(TextReference other) {
		return new TextReference(startIndex+other.startIndex, endIndex+other.startIndex,line+other.line);
	}
	
	/**
	 * Moves the TextReference by a given number of characters.
	 * @param translationSize
	 */
	public void move(int translationSize) {
		startIndex += translationSize;
		endIndex   += translationSize;	
	}
}
