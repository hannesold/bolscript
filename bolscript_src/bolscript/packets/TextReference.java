package bolscript.packets;

/**
 * Stores a reference to a section in a string,<br>
 * for example in the raw data of a composition.<br>
 * Stores start and endindex.
 * @author hannes
 *
 */
public class TextReference {
	private int startIndex;
	private int endIndex;
	private int length;
	
	public TextReference (int startIndex, int endIndex) {
		this.setStart(startIndex);
		this.setEnd(endIndex);
		this.length = endIndex-startIndex;
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

	public TextReference clone() {
		return new TextReference(startIndex, endIndex);
	}
}