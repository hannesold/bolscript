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
	
	public TextReference (int startIndex, int endIndex, int line) {
		this.setStart(startIndex);
		this.setEnd(endIndex);
		this.length = endIndex-startIndex;
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
}
